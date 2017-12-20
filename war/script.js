var app = angular.module('twitt',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.twitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.author;
	$scope.smessage;
	$scope.login ='';
	$scope.userlogin ='';
	$scope.usermail ='';
	$scope.usermdp ='';
	$scope.userprenom ='';
	$scope.usernom ='';
	$scope.log = 0;
	
	// Ajoute l'utilisateur saisie en variable local
	$scope.connection = function(nom){
		$scope.author = nom;
		$scope.log = 1;
//		console.log($scope.author);
	}
	
	
	$scope.listeTwitt = function(){
		if($scope.log == 1){
			gapi.client.tinytwittAPI.getTimelineOf($scope.login).execute(
					function(resp){
						$scope.twitt = resp.items;
						$scope.$apply();
						console.log(resp);
					}
			);
		}
	}
	
	$scope.postTwitt = function(){
		if($scope.log == 1 ){
			gapi.client.tinytwittAPI.insertTweet($scope.login,$scope.smessage).execute(
				function(resp){
					console.log(resp);
					$scope.twitt.push({
						author: $scope.login,
						message: $scope.smessage
					});
					$scope.$apply();
				}		
			)
		}
	}
	
	// Ajoute un follower a l'utilisateur courant, SI un utilisateur courant est actif, et que le pseudo
	// a suivre existe
	$scope.addFollow = function(author){
		if($scope.log == 1){
			gapi.client.tinytwittAPI.getUser(author).execute(
					function(resp){
						if(resp.login == author){
							gapi.client.tinytwittAPI.addFollower(author,$scope.login);
						}
					}
			)
		}
	}
	
	// Copie le formulaire dans une variable et l'ajoute au datastore
	$scope.inscription = function(){
		console.log($scope.userlogin);
		console.log($scope.usermail);
		console.log($scope.usermdp);
		console.log($scope.userprenom);
		console.log($scope.usernom);
		gapi.client.tinytwittAPI.createUser({
			login: $scope.userlogin,
			mail: $scope.usermail,
			mdp: $scope.usermdp,
			prenom: $scope.userprenom,
			nom: $scope.usernom
		}).execute(function(resp){
			console.log(resp);
		});
		console.log("test");
		$scope.log = 1;
		
	}
	
	// Idée d'astuce de pascal Molli pour s'assurer du chargement d'angular
	$window.init = function() {
	      console.log("windowinit called");
	      var rootApi = 'https://tiny-twitt.appspot.com/_ah/api/';
	      gapi.client.load('tinytwittAPI', 'v1', function() {
	        console.log("twitt api loaded");
	        $scope.log = 0;
	      }, rootApi);
	 }
 }                                                             
]);