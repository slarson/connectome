package org.wholebrainproject.mcb.data;
/*Copyright (C) 2010 contact@wholebraincatalog.org
 *
 * Whole Brain Catalog is Licensed under the GNU Lesser Public License (LGPL), Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the license at
 *
 * http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The class is used to create the edges for the graph.  This class is implemented 
 * by Multi-Scale Connectome Browser.
 * @date    March 1, 2010
 * @author  Ruggero Carloz
 * @version 0.0.1
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.ConnectionEdge;
import org.wholebrainproject.mcb.graph.CustomGraphCollapser;
import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BrainRegionNameShortener;
import org.wholebrainproject.mcb.util.SparqlQuery;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;


/**
 * Responsible for accessing the server, querying data, and turning those
 * data into edges for the graph.
 * @author Ruggero Carloz - adaptation 
 * 
 */
@SuppressWarnings("serial")

public class BuildConnections {

	public static List<String> initialBamsURIs = new ArrayList<String>();
	public static MultiHashMap<String, BAMSToNeurolexData> BAMSToNeurolexHashMap;
	private static BuildConnections instance = null;

	private BuildConnections() {

	}

	public static BuildConnections getInstance() {
		if (instance == null) {
			instance = new BuildConnections();
		}
		return instance;
	}

	public void getDataAndCreateGraphBetter(Graph<Node,Edge> graph) {
		/**try {
			BAMSToNeurolexHashMap = BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		String[] initialBamsNames = {"brainstem","basal-ganglia","cerebral-cortex","thalamus"};//{"cerebral-cortex", "thalamus-4", "basal-ganglia", "midbrain-hindbrain-motor-extrapyramidal"};

		//obtain the BAMS brain region names that intersect with neurolex.
		//String[] initialBamsNames = getInitialBAMSNames();
		//obtain the BAMS uris.
		//addInitialBamsURIs();
		
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/brainstem/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/thalamus-2/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/basal-ganglia/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/cerebral-cortex-10/");

		MultiHashMap<String,String> results = getBAMSPartOfResults(initialBamsNames);
		/**for(String key: results.keySet()){
			for(String currentResult: results.get(key)){
				System.out.println("Current key: "+ key +" result: "+currentResult);
			}
		}**/
		Node[] nodes = convertPartOfResultsIntoNodes(initialBamsNames, results);
		
		Node[] childlessNodes = filterParentNodes(nodes);
		MultiHashMap<String,String> deeperResults = increaseDepthOfPartOfResults(childlessNodes);
		//System.out.println("deeperResults: "+deeperResults.size());
		Node[] deeperNodes = convertDeeperResultsIntoNodes(childlessNodes, deeperResults);
		//Node[] deeperNodes = new Node[0];
		//System.out.println("depperNodes: "+deeperNodes.length);
		List<Node> nodeList = mergeNodes(deeperNodes, nodes);

		//populate the nodes with the cell data.
		//MultiHashMap<String,String> cellResults = NeuroLexDataLoader.populate(nodes);
		//NeuroLexDataLoader.storeData(nodes, cellResults);
		
		//System.out.println("nodeList: "+nodeList.size());
		List<ConnectionEdge> edges = new ArrayList<ConnectionEdge>();

		//to avoid making query strings too long for the server to handle,
		//break the request for edges into chunks of 10 nodes at a time
		List<Node> partialNodeChunk = new ArrayList<Node>();
		for (int i = 0; i < nodeList.size(); i++) {
			partialNodeChunk.add(nodeList.get(i));
			if (i % 10 == 0 || i == nodeList.size() -1) {

				MultiHashMap<String,String> connResults = 
					getConnectionsResults(partialNodeChunk);

				List<ConnectionEdge> partialEdges = 
					convertConnectionResultsIntoEdges(connResults, partialNodeChunk);

				edges.addAll(partialEdges);

				partialNodeChunk = new ArrayList<Node>();
			}
		}

		Set<String> missingNodeNames = findMissingNodeStrings(edges, nodeList);

		List<String> partialNameChunk = new ArrayList<String>();
		Iterator<String> missingNodeNamesIt = missingNodeNames.iterator();
		for (int i = 0; i < missingNodeNames.size(); i++) {
			partialNameChunk.add(missingNodeNamesIt.next());
			if (i % 10 == 0 || i == missingNodeNames.size() -1) {

				MultiHashMap<String,String> missingResults = getMissingNodesByName(partialNameChunk);
				List<Node> missingNodes = convertMissingNodesResultsIntoNodes(missingResults, partialNameChunk);

				nodeList.addAll(missingNodes);
				partialNameChunk = new ArrayList<String>();
			}
		}

		//by repeating find missing node strings, we populate the edges
		missingNodeNames = findMissingNodeStrings(edges, nodeList);

		for (ConnectionEdge edge : edges ){
			Node sending = edge.getSendingNode();
			Node sendingParent = sending.getParent();
			Node receiving = edge.getReceivingNode();
			Node receivingParent = receiving.getParent();

			boolean sendingParentContains = sendingParent != null && 
			initialBamsURIs.contains(sendingParent.getUri());


			boolean receivingParentContains = receivingParent != null && 
			initialBamsURIs.contains(receivingParent.getUri());

			if (initialBamsURIs.contains(sending.getUri()) || initialBamsURIs.contains(receiving.getUri())
					|| sendingParentContains ||
					receivingParentContains) {
				graph.addEdge(edge, sending, receiving);
				//CustomGraphCollapser.getInstance().collapse();
				//CustomGraphCollapser.getInstance().initialCollapse(graph);
			}
		}

		for (Node n: nodeList) {
			if (BuildConnections.initialBamsURIs.contains(n.getUri())) {
				graph.addVertex(n);
			}
		}
		//CustomGraphCollapser.getInstance().collapse();
	}

