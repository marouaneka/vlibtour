This is the case study "VlibTour".

Since Glassfish requires JAVA 1.8, we assume that you have a shell script 'java8', which is acessible via the shell variable PATH:
$ cat .../java8
#! /bin/bash
JAVA_HOME=... # adapt the path to your configuration
CLASSPATH=$JAVA_HOME/lib
export PATH CLASSPATH JAVA_HOME

To compile and install, execute the following command:
$ (. java8;mvn clean install)

To run the scenario of the demonstrator:
$ (. java8;./run_scenario_w_mapviewer.sh)

Here follows some explanations on the content.
The shell script of the scenario is 'run_scenario_w_mapviewer.sh'. It uses the
shell scripts of the directory 'Scripts'.
The Maven modules are the following ones,
mainly in the order of their compilation:
- root: the VLibTour project;
- vlibtour-libraries: the libraries for VLibTour:
  - Geocalc: external libraries without any Maven repository, thus explaining
    why the source code is included here;
  - vlibtour-common: the VLibTour library for common classes;
- vlibtour-tour-management-system: The VLibTour Tour Management
  (EJB technology):
  - vlibtour-tour-management-api: the VLibTour Tour Management API,
  - vlibtour-tour-management-entity: the VLibTour Tour Management entity;
  - vlibtour-tour-management-bean: the VLibTour Tour Management bean server;
  - vlibtour-tour-management-admin-client: the VLibTour Tour Management client
    (to populate the data base for the EJB entities);
- vlibtour-lobby-room-system: the VLibTour Lobby Room System (AMQP technology):
  - vlibtour-lobby-room-api: the API of the VLibTour lobby room;
  - vlibtour-lobby-room-server: the VLibTour lobby room server;
  - vlibtour-lobby-room-proxy: the VLibTour proxy of the lobby room system;
- vlibtour-visit-emulation-system: the VLibTour Visit Emulation System
  (REST technology)
  - vlibtour-visit-emulation-server: the REST server of the VLibTour emulation
    of a visit in a graph positions (Bikestation services and
    POIs [points of interest])
  - vlibtour-visit-emulation-proxy: the VLibTour proxy of visit emulation
    system;
- vlibtour-group-communication-system: the VLibTour Group Communication
  System (AMQP technology)
  - vlibtour-client-group-communication-proxy: the VLibTour proxy
    of the group communication system;
- vlibtour-scenario: the VLibTour Scenario module, which includes the client
  application VLibTourVisitTouristApplication and the classes to manage an
  OpenStreetMap map viewer.

The directory structure of these Maven modules is as follows:
$ tree
.
├── LICENSE
├── pom.xml
├── README.txt
├── run_scenario_w_mapviewer.sh
├── Scripts
├── vlibtour-bikestation
├── vlibtour-group-communication-system
|   ├── pom.xml
|   └── vlibtour-group-communication-proxy
├── vlibtour-libraries
|   ├── pom.xml
|   ├── geocalc
|   └── vlibtour-common
├── vlibtour-lobby-room-system
|   ├── pom.xml
|   ├── vlibtour-lobby-room-api
|   ├── vlibtour-lobby-room-proxy
|   └── vlibtour-lobby-room-server
├── vlibtour-scenario
├── vlibtour-tour-management-system
|   ├── pom.xml
|   ├── vlibtour-tour-management-admin-client
|   ├── vlibtour-tour-management-api
|   ├── vlibtour-tour-management-bean
|   └── vlibtour-tour-management-entity
└── vlibtour-visit-emulation-system
    ├── pom.xml
    ├── vlibtour-visit-emulation-proxy
    └── vlibtour-visit-emulation-server

The dependencies of the Maven modules is as follows (we detail only the
dependencies upon modules of functional components, hence ignoring dependencies
upon modules of the technologies used [EJB, AMQP, REST, etc.]):
$ mvn -B dependency:tree
* vlibtour-tour-management-api
  depends upon
  vlibtour-tour-management-entity
* vlibtour-tour-management-bean
  depends upon
  vlibtour-tour-management-api
* vlibtour-tour-management-admin-client
  depends upon
  vlibtour-tour-management-entity
  vlibtour-tour-management-api
* vlibtour-lobby-room-server
  depends upon
  vlibtour-lobby-room-api
* vlibtour-lobby-room-proxy
  depends upon
  vlibtour-lobby-room-api
  vlibtour-lobby-room-server
* vlibtour-group-communication-proxy
  depends upon
  vlibtour-lobby-room-api
* vlibtour-visit-emulation-server
  depends upon 
  vlibtour-common
* vlibtour-visit-emulation-proxy
  depends upon
  vlibtour-common
* vlibtour-scenario
  depends upon
  vlibtour-common
  vlibtour-tour-management-entity
  vlibtour-tour-management-api
  vlibtour-lobby-room-server
  vlibtour-lobby-room-proxy
  vlibtour-group-communication-proxy
  vlibtour-visit-emulation-proxy
  
  Commands vlibtour-tour-managment-system:
  $ asadmin undeploy vlibtour-tour-management-bean; asadmin stop-database; asadmin stop-domain domain1
  $ mvn clean install -DskipTests -Dmaven.skip.javadoc=true
  $ asadmin start-domain domain1; asadmin start-database; asadmin deploy vlibtour-tour-management-bean/target/vlibtour-tour-management-bean.jar
  $ cd vlibtour-tour-management-admin-client/; java -classpath $CLASSPATH:../vlibtour-tour-management-bean/target/vlibtour-tour-management-bean.jar:../vlibtour-tour-management-api/target/vlibtour-tour-management-api-1.0-SNAPSHOT.jar:./target/vlibtour-tour-management-admin-client-1.0-SNAPSHOT.jar vlibtour/vlibtour_tour_management_admin_client/VlibTourTourManagementAdminClient
  
     *** connect 'jdbc:derby://localhost:1527/sun-appserv-samples';
     
 Commands for the demo:
 $cd vlibtour-visit-emulation-system/vlibtour-visit-emulation-server/
 $mvn exec:java@server
 $cd vlibtour-lobby-room-system/vlibtour-lobby-room-server/
 $rabbitmq-server -detached
 $rabbitmqctl stop_app
 $rabbitmqctl reset
 $rabbitmqctl start_app
 $mvn exec:java@server
 $cd vlibtour-scenario/
 $mvn exec:java@touristapplijoe
 $mvn exec:java@touristappliavrel
 $rabbitmqctl stop_app
 $rabbitmqctl stop
 
 
 
 
 
 
   
 
     

  
