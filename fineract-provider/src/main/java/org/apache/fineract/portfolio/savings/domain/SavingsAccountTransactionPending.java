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

package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.domain.Client;

@lombok.Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "m_savings_account_transaction_pending")
public class SavingsAccountTransactionPending extends AbstractAuditableCustom {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_account_id", referencedColumnName = "id")
    private SavingsAccount savingsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committed_transaction_id", referencedColumnName = "id")
    private SavingsAccountTransaction committedTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @Column(name = "transaction_type_enum")
    private Integer transactionTypeEnum;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "locale")
    private String locale;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "is_processed")
    private Boolean isProcessed;

    public static SavingsAccountTransactionPending createNew(final SavingsAccountTransaction savingsAccountTransaction,
            final SavingsAccount savingsAccount, final Client client, final String externalId, final String locale) {
        return new SavingsAccountTransactionPending(savingsAccount, null, client, savingsAccountTransaction.getTypeOf(),
                savingsAccountTransaction.getAmount(), locale, savingsAccount.getCurrency().getCode(), externalId, false);
    }

    public void updateCommittedTransaction(final SavingsAccountTransaction committedTransaction) {
        this.isProcessed = true;
        this.committedTransaction = committedTransaction;
    }
}
