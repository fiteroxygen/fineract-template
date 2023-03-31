Feature: Funds creation api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true


  @list
  Scenario: Retrieve all funds
    Given path 'funds'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def funds = response


  @create
  Scenario: Create funds
    Given path 'funds'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def fundName = faker.funnyName().name()
    * def administration = read('classpath:templates/administration.json')
    And request administration.funds
    When method POST
    Then status 200
    Then def fundResponse = call read('classpath:features/administration/funds/funds.feature@list')
    Then match fundResponse.funds[*].name contains fundName