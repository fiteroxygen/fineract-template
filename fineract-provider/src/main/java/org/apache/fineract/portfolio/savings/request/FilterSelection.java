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
package org.apache.fineract.portfolio.savings.request;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public interface FilterSelection {

    String USER_ID = "userId";
    String PRODUCT_ID = "productId";

    String OFFICE_ID = "officeId";
    String CURRENCY_CODE = "currencyCode";
    String WAS_REVERSED = "isReversal";
    String TRANSACTION_ID = "transactionId";

    String TRANSACTION_DATE = "transactionDate";
    String AVAILABLE_BALANCE = "availableBalance";
    String INTEREST_RATE = "interestRate";
    String OVERDRAFT_INTEREST_RATE = "overdraftInterestRate";
    String OVERDRAFT_INTEREST_LIMIT = "overdraftInterestLimit";
    String FEES_PAID = "feesPaid";
    String PENALTY_PAID = "penaltyPaid";
    String TRANSACTION_AMOUNT = "transactionAmount";

    Map<String, String> SAVINGS_SEARCH_REQUEST_MAP = ImmutableMap.<String, String>builder()
            .put(USER_ID, "au.id")
            .put(PRODUCT_ID, "sp.id")
            .put(CURRENCY_CODE, "sa.currency_code")
            .put(WAS_REVERSED, "tr.is_reversal")
            .put(TRANSACTION_ID, "tr.id")
            .put(TRANSACTION_DATE, "tr.transaction_date ")
            .put(AVAILABLE_BALANCE, "tr.running_balance_derived")
            .put(INTEREST_RATE, "sp.nominal_annual_interest_rate")
            .put(OVERDRAFT_INTEREST_RATE, "sp.nominal_annual_interest_rate_overdraft")
            .put(OVERDRAFT_INTEREST_LIMIT, "sp.overdraft_limit")
            .put(FEES_PAID, "feesPaid")
            .put(PENALTY_PAID, "penaltyPaid")
            .put(TRANSACTION_AMOUNT, "tr.amount").build();
}
