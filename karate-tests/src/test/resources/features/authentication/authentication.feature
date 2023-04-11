Feature: Getting token by making call to authorisation server.

  Background:
    * url baseUrl

  Scenario: Authenticate with right credentials
    Given configure readTimeout = 100000
    Given configure ssl = true
    Given path 'authentication'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json, text/plain, */*'
    And header Fineract-Platform-TenantId = 'default'
    And request { username:'#(username)', password:'#(password)' }
    When method post
    Then status 200
    Then assert response.authenticated == true

  Scenario: Authenticate with wrong username
    Given configure readTimeout = 100000
    Given configure ssl = true
    Given path 'authentication'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json, text/plain, */*'
    And header Fineract-Platform-TenantId = 'default'
    And request { username:'#(faker.name().username())', password:'#(password)' }
    When method post
    Then status 401
    Then match $ contains { defaultUserMessage: '#notnull' }
    Then assert response.defaultUserMessage == 'Unauthenticated. Please login.'


  Scenario: Authenticate with wrong password
    Given configure readTimeout = 100000
    Given configure ssl = true
    Given path 'authentication'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json, text/plain, */*'
    And header Fineract-Platform-TenantId = 'default'
    And request { username:'#(username)', password:'#(faker.name.password())' }
    When method post
    Then status 401
    Then match $ contains { defaultUserMessage: '#notnull' }
    Then assert response.developerMessage == 'Invalid authentication details were passed in api request.'