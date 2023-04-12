Feature: Test loan account apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl

  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseByCash
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges and disburse it by Cash
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburse') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

      # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges',totalCharges)
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue


    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 12
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1


  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccount
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId



        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId



    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanWithConfigurableProductStep') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)' }
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges',totalCharges)
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue


#         # Loop through the Repayment Schedule Object  --> Double value is failing to be casted as integer - WIP
#    * def assertRepaymentSchedulePenaltyChargeAmount =
#      """
#      function(repaymentSchedules,expectedTotalAmountToBeCharged){
#        for(var i = 0; i < repaymentSchedules.length; i++){
#          expectedTotalAmountToBeCharged = expectedTotalAmountToBeCharged + repaymentSchedules[i].penaltyChargesDue;
#        }
#        return expectedTotalAmountToBeCharged;
#      }
#      """
#    * def expectedTotalAmountToBeCharged = 0.0;
#    * def totalChargesApplied = assertRepaymentSchedulePenaltyChargeAmount(loanResponse.loanAccount.repaymentSchedule.periods,expectedTotalAmountToBeCharged);
#    * karate.log('Total Charges Applied on Repayment Schedule',totalChargesApplied)
#    * assert totalChargesApplied == loanResponse.loanAccount.summary.penaltyChargesOverdue

        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 12
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 1

    # Make Transactions Prepay Loan Account
    * def repaymentAmount = loanResponse.loanAccount.summary.totalOverdue
    * def repaymentDate = df.format(faker.date().past(2, 1, TimeUnit.DAYS))
    * def loanTransaction = call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentAmount)', repaymentDate : '#(repaymentDate)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert loanResponse.loanAccount.status.value == 'Closed (obligations met)'
    * assert loanResponse.loanAccount.status.id == 600
    * assert loanResponse.loanAccount.status.active == false
    * assert loanResponse.loanAccount.status.closedObligationsMet == true
    * assert loanResponse.loanAccount.status.closed == true
    * assert loanResponse.loanAccount.status.overpaid == false
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2



  @testThatICanCreateLoanAccountWithFlatOverdueChargesAndDisburseBySavingsAccountWithDisburseToSavingsCharge
  Scenario: Test That I Can Create Loan Account With Flat Overdue Charges And Disburse it on a savings Account With Disburse to Savings Charge
    * def chargeAmount = 100;
    # Create Flat Overdue Charge
    * def charges = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createFlatOverdueChargeWithOutFrequencySteps') { chargeAmount : '#(chargeAmount)' }
    * def chargeId = charges.chargeId

       # Create Disburse to Savings Account charge
    * def disburseSavingsCharge = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createDisburseToSavingsAccountLoanChargeFeesOnApprovedAmountSteps') { chargeAmount : '#(chargeAmount)' }
    * def disburseSavingsAccountChargeId = disburseSavingsCharge.chargeId
    * def disburseToSavingsChargeAmount = 10

        # Create Loan Product With Flat Overdue Charge
    * def loanProduct = call read('classpath:features/portfolio/products/LoanProductSteps.feature@createLoanProductWithOverdueChargeSteps') { chargeId : '#(chargeId)' }
    * def loanProductId = loanProduct.loanProductId

    #create savings account with clientCreationDate
    * def submittedOnDate = df.format(faker.date().past(425, 421, TimeUnit.DAYS))

    * def result = call read('classpath:features/portfolio/clients/clientsteps.feature@create') { clientCreationDate : '#(submittedOnDate)' }
    * def clientId = result.response.resourceId

        #Create Savings Account Product and Savings Account
    * def savingsAccount = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@createSavingsAccountStep') { submittedOnDate : '#(submittedOnDate)', clientId : '#(clientId)'}
    * def savingsId = savingsAccount.savingsId
    #approve savings account step setup approval Date

    * call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@approve') { savingsId : '#(savingsId)', approvalDate : '#(submittedOnDate)' }
    #activate savings account step activation Date
    * def activateSavings = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@activate') { savingsId : '#(savingsId)', activationDate : '#(submittedOnDate)' }
    Then def activeSavingsId = activateSavings.activeSavingsId


    * def loanAmount = 8500
    * def loan = call read('classpath:features/portfolio/loans/loansteps.feature@createLoanAccountWithDisburseToSavingsAccountChargeAndPenaltyChargeSteps') { submittedOnDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', clientCreationDate : '#(submittedOnDate)', loanProductId : '#(loanProductId)', clientId : '#(clientId)', chargeId : '#(chargeId)', savingsAccountId : '#(savingsId)', disburseSavingsAccountChargeId : '#(disburseSavingsAccountChargeId)', disburseToSavingsChargeAmount : '#(disburseToSavingsChargeAmount)'}
    * def loanId = loan.loanId

      #approval
    * call read('classpath:features/portfolio/loans/loansteps.feature@approveloan') { approvalDate : '#(submittedOnDate)', loanAmount : '#(loanAmount)', loanId : '#(loanId)' }

      #disbursal
    * def disburseloan = call read('classpath:features/portfolio/loans/loansteps.feature@disburseToSavingsAccountStep') { loanAmount : '#(loanAmount)', disbursementDate : '#(submittedOnDate)', loanId : '#(loanId)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }
     # Loop through the Charges Object
    * def assertAmountsOnCharges =
      """
      function(charges,expectedAmountToBeCharged){
        for(var i = 0; i < charges.length; i++){
          karate.log(charges[i].amount);
          expectedAmountToBeCharged = expectedAmountToBeCharged + charges[i].amount;
        }
        return expectedAmountToBeCharged;
      }
      """
    * def expectedAmountToBeCharged = 0;
    * def totalCharges = assertAmountsOnCharges(loanResponse.loanAccount.charges,expectedAmountToBeCharged);
    * karate.log('Total Charges and Fees',totalCharges)
     # Expectation Disbursement to Savings Account and Penalty/Overdue Charge
    * assert totalCharges == loanResponse.loanAccount.summary.penaltyChargesOverdue + loanResponse.loanAccount.summary.feeChargesOverdue

        #Get Savings Account details and check if money hads been deposited
    * def savingsResponse = call read('classpath:features/portfolio/savingsaccount/savingssteps.feature@findsavingsbyid') { savingsId : '#(savingsId)' }
    * assert loanAmount == savingsResponse.savingsAccount.summary.availableBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.accountBalance
    * assert loanAmount == savingsResponse.savingsAccount.summary.totalDeposits
    * assert clientId == savingsResponse.savingsAccount.clientId

    # Assert Loan Account Status is Active and check the Disbursed principle is Expected
    * assert savingsId == loanResponse.loanAccount.linkedAccount.id
    * assert clientId == loanResponse.loanAccount.linkedAccount.clientId

    * assert clientId == loanResponse.loanAccount.clientId
    * assert loanAmount == loanResponse.loanAccount.principal
    * assert loanProductId == loanResponse.loanAccount.loanProductId
    * assert loanResponse.loanAccount.summary.penaltyChargesCharged == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOutstanding == 1200
    * assert loanResponse.loanAccount.summary.penaltyChargesOverdue == 1200
    * assert loanResponse.loanAccount.summary.feeChargesOverdue == 850
    * assert loanResponse.loanAccount.summary.feeChargesOutstanding == 850
    * assert loanResponse.loanAccount.summary.feeChargesCharged == 850
    * assert loanResponse.loanAccount.status.value == 'Active'
    * assert karate.sizeOf(loanResponse.loanAccount.charges) == 13
    * def loanTerm = loanResponse.loanAccount.termFrequency
    Then print 'Loan Term',loanTerm
    * assert karate.sizeOf(loanResponse.loanAccount.repaymentSchedule.periods) == loanTerm + 1
    # Expecting Two Transactions Disbursement Transaction and Disbursement charge
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 2

    # Make Transactions Prepay Loan Account
    * def repaymentAmount = loanResponse.loanAccount.summary.totalOverdue
    * def repaymentDate = df.format(faker.date().past(2, 1, TimeUnit.DAYS))
    * def loanTransaction = call read('classpath:features/portfolio/loans/loansteps.feature@loanRepaymentSteps') { repaymentAmount : '#(repaymentAmount)', repaymentDate : '#(repaymentDate)'}
     #fetch loan details here
    * def loanResponse = call read('classpath:features/portfolio/loans/loansteps.feature@findloanbyidWithAllAssociationStep') { loanId : '#(loanId)' }

    * assert loanResponse.loanAccount.status.value == 'Closed (obligations met)'
    * assert loanResponse.loanAccount.status.id == 600
    * assert loanResponse.loanAccount.status.active == false
    * assert loanResponse.loanAccount.status.closedObligationsMet == true
    * assert loanResponse.loanAccount.status.closed == true
    * assert loanResponse.loanAccount.status.overpaid == false
    * assert karate.sizeOf(loanResponse.loanAccount.transactions) == 3

