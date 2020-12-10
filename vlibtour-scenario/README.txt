This module includes helper methods for painting the maps of Paris
with users as MapMarkerDot objects. Please refer to the class MapDemo
for learning how to draw the map, add users, and move them on the
map.

To see how it runs, execute the demonstration:
$ mvn clean install
$ mvn exec:java@map

NB: the command "mvn exec:java@map" is only for a demonstration of the map. It
is not the command that is used for the running the scenario of the
micro-project.

The class VLibTourVisitTouristApplication is the main of a tourist in the
scenario of the micro-project. See the shell scripts
.../Scripts/start_tourist_application_w_emulated_location.sh
