# MyERP

[![Build Status](https://travis-ci.org/AndreM-1/Projet_9_Testez_vos_developpements_Java.svg?branch=master)](https://travis-ci.org/AndreM-1/Projet_9_Testez_vos_developpements_Java)

## Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs _docker_ utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application


## Environnement de développement

Les composants nécessaires lors du développement sont disponibles via des conteneurs _docker_.
L'environnement de développement est assemblé grâce à _docker-compose_
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données _PostgreSQL_ contenant un jeu de données de démo (`postgresql://127.0.0.1:9032/db_myerp`)



### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up

### Remarque concernant Docker Toolbox

En cas d'utilisation de Docker Toolbox en local, il faut vérifier les points suivants :

*   Le présent projet doit être installé dans la sous arborescence du répertoire C:\Users.
*	Au niveau du fichier src/myerp-business/src/test-business/resources/db-myerp.properties utilisé pour configurer la 
	DataSource pour les tests d'intégration, 2 url sont définies. On peut utiliser l'une ou l'autre des url, mais pas les 
	2 en même temps.
    *   1 url pour l'environnement d'intégration continue avec Travis CI (configuration actuelle).
    * 	1 url pour les tests d'intégration en local (en commentaire). Enlever le commentaire pour cet url et mettre l'autre url
    	en commentaire avant de lancer les tests d'intégration en local.
*	Ne pas oublier en vue d'instancier le conteneur de lancer les commandes indiquées ci-dessus dans la partie Lancement   
	puis de modifier le HOSTNAME via Kitematic dans Settings -> Hostname / Ports -> Configure Hostname -> HOSTNAME.
* 	Dans le cas où le conteneur existe déjà, il faut s'assurer qu'il est bien en cours d'exécution. Si ce n'est pas le cas, il faut
	utiliser la commande : docker container start CONTAINER ID.

### Rapports Maven

Il est possible d'obtenir un rapport sur les tests unitaires et d'intégration, ainsi que sur la qualité du code, mais également la Javadoc
du projet, en lançant la commande : mvn clean test -Ptest-business site site:stage. Le rapport généré se trouve au niveau du module parent
répertoire target/staging/index.html