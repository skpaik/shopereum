var Product = artifacts.require("Product");

module.exports = function(deployer) {
  deployer.deploy(Product);
};
