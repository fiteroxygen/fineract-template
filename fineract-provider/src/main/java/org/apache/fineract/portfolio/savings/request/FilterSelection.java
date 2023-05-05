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

    String USER_ID = "USER_ID";
    String ACCOUNT_OWNER_ID = "ACCOUNT_OWNER_ID";
    String PRODUCT_ID = "PRODUCT_ID";

    String OFFICE_ID = "OFFICE_ID";
    String CURRENCY_CODE = "CURRENCY_CODE";
    String WAS_REVERSED = "WAS_REVERSED";
    String TRANSACTION_ID = "TRANSACTION_ID";

    String TRANSACTION_DATE = "TRANSACTION_DATE";
    String AVAILABLE_BALANCE = "AVAILABLE_BALANCE";
    String INTEREST_RATE = "INTEREST_RATE";
    String OVERDRAFT_INTEREST_RATE = "OVERDRAFT_INTEREST_RATE";
    String OVERDRAFT_INTEREST_LIMIT = "OVERDRAFT_INTEREST_LIMIT";
    String FEES_PAID = "FEES_PAID";
    String PENALTY_PAID = "PENALTY_PAID";

    String AMOUNT = "TRANSACTION_AMOUNT";

    String INTEREST_PAID = "INTEREST_PAID";
    String PRINCIPAL_BALANCE = "PRINCIPAL_BALANCE";

    String TRANSACTION_TYPE = "TRANSACTION_TYPE";

    Map<String, String> SAVINGS_SEARCH_REQUEST_MAP = ImmutableMap.<String, String>builder().put(USER_ID, "au.id")
            .put(ACCOUNT_OWNER_ID, "mc.id").put(PRODUCT_ID, "sp.id").put(TRANSACTION_TYPE, "tr.transaction_type_enum")
            .put(OFFICE_ID, "tr.office_id").put(CURRENCY_CODE, "sa.currency_code").put(WAS_REVERSED, "tr.is_reversal")
            .put(TRANSACTION_ID, "tr.id").put(TRANSACTION_DATE, "tr.transaction_date ").put(AVAILABLE_BALANCE, "tr.running_balance_derived")
            .put(INTEREST_RATE, "sp.nominal_annual_interest_rate").put(OVERDRAFT_INTEREST_RATE, "sp.nominal_annual_interest_rate_overdraft")
            .put(OVERDRAFT_INTEREST_LIMIT, "sp.overdraft_limit").put(FEES_PAID, "feesPaid").put(PENALTY_PAID, "penaltyPaid")
            .put(AMOUNT, "tr.amount").build();
    Map<String, String> LOAN_SEARCH_REQUEST_MAP = ImmutableMap.<String, String>builder().put(USER_ID, "au.id").put(PRODUCT_ID, "lp.id")
            .put(ACCOUNT_OWNER_ID, "mc.id").put(TRANSACTION_TYPE, "tr.transaction_type_enum").put(CURRENCY_CODE, "l.currency_code")
            .put(WAS_REVERSED, "tr.is_reversed").put(TRANSACTION_ID, "tr.id").put(TRANSACTION_DATE, "tr.transaction_date ")
            .put(AVAILABLE_BALANCE, "tr.outstanding_loan_balance_derived").put(OFFICE_ID, "tr.office_id")
            .put(INTEREST_PAID, "tr.interest_portion_derived").put(PRINCIPAL_BALANCE, "tr.principal_portion_derived")
            .put(FEES_PAID, "tr.fee_charges_portion_derived").put(PENALTY_PAID, "tr.penalty_charges_portion_derived")
            .put(AMOUNT, "tr.amount").build();
}
