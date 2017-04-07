(function () {
  'use strict';
  var loggedIn = false;

  angular.module('helloCoreApi').controller('MainCtrl', [
    '$location',
    '$routeParams',
    '$rootScope',
    'c8yAuth',
    MainCtrl
  ]);

  function MainCtrl(
      $location,
      $routeParams,
      $rootScope,
      c8yAuth
  ) {

    $rootScope.$on('authStateChange', function (evt, state) {
      loggedIn = state.hasAuth;
    });

    c8yAuth.initializing.then(function() {
      if (!loggedIn) {
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