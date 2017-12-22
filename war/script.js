var app = angular.module('twitt',[]).controller('TController',['$scope','$window',
 function($scope, $window){
	
	$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	$scope.items = ['pseudo','UserForTest100','UserForTest1000','UserForTest2500'];
	$scope.author;
	$scope.smessage;
	$scope.login='';
	$scope.selection = $scope.items[0];
	$scope.userlogin ='';
	$scope.usermail ='';
	$scope.usermdp ='';
	$scope.userprenom ='';
	$scope.usernom ='';
	$scope.followed ='';
	$scope.log = false;
	$scope.start;
	$scope.stop;
	$scope.reponseTwitt = 0;
	$scope.reponseActualisation = 0;
	$scope.reponseFollow = 0;
	$scope.erreurLog=false;
	$scope.tempo=0;
	 
	// Ajoute l'utilisateur saisie en variable local
	$scope.connection = function(nom){
			$scope.login = nom;
		$scope.author = $scope.login;
		//$scope.items[0] = $scope.login;
		$scope.listeTwitt();
		$scope.log = true;
		$scope.erreurLog=false;
		console.log($scope.author);
		console.log(" is connected");
	}
	
	$scope.deconnexion = function(){
		$scope.author = '';
		$scope.log = false;
		$scope.selection = $scope.items[0];
		$scope.login = '';
		//$scope.items[0] = 'pseudo';
		$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
	}
	
	// Recupere nb fois les tweet des follows
	$scope.listeTwitt = function(nb){
		if($scope.log == true){
			
			for(var i=0;i<nb;i++){
				$scope.start = new Date().getTime();
				$scope.reponseActualisation = 0;
				$scope.tempo=0;
				gapi.client.tinytwittAPI.getTimelineOf({
					login: $scope.author
				}).execute(function(resp){
					
					console.log(resp);
					$scope.listtwitt.push(resp.items);
					if($scope.listtwitt == null){
						$scope.listtwitt = [{author: 'admin',message :'Bienvenu sur tiny twitt'}];
					}
					$scope.$apply();
					$scope.stop = new Date().getTime();
					$scope.tempo = $scope.stop - $scope.start;
					$scope.reponseActualisation = ($scope.reponseActualisation + $scope.tempo);
				});
			}
			
			//console.log($scope.reponseActualisation);	
		}else{
			$scope.erreurLog=true;
		}
	}
	
	// Post nb fois le twitt
	$scope.postTwitt = function(nb){
		if($scope.log == true ){
			$scope.start = new Date().getTime();
			$scope.reponseTwitt=0;
			
			for(var i=0;i<nb;i++){
				$scope.tempo=0;
				gapi.client.tinytwittAPI.insertTwitt({
					login: $scope.author,
					message: $scope.smessage
				}).execute(function(resp){
					
					//console.log(resp);
					$scope.listtwitt.push({
						author: $scope.author,
						message: $scope.smessage
					});
					$scope.$apply();
					$scope.stop = new Date().getTime();
					$scope.tempo = $scope.stop - $scope.start;
					$scope.reponseTwitt = ($scope.reponseTwitt + $scope.tempo);
				});
			}
			console.log($scope.reponseTwitt);
			
		}else{
			$scope.erreurLog=true;
		}
		$scope.reponseTwitt = $scope.reponseTwitt;
	}
	
	// Ajoute un follower a l'utilisateur courant, SI un utilisateur courant est actif, et que le pseudo
	// a suivre existe
	$scope.addFollow = function(){
		if($scope.log == true){
			$scope.start = new Date().getTime();
			gapi.client.tinytwittAPI.getUser({
				login: $scope.followed
			}).execute(function(resp){
				if(resp.login == $scope.followed){
					console.log($scope.followed);
					console.log($scope.author);
					gapi.client.tinytwittAPI.addFollower({
						loginFollowed: $scope.followed,
						loginFollower: $scope.author
					}).execute(function(resp){
						$scope.stop = new Date().getTime();
						$scope.tempo = $scope.stop - $scope.start;
						$scope.reponseFollow = $scope.reponseFollow + $scope.tempo;
						console.log(resp);
						$scope.$apply();
					});
				}
			});
		}else{
			$scope.erreurLog=true;
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