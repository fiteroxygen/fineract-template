@ignore
Feature: Client creations steps
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def clientdata = read('classpath:templates/client.json')


  #set parameter clientCreationDate
  @ignore
  @create
  Scenario: Create client test
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request clientdata.createClientPayload
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def clientId = response.resourceId

  @ignore
  @findbyid
  Scenario: Get client by id test
    * def result = call read('classpath:features/portfolio/clients/clientcreation.feature@create')
    * def clientId = result.response.resourceId
    Given path 'clients',clientId
    Given configure ssl = true
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def client = response

  @ignore
  @list
  Scenario: Get all clients
    Given configure ssl = true
    Given path 'clients'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
