@ignore
Feature: Create loan stapes
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def productsData = read('classpath:templates/savings.json')


  #Set up parameters submittedOnDate, clientId, loanProductId, loanAmount, clientCreationDate
  @ignore
  @createloan
  Scenario: Create loan accounts
    Given configure ssl = true
    * def loanProduct = call read('classpath:features/portfolio/products/loanproduct.feature@fetchdefaultproduct')
    * def loanProductId = loanProduct.loanProductId
    #create savings account with clientCreationDate
    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(clientCreationDate)' }
    * def clientId = result.response.resourceId
    * def loansData = read('classpath:templates/loans.json')
    Given path 'loans'
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.loan1
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanId = response.resourceId


  #ensure to set parameters approvalDate, loanAmount, loanId
  @ignore
  @approveloan
  Scenario: Approve loan accounts
    Given configure ssl = true
    * def loansData = read('classpath:templates/loans.json')
    Given path 'loans',loanId
    And params {command:'approve'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.approve
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }

  #Ensure that we set loanAmount, disbursementDate, loanId
  @ignore
  @disburse
  Scenario: Disburse loans account
    Given configure ssl = true
    * def loansData = read('classpath:templates/loans.json')
    Given path 'loans',loanId
    And params {command:'disburse'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.disburse
    When method POST
    Then status 200
    Then match $ contains { resourceId: '#notnull' }
    Then def loanId = response.resourceId

  #Ensure that we set repaymentDate, repaymentAmount, loanId
  @ignore
  @loanRepayment
  Scenario: Loan repayment
    Given configure ssl = true
    * def loansData = read('classpath:templates/loans.json')
    Given path 'loans',loanId,'transactions'
    And params {command:'repayment'}
    And header Accept = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    And request loansData.transaction
    When method POST
    Then status 200
    Then match $ contains { loanId: '#notnull' }
    Then def loanId = response.loanId

  #ensure to set loanId
  @ignore
  @findloanbyid
  Scenario: Get loan account by id
    Given configure ssl = true
    Given path 'loans',loanId
    And header Accept = 'application/json'
    And header Content-Type = 'application/json'
    And header Authorization = authToken
    And header fineract-platform-tenantid = tenantId
    When method GET
    Then status 200
    * def loanAccount = response