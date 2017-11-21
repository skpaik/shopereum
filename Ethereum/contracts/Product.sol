pragma solidity ^0.4.4;

contract Product {
    address[16] public products;

    // Adopting a pet
    function buy(uint productId) public returns (uint) {
      require(productId >= 0 && productId <= 15);

      products[productId] = msg.sender;

      return productId;
    }

    // Retrieving the products
    function getProducts() public returns (address[16]) {
      return products;
    }
}
