(function () {
  'use strict';

  angular.module('helloCoreApi').controller('MainCtrl', [
    '$location',
    '$routeParams',
    'c8yUser',
    'c8yAuth',
    MainCtrl
  ]);

  function MainCtrl(
      $location,
      $routeParams,
      c8yUser,
      c8yAuth
  ) {

    c8yAuth.initializing.then(function() {
      if(c8yUser.current().$$state.status != 1) {
        $location.path('/login');
      }
    });

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
