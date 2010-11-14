package org.wholebrainproject.mcb.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.wholebrainproject.mcb.graph.ConnectionEdge;
import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.Node;
/**
 * Class takes care of managing the data map that pretends to 
 * the BAMS projection data.
 * @author ruggero carloz
 *
 */
public class BAMSProjectionMap {

	
	public static HashMap<String, BAMSProjectionData> dataMap;
	private static BAMSProjectionMap instance;

	/**
	 * Constructor instantiates the data map.
	 * @throws IOException
	 */
	private BAMSProjectionMap() throws IOException {
		dataMap = ReadProjectionDataFile.getInstance().getMap();
		
	}
	
	public static BAMSProjectionMap  getInstance() throws IOException {
		if(instance == null)
			instance = new BAMSProjectionMap();
		return instance;
	}
	
	/**
	 * Method returns the hash map containing the projection data
	 * from BAMS.
	 * @return - a map with Brain Region names (simple strings) mapped to
	 *           BAMSProjectionData objects, which provide more info about
	 *           projections.
	 */
	public HashMap<String, BAMSProjectionData> getBAMSProjectionMap(){
		return this.dataMap;
	}


	/**
	 * Gets edges that exist between Node n, and a collection of nodes.
	 * @param n
	 * @param nodes
	 * @return - a map with a node that is a member of nodes and the edge
	 *           that corresponds to its connection.
	 */
	public Map<Node,Edge> getEdgesBetween(Node n, Collection<Node> nodes) {
		Map<Node,Edge> nodesToEdges = new HashMap<Node,Edge>();
		for (Node no : nodes) {
			ConnectionEdge c = findConnectionEdgeBetween(no, n);
			if (c != null) {
				nodesToEdges.put(no, c);
			}
			ConnectionEdge c2 = findConnectionEdgeBetween(n, no);
			if (c2 != null) {
				nodesToEdges.put(no, c);
			}
		}
		return nodesToEdges;
	}
	
	/**
	 * Find a connection edge between two nodes
	 * @param n - sending node
	 * @param no - receiving node
	 * @return - null if no edge exists, a connection edge appropriately initialized if 
	 *           it does exists
	 */
	private ConnectionEdge findConnectionEdgeBetween(Node n, Node no) {
		BAMSProjectionData project = getBAMSProjectionMap().get(no.getName());
		if (project != null) {
			//look over projections to see if Node n is present!
			for (String name : project.getProjections()) {
				if (n.getName().equals(name)) {
					return createConnectionEdge(no, n, project);
				}
			}
		}
		return null;
	}

	
	private ConnectionEdge createConnectionEdge(Node no, Node n,
			BAMSProjectionData project) {
		//FIXME: need to replace empty strings with real values
		return new ConnectionEdge("", "", no, n);
	}
}
