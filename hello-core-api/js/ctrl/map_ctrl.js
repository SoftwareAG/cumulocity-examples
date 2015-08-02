(function () {
  'use strict';

  angular.module('helloCoreApi').controller('MapCtrl', [
    MapCtrl
  ]);

  function MapCtrl(
  ) {
    this.config = {
      centerPoint: {
        lat: 51.235267210116255,
        lng: 6.7144811153411865,
        zoom: 11
      }
    };
  }
})();
