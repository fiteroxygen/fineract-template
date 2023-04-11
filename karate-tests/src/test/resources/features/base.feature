Feature: Base test configuration

  Scenario:
    * def basiccred = username+":"+password
    * def authToken = 'Basic '+ Base64.encoder.encodeToString(basiccred.getBytes())