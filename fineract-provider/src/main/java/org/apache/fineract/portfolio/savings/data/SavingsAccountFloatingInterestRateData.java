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

package org.apache.fineract.portfolio.savings.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.apache.fineract.infrastructure.core.domain.LocalDateInterval;

public class SavingsAccountFloatingInterestRateData implements Serializable, Comparable<SavingsAccountFloatingInterestRateData> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final Long id;

    private final Long savingsAccountId;

    private LocalDate fromDate;

    private LocalDate endDate;

    private BigDecimal floatingInterestRate;

    private String locale = "";

    private String dateFormat = "";

    public SavingsAccountFloatingInterestRateData(Long id, Long savingsAccountId, LocalDate fromDate, LocalDate endDate,
            BigDecimal floatingInterestRate) {
        this.id = id;
        this.savingsAccountId = savingsAccountId;
        this.fromDate = fromDate;
        this.endDate = endDate;
        this.floatingInterestRate = floatingInterestRate;
    }

    public static SavingsAccountFloatingInterestRateData instance(Long id, Long savingsAccountId, LocalDate fromDate, LocalDate endDate,
            BigDecimal floatingInterestRate) {
        return new SavingsAccountFloatingInterestRateData(id, savingsAccountId, fromDate, endDate, floatingInterestRate);
    }

    public Long getId() {
        return id;
    }

    public Long getSavingsAccountId() {
        return savingsAccountId;
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

    public static SavingsAccountFloatingInterestRateData instanceWithProductDateForTemplate(LocalDate fromDate, LocalDate endDate,
            BigDecimal floatingInterestRate) {

        SavingsAccountFloatingInterestRateData savingsAccountFloatingInterestRateData = new SavingsAccountFloatingInterestRateData(null,
                null, fromDate, endDate, floatingInterestRate);
        savingsAccountFloatingInterestRateData.dateFormat = "dd MMMM yyyy";
        savingsAccountFloatingInterestRateData.locale = "en";
        return savingsAccountFloatingInterestRateData;
    }

    @Override
    public int compareTo(@NotNull SavingsAccountFloatingInterestRateData o) {
        return this.fromDate.compareTo(o.fromDate);
    }

    public Boolean isApplicableFloatingInterestRateForDate(final LocalDateInterval targetDateInterval) {
        final LocalDateInterval interval = LocalDateInterval.create(this.fromDate, this.endDate);
        return interval.contains(targetDateInterval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SavingsAccountFloatingInterestRateData)) {
            return false;
        }

        SavingsAccountFloatingInterestRateData that = (SavingsAccountFloatingInterestRateData) o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.fromDate, that.fromDate)
                && Objects.equals(this.floatingInterestRate, that.floatingInterestRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.fromDate, this.floatingInterestRate);
    }

}
