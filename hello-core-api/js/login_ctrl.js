(function () {
  'use strict';

  angular.module('helloCoreApi').controller('LoginCtrl', [
    '$location',
    '$rootScope',
    'c8yUser',
    LoginCtrl
  ]);

  function LoginCtrl(
    $location,
    $rootScope,
    c8yUser
  ) {

    c8yUser.current().then(function () {
      $location.path('/');
      $rootScope.c8y.user = c8yUser;
    });

    this.credentials = {};
    this.onSuccess = function () {
      $location.path('/');
    };

  }
})();
