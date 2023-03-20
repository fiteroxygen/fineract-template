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

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RecurringMissedTargetData {

    @Id
    private Integer savingsId;
    private String accountNo;
    private Integer statusEnum;
    private BigDecimal depositTillDate;
    private BigDecimal totalInterest;
    private BigDecimal principalAmount;
    private LocalDate maturityDate;
    private Boolean addPenaltyOnMissedTargetSavings;
    private Long productId;

    public RecurringMissedTargetData(Integer savingsId, String accountNo, Integer statusEnum, BigDecimal depositTillDate,
            BigDecimal totalInterest, BigDecimal principalAmount, LocalDate maturityDate, Boolean addPenaltyOnMissedTargetSavings,
            Long productId) {
        this.savingsId = savingsId;
        this.accountNo = accountNo;
        this.statusEnum = statusEnum;
        this.depositTillDate = depositTillDate;
        this.totalInterest = totalInterest;
        this.principalAmount = principalAmount;
        this.maturityDate = maturityDate;
        this.addPenaltyOnMissedTargetSavings = addPenaltyOnMissedTargetSavings;
        this.productId = productId;
    }
}