	private MultiHashMap<String,String> getBAMSPartOfResults(String[] initialBamsNames) {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		q.addPrefixMapping("bams_rdf", "<http://brancusi1.usc.edu/RDF/>");


		for (String brainRegion : initialBamsNames) {

			String uri = "http://brancusi1.usc.edu/brain_parts/" + brainRegion
			+ "/";
			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String nameVar = "$" + var + "_name";
			String uriVar = "$" + var + "_uri";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			// add query triplets
			q.addQueryTriplet("<" + uri + ">" + " bams_rdf:name " + nameVar);
			q.addQueryTriplet(SparqlQuery.UNION);
			q.addQueryTriplet(uriVar + " bams_rdf:class1 " + "<" + uri + ">");
			q.addQueryTriplet(uriVar + " bams_rdf:class2 " + childUriVar);
			q.addQueryTriplet(childUriVar + " bams_rdf:name " + childNameVar);

			q.addSelectVariable(nameVar);
			q.addSelectVariable(childUriVar);
			q.addSelectVariable(childNameVar);

			//add union between all sets of variables except the last
			if (brainRegion.equals(initialBamsNames[initialBamsNames.length - 1]) == false) {
				q.addQueryTriplet(SparqlQuery.UNION);
			}
		}
		return q.runSelectQuery();
	}

	public Node[] convertPartOfResultsIntoNodes(String[] initialBamsNames, 
			MultiHashMap<String,String> results) {
		List<Node> nodes = new ArrayList<Node>();
		Node n;
		for (String brainRegion: initialBamsNames) {

			String uri = "http://brancusi1.usc.edu/brain_parts/" + brainRegion + "/";

			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String nameVar = "$" + var + "_name";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";
			String brainRegionPrettyName = results.get(nameVar).iterator().next();
			if(brainRegionPrettyName.equalsIgnoreCase("midbrain-hindbrain, motor, extrapyramidal"))
				n = new Node(uri, brainRegionPrettyName.toLowerCase());
			else{
			    n = new Node(uri, "basal_ganglia");
			}
			nodes.add(n);

			Collection<String> childUris = results.get(childUriVar);
			Collection<String> childNames = results.get(childNameVar);

			// might not have children
			if (childUris != null) {
				Iterator<String> urisIt = childUris.iterator();
				Iterator<String> namesIt = childNames.iterator();

				ArrayList<Node> childrenNodes = new ArrayList<Node>();
				for (int i = 0; i < childUris.size(); i++) {
					Node child = new Node(urisIt.next(), namesIt.next());
					child.setParent(n);
					childrenNodes.add(child);
					nodes.add(child);
				}

				n.setPartOfNodes(childrenNodes);
			}
		}

		Node[] out = new Node[nodes.size()];
		return (Node[]) nodes.toArray(out);
	}


	private MultiHashMap<String,String> increaseDepthOfPartOfResults(Node[] nodes) {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		q.addPrefixMapping("bams_rdf", "<http://brancusi1.usc.edu/RDF/>");

		for (Node n : nodes) {
			String brainRegion = n.getName();

			String uri = n.getUri();

			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String uriVar = "$" + var + "_uri";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			// add query triplets
			q.addQueryTriplet(uriVar + " bams_rdf:class1 " + "<" + uri + ">");
			q.addQueryTriplet(uriVar + " bams_rdf:class2 " + childUriVar);
			q.addQueryTriplet(childUriVar + " bams_rdf:name " + childNameVar);

			q.addSelectVariable(childUriVar);
			q.addSelectVariable(childNameVar);

			//add union between all sets of variables except the last
			if (brainRegion.equals(nodes[nodes.length - 1].getName()) == false) {
				q.addQueryTriplet(SparqlQuery.UNION);
			}
		}
		return q.runSelectQuery();
	}

	private Node[] convertDeeperResultsIntoNodes(Node[] nodes, MultiHashMap<String,String> results) {
		List<Node> nodesOut = new ArrayList<Node>();
		//System.out.println("Number of nodes: "+nodes.length);
		Iterator<String> namesIt = null;
		Iterator<String> urisIt = null;

		for (Node n : nodes) {
			System.out.println("Current node:"+n.toString());
			nodesOut.add(n);
			String brainRegion = n.getName();

			String var = BrainRegionNameShortener.reduceName(brainRegion);
			
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			Collection<String> childUris = results.get(childUriVar);
			Collection<String> childNames = results.get(childNameVar);
			//System.out.println("results.size(): "+results.size());
			if(childUris != null){
				urisIt = childUris.iterator();
			}
			
			if(childNames != null){
				namesIt = childNames.iterator();
			}

			ArrayList<Node> childrenNodes = new ArrayList<Node>();

			//System.out.println("childrenNodes :"+childUris.size());
			
			if(urisIt != null && namesIt != null){
				for (int i = 0; i < childUris.size(); i++) { 
					Node child = new Node(urisIt.next(), namesIt.next());
					child.setParent(n);
					childrenNodes.add(child);
					nodesOut.add(child);
				}
			}
			
			urisIt = null;
			namesIt = null;
			
			n.setPartOfNodes(childrenNodes);

		}
		Node[] nodesOutArray = new Node[nodesOut.size()];
		return nodesOut.toArray(nodesOutArray);
	}


	private MultiHashMap<String,String> getConnectionsResults(List<Node> nodes) {
		String sparql = "http://rdf.neuinfo.org/sparql";
		SparqlQuery q = new SparqlQuery(sparql);

		q.addPrefixMapping("nif_cnxn", "<http://connectivity.neuinfo.org#>");

		for (Node brainRegion : nodes){
			String brainRegionName = brainRegion.getName();
			System.out.println("brainRegionName: "+brainRegion.getName());
			String var = BrainRegionNameShortener.reduceName(brainRegionName);
			String uriVar = "$" + var + "_uri";
			String referenceReceivingVar = "$" + var + "_ref_rec";
			String strengthReceivingVar = "$" + var + "_str_rec";
			String receivingVar = "$" + var + "_rec";
			String referenceSendingVar = "$" + var + "_ref_send";
			String strengthSendingVar = "$" + var + "_str_send";
			String sendingVar = "$" + var + "_send";

			q.addQueryTriplet(uriVar + 
					" nif_cnxn:sending_structure  \"" + brainRegionName+"\"");
			q.addQueryTriplet(uriVar + " nif_cnxn:projection_strength "+ strengthReceivingVar);
			q.addQueryTriplet(uriVar + " nif_cnxn:receiving_structure " + receivingVar);
			q.addQueryTriplet(uriVar + " nif_cnxn:reference "+ referenceReceivingVar);

			q.addQueryTriplet(SparqlQuery.UNION);

			q.addQueryTriplet(uriVar + 
					" nif_cnxn:receiving_structure  \"" + brainRegionName+"\"");
			q.addQueryTriplet(uriVar + " nif_cnxn:projection_strength "+ strengthSendingVar);
			q.addQueryTriplet(uriVar + " nif_cnxn:sending_structure " + sendingVar);
			q.addQueryTriplet(uriVar + " nif_cnxn:reference "+ referenceSendingVar);

			q.addSelectVariable(strengthReceivingVar);
			q.addSelectVariable(referenceReceivingVar);
			q.addSelectVariable(receivingVar);
			q.addSelectVariable(strengthSendingVar);
			q.addSelectVariable(referenceSendingVar);
			q.addSelectVariable(sendingVar);

			//add union between all sets of variables except the last
			if (brainRegionName.equals(nodes.get(nodes.size() - 1).getName()) == false) {
				q.addQueryTriplet(SparqlQuery.UNION);
			}
		}

		return q.runSelectQuery();
	}

	private List<ConnectionEdge> convertConnectionResultsIntoEdges(MultiHashMap<String,String> results, List<Node> nodes) {
		List<ConnectionEdge> edges = new ArrayList<ConnectionEdge>();

		for (Node brainRegion : nodes){
			String brainRegionName = brainRegion.getName();

			String var = BrainRegionNameShortener.reduceName(brainRegionName);
			String referenceReceivingVar = "$" + var + "_ref_rec";
			String strengthReceivingVar = "$" + var + "_str_rec";
			String receivingVar = "$" + var + "_rec";
			String referenceSendingVar = "$" + var + "_ref_send";
			String strengthSendingVar = "$" + var + "_str_send";
			String sendingVar = "$" + var + "_send";

			
			Collection<String> receivingStrengths = results.get(strengthReceivingVar);
			//System.out.println("receivingStrengths: "+receivingStrengths);
			Collection<String> receivingReferences = results.get(referenceReceivingVar);
			Collection<String> receivingRegions = results.get(receivingVar);

			if (receivingStrengths != null) {
				Iterator<String> strengthsIt = receivingStrengths.iterator();
				Iterator<String> referencesIt = receivingReferences.iterator();
				Iterator<String> receivingIt = receivingRegions.iterator();

				for (int i = 0; i < receivingRegions.size(); i++) {
					ConnectionEdge e = new ConnectionEdge(strengthsIt.next(),
							referencesIt.next(), brainRegion, receivingIt
							.next());
					edges.add(e);
				}
			}

			Collection<String> sendingStrengths = results.get(strengthSendingVar);
			Collection<String> sendingReferences = results.get(referenceSendingVar);
			Collection<String> sendingRegions = results.get(sendingVar);

			if (sendingStrengths != null) {
				Iterator<String> strengthsIt = sendingStrengths.iterator();
				Iterator<String> referencesIt = sendingReferences.iterator();
				Iterator<String> sendingIt = sendingRegions.iterator();

				for (int i = 0; i < sendingRegions.size(); i++) {
					ConnectionEdge e = new ConnectionEdge(strengthsIt.next(),
							referencesIt.next(), sendingIt.next(), brainRegion);
					edges.add(e);
				}
			}

		}
		return edges;
	}

	/**
	 * Filter out nodes that have children.
	 * @param nodes - the incoming set
	 * @return - the filtered set.
	 */
	private Node[] filterParentNodes(Node[] nodes) {
		List<Node> nodesOut = new ArrayList<Node>();
		for (Node n: nodes) {
			if (n.hasParts() == false)
				nodesOut.add(n);
		}

		Node[] nodesOutArray = new Node[nodesOut.size()];
		return nodesOut.toArray(nodesOutArray);
	}

	/**
	 * Merge two arrays of nodes, eliminating duplicates.
	 * @param nodeSet1
	 * @param nodeSet2
	 * @return - an array with all nodes in nodeSet1 and nodeSet2.
	 */
	private List<Node> mergeNodes(Node[] nodeSet1, Node[] nodeSet2) {
		Set<Node> nodesOut = new HashSet<Node>();
		for (Node n: nodeSet1) {
			nodesOut.add(n);
		}
		for (Node n: nodeSet2) {
			nodesOut.add(n);
		}

		return new ArrayList<Node>(nodesOut);
	}

	private Set<String> findMissingNodeStrings(List<ConnectionEdge> edges, List<Node> nodes) {
		Set<String> missingNodeNames = new HashSet<String>();
		Map<String, Node> nodeNames = new HashMap<String,Node>();
		for (Node n : nodes) {
			nodeNames.put(n.getName(), n);
		}

		for (ConnectionEdge e : edges) {
			if (e.getSendingNode() == null) {
				if (nodeNames.keySet().contains(e.getSendingNodeString())) {
					e.setSendingNode(nodeNames.get(e.getSendingNodeString()));
				} else {
					missingNodeNames.add(e.getSendingNodeString());
				}
			}
			if (e.getReceivingNode() == null) {
				if (nodeNames.keySet().contains(e.getReceivingNodeString())) {
					e.setReceivingNode(nodeNames.get(e.getReceivingNodeString()));
				} else {
					missingNodeNames.add(e.getReceivingNodeString());
				}
			}
		}
		return missingNodeNames;
	}

