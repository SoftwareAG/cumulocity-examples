(function () {
  'use strict';

  angular.module('helloCoreApi').controller('LoginCtrl', [
    '$location',
    'c8yUser',
    LoginCtrl
  ]);

  function LoginCtrl(
    $location,
    c8yUser
  ) {
    c8yUser.current().then(function () {
      $location.path('/');
    });

    this.credentials = {};
    this.onSuccess = function () {
      $location.path('/');
    };
  }
})();
