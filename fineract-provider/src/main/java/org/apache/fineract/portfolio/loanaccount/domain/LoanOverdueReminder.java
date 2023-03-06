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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanOverdueReminderData;

@Entity
@Table(name = "m_loan_overdue_reminder")
public final class LoanOverdueReminder extends AbstractAuditableCustom {

    @Column(name = "loan_id", nullable = false)
    private Long loanId;
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    @Column(name = "loan_product_id", nullable = false)
    private Long loanProductId;
    @Column(name = "loan_schedule_id", nullable = false)
    private Long loanScheduleId;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;
    @Column(name = "principal_amount_outstanding", nullable = true)
    private BigDecimal principalAmountOutStanding;
    @Column(name = "interest_amount_outstanding", nullable = true)
    private BigDecimal interestAmountOutStanding;
    @Column(name = "fees_charge_amount_outstanding", nullable = true)
    private BigDecimal feesChargeAmountOutStanding;
    @Column(name = "penalty_charge_amount_outstanding", nullable = true)
    private BigDecimal penaltyChargeAmountOutStanding;
    @Column(name = "total_amount_outstanding", nullable = false)
    private BigDecimal totalAmountOutStanding;
    @Column(name = "loan_repayment_reminder_settings_id", nullable = false)
    private Long loanRepaymentReminderSettingsId;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "client_name", nullable = true)
    private String clientName;
    @Column(name = "group_name", nullable = true)
    private String groupName;
    @Column(name = "total_overdue_amount", nullable = false)
    private BigDecimal totalOverdueAmount;
    @Column(name = "message_status", nullable = true)
    private String messageStatus;
    @Column(name = "batch_id", nullable = true)
    private String batchId;

    public LoanOverdueReminder() {
        // default
    }

    public LoanOverdueReminder(Long loanRepaymentReminderSettingsId, LoanOverdueReminderData loanOverdueReminderData, String messageStatus,
            String batchId) {
        this.loanId = loanOverdueReminderData.getLoanId();
        this.clientId = loanOverdueReminderData.getClientId();
        this.groupId = loanOverdueReminderData.getGroupId();
        this.loanProductId = loanOverdueReminderData.getLoanProductId();
        this.loanScheduleId = loanOverdueReminderData.getLoanScheduleId();
        this.dueDate = loanOverdueReminderData.getDueDate();
        this.installmentNumber = loanOverdueReminderData.getInstallmentNumber();
        this.principalAmountOutStanding = loanOverdueReminderData.getPrincipalAmountOutStanding();
        this.interestAmountOutStanding = loanOverdueReminderData.getInterestAmountOutStanding();
        this.feesChargeAmountOutStanding = loanOverdueReminderData.getFeesChargeAmountOutStanding();
        this.penaltyChargeAmountOutStanding = loanOverdueReminderData.getPenaltyChargeAmountOutStanding();
        this.totalAmountOutStanding = loanOverdueReminderData.getTotalAmountOutStanding();
        this.loanRepaymentReminderSettingsId = loanRepaymentReminderSettingsId;
        this.productName = loanOverdueReminderData.getProductName();
        this.clientName = loanOverdueReminderData.getClientName();
        this.groupName = loanOverdueReminderData.getGroupName();
        this.totalOverdueAmount = loanOverdueReminderData.getTotalOverdueAmount();
        this.messageStatus = messageStatus;
        this.batchId = batchId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getLoanProductId() {
        return loanProductId;
    }

    public Long getLoanScheduleId() {
        return loanScheduleId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public BigDecimal getPrincipalAmountOutStanding() {
        return principalAmountOutStanding;
    }

    public BigDecimal getInterestAmountOutStanding() {
        return interestAmountOutStanding;
    }

    public BigDecimal getFeesChargeAmountOutStanding() {
        return feesChargeAmountOutStanding;
    }

    public BigDecimal getPenaltyChargeAmountOutStanding() {
        return penaltyChargeAmountOutStanding;
    }

    public BigDecimal getTotalAmountOutStanding() {
        return totalAmountOutStanding;
    }

    public Long getLoanRepaymentReminderSettingsId() {
        return loanRepaymentReminderSettingsId;
    }

    public String getProductName() {
        return productName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getGroupName() {
        return groupName;
    }

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public String getBatchId() {
        return batchId;
    }

}
