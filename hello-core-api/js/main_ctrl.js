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

    // if (!$routeParams.section) {
    //   $location.path('/devices');
    // }

    this.currentSection = $routeParams.section;
    this.sections = {
      Devices: 'devices',
      Alarms: 'alarms',
      Events: 'events'
    };
    this.filter = {};

    this.logout = function () {
      $location.path('/login');
    };
  }
})();
