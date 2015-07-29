(function () {
  'use strict';

  angular.module('helloCumulocity').controller('MainCtrl', [
    '$location',
    'c8yUser',
     MainCtrl
  ]);

  function MainCtrl(
    $location,
    c8yUser
  ) {
    c8yUser.current().catch(function () {
      $location.path('/login');
    });

    this.filter = {pageSize: 10};

    this.logout = function () {
      $location.path('/login');
    };
  }
})();
