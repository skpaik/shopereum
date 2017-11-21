App = {
    web3Provider: null,
    contracts: {},
    data_list: [],

    init: function () {
        $.getJSON('../furnitures.json', function (data) {
            for (i = 0; i < data.length; i++) {
                var each_data = data[i];

                App.data_list[each_data.id]=each_data;
            }

            var petsRow = $('#petsRow2');
            var petTemplate = $('#petTemplate2');


            App.data_list.forEach(function (each_data) {
              //each_data=json.parse(each_data);
                //console.log(each_data);

                petTemplate.find('.title').text(each_data.name);
                petTemplate.find('img').attr('src', each_data.picture);
                petTemplate.find('.product-price').text(each_data.price + "$");
                petTemplate.find('.product-quantity').text(" (" + each_data.quantity + " available)");
                petTemplate.find('.btn-add-to-cart').attr('data-id', each_data.id);
                petTemplate.find('.btn-buy').attr('data-id', each_data.id);

                petsRow.append(petTemplate.html());
            });
        });


        App.saveItem();
        console.log(App.getItem());
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
        $.getJSON('Adoption.json', function (data) {
            // Get the necessary contract artifact file and instantiate it with truffle-contract
            var AdoptionArtifact = data;
            App.contracts.Adoption = TruffleContract(AdoptionArtifact);

            // Set the provider for our contract
            App.contracts.Adoption.setProvider(App.web3Provider);

            // Use our contract to retrieve and mark the adopted pets
            return App.markAdopted();
        });

        return App.bindEvents();
    },

    bindEvents: function () {
        $(document).on('click', '.btn-buy', App.handleAdopt);
    },

    markAdopted: function (adopters, account) {
        var adoptionInstance;

        App.contracts.Adoption.deployed().then(function (instance) {
            adoptionInstance = instance;

            return adoptionInstance.getAdopters.call();
        }).then(function (adopters) {
            for (i = 0; i < adopters.length; i++) {
                if (adopters[i] !== '0x0000000000000000000000000000000000000000') {
                    $('.panel-action').eq(i).css('display', 'none');
                }
            }
        }).catch(function (err) {
            console.log(err.message);
        });
    },

    handleAdopt: function (event) {
        event.preventDefault();

        var petId = parseInt($(event.target).data('id'));

        var adoptionInstance;

        web3.eth.getAccounts(function (error, accounts) {
            if (error) {
                console.log(error);
            }

            var account = accounts[0];

            App.contracts.Adoption.deployed().then(function (instance) {
                adoptionInstance = instance;

                // Execute adopt as a transaction by sending account
                return adoptionInstance.adopt(petId, {from: account});
            }).then(function (result) {
                return App.markAdopted();
            }).catch(function (err) {
                console.log(err.message);
            });
        });
    },
    saveItem:function(){
      console.log(App.data_list);
      var string_data=JSON.stringify(App.data_list);
console.log(string_data);
      localStorage.setItem('keyPair', App.data_list);
    },
    getItem:function () {
      return localStorage.getItem('keyPair');
    }
};

$(function () {
    $(window).on('load', function () {
        App.init();
    });
});