	private MultiHashMap<String,String> getMissingNodesByName(List<String> nodeNames) {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		q.addPrefixMapping("bams_rdf", "<http://brancusi1.usc.edu/RDF/>");

		int i = 0;
		for (String brainRegion : nodeNames) {

			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String connUriVar = "$" + var + "_conn_uri";
			String uriVar = "$" + var + "_uri";
			String uriVar2 = "$" + var + "_uri2";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			// add query triplets
			q.addQueryTriplet(uriVar + " bams_rdf:name \"" + brainRegion + "\"");
			q.addQueryTriplet(SparqlQuery.UNION);
			q.addQueryTriplet(uriVar2 + " bams_rdf:name \"" + brainRegion + "\"");
			q.addQueryTriplet(connUriVar + " bams_rdf:class1 " + uriVar2);
			q.addQueryTriplet(connUriVar + " bams_rdf:class2 " + childUriVar);
			q.addQueryTriplet(childUriVar + " bams_rdf:name " + childNameVar);

			q.addSelectVariable(uriVar);
			q.addSelectVariable(childUriVar);
			q.addSelectVariable(childNameVar);

			//add union between all sets of variables except the last
			if (i++ < nodeNames.size() - 1) {
				q.addQueryTriplet(SparqlQuery.UNION);
			}
		}
		return q.runSelectQuery();
	}

	private List<Node> convertMissingNodesResultsIntoNodes(MultiHashMap<String,String> results, 
			List<String> partialNameChunk) {
		List<Node> nodes = new ArrayList<Node>();

		for (String brainRegion: partialNameChunk) {

			String var = BrainRegionNameShortener.reduceName(brainRegion);

			String uriVar = "$" + var + "_uri";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			Collection<String> uri = results.get(uriVar);

			Node n = new Node(uri.iterator().next(), brainRegion);
			nodes.add(n);

			Collection<String> childUris = results.get(childUriVar);
			Collection<String> childNames = results.get(childNameVar);

			//might not have children.. that's ok!
			if (childUris != null) {
				Iterator<String> urisIt = childUris.iterator();
				Iterator<String> namesIt = childNames.iterator();

				ArrayList<Node> childrenNodes = new ArrayList<Node>();
				for (int i = 0; i < childUris.size(); i++) {
					Node child = new Node(urisIt.next(), namesIt.next());
					child.setParent(n);
					childrenNodes.add(child);
					nodes.add(child);
				}

				n.setPartOfNodes(childrenNodes);
			} 
		}

		return nodes;
	}

	public static void connectNodesIfEdgeIsAppropriate(Graph<Node,Edge> graph, Node x, Node y) {
		if (x == null || y == null) {
			throw new IllegalArgumentException();
		}
		//test to see if there is already an existing edge between these two nodes
		Set<Edge> a = new HashSet<Edge>();
		Collection<Edge> e = graph.getIncidentEdges(x);
		if (e != null) 
			a.addAll(e);
		Collection<Edge> f = graph.getIncidentEdges(y);
		//leaves a with the intersection of the edges that are incident to x & y
		if (f != null)
			a.retainAll(f);

		if (a.isEmpty()) {
			if (x.getRegionToStrengthMap().get(y.getName()) != null) {
				String strength = x.getRegionToStrengthMap().get(y.getName());
				String reference = x.getReferenceSet().get(y.getName());
				ConnectionEdge j = new ConnectionEdge(strength, reference);

				graph.addEdge(j, x, y, EdgeType.DIRECTED);
			}
		}
	}

	private static void subNodesConnection(Graph graph, Node[] node){
		ArrayList<Node> repeats = new ArrayList<Node>();

		for(int i = 0; i < node.length; i++){
			if(!node[i].getPartOfNodes().isEmpty()){
				for(Node partOf: node[i].getPartOfNodes()){
					if(!repeats.contains(partOf)){
						node[i].addPartOfNodes(graph);
						repeats.add(partOf);
					}	
				}
			}
		}

	}
	/**
	 * Method returns a vector containing the names of the brain regions
	 * that are present in BAMS and Neurolex.
	 * @return
	 */
	private String[] getInitialBAMSNames() {
		Vector<String> names = new Vector<String>();
		int index = 0;
		for(String key: BAMSToNeurolexHashMap.keySet()){
			for(BAMSToNeurolexData data:BAMSToNeurolexHashMap.get(key)){
				names.add(data.getName());
			}
		}
		String[] brainRegionNames = new String[names.size()];
		for(String currentBrainRegionName: names){
			brainRegionNames[index] = currentBrainRegionName;
			index++;
		}
		
		return brainRegionNames;
	}
	
	/**
	 * Method obtains the BAMS uris and stores them in a list.
	 */
	private void addInitialBamsURIs() {
		for(String bamsUri: BAMSToNeurolexHashMap.keySet())
			initialBamsURIs.add(bamsUri);
		
	}
}