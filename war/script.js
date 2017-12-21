var app = angular.module('twitt',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.author;
	$scope.smessage;
	$scope.login ='';
	$scope.userlogin ='';
	$scope.usermail ='';
	$scope.usermdp ='';
	$scope.userprenom ='';
	$scope.usernom ='';
	$scope.followed ='';
	$scope.log = false;
	
	// Ajoute l'utilisateur saisie en variable local
	$scope.connection = function(){
		$scope.author = $scope.login;
		$scope.listeTwitt();
		$scope.log = true;
		console.log($scope.author);
		console.log(" is connected");
	}
	
	$scope.deconnexion = function(){
		$scope.author = '';
		$scope.log = false;
		$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	}
	
	
	$scope.listeTwitt = function(){
		if($scope.log == true){
			gapi.client.tinytwittAPI.getTimelineOf({
				login: $scope.author
			}).execute(function(resp){
				console.log(resp);
				$scope.listtwitt.push(resp.items);
				if($scope.listtwitt == null){
					$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
				}
				$scope.$apply();
				console.log("List updated");
			});
		}
	}
	
	
	$scope.postTwitt = function(){
		if($scope.log == true ){
			gapi.client.tinytwittAPI.insertTwitt({
				login: $scope.author,
				message: $scope.smessage
			}).execute(
				function(resp){
					console.log(resp);
					$scope.listtwitt.push({
						author: $scope.author,
						message: $scope.smessage
					});
					$scope.$apply();
					console.log("Twitt posted");
				}		
			)
		}
	}
	
	// Ajoute un follower a l'utilisateur courant, SI un utilisateur courant est actif, et que le pseudo
	// a suivre existe
	$scope.addFollow = function(){
		if($scope.log == true){
			gapi.client.tinytwittAPI.getUser({
				login: $scope.followed
			}).execute(function(resp){
				if(resp.login == $scope.followed){
					gapi.client.tinytwittAPI.addFollower({
						loginFollowed: $scope.followed,
						loginFollower: $scope.author
					});
					console.log("Follow succeded");
				}
			});
		}
	}
	
	// Copie le formulaire dans une variable et l'ajoute au datastore
	$scope.inscription = function(){
		if($scope.log == false){
			gapi.client.tinytwittAPI.createUser({
				login: $scope.userlogin,
				mail: $scope.usermail,
				mdp: $scope.usermdp,
				prenom: $scope.userprenom,
				nom: $scope.usernom
			}).execute(function(resp){
				console.log(resp);
				console.log("User created");
				$scope.author = $scope.userlogin;
				$scope.log = true;
				console.log($scope.author);
				console.log(" is connected");
			});
		}	
	}
	
	// Idée d'astuce de pascal Molli pour s'assurer du chargement d'angular
	$window.init = function() {
	      console.log("windowinit called");
	      var rootApi = 'https://tiny-twitt.appspot.com/_ah/api/';
	      gapi.client.load('tinytwittAPI', 'v1', function() {
	        console.log("twitt api loaded");
	        $scope.log = false;
	      }, rootApi);
	 }
 }                                                             
]);