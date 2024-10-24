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
package org.apache.fineract.portfolio.account.service;

import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromAccountTypeParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.fromClientIdParamName;
import static org.apache.fineract.portfolio.account.AccountDetailConstants.toAccountTypeParamName;
import static org.apache.fineract.portfolio.account.api.StandingInstructionApiConstants.statusParamName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.exception.JobExecutionException;
import org.apache.fineract.infrastructure.jobs.service.JobName;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.notification.eventandlistener.NotificationEventPublisher;
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.api.StandingInstructionApiConstants;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.data.PortfolioAccountData;
import org.apache.fineract.portfolio.account.data.StandinAmountDueData;
import org.apache.fineract.portfolio.account.data.StandingInstructionDTO;
import org.apache.fineract.portfolio.account.data.StandingInstructionData;
import org.apache.fineract.portfolio.account.data.StandingInstructionDataValidator;
import org.apache.fineract.portfolio.account.data.StandingInstructionDuesData;
import org.apache.fineract.portfolio.account.data.StandingInstructionHistoryData;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetailRepository;
import org.apache.fineract.portfolio.account.domain.AccountTransferDetails;
import org.apache.fineract.portfolio.account.domain.AccountTransferRecurrenceType;
import org.apache.fineract.portfolio.account.domain.AccountTransferStandingInstruction;
import org.apache.fineract.portfolio.account.domain.StandingInstructionAssembler;
import org.apache.fineract.portfolio.account.domain.StandingInstructionRepository;
import org.apache.fineract.portfolio.account.domain.StandingInstructionStatus;
import org.apache.fineract.portfolio.account.domain.StandingInstructionType;
import org.apache.fineract.portfolio.account.exception.StandingInstructionNotFoundException;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.ScheduledDateGenerator;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandingInstructionWritePlatformServiceImpl implements StandingInstructionWritePlatformService {

    private static final Logger LOG = LoggerFactory.getLogger(StandingInstructionWritePlatformServiceImpl.class);

    private final StandingInstructionDataValidator standingInstructionDataValidator;
    private final StandingInstructionAssembler standingInstructionAssembler;
    private final AccountTransferDetailRepository accountTransferDetailRepository;
    private final StandingInstructionRepository standingInstructionRepository;
    private final StandingInstructionReadPlatformService standingInstructionReadPlatformService;
    private final StandingInstructionHistoryReadPlatformService standingInstructionHistoryReadPlatformService;
    private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseSpecificSQLGenerator sqlGenerator;
    private final PlatformSecurityContext context;
    private final NotificationEventPublisher notificationEventPublisher;
    private final Environment env;
    private final FromJsonHelper fromJsonHelper;

    @Autowired
    public StandingInstructionWritePlatformServiceImpl(PlatformSecurityContext context,
            final StandingInstructionDataValidator standingInstructionDataValidator,
            final StandingInstructionAssembler standingInstructionAssembler,
            final AccountTransferDetailRepository accountTransferDetailRepository,
            final StandingInstructionRepository standingInstructionRepository,
            final StandingInstructionReadPlatformService standingInstructionReadPlatformService,
            final AccountTransfersWritePlatformService accountTransfersWritePlatformService, final JdbcTemplate jdbcTemplate,
            DatabaseSpecificSQLGenerator sqlGenerator, FromJsonHelper fromJsonHelper,
            final StandingInstructionHistoryReadPlatformService standingInstructionHistoryReadPlatformService,
            final NotificationEventPublisher notificationEventPublisher, final Environment env) {
        this.standingInstructionDataValidator = standingInstructionDataValidator;
        this.standingInstructionAssembler = standingInstructionAssembler;
        this.accountTransferDetailRepository = accountTransferDetailRepository;
        this.standingInstructionRepository = standingInstructionRepository;
        this.standingInstructionReadPlatformService = standingInstructionReadPlatformService;
        this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
        this.jdbcTemplate = jdbcTemplate;
        this.sqlGenerator = sqlGenerator;
        this.standingInstructionHistoryReadPlatformService = standingInstructionHistoryReadPlatformService;
        this.context = context;
        this.notificationEventPublisher = notificationEventPublisher;
        this.fromJsonHelper = fromJsonHelper;
        this.env = env;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        this.standingInstructionDataValidator.validateForCreate(command);

        final Integer fromAccountTypeId = command.integerValueSansLocaleOfParameterNamed(fromAccountTypeParamName);
        final PortfolioAccountType fromAccountType = PortfolioAccountType.fromInt(fromAccountTypeId);

        final Integer toAccountTypeId = command.integerValueSansLocaleOfParameterNamed(toAccountTypeParamName);
        final PortfolioAccountType toAccountType = PortfolioAccountType.fromInt(toAccountTypeId);

        final Long fromClientId = command.longValueOfParameterNamed(fromClientIdParamName);

        Long standingInstructionId = null;
        try {
            if (isSavingsToSavingsAccountTransfer(fromAccountType, toAccountType)) {
                final AccountTransferDetails standingInstruction = this.standingInstructionAssembler
                        .assembleSavingsToSavingsTransfer(command);
                this.accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();
            } else if (isSavingsToLoanAccountTransfer(fromAccountType, toAccountType)) {
                final AccountTransferDetails standingInstruction = this.standingInstructionAssembler.assembleSavingsToLoanTransfer(command);
                this.accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();
            } else if (isLoanToSavingsAccountTransfer(fromAccountType, toAccountType)) {

                final AccountTransferDetails standingInstruction = this.standingInstructionAssembler.assembleLoanToSavingsTransfer(command);
                this.accountTransferDetailRepository.saveAndFlush(standingInstruction);
                standingInstructionId = standingInstruction.accountTransferStandingInstruction().getId();

            }
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            final Throwable throwable = dve.getMostSpecificCause();
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
        final CommandProcessingResultBuilder builder = new CommandProcessingResultBuilder().withEntityId(standingInstructionId)
                .withClientId(fromClientId);
        return builder.build();
    }

    private void handleDataIntegrityIssues(final JsonCommand command, Throwable realCause, final NonTransientDataAccessException dve) {

        if (realCause.getMessage().contains("name")) {
            final String name = command.stringValueOfParameterNamed(StandingInstructionApiConstants.nameParamName);
            throw new PlatformDataIntegrityException("error.msg.standinginstruction.duplicate.name",
                    "Standinginstruction with name `" + name + "` already exists", "name", name);
        }
        LOG.error("Error occured.", dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private boolean isLoanToSavingsAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isLoanAccount() && toAccountType.isSavingsAccount();
    }

    private boolean isSavingsToLoanAccountTransfer(final PortfolioAccountType fromAccountType, final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isLoanAccount();
    }

    private boolean isSavingsToSavingsAccountTransfer(final PortfolioAccountType fromAccountType,
            final PortfolioAccountType toAccountType) {
        return fromAccountType.isSavingsAccount() && toAccountType.isSavingsAccount();
    }

    @Override
    public CommandProcessingResult update(final Long id, final JsonCommand command) {
        this.standingInstructionDataValidator.validateForUpdate(command);
        AccountTransferStandingInstruction standingInstructionsForUpdate = this.standingInstructionRepository.findById(id)
                .orElseThrow(() -> new StandingInstructionNotFoundException(id));
        final Map<String, Object> actualChanges = standingInstructionsForUpdate.update(command);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(id).with(actualChanges).build();
    }

    @Override
    public CommandProcessingResult delete(final Long id) {
        AccountTransferStandingInstruction standingInstructionsForUpdate = this.standingInstructionRepository.findById(id).orElseThrow();
        // update the "deleted" and "name" properties of the standing
        // instruction
        standingInstructionsForUpdate.delete();

        final Map<String, Object> actualChanges = new HashMap<>();
        actualChanges.put(statusParamName, StandingInstructionStatus.DELETED.getValue());
        return new CommandProcessingResultBuilder().withEntityId(id).with(actualChanges).build();
    }

    @Override
    @CronTarget(jobName = JobName.NOTIFY_FAILED_STANDING_INSTRUCTIONS)
    public void sendNotificationForFailedStandingInstructions() throws JobExecutionException {
        LOG.info("Sending notification for failed SI with Insufficient Balance");

        /// get all the standing instruction failed with insufficient balance
        StandingInstructionDTO standingInstructionDTO = new StandingInstructionDTO(null, null, null, null, null, null, null, null);
        Collection<StandingInstructionHistoryData> standingInstructionHistoryDataCollection = this.standingInstructionHistoryReadPlatformService
                .retrieveAllFailedWithInsufficientBalance(standingInstructionDTO);

        if (CollectionUtils.isNotEmpty(standingInstructionHistoryDataCollection)) {
            for (StandingInstructionHistoryData standingInstructionHistoryData : standingInstructionHistoryDataCollection) {
                /// create and send the data for notification message - loan account, saving account id, client name
                String notificationContent = String.format(
                        "Standing Instruction to transfer Amount: %s to  ToAccount: %s is failed due to insufficient fund in FromAccount: %s.",
                        standingInstructionHistoryData.getAmount(), standingInstructionHistoryData.getToAccount().accountId(),
                        standingInstructionHistoryData.getFromAccount().accountId());
                buildNotification("Standing Instruction", standingInstructionHistoryData.getStandingInstructionId(), notificationContent,
                        "standingInstructionFailed", context.authenticatedUser().getId(),
                        standingInstructionHistoryData.getToClient().getId());

                // update the notification_sent flag in Standing Instruction History to indicate notification has been
                // sent
                final String updateQuery = "UPDATE m_account_transfer_standing_instructions_history SET is_notification_sent = ? where standing_instruction_id = ?";
                this.jdbcTemplate.update(updateQuery, true, standingInstructionHistoryData.getStandingInstructionId());
            }
        }
    }

    @Override
    @CronTarget(jobName = JobName.PROCESS_TOTAL_AMOUNT_DUE_FOR_FAILED_STANDING_INSTRUCTIONS)
    public void processTotalAmountDueFailedStandingInstructions() throws JobExecutionException {
        LOG.info("Sending Failed Instruction to Process Total Amount Due");
        AppUser appUser = context.authenticatedUser();
        /// get all the standing instruction failed with insufficient balance
        Collection<StandingInstructionHistoryData> standingInstructionHistoryDataCollection = this.standingInstructionHistoryReadPlatformService
                .retrieveAllForProcessingAmountDue();

        if (CollectionUtils.isNotEmpty(standingInstructionHistoryDataCollection)) {
            for (StandingInstructionHistoryData standingInstructionHistoryData : standingInstructionHistoryDataCollection) {
                /// create and send the data for notification message - loan account, saving account id, client name
                buildProcessAmountMessage(standingInstructionHistoryData, appUser);

                // increase the process_count field in Standing Instruction History to indicate the number of times the
                // amount_due has been processed
                final String updateQuery = "UPDATE m_account_transfer_standing_instructions_history SET processing_count = ? where  id = ?";
                Long finalProcessingCount = standingInstructionHistoryData.getProcessingCount() + 1;
                this.jdbcTemplate.update(updateQuery, finalProcessingCount, standingInstructionHistoryData.getHistoryId());
            }
        }
    }

    private void buildProcessAmountMessage(StandingInstructionHistoryData standingInstructionHistoryData, AppUser appUser) {

        String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        PortfolioAccountData loanAccount = standingInstructionHistoryData.getToAccount();
        ClientData toClient = standingInstructionHistoryData.getToClient();

        BigDecimal amountDue = standingInstructionHistoryData.getAmount();

        StandingInstructionDuesData duesData = this.standingInstructionReadPlatformService.retriveLoanDuesData(loanAccount.accountId());
        if (duesData != null) {
            amountDue = duesData.totalDueAmount();
        }

        // if there is no amount due on the loan. Update the amount_due_processed to true and dont send any notification
        if (amountDue.compareTo(BigDecimal.ZERO) <= 0) {
            final String updateQuery = "UPDATE m_account_transfer_standing_instructions_history SET amount_due_processed=? where  id = ?";
            this.jdbcTemplate.update(updateQuery, true, standingInstructionHistoryData.getHistoryId());
            return;
        }

        StandinAmountDueData.StandinAmountDueDataBuilder standinAmountDue = StandinAmountDueData.builder().clientId(toClient.id())
                .loanId(loanAccount.accountId()).amountDUe(amountDue);
        Set<Long> userIds = new HashSet<>();
        NotificationData notificationData = new NotificationData("Process Amount Due",
                standingInstructionHistoryData.getStandingInstructionId(), "PROCESS_AMOUNT_DUE", appUser.getId(),
                this.fromJsonHelper.toJson(standinAmountDue), false, false, tenantIdentifier, appUser.getOffice().getId(), userIds);
        try {
            notificationEventPublisher.broadcastGenericActiveMqNotification(notificationData,
                    env.getProperty("fineract.activemq.loanOverdueCollectionsQueue"));
        } catch (Exception e) {
            // We want to avoid rethrowing the exception to stop the business transaction from rolling back
            LOG.error("Error while broadcasting notification event", e);
        }
    }

    private void buildNotification(String objectType, Long objectIdentifier, String notificationContent, String eventType, Long appUserId,
            Long officeId) {

        String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        NotificationData notificationData = new NotificationData(objectType, objectIdentifier, eventType, appUserId, notificationContent,
                false, false, tenantIdentifier, officeId, null);
        try {
            notificationEventPublisher.broadcastGenericActiveMqNotification(notificationData,
                    env.getProperty("fineract.activemq.standingInstructionInsufficientBalanceFailureQueue"));
        } catch (Exception e) {
            // We want to avoid rethrowing the exception to stop the business transaction from rolling back
            LOG.error("Error while broadcasting notification event", e);
        }
    }

    @Override
    @CronTarget(jobName = JobName.EXECUTE_STANDING_INSTRUCTIONS)
    public void executeStandingInstructions() throws JobExecutionException {
        Collection<StandingInstructionData> instructionDatas = this.standingInstructionReadPlatformService
                .retrieveAll(StandingInstructionStatus.ACTIVE.getValue());
        List<Throwable> errors = new ArrayList<>();
        LocalDate transactionDate = DateUtils.getBusinessLocalDate();
        for (StandingInstructionData data : instructionDatas) {
            boolean isDueForTransfer = false;
            AccountTransferRecurrenceType recurrenceType = data.recurrenceType();
            StandingInstructionType instructionType = data.instructionType();
            if (recurrenceType.isPeriodicRecurrence()) {
                final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
                PeriodFrequencyType frequencyType = data.recurrenceFrequency();
                LocalDate startDate = data.validFrom();
                if (frequencyType.isMonthly()) {
                    startDate = startDate.withDayOfMonth(data.recurrenceOnDay());
                    if (startDate.isBefore(data.validFrom())) {
                        startDate = startDate.plusMonths(1);
                    }
                } else if (frequencyType.isYearly()) {
                    startDate = startDate.withDayOfMonth(data.recurrenceOnDay()).withMonth(data.recurrenceOnMonth());
                    if (startDate.isBefore(data.validFrom())) {
                        startDate = startDate.plusYears(1);
                    }
                }
                isDueForTransfer = scheduledDateGenerator.isDateFallsInSchedule(frequencyType, data.recurrenceInterval(), startDate,
                        transactionDate);

            }
            BigDecimal transactionAmount = data.amount();
            if (data.toAccountType().isLoanAccount()
                    && (recurrenceType.isDuesRecurrence() || (isDueForTransfer && instructionType.isDuesAmoutTransfer()))) {
                StandingInstructionDuesData standingInstructionDuesData = this.standingInstructionReadPlatformService
                        .retriveLoanDuesData(data.toAccount().accountId());
                if (data.instructionType().isDuesAmoutTransfer()) {
                    transactionAmount = standingInstructionDuesData.totalDueAmount();
                }
                if (recurrenceType.isDuesRecurrence()) {
                    isDueForTransfer = (DateUtils.getBusinessLocalDate().equals(standingInstructionDuesData.dueDate())
                            || (standingInstructionDuesData.dueDate() != null
                                    && DateUtils.getBusinessLocalDate().isAfter(standingInstructionDuesData.dueDate())));
                }
            }

            if (isDueForTransfer && transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
                final SavingsAccount fromSavingsAccount = null;
                final boolean isRegularTransaction = true;
                final boolean isExceptionForBalanceCheck = false;
                AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDate, transactionAmount, data.fromAccountType(),
                        data.toAccountType(), data.fromAccount().accountId(), data.toAccount().accountId(),
                        data.name() + " Standing instruction transfer ", null, null, null, null, data.toTransferType(), null, null,
                        data.transferType().getValue(), null, null, null, null, null, fromSavingsAccount, isRegularTransaction,
                        isExceptionForBalanceCheck);
                final boolean transferCompleted = transferAmount(errors, accountTransferDTO, data.getId());

                if (transferCompleted) {
                    final String updateQuery = "UPDATE m_account_transfer_standing_instructions SET last_run_date = ? where id = ?";
                    this.jdbcTemplate.update(updateQuery, transactionDate, data.getId());
                }

            }
        }
        if (!errors.isEmpty()) {
            throw new JobExecutionException(errors);
        }
    }

    private boolean transferAmount(final List<Throwable> errors, final AccountTransferDTO accountTransferDTO, final Long instructionId) {
        boolean transferCompleted = true;
        StringBuilder errorLog = new StringBuilder();
        StringBuilder updateQuery = new StringBuilder(
                "INSERT INTO m_account_transfer_standing_instructions_history (standing_instruction_id, " + sqlGenerator.escape("status")
                        + ", amount, execution_time, error_log) VALUES (");
        try {
            this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO);
        } catch (final PlatformApiDataValidationException e) {
            errors.add(new Exception("Validation exception while transferring funds for standing Instruction id" + instructionId + " from "
                    + accountTransferDTO.getFromAccountId() + " to " + accountTransferDTO.getToAccountId(), e));
            errorLog.append("Validation exception while transferring funds " + e.getDefaultUserMessage());
        } catch (final InsufficientAccountBalanceException e) {
            errors.add(new Exception(StandingInstructionApiConstants.insufficientBalanceExceptionMessage
                    + " while transferring funds for standing Instruction id" + instructionId + " from "
                    + accountTransferDTO.getFromAccountId() + " to " + accountTransferDTO.getToAccountId(), e));
            errorLog.append(StandingInstructionApiConstants.insufficientBalanceExceptionMessage);
        } catch (final AbstractPlatformServiceUnavailableException e) {
            errors.add(new Exception("Platform exception while transferring funds for standing Instruction id" + instructionId + " from "
                    + accountTransferDTO.getFromAccountId() + " to " + accountTransferDTO.getToAccountId(), e));
            errorLog.append("Platform exception while transferring funds " + e.getDefaultUserMessage());
        } catch (Exception e) {
            errors.add(new Exception("Unhandled System Exception while transferring funds for standing Instruction id" + instructionId
                    + " from " + accountTransferDTO.getFromAccountId() + " to " + accountTransferDTO.getToAccountId(), e));
            errorLog.append("Exception while transferring funds " + e.getMessage());

        }
        if (accountTransferDTO.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0) {
            updateQuery.append(instructionId).append(",");
            if (errorLog.length() > 0) {
                transferCompleted = false;
                updateQuery.append("'failed'").append(",");
            } else {
                updateQuery.append("'success'").append(",");
            }
            updateQuery.append(accountTransferDTO.getTransactionAmount().doubleValue());
            updateQuery.append(", ").append(sqlGenerator.currentTenantDateTime()).append(" ");
            updateQuery.append(", '").append(errorLog).append("')");
            this.jdbcTemplate.update(updateQuery.toString());
        } else
            transferCompleted = false;
        return transferCompleted;
    }
}
