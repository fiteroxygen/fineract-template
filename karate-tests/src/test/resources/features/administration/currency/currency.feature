Feature: Currency creation api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true


  @list
  Scenario: Retrieve currency Configuration
    Given path 'currencies'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def currencyConfigurations = response


  @create
  Scenario: Create currency configuration
    Given path 'currencies'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def administration = read('classpath:templates/administration.json')
    And request administration.currencies
    When method PUT
    Then status 200
    Then match response.changes.currencies == administration.currencies.currencies
    Then def conf = call read('classpath:features/administration/currency/currency.feature@list')
    Then match conf.currencyConfigurations.selectedCurrencyOptions[*].code contains response.changes.currencies