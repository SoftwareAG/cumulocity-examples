(function () {
  'use strict';

  angular.module('helloCoreApi').controller('MainCtrl', [
    '$scope',
    '$location',
    'c8yUser',
     MainCtrl
  ]);

  function MainCtrl(
    $scope,
    $location,
    c8yUser
  ) {
    c8yUser.current().catch(function () {
      $location.path('/login');
    });

    this.filter = {pageSize: 10};

    $scope.$watch(angular.bind(this, function () {
      return this.refresh;
    }), function (val) {
      this.refresh = val;
    });

    this.logout = function () {
      $location.path('/login');
    };
  }
})();
