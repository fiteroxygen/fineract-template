Feature: Office creation api tests
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * configure ssl = true


  @list
  Scenario: Retrieve all offices
    Given path 'offices'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def offices = response

  @ignore
  @findone
  Scenario: Retrieve one by id
    Given path 'offices',officeId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    Then def office = response

  @ignore
  @edit
  Scenario: Edit office
    Given path 'offices',officeId
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def officeName = faker.funnyName().name()
    * def openingDate = df.format(calendar.getTime())
    * def administration = read('classpath:templates/administration.json')
    And request administration.office
    When method PUT
    Then status 200
    Then def officeId = response.officeId
    Then def officeResponse = call read('classpath:features/administration/office/office.feature@findone')
    Then match officeResponse.office.name contains officeName

  @create
  Scenario: Create office
    Given path 'offices'
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    * def officeName = faker.funnyName().name()
    * def openingDate = df.format(calendar.getTime())
    * def administration = read('classpath:templates/administration.json')
    And request administration.office
    When method POST
    Then status 200
    Then def officeId = response.officeId
    Then def officeResponse = call read('classpath:features/administration/office/office.feature@findone')
    Then match officeResponse.office.name contains officeName
    #test update or edit
    Then call read('classpath:features/administration/office/office.feature@edit') { officeId : '#(officeId)' }