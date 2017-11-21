App = {
    web3Provider: null,
    contracts: {},
    data_list: [],

    init: function () {
        $.getJSON('../furnitures.json', function (data) {
            for (i = 0; i < data.length; i++) {
                var each_data = data[i];

                App.data_list[each_data.id] = each_data;
            }

            var productRow = $('#productRow');
            var productTemplate = $('#productTemplate');

            App.data_list.forEach(function (each_data) {
                productTemplate.find('.title').text(each_data.name);
                productTemplate.find('img').attr('src', each_data.picture);
                productTemplate.find('.product-price').text(each_data.price + "$");
                productTemplate.find('.product-quantity').text(" (" + each_data.quantity + " available)");
                productTemplate.find('.btn-add-to-cart').attr('data-id', each_data.id);
                productTemplate.find('.btn-buy').attr('data-id', each_data.id);

                productRow.append(productTemplate.html());
            });
        });

        //App.saveItem();
        //console.log(App.getItem());
        return App.initWeb3();
    },
    initWeb3: function () {
        // Is there is an injected web3 instance?
        if (typeof web3 !== 'undefined') {
            App.web3Provider = web3.currentProvider;
        } else {
            // If no injected web3 instance is detected, fallback to the TestRPC
            App.web3Provider = new Web3.providers.HttpProvider('http://localhost:8545');
        }
        web3 = new Web3(App.web3Provider);

        return App.initContract();
    },

    initContract: function () {
        $.getJSON('Product.json', function (data) {
            // Get the necessary contract artifact file and instantiate it with truffle-contract
            var ProductArtifact = data;
            App.contracts.Product = TruffleContract(ProductArtifact);

            // Set the provider for our contract
            App.contracts.Product.setProvider(App.web3Provider);

            // Use our contract to retrieve and mark the buy product
            return App.markSoled();
        });

        return App.bindEvents();
    },

    bindEvents: function () {
        $(document).on('click', '.btn-buy', App.handleBuy);
    },

    markSoled: function (products, account) {
        var productInstance;

        App.contracts.Product.deployed().then(function (instance) {
            productInstance = instance;

            return productInstance.getProducts.call();
        }).then(function (products) {
            for (i = 0; i < products.length; i++) {
                if (products[i] !== '0x0000000000000000000000000000000000000000') {
                    $('.panel-action').eq(i).css('display', 'none');
                }
            }
        }).catch(function (err) {
            console.log(err.message);
        });
    },

    handleBuy: function (event) {
        event.preventDefault();

        var productId = parseInt($(event.target).data('id'));

        var productInstance;

        web3.eth.getAccounts(function (error, accounts) {
            if (error) {
                console.log(error);
            }

            var account = accounts[0];

            App.contracts.Product.deployed().then(function (instance) {
                productInstance = instance;

                // Execute buy as a transaction by sending account
                return productInstance.buy(productId, {from: account});
            }).then(function (result) {
                return App.markSoled();
            }).catch(function (err) {
                console.log(err.message);
            });
        });
    },
    saveItem: function () {
        console.log(App.data_list);
        var string_data = JSON.stringify(App.data_list);
        console.log(string_data);
        localStorage.setItem('keyPair', App.data_list);
    },
    getItem: function () {
        return localStorage.getItem('keyPair');
    }
};

$(function () {
    $(window).on('load', function () {
        App.init();
    });
});
