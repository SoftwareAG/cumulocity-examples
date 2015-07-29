(function () {
  'use strict';
  var app = angular.module('helloCoreApi', [
    'c8y.sdk',
    'ngRoute',
    'ui.bootstrap'
  ]);
  app.config([
    '$routeProvider',
    configRoutes
  ]);
  app.config([
    'c8yCumulocityProvider',
    configCumulocity
  ]);

  function configRoutes(
    $routeProvider
  ) {
    $routeProvider
      .when('/login', {
        templateUrl: 'login.html',
        controller: 'LoginCtrl',
        controllerAs: 'login'
      })
      .when('/', {
        templateUrl: 'main.html',
        controller: 'MainCtrl',
        controllerAs: 'main'
      })
  }

  function configCumulocity(
    c8yCumulocityProvider
  ) {
    c8yCumulocityProvider.setAppKey('core-application-key');
    c8yCumulocityProvider.setBaseUrl('https://demos.cumulocity.com/');
  }
})();
