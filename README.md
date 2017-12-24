# Tiny Twitt

## Intro
Ce projet avait pour objectif la création d'une twitter like application en utilisant le Google App Engine, et ce afin de mieux comprendre le fonctionnement d'une application telle que celle-ci et les enjeux présents pour la scalabilité des ces applications.

Ce projet a été réalisé en collaboration par Pierre CAILLAUD, Ivan DROMIGNY--CHEVREUIL et Marvin JEAN.

L'URL vers le AppEngine :  [Tiny Twitt](http://tiny-twitt.appspot.com/?).
L'URL vers l'interface REST : [Tiny Twitt REST](http://tiny-twitt.appspot.com/_ah/api/explorer).


## Backend
La partie backend du projet et les classes java qui la composent se situent dans le répertoire [/src/projetwcd](https://github.com/PierreCaillaudM/tinytwitt/tree/master/src/projetwcd). Il contient deux beans, [Utilisateur.java](https://github.com/PierreCaillaudM/tinytwitt/tree/master/src/projetwcd/beans/Utilisateur.java) et [Twitt.java](https://github.com/PierreCaillaudM/tinytwitt/tree/master/src/projetwcd/beans/Twitt.java), et notre endpoint [TinytwittEndpoint.java](https://github.com/PierreCaillaudM/tinytwitt/tree/master/src/projetwcd/TinytwittEndpoint.java) contenant l'ensemble des fonctions de notre API.

#### Structure chosie
![UML](UML.png)

Pour ce projet, il nous faut pouvoir représenter au moins deux entités: une entité <i>User</i> contenant les informations relative à l'utilisateur inscrit, et une entité <i>Twitt</i> contenant les informations relatives à un twitt posté par un utilisateur.

L'entité <i>UserFollowers</i> quant à elle, à pour rôle de stocker l'ensemble des idUtilisateur des followers de son User parent. Le choix d'une telle structure permet d'éviter d'avoir à sérialiser/déserialiser la liste des utilisateur à chaque fois que l'on souhaite récupérer un utilisateur dans le datastore, limitant ainsi le temps et la bande passante requis pour récupérer un utilisateur. Le même principe s'applique aux entités <i>Twitt</i> et <i>TwittIndex</i> dont le fonctionnement est explicité dans la prochaine section.

#### Récupérer la timeline d'un utilisateur

Nous n’expliciterons que le fonctionnement de la fonction getTimelineOf(loginUser) dont l'implémentation est visible dans [ce fichier](https://github.com/PierreCaillaudM/tinytwitt/tree/master/src/projetwcd/TinytwittEndpoint.java) (les autres fonctions présentant moins d'intérêt en comparaison).

La première partie consiste à récupérer l'id de l'utilisateur dont on souhaite récupérer la timeline et dont on a passé le login en paramètre.<br>
L'idée est, ensuite, d'aller récupérer l'ensemble des parent des TwittIndex dont la liste des receivers contient l'id de notre user (l.249-259). Ici l'emploi de setKeysOnly() (l.251) est donc capital pour éviter le problème évoqué dans la partie précédente. Une fois l'ensemble de ces clés récupérées, il suffit d'aller chercher l'ensemble des Twitts correspondant dans le datastore puis de les retourner sous forme de liste.

## Frontend
Pour la conception du front nous nous sommes basé sur l'aspect que twitter arbore. C'est a dire que le fil de messages est au millieu de l'écran, car c'est le coeur du site. Le tout tourne autour des messages que les differents utilisateurs post il est donc logique que ce soit visible et pratique. Dans le millieu on retrouve aussi la zone de saisie qui permet de poster ses messages et le bouton pour actualiser le fil d'actualité. C'est exactement comme twitter et cela est assez coherent avec les twitt qui sont en plein millieu, de ce fait poster ses propres messages et actualiser son fils est facil d'accès. Il y a donc au millieux tout ce qui concerne les twitts.<br>
Sur la gauche nous avons decider de mettre tout ce qui etait connection des utilisateurs. Contrairement à twitter la page principale est visible sans etre connecter, mais la majorité des fonctions est bloqué tout de meme. Il est naturel que rien ne puisse etre fait si l'utilisateur n'est pas authentifié. En plus d'un système de connection et d'inscription, nous avons rajouté des utilisateurs par défauts, car dans le cadre de notre projet il était demandé de faire des tests avec differente configurations, c'est donc pour facilité les tests que nous avons rajoutés ce petit raccourci.<br>
Il reste donc à droite les dernieres fonctionnalités demandée, suivre un autre utilisateur et afficher les temps de réponse du twitt, de rafraichisement du fil d'actualité et de follow. Contrairement à twitter la zone pour suivre quelqu'un n'est pas dans la barre de navigation en haut, mais comme nous n'avons pas le système de suggestion, l'espace à droite aurait été un peu vide.<br>
## Résultat des différentes mesures
Voici le temps moyens (sur 30 mesures) pour le post d'un twitt pour une personne suivie par 100, 1000 et 2500 followers.

 100 | 1000 | 2500 
 --- | ---  | ---  
 ~900 ms| ~900 ms | ~2000 ms

Voici le temps moyens (sur 30 mesures) pour la récuperation de la timeline par une personne qui suit 100, 1000 et 2500 et pour les 10, 50 et 100 derniers messages.

  --- | 100 | 1000 | 2500
 ------ | -------------   | --------- |  ----- 
 **10**     |    ~4000 ms      |     ~10000 ms |  ---      
 **50**     |     ~4000 ms        |    ---        |  ---   
 **100**    |     ---        |    ---        |  ---    

Etant donné que nous étions restraint par les quotas de lecture et d'ecriture du Google Cloud Plateform il nous a été impossible à l'heure actuelle de faire la mesure de temps des fonctions dans les temps imparties.
Néamoins on remarquera que les temps sont ici supérieurs à ceux attendu. Les mesures ayat été effectué en frontend, un temps de latence est donc à ajouter à ces temps. 
Nous sommes bien conscient qu'une mesure des temps en backend aurait été plus approprié mais faute de temps et du fait de choix qui nous parraissaient dans un premier temps meilleurs et qui se sont trouvé en définitifs mois adéquate cela n'a pas pu être réalisé.
