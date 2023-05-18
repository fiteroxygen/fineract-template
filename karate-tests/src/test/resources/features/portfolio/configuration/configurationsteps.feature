@ignore
Feature: Client creations steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def configurationData = read('classpath:templates/configuration.json')

  # enable enforce_loan_overdue_amount_min_balance_check configuration
  @ignore
  @enable_enforce_loan_overdue_amount_min_balance_check_step
  Scenario: Enable enforce_loan_overdue_amount_min_balance_check
    Given configure ssl = true
    Given path 'configurations' ,configurationsId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request configurationData.enabled_enforce_loan_overdue_amount_min_balance_check
    When method PUT
    Then status 200
    Then def res = response

  # disable enforce_loan_overdue_amount_min_balance_check configuration
  @ignore
  @disable_enforce_loan_overdue_amount_min_balance_check_step
  Scenario: Disable enforce_loan_overdue_amount_min_balance_check
    Given configure ssl = true
    Given path 'configurations' ,configurationsId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request configurationData.disable_enforce_loan_overdue_amount_min_balance_check
    When method PUT
    Then status 200
    Then def res = response