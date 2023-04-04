@ignore
Feature: Create Loan Product and It's charges Steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @ignore
  @createLoanProductWithOverdueChargeSteps
  Scenario: Create loan product With Overdue Charge
    Given configure ssl = true
    * def productsData = read('classpath:templates/loanProduct.json')
    Given path 'loanproducts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.loanProductWithOverDueChargePayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanProductId = response.resourceId



  @ignore
  @createFlatOverdueChargeWithOutFrequencySteps
  Scenario: Create Flat Overdue Charge WithOut Frequency
    Given configure ssl = true
    * def chargesData = read('classpath:templates/loansCharges.json')
    Given path 'charges'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request chargesData.penaltyFlatChargeWithNoFrequencyPayLoad
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def chargeId = response.resourceId



