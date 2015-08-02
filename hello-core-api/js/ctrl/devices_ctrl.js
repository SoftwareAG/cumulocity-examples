(function () {
  'use strict';

  angular.module('helloCoreApi').controller('DevicesCtrl', [
    '$location',
    DevicesCtrl
  ]);

  function DevicesCtrl(
    $location
  ) {
    this.filter = {
      pageSize: 10
    };

    this.onSearch = function (query) {
      this.filter.text = query;
    };

    this.goToDeviceDetail = function (deviceId) {
      $location.path('/devices/' + String(deviceId));
    };
  }
})();
