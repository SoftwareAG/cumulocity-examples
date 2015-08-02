(function () {
  'use strict';

  angular.module('helloCoreApi').controller('EventsCtrl', [
    EventsCtrl
  ]);

  function EventsCtrl(
  ) {
    this.filter = {
      pageSize: 10
    };
  }
})();
