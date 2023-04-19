
Feature: Search loan transactions
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def loanSearchPayload = read('classpath:templates/searchingpayload.json')

  @search
  Scenario: Search today's loan transactions
    Given configure ssl = true
    Given path 'loans/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loanSearchPayload.loanTodaySearchPayload
    When method POST
    Then status 200

  @search
  Scenario: Search loan transactions
    Given configure ssl = true
    Given path 'loans/transactions/search'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loanSearchPayload.loanPreviousSearchPayload
    When method POST
    Then status 200
    And  response.length > 0