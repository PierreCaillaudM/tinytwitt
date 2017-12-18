var app = angular.module('twitt',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.twitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.author;
	$scope.smessage;
	$scope.login ='';
	$scope.newUser = {};
	$scope.log = 0;
	
	$scope.connection = function(nom){
		$scope.author = nom;
		$scope.log = 1;
	}
	
	$scope.listeTwitt = function(){
		if($scope.log == 1){
			gapi.client.tinytwittAPI.getTweetsOf($scope.login).execute(
					function(resp){
						$scope.twitt = resp.items;
						$scope.$apply();
						console.log(resp);
					}
			);
		}
	}
	
//	$scope.postTwitt = function(messageTwitt){
//		if($scope.log == 1 && gapi.client.tinytwittAPI.getUser($scope.login)){
//			gapi.client.tinytwittAPI.insertTweet($scope.login,$scope.smessage).execute(
//				function(resp){
//					console.log(resp);
//					$scope.twitt.push({
//						author: $scope.login;
//						message: $scope.smessage;
//					});
//					$scope.$apply();
//				}		
//			)
//		}
//	}
//	
	$scope.inscription = function(user){
		if($scope.log == 0){
			$scope.newUser = angular.copy(user);
			console.log($scope.newUser);
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