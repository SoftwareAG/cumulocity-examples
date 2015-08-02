(function() {
  'use strict';

  angular.module('helloCoreApi').controller('ChartsCtrl', [
    'c8yBase',
    'c8yMeasurements',
    ChartsCtrl
  ]);

  function ChartsCtrl(
    c8yBase,
    c8yMeasurements
  ) {

    this.init = function() {
      this.sourceId = "144880600";
      this.fragmentType = "c8y_Temperature";
      this.series = "Temperature";
      this.dp = {
        min: 0,
        max: 100,
        yellowRangeMin: 75,
        yellowRangeMax: 90,
        redRangeMin: 90,
        redRangeMax: 100
      };
      this.measurement = {value: 0};
    }

    this.onMeasurement = function(fragmentType, series, data) {
      this.measurement = data[0][fragmentType][series];
    }

    this.getMeasurement = function() {
      if (!this.sourceId || !this.fragmentType || !this.series) return;
      c8yMeasurements.list(angular.extend(c8yBase.timeOrderFilter(), {
        revert: true,
        fragmentType: this.fragmentType,
        source: this.sourceId,
        reverse: true,
        pageSize: 1
      })).then(this.onMeasurement.bind(this, this.fragmentType, this.series));
    }

    this.init();
  }
})();
