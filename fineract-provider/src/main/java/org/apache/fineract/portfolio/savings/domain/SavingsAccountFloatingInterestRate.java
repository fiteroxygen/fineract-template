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
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.infrastructure.core.domain.LocalDateInterval;

@Entity
@Table(name = "m_savings_account_floating_interest_rate")
public class SavingsAccountFloatingInterestRate extends AbstractAuditableCustom implements Comparable<SavingsAccountFloatingInterestRate> {

    @ManyToOne
    @JoinColumn(name = "savings_account_id")
    private SavingsAccount savingsAccount;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "floating_interest_rate", scale = 6, precision = 19, nullable = false)
    private BigDecimal floatingInterestRate;

    public SavingsAccountFloatingInterestRate(SavingsAccount savingsAccount, LocalDate fromDate, LocalDate endDate,
            BigDecimal floatingInterestRate) {
        this.savingsAccount = savingsAccount;
        this.fromDate = fromDate;
        this.endDate = endDate;
        this.floatingInterestRate = floatingInterestRate;
    }

    protected SavingsAccountFloatingInterestRate() {
        //
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFloatingInterestRate() {
        return floatingInterestRate;
    }

    public void setFloatingInterestRate(BigDecimal floatingInterestRate) {
        this.floatingInterestRate = floatingInterestRate;
    }

    public static SavingsAccountFloatingInterestRate createNew(LocalDate fromDate, LocalDate toDate, BigDecimal floatingInterestRate,
            SavingsAccount savingsAccount) {
        return new SavingsAccountFloatingInterestRate(savingsAccount, fromDate, toDate, floatingInterestRate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SavingsAccountFloatingInterestRate)) {
            return false;
        }

        SavingsAccountFloatingInterestRate that = (SavingsAccountFloatingInterestRate) o;
        return Objects.equals(this.savingsAccount.getId(), that.savingsAccount.getId()) && Objects.equals(this.fromDate, that.fromDate)
                && Objects.equals(this.floatingInterestRate, that.floatingInterestRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.savingsAccount.getId(), this.fromDate, this.floatingInterestRate);
    }

    @Override
    public int compareTo(@NotNull SavingsAccountFloatingInterestRate o) {
        return this.fromDate.compareTo(o.fromDate);
    }

    public Boolean isApplicableFloatingInterestRateForDate(final LocalDateInterval targetDateInterval) {
        final LocalDateInterval interval = LocalDateInterval.create(this.fromDate, this.endDate);
        return interval.contains(targetDateInterval);
    }
}
