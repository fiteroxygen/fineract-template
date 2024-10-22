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
package org.apache.fineract.portfolio.account.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.client.data.ClientData;

@SuppressWarnings("unused")
public class StandingInstructionHistoryData {

    private final Long standingInstructionId;
    private final String name;
    private final OfficeData fromOffice;
    private final ClientData fromClient;
    private final EnumOptionData fromAccountType;
    private final PortfolioAccountData fromAccount;
    private final EnumOptionData toAccountType;
    private final PortfolioAccountData toAccount;
    private final OfficeData toOffice;
    private final ClientData toClient;
    private final BigDecimal amount;
    private final String status;
    private final LocalDate executionTime;
    private final String errorLog;
    private final Long historyId;
    private final Long processingCount;

    public StandingInstructionHistoryData(final Long standingInstructionId, final String name, final OfficeData fromOffice,
            final ClientData fromClient, final EnumOptionData fromAccountType, final PortfolioAccountData fromAccount,
            final EnumOptionData toAccountType, final PortfolioAccountData toAccount, final OfficeData toOffice, final ClientData toClient,
            final BigDecimal amount, final String status, final LocalDate executionTime, final String errorLog, Long historyId,
            final Long processingCount) {
        this.standingInstructionId = standingInstructionId;
        this.name = name;
        this.fromOffice = fromOffice;
        this.fromClient = fromClient;
        this.fromAccountType = fromAccountType;
        this.toAccountType = toAccountType;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.toOffice = toOffice;
        this.toClient = toClient;
        this.amount = amount;
        this.errorLog = errorLog;
        this.status = status;
        this.executionTime = executionTime;
        this.processingCount = processingCount;
        this.historyId = historyId;
    }

    public Long getStandingInstructionId() {
        return standingInstructionId;
    }

    public String getName() {
        return name;
    }

    public OfficeData getFromOffice() {
        return fromOffice;
    }

    public ClientData getFromClient() {
        return fromClient;
    }

    public EnumOptionData getFromAccountType() {
        return fromAccountType;
    }

    public PortfolioAccountData getFromAccount() {
        return fromAccount;
    }

    public EnumOptionData getToAccountType() {
        return toAccountType;
    }

    public PortfolioAccountData getToAccount() {
        return toAccount;
    }

    public OfficeData getToOffice() {
        return toOffice;
    }

    public ClientData getToClient() {
        return toClient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getExecutionTime() {
        return executionTime;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public Long getProcessingCount() {
        Long processingCount = this.processingCount;
        if (processingCount == null) {
            processingCount = 0L;
        }
        return processingCount;
    }
}
