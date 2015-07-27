(function () {
  'use strict';

  angular.module('helloCoreApi').directive('datapoint', [
    datapoint
  ]);

  function datapoint() {
    return {
      restrict: 'E',
      scope: {
        dp: '='
      },
      templateUrl: 'views/datapoint.html'
    };
  }
})();
