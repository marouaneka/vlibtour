This module is a library and proposes classes that are used in other
Maven modules, which are systems of the architecture.

The class Position defines the node of the graph for the emulation. A
position contains (by delegation) a GPSPosition. It is assumed that
some of the positions are Points Of Interest (POI). The class Position
then possesses a reference to an Object that will refer to your
version of the class POI.

NB1: the class Position is the class used in the VLibTour Visit Emulation System,
e.g. in the Maven module vlibtour-visit-emulation-server.

NB2: the GPSPosition objects and the Position objects can be serialised
(1) using Glassfish Jersey and (2) using Google Gson (JSON format). Please read
the Javadoc of the classes.

The class ExampleOfAVisitWithTwoTourists contains constants, i.e. public final
attributes that are used in the demonstrator, i.e. in the Maven module
vlibtour-visit-emulation-server.

The class Log contains the configuration of some logging facilities.
