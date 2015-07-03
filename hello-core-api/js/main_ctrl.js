(function () {
  'use strict';

  angular.module('helloCoreApi').controller('MainCtrl', [
    '$location',
    '$routeParams',
    'c8yUser',
     MainCtrl
  ]);

  function MainCtrl(
    $location,
    $routeParams,
    c8yUser
  ) {
    c8yUser.current().catch(function () {
      $location.path('/login');
    });

    if (!$routeParams.section) {
      $location.path('/devices');
    }
    
    this.routeParams = $routeParams;

    this.logout = function () {
      $location.path('/login');
    };

    this.showDevices = function () {
      $location.path('/devices');
    };
  }
})();
