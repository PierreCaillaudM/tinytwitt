var app = angular.module('twitter',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	
	$window.init = function() {
	      console.log("windowinit called");
	      var rootApi = 'https://1-dot-sobike44.appspot.com/_ah/api/';
	      gapi.client.load('scoreentityendpoint', 'v1', function() {
	        console.log("score api loaded");
	        $scope.listScore();
	      }, rootApi);
	 }
 }                                                             
]);