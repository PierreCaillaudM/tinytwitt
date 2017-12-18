var app = angular.module('twitt',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.twitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.author;
	$scope.smessage;
	$scope.login ='';
	$scope.log = 0;
	
	$scope.connection = function(nom){
		$scope.author = nom;
		$scope.log = 1;
	}
	
	$scope.listeTwitt = function(){
		if($scope.login != ''){
			gapi.client.TinytwittEndpoint.getTweetsOf($scope.login).execute(
					function(resp){
						$scope.twitt = resp.items;
						$scope.$apply();
						console.log(resp);
					}
			);
		}
	}
	
	$window.init = function() {
	      console.log("windowinit called");
	      var rootApi = 'https://tiny-twitt.appspot.com/';
	      gapi.client.load('tinytwittAPI', 'v1', function() {
	        console.log("twitt api loaded");
	        $scope.log = 0;
	      }, rootApi);
	 }
 }                                                             
]);