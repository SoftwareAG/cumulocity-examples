(function () {
  'use strict';
  var app = angular.module('helloCoreApi', [
    'c8y.sdk',
    'c8y.ui',
    'ui.router',
    'ui.bootstrap'
  ]);
  app.config([
    '$stateProvider',
    '$urlMatcherFactoryProvider',
    configRoutes
  ]);
  app.config([
    'c8yCumulocityProvider',
    configCumulocity
  ]);

  function configRoutes(
    $stateProvider,
    $urlMatcherFactoryProvider
  ) {
    $urlMatcherFactoryProvider.strictMode(false);
    $stateProvider
      .state('login', {
        url: '/login',
        templateUrl: '/views/ctr/login.html'
      })
      .state('main', {
        url: '',
        templateUrl: '/views/ctrl/main.html'
      })
      .state('main.alarms', {
        url: '/alarms',
        templateUrl: '/views/ctrl/alarms.html'
      })
      .state('main.events', {
        url: '/events',
        templateUrl: '/views/ctrl/events.html'
      })
      .state('main.devices', {
        url: '/devices',
        templateUrl: '/views/ctrl/devices.html'
      })
      .state('main.deviceDetail', {
          url: '/devices/:deviceId',
          templateUrl: '/views/ctrl/device_detail.html'
      })
      .state('main.charts', {
        url: '/charts',
        templateUrl: '/views/ctrl/charts.html'
      })
      .state('main.deviceList', {
        url: '/deviceList',
        templateUrl: '/views/ctrl/device_list.html'
      })
      .state('main.alarmList', {
        url: '/alarmList',
        templateUrl: '/views/ctrl/alarm_list.html'
      })
      .state('main.map', {
        url: '/map',
        templateUrl: '/views/ctrl/map.html'
      });
  }

  function configCumulocity(
    c8yCumulocityProvider
  ) {
    c8yCumulocityProvider.setAppKey('core-application-key');
    c8yCumulocityProvider.setBaseUrl('https://demos.cumulocity.com/');
  }
})();
