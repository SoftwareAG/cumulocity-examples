(function () {
  'use strict';

  angular.module('helloCoreApi').controller('DeviceDetailCtrl', [
    'c8yDevices',
    '$stateParams',
    DeviceDetailCtrl
  ]);

  function DeviceDetailCtrl(
    c8yDevices,
    $stateParams
  ) {
    var deviceId = $stateParams.deviceId;
    c8yDevices.detail(deviceId).then(function (res) {
      this.device = res.data;
    }.bind(this));
  }
})();
