Feature: Test savings product apis
  Background:
    * callonce read('classpath:features/base.feature')
    * url baseUrl
    * def productsData = read('classpath:templates/product.json')
    * def createIfNotExists =
    """
      function(x) {
          karate.log(x);
          if (x.length === 0) {
            var result = karate.call('classpath:features/portfolio/products/productsteps.feature@createsavings');
            var savingsResponse = result.savingsResponse;
            savingsResponse.id = savingsResponse.resourceId;
            return savingsResponse;
          }else{
            return x[0];
          }
      }
    """


  @fetchdefaultproduct
  Scenario: Fetch or Create Default Saving Product
    * def savingsProduct = call read('classpath:features/portfolio/products/productsteps.feature@listsavings')
    * def result = call createIfNotExists savingsProduct.res
    * def savingsProductId = result.id