@ignore
Feature: Create default non-cashbased product
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def productsData = read('classpath:templates/product.json')

  @ignore
  @createsavings
  Scenario: Create default savings product
    Given path 'savingsproducts'
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.defaultSavings
    When method POST
    Then status 200
    Then def savingsResponse = response

  @ignore
  @listsavings
  Scenario: Get all savings product
    Given path 'savingsproducts'
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    * def filt = function(x){ return x.shortName == 'DS' }
    * def res = karate.filter(response, filt)

  @ignore
  @listloanproducts
  Scenario: Get all loan product
    Given path 'loanproducts'
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    * def filt = function(x){ return x.shortName == 'DL' }
    * def res = karate.filter(response, filt)

  @ignore
  @createloan
  Scenario: Create loan product
    Given configure ssl = true
    Given path 'loanproducts'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request productsData.defaultLoans
    When method POST
    Then status 200
    Then def loanProduct = response



