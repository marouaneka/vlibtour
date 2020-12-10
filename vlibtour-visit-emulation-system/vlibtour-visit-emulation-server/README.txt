This module is provided for the emulation of a visit in Paris. It
proposes generic methods to build the graph of positions as a set of
adjacency list and to search for paths from a departure node to a
destination node, and utility methods to manage the pathway in the
choosen path.

In the class GraphOfPositionsForEmulation, the graph of positions is
built in the method initTourWithPOIs. In the same class, the generic
method addEdge builds the graph as a set of adjacency sets and the
generic method computePathsFromDepartureToDestination computes all the
paths from a departure position to a destination position.

The classVisitEmulationServer proposes public methods that are the API
of the REST server---i.e., getNextPOIPosition, getCurrentPosition,
stepInCurrentPath, stepsInVisit. In order to learn how to use this module,
please refer to Javadoc class documentation, the class VisitEmulationTestClient
that contains call examples, and the JUnit test classes to learn how to use this
module.
