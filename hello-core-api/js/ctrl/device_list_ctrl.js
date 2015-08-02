(function () {
  'use strict';

  angular.module('helloCoreApi').controller('DeviceListCtrl', [
    '$location',
    DeviceListCtrl
  ]);

  function DeviceListCtrl(
    $location
  ) {
    this.goToDeviceDetail = function (deviceId) {
      $location.path('/devices/' + String(deviceId));
    };
  }
})();
