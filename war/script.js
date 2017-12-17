var app = angular.module('twitter',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.twitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.sauthor;
	$scope.smessage;
	$scope.login = 'null';
	
	$scope.connection = function(nom){
		$scope.login = nom;
		console.log($scope.login);
	}
	
	$scope.listeTwitt = function(){
		if($scope.login != 'null'){
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
	      var rootApi = 'https://tiny-twitt.appspot.com/_ah/api/';
	      gapi.client.load('TinytwittEndpoint', 'v1', function() {
	        console.log("twitt api loaded");
	      }, rootApi);
	 }
 }                                                             
]);