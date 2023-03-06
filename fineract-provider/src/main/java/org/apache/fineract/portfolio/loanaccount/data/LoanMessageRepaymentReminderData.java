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
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.portfolio.loanaccount.domain.LoanOverdueReminder;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentReminder;

@Data
@NoArgsConstructor
public class LoanMessageRepaymentReminderData {

    private Long id;

    private Long loanId;
    private Long clientId;
    private Long groupId;
    private Long loanProductId;
    private Long loanScheduleId;
    private String dueDate;
    private Integer installmentNumber;
    private BigDecimal principalAmountOutStanding;
    private BigDecimal interestAmountOutStanding;
    private BigDecimal feesChargeAmountOutStanding;
    private BigDecimal penaltyChargeAmountOutStanding;
    private BigDecimal totalAmountOutStanding;
    private Long loanRepaymentReminderSettingsId;
    private String productName;
    private String clientName;
    private String groupName;
    private BigDecimal totalOverdueAmount;
    private String messageStatus;
    private String batchId;
    private Long createdBy;
    private String createdDate;
    private Long lastModifiedBy;
    private String lastModifiedDate;

    public LoanMessageRepaymentReminderData(LoanRepaymentReminder repaymentReminder) {
        this.id = repaymentReminder.getId();
        this.loanId = repaymentReminder.getLoanId();
        this.clientId = repaymentReminder.getClientId();
        this.groupId = repaymentReminder.getGroupId();
        this.loanProductId = repaymentReminder.getLoanProductId();
        this.loanScheduleId = repaymentReminder.getLoanScheduleId();
        this.dueDate = repaymentReminder.getDueDate().toString();
        this.installmentNumber = repaymentReminder.getInstallmentNumber();
        this.principalAmountOutStanding = repaymentReminder.getPrincipalAmountOutStanding();
        this.interestAmountOutStanding = repaymentReminder.getInterestAmountOutStanding();
        this.feesChargeAmountOutStanding = repaymentReminder.getFeesChargeAmountOutStanding();
        this.penaltyChargeAmountOutStanding = repaymentReminder.getPenaltyChargeAmountOutStanding();
        this.totalAmountOutStanding = repaymentReminder.getTotalAmountOutStanding();
        this.loanRepaymentReminderSettingsId = repaymentReminder.getLoanRepaymentReminderSettingsId();
        this.productName = repaymentReminder.getProductName();
        this.clientName = repaymentReminder.getClientName();
        this.groupName = repaymentReminder.getGroupName();
        this.totalOverdueAmount = repaymentReminder.getTotalOverdueAmount();
        this.messageStatus = repaymentReminder.getMessageStatus();
        this.batchId = repaymentReminder.getBatchId();
        this.createdBy = repaymentReminder.getCreatedBy().get();
        this.createdDate = repaymentReminder.getCreatedDate().get().toString();
        this.lastModifiedBy = repaymentReminder.getLastModifiedBy().get();
        this.lastModifiedDate = repaymentReminder.getLastModifiedDate().get().toString();
    }

    public LoanMessageRepaymentReminderData(LoanOverdueReminder loanOverdueReminder) {
        this.id = loanOverdueReminder.getId();
        this.loanId = loanOverdueReminder.getLoanId();
        this.clientId = loanOverdueReminder.getClientId();
        this.groupId = loanOverdueReminder.getGroupId();
        this.loanProductId = loanOverdueReminder.getLoanProductId();
        this.loanScheduleId = loanOverdueReminder.getLoanScheduleId();
        this.dueDate = loanOverdueReminder.getDueDate().toString();
        this.installmentNumber = loanOverdueReminder.getInstallmentNumber();
        this.principalAmountOutStanding = loanOverdueReminder.getPrincipalAmountOutStanding();
        this.interestAmountOutStanding = loanOverdueReminder.getInterestAmountOutStanding();
        this.feesChargeAmountOutStanding = loanOverdueReminder.getFeesChargeAmountOutStanding();
        this.penaltyChargeAmountOutStanding = loanOverdueReminder.getPenaltyChargeAmountOutStanding();
        this.totalAmountOutStanding = loanOverdueReminder.getTotalAmountOutStanding();
        this.loanRepaymentReminderSettingsId = loanOverdueReminder.getLoanRepaymentReminderSettingsId();
        this.productName = loanOverdueReminder.getProductName();
        this.clientName = loanOverdueReminder.getClientName();
        this.groupName = loanOverdueReminder.getGroupName();
        this.totalOverdueAmount = loanOverdueReminder.getTotalOverdueAmount();
        this.messageStatus = loanOverdueReminder.getMessageStatus();
        this.batchId = loanOverdueReminder.getBatchId();
        this.createdBy = loanOverdueReminder.getCreatedBy().get();
        this.createdDate = loanOverdueReminder.getCreatedDate().get().toString();
        this.lastModifiedBy = loanOverdueReminder.getLastModifiedBy().get();
        this.lastModifiedDate = loanOverdueReminder.getLastModifiedDate().get().toString();
    }
}
