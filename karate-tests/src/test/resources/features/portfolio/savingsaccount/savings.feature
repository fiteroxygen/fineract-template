Feature: Test savings account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl




  @createandactivateaccount
  Scenario: Create and activate savings account
    #create savings account step
    * def submittedOnDate = df.format(faker.date().past(30, 29, TimeUnit.DAYS))

    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@create') { submittedOnDate : '#(submittedOnDate)', clientCreationDate : '#(submittedOnDate)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId
    # Test deposit to savings account
    * def depositAmount = 1000
    * def transactionDate = df.format(faker.date().past(15, TimeUnit.DAYS))
    * def requestVariables = { savingsId : '#(activeSavingsId)', transactionAmount : '#(depositAmount)', transactionDate : '#(transactionDate)', command : 'deposit' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transaction') requestVariables
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert transactionAmount == savingsResponse.savingsAccount.summary.availableBalance
    # Test withdraw from savings account
    * def withdrawAmount = 500
    * def withdrawDate = df.format(calendar.getTime())
    * def withdrawalparams = { savingsId : '#(activeSavingsId)', transactionAmount : '#(withdrawAmount)', transactionDate : '#(withdrawDate)', command : 'withdrawal' }
    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@transaction') withdrawalparams
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * def newBalance = depositAmount - withdrawAmount
    * assert newBalance == savingsResponse.savingsAccount.summary.availableBalance