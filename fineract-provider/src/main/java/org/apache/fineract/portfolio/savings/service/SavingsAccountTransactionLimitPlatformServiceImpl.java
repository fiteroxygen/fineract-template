/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationProperty;
import org.apache.fineract.infrastructure.configuration.domain.GlobalConfigurationRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformServiceUnavailableException;
import org.apache.fineract.infrastructure.core.service.TransactionHandler;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionPending;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionPendingRepository;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class SavingsAccountTransactionLimitPlatformServiceImpl implements SavingsAccountTransactionLimitPlatformService {

    private final GlobalConfigurationRepositoryWrapper globalConfigurationRepositoryWrapper;
    private final SavingsAccountTransactionPendingRepository savingsAccountTransactionPendingRepository;
    private final PlatformSecurityContext context;
    private final TransactionHandler transactionHandler;

    @Override
    public void handleApprovalsForSessionTransactionLimits(final JsonCommand command, final SavingsAccount savingsAccount,
            final SavingsAccountTransaction savingsAccountTransaction, final Client client, final CommandProcessingResultBuilder builder) {

        GlobalConfigurationProperty maxWithdrawPerSessionConfig = this.globalConfigurationRepositoryWrapper
                .findOneByNameWithNotFoundDetection("max-withdraw-amount-per-session");
        if (maxWithdrawPerSessionConfig.isEnabled() && maxWithdrawPerSessionConfig.getValue() > 0l && command.entityId() == null
                && maxWithdrawPerSessionConfig.getValue() < savingsAccountTransaction.getAmount().longValue()) {
            // check if max withdraw limit is per session is configured
            transactionHandler.pauseTransactionAndRun(() -> {
                SavingsAccountTransactionPending transactionPendingApproval = SavingsAccountTransactionPending
                        .createNew(savingsAccountTransaction, savingsAccount, client, null, command.locale());
                this.savingsAccountTransactionPendingRepository.saveAndFlush(transactionPendingApproval);
                builder.setRollbackTransaction(true);
                builder.withSubEntityId(transactionPendingApproval.getId());
                builder.withActionName("WITHDRAWAL_WITH_LIMIT");
                return null;
            });
        }

        if (maxWithdrawPerSessionConfig.isEnabled() && maxWithdrawPerSessionConfig.getValue() > 0l && command.entityId() != null
                && maxWithdrawPerSessionConfig.getValue() < savingsAccountTransaction.getAmount().longValue()) {

            // Check that app user has specific permission APPROVEWITHDRAWAL

            transactionHandler.runInTransaction(() -> {
                final AppUser currentUser = this.context.authenticatedUser();
                final boolean hasPermission = currentUser.hasSpecificPermissionTo("WITHDRAWAL_WITH_LIMIT_SAVINGSACCOUNT_CHECKER");
                if (!hasPermission) {
                    throw new PlatformServiceUnavailableException("error.msg.savings.account.transaction.approve.withdrawal.permission",
                            "User does not have permission to approve limit withdrawal");
                }

                SavingsAccountTransactionPending transactionPendingApproval = this.savingsAccountTransactionPendingRepository
                        .findById(command.subentityId()).orElse(null);
                if (transactionPendingApproval == null) {
                    throw new PlatformServiceUnavailableException("error.msg.savings.account.transaction.approve.withdrawal.not.found",
                            "Transaction Pending Approval not found");
                }
                transactionPendingApproval.updateCommittedTransaction(savingsAccountTransaction);
                this.savingsAccountTransactionPendingRepository.saveAndFlush(transactionPendingApproval);
                return null;
            });

        }
    }
}
