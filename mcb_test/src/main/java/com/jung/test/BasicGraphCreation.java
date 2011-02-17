package com.jung.test;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * This class takes care of creating the connections between a predetermined
 * set of nodes.  The connections are latter implemented to create a directed
 * graph.
 * @author ruggero
 *
 */
public class BasicGraphCreation {
	Graph<MyNode, MyLink> g;
	public BasicGraphCreation(){
		g = new DirectedSparseMultigraph<MyNode, MyLink>();
		// Create some MyNode objects to use as vertices
		MyNode n1 = new MyNode(1);
		MyNode n2 = new MyNode(2);
		MyNode n3 = new MyNode(3);
		MyNode n4 = new MyNode(4);
		MyNode n5 = new MyNode(5);

		// Add some directed edges along with the vertices to the graph
		g.addEdge(new MyLink(1,2),n1, n2, EdgeType.DIRECTED); // This method
		g.addEdge(new MyLink(1,2),n2, n3, EdgeType.DIRECTED);
		g.addEdge(new MyLink(1,2), n3, n5, EdgeType.DIRECTED);
		g.addEdge(new MyLink(1,2), n5, n4, EdgeType.DIRECTED); // or we can use
		g.addEdge(new MyLink(1,2), n4, n2); // In a directed graph the
		g.addEdge(new MyLink(1,2), n3, n1); // first node is the source
		g.addEdge(new MyLink(1,2), n2, n5);// and the second the destination
	}
}
