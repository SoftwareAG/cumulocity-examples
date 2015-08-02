(function () {
  'use strict';

  angular.module('helloCoreApi').controller('MainCtrl', [
    '$location',
    '$stateParams',
    'c8yUser',
     MainCtrl
  ]);

  function MainCtrl(
    $location,
    $stateParams,
    c8yUser
  ) {
    c8yUser.current().catch(function () {
      $location.path('/login');
    });

    this.logout = function () {
      $location.path('/login');
    };
  }
})();
