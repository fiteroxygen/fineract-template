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
              var result = karate.call('classpath:features/portfolio/products/productsteps.feature@createloan');
              var loanProduct = result.loanProduct;
              loanProduct.id = loanProduct.resourceId;
              return loanProduct;
            }else{
              return x[0];
            }
        }
      """


  @fetchdefaultproduct
  Scenario: Fetch or Create Default loan Product
    * def loanProduct = call read('classpath:features/portfolio/products/productsteps.feature@listloanproducts')
    * def result = call createIfNotExists loanProduct.res
    * def loanProductId = result.id