(function () {
  'use strict';

  angular.module('helloCoreApi').controller('SectionCtrl', [
    '$scope',
    SectionCtrl
  ]).directive('egSection', [
    egSection
  ]);

  function SectionCtrl(
    $scope
  ) {
    this.filter = $scope.filter || {};
    this.filter.pageSize = 10;
    this.service = $scope.service;
    $scope.$watch('section.refresh', function (val) {
        $scope.refresh = val;
    });
  }

  function egSection(
  ) {
    return {
      restrict: 'AE',
      templateUrl: 'views/section.html',
      controller: 'SectionCtrl',
      controllerAs: 'section',
      transclude: true,
      replace: true,
      scope: {
        service: '@',
        filter: '=?',
        refresh: '=?'
      }
    };
  }
})();
