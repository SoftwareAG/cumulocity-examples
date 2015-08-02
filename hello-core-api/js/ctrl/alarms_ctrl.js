(function () {
  'use strict';

  angular.module('helloCoreApi').controller('AlarmsCtrl', [
    AlarmsCtrl
  ]);

  function AlarmsCtrl(
  ) {
    this.filter = {
      pageSize: 10
    };
    
    this.severities = [
      {name: 'Critical', value: 'CRITICAL', cls: 'btn-danger'},
      {name: 'Major', value: 'MAJOR', cls: 'btn-warning'},
      {name: 'Minor', value: 'MINOR', cls: 'btn-primary'},
      {name: 'Warning', value: 'WARNING', cls: 'btn-info'}
    ];

    this.onClick = function (filter, severity) {
      if (filter.severity === severity.value) {
        filter.severity = undefined;
      } else {
        filter.severity = severity.value;
      }
    };

    this.isActive = function (filter, severity) {
      return filter.severity === severity.value;
    };
  }
})();
