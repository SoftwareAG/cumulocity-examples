(function () {
  'use strict';

  angular.module('helloCoreApi').controller('SectionCtrl', [
    SectionCtrl
  ]);

  function SectionCtrl(
  ) {
    this.filter = {
      pageSize: 10
    };
  }
})();
