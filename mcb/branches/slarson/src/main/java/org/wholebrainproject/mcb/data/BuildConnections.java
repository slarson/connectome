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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.ConnectionEdge;
import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.GraphManager;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BrainRegionNameShortener;
import org.wholebrainproject.mcb.util.SparqlQuery;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;


/**
 * Responsible for accessing the server, querying data, and turning the
 * data into nodes and edges for the graph.
 * @author Ruggero Carloz - adaptation
 *
 */
public class BuildConnections {

	//the names in BAMS of the initial set of brain regions
	private String[] initialBamsNames = null;
	//the uris in BAMS of the initial set of brain regions
	private static List<String> initialBamsURIs = new ArrayList<String>();
	private static MultiHashMap<String, BAMSToNeurolexData> masterList;
	private static HashMap<String,BAMSProjectionData> projectionList;
	private static MultiHashMap<String, BAMSToNeurolexData> BAMSToNeurolexHashMap;
	private static BuildConnections instance = null;

	/**
	 * Constructor takes care of instantiating the master list and the
	 * projection list.
	 */
	private BuildConnections() {
		try {
			masterList = BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap();
			projectionList = BAMSProjectionMap.getInstance().getBAMSProjectionMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static BuildConnections getInstance() {
		if (instance == null) {
			instance = new BuildConnections();
		}
		return instance;
	}

	public void getDataAndCreateGraphBetter(Graph<Node,Edge> graph) {
		//tell the system what brain regions we want to load up first.
		setInitialBrainRegions();

		//do a query to get a map with brain regions and their parts
		MultiHashMap<String,String> brainRegionToChildBrainRegion =
			getBAMSPartOfResults(initialBamsNames);



		//Filter out brain regions that are not in our master connection list.
		brainRegionToChildBrainRegion =
			eliminateDataNotPresentInIntersection(brainRegionToChildBrainRegion);

		/*
		for(String key: brainRegionToChildBrainRegion.keySet()){
			for(String value: brainRegionToChildBrainRegion.get(key)){
				System.out.println("key: "+key+"   value: "+value);
			}
		}*/

		//turn the map of brain regions into a set of nodes
		Node[] nodes = convertPartOfResultsIntoNodes(initialBamsNames,
				brainRegionToChildBrainRegion);

		//take current list of nodes and find children one more level down
		//from those nodes that do not have children
		Node[] deeperNodes = getMoreChildNodes(nodes);

		/*for(Node node: deeperNodes){
			System.out.println("node: "+node.getName()+" node.parent: "+node.getParent());
		}*/



		Set<Node> nodeList = mergeNodes(deeperNodes, nodes);


		//populate the nodes with the cell data.
		MultiHashMap<String,String> cellResults = NeuroLexDataLoader.populate(nodes);

		for(String key:cellResults.keySet()){
			for(String value: cellResults.get(key)){
				System.out.println("key: "+key+"  value: "+value);
			}
		}
		/*
		NeuroLexDataLoader.storeData(nodes, cellResults);

		//create the edges based on the list.
		List<ConnectionEdge> edges = createAndPopulateEdges(nodeList);

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
		*/

	}

	/**
	 * Create the connection edges based on the list of nodes.
	 * @param nodes
	 * @return
	 */
	private List<ConnectionEdge> createAndPopulateEdges(Set<Node> nodes) {
		List<Node> nodeList = new ArrayList<Node>(nodes);
		List<ConnectionEdge> edges = new ArrayList<ConnectionEdge>();

		//to avoid making query strings too long for the server to handle,
		//break the request for edges into chunks of 10 nodes at a time
		List<Node> partialNodeChunk = new ArrayList<Node>();
		for (int i = 0; i < nodeList.size(); i++) {
			partialNodeChunk.add(nodeList.get(i));
			if (i % 10 == 0 || i == nodeList.size() -1) {

				MultiHashMap<String,String> connResults =
					getConnectionsResults(partialNodeChunk);
				//eliminate connection results that contain brain regions that are not present in
				//master list.
				connResults = eliminateDataNotNeeded(connResults);

				List<ConnectionEdge> partialEdges =
					convertConnectionResultsIntoEdges(connResults, partialNodeChunk);
				//System.out.println("partialEdges: "+partialEdges.size());
				edges.addAll(partialEdges);

				partialNodeChunk = new ArrayList<Node>();
			}
		}
		return edges;
	}

	/**
	 * Takes the list of nodes and gives back a larger list that contains
	 * children of any node that was missing them.
	 * @param nodes - the list to begin with
	 * @return - a Node array that just has the additional children of the given
	 *           nodes, as defined on the BAMS graph by the part of relationships
	 */
	private Node[] getMoreChildNodes(Node[] nodes) {
		Node[] childlessNodes = filterParentNodes(nodes);

		MultiHashMap<String,String> deeperResults = increaseDepthOfPartOfResults(childlessNodes);
		deeperResults = eliminateDataNotPresentInIntersection(deeperResults);
		//deeperResults = eliminateRepeats(nodes,deeperResults);
		//***Some filtering being done here
		return convertDeeperResultsIntoNodes(nodes,childlessNodes, deeperResults);
	}

	/**
	 * Method finds if a given brain region is present in node list.
	 * @param nodes 		    - array of nodes.
	 * @param brainRegionName   -  brain region name to look for.
	 * @return
	 */
	private boolean findElement(Node[] nodes, String brainRegionName){

		for(Node currentNode: nodes){
			if(currentNode.getName().equalsIgnoreCase(brainRegionName)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Method creates a list of brain regions to instantiate the graph with when the
	 * user starts the application.
	 */
	private void setInitialBrainRegions() {
		String[] initialBamsNamesTemp = {"brainstem","basal-ganglia","cerebral-cortex","thalamus","striatum",
				"substantia-nigra-pars-compacta","ventral-tegmental-area","septofimbrial-nucleus",
				"caudoputamen","cuneiform-nucleus"};//,"hippocampal-region"};
		//{"cerebral-cortex", "thalamus-4", "basal-ganglia", "midbrain-hindbrain-motor-extrapyramidal"};

		//taking advantage of the string array initializer
		initialBamsNames = initialBamsNamesTemp;

		//obtain the BAMS brain region names that intersect with neurolex.
		//String[] initialBamsNames = getInitialBAMSNames();
		//obtain the BAMS uris.
		//addInitialBamsURIs();

		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/brainstem/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/basal-ganglia/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/cerebral-cortex/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/thalamus/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/striatum/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/substantia-nigra-pars-compacta/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/ventral-tegmental-area/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/septofimbrial-nucleus/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/substantia-innominata/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/caudoputamen/");
		initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/cuneiform-nucleus/");
		//initialBamsURIs.add("http://brancusi1.usc.edu/brain_parts/hippocampal-region/");
	}

	/**
	 * Method finds the elements that are not present in the file
	 * BAMSBrainRegionsMatchedWithNeurolex.
	 * @param results
	 * @return MultiHashMap - a filtered map containing only brain regions
	 * 						  present in the master list.
	 */
	private MultiHashMap<String, String> eliminateDataNotNeeded(
			MultiHashMap<String, String> results) {
		MultiHashMap<String,String> sendingStruc = new MultiHashMap<String, String>();
		MultiHashMap<String,String> receivingStruc = new MultiHashMap<String, String>();

		for(String key: results.keySet()){
			for(String value: results.get(key)){
				if(sendingStructure(key)){
					if(!isStructureInList(value))
						sendingStruc.put(key,value);
				}
				else if(receivingStructure(key)){
					if(!isStructureInList(value))
						receivingStruc.put(key,value);
				}
			}
		}
		return filterList(results,sendingStruc,receivingStruc);
	}

	/**
	 * Method removes the elements that are not present in the master list
	 * and returns the updated list.
	 * @param results
	 * @param sendingStruc - sending structures not present in master list
	 * @param receivingStruc - receiving structures not present in master list.
	 * @return
	 */
	private MultiHashMap<String, String> filterList(
			MultiHashMap<String, String> results,
			MultiHashMap<String, String> sendingStruc,
			MultiHashMap<String, String> receivingStruc) {
		String variableName;

		for(String sendingKey: sendingStruc.keySet()){
			variableName = getVarKey(sendingKey);
			results.remove(variableName+"_str_rec");
			results.remove(variableName+"_ref_rec");
			results.remove(variableName+"_rec");
			results.remove(variableName+"_str_send");
			results.remove(variableName+"_ref_send");
			results.remove(variableName+"_send");
			results.remove(variableName+"str_send");

		}
		for(String receivingKey: receivingStruc.keySet()){
			variableName = getVarKey(receivingKey);
			results.remove(variableName+"_str_rec");
			results.remove(variableName+"_ref_rec");
			results.remove(variableName+"_rec");
			results.remove(variableName+"_str_send");
			results.remove(variableName+"_ref_send");
			results.remove(variableName+"_send");
			results.remove(variableName+"str_send");

		}

		return results;
	}

	/**
	 * Method checks that a given brain region is present in the master list.
	 * @param value
	 * @return boolean - true if the brain region is in master list
	 * 					 false if the brain region is not present in master list.
	 */
	private boolean isStructureInList(String value){
		value = value.toLowerCase();
		value = "http://brancusi1.usc.edu/brain_parts/"+value.replace(' ', '-')+"/";
		if(masterList.containsKey(value))
			return true;

		return false;
	}
	/**
	 * Method removes the elements that are not present in the file
	 * BAMSBrainRegionsMatchedWithNeurolex.
	 * @param results	   - The list to be filtered.
	 * @return returnedMap - The multi hash map containing only elements present in
	 * 						 master list.
	 */
	private MultiHashMap<String, String> eliminateDataNotPresentInIntersection(
			MultiHashMap<String, String> results) {

		String keyValue;
		Collection<String> childURIs;
		MultiHashMap<String,String> returnedMap = new MultiHashMap<String,String>();

		//make a copy of the original list in order to iterate through the list
		//and modify the copy.
		for(String key: results.keySet()){
			for(String value: results.get(key))
				returnedMap.put(key, value);
		}

		//iterate through the original list and modify the copy if the
		//child uri is not present in the master list.
		for(String key: results.keySet()){
			keyValue = getVarKey(key);
			childURIs = results.get(keyValue+"_child_uri");
			if(childURIs != null){
				for(String childURI: childURIs){
					if(!masterList.containsKey(childURI)){
						returnedMap.remove(keyValue+"_child_uri");
						returnedMap.remove(keyValue+"_child_name");
					}
				}
			}
			else if(childURIs == null){
				returnedMap.remove(keyValue+"_child_uri");
				returnedMap.remove(keyValue+"_child_name");
			}
		}
		return returnedMap;
	}

	/**
	 * Method obtains the unique variable name for the given key.
	 * @param key  - the key to extract the unique variable from.
	 * @return	   - return the unique variable name.
	 */
	private String getVarKey(String key){
		return key.substring(0,key.indexOf("_"));
	}

	private boolean containsStructure(String structure) {
		structure = "http://brancusi1.usc.edu/brain_parts/"
			+ structure.replace(" ", "-").toLowerCase() + "/";

		if (masterList.containsKey(structure))
			return true;
		return false;
	}

	private boolean sendingStructure(String key) {

		if(key.substring(key.indexOf('_')+1).equalsIgnoreCase("send")){
			//System.out.println("is uri: "+key.substring(key.indexOf('_')+1)+"       key: "+key   );
			return true;
		}
		return false;
	}

	private boolean receivingStructure(String key) {
		//System.out.println("is uri: "+key.substring(key.indexOf('_')+1));
		if(key.substring(key.indexOf('_')+1).equalsIgnoreCase("rec")){
			//System.out.println("is uri: "+key.substring(key.indexOf('_')+1)+"       key: "+key   );
			return true;
		}
		return false;
	}
	private String getVarName(String key) {
		return key.substring(0, key.indexOf("_"));

	}

	/**
	 * Get BAMS "part of" results.  Use the BAMS "part of" graph to get a list
	 * of brain regions that are a part of the parameter list of brain regions.
	 * @param initialBamsNames - a list of brain regions names known
	 * 							to be in the BAMS system.
	 * @return - a multi hash map with key: [brain region name]_name
	 * 			 and value: set of (brain region URIs), where each brain region
	 *           URI is known in BAMS to be a part of the brain region that
	 *           is the key.
	 */
	private MultiHashMap<String,String> getBAMSPartOfResults(String[] initialBamsNames) {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		q.addPrefixMapping("bams_rdf", "<http://brancusi1.usc.edu/RDF/>");

		//loop over the brain regions.
		for (String brainRegion : initialBamsNames) {

			String uri = "http://brancusi1.usc.edu/brain_parts/" + brainRegion
			+ "/";
			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String nameVar = "$" + var + "_name";
			String uriVar = "$" + var + "_uri";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			// bind each URI to the name variable
			q.addQueryTriplet("<" + uri + ">" + " bams_rdf:name " + nameVar);
			//union those results
			q.addQueryTriplet(SparqlQuery.UNION);
			//bind the URI variable with each URI
			q.addQueryTriplet(uriVar + " bams_rdf:class1 " + "<" + uri + ">");
			//bind the child URI variable to those URIs that are the children
			//of the brain region in the URI variable
			q.addQueryTriplet(uriVar + " bams_rdf:class2 " + childUriVar +
					" FILTER regex(str("+childUriVar+"),\"^?[a-z]/$\") ");
			//bind the child name variable to the name given for the URI
			//stored in the child URI variable.
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

	/**
	 * Processing the results map and turning them into Node objects
	 * @param initialBamsNames
	 * @param results
	 * @return
	 */
	public Node[] convertPartOfResultsIntoNodes(String[] initialBamsNames,
			MultiHashMap<String,String> results) {
		List<Node> nodes = new ArrayList<Node>();
		String brainRegionPrettyName;
		Node n = null;
		for (String brainRegion: initialBamsNames) {

			String uri = "http://brancusi1.usc.edu/brain_parts/" + brainRegion + "/";

			String var = BrainRegionNameShortener.reduceName(brainRegion);
			String nameVar = "$" + var + "_name";
			String childUriVar = "$" + var + "_child_uri";
			String childNameVar = "$" + var + "_child_name";

			if(results.containsKey(nameVar)){
				brainRegionPrettyName = results.get(nameVar).iterator().next();

				if(brainRegionPrettyName.equalsIgnoreCase("midbrain-hindbrain, motor, extrapyramidal"))
					n = new Node(uri, brainRegionPrettyName.toLowerCase());
				else if(!containsNode(nodes,brainRegionPrettyName)){
					n = new Node(uri,brainRegionPrettyName);
				}
			}
			if(n!= null){
				nodes.add(n);

				Collection<String> childUris = results.get(childUriVar);
				Collection<String> childNames = results.get(childNameVar);

				// might not have children
				if (childUris != null) {
					Iterator<String> urisIt = childUris.iterator();
					Iterator<String> namesIt = childNames.iterator();

					ArrayList<Node> childrenNodes = new ArrayList<Node>();
					for (int i = 0; i < childUris.size(); i++) {
						String currentUri = urisIt.next();
						String currentName = namesIt.next();

						try {
							if(BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().containsKey(currentUri)){
								Node child = new Node(currentUri,currentName);
								child.setParent(n);
								childrenNodes.add(child);
								nodes.add(child);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					n.setPartOfNodes(childrenNodes);
				}
			}

		}
		Node[] out = new Node[nodes.size()];
		return (Node[]) nodes.toArray(out);
	}

	/**
	 * Method checks if a list of nodes contains a node with the given
	 * name.  This takes care of duplicates.
	 * @param nodes				- the list of nodes.
	 * @param brainRegionName	- the name of the node to look for.
	 * @return					- true if node is found and false otherwise.
	 */
	private boolean containsNode(List<Node> nodes, String brainRegionName){
		if(nodes.isEmpty())
			return false;
		else{
			for(Node currentNode: nodes){
				if(currentNode.getName().equalsIgnoreCase(brainRegionName)){
					System.out.println("match found: "+currentNode.getName()+"  string: "+brainRegionName);
					return true;
				}
			}
		}
		return false;
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

	private Node[] convertDeeperResultsIntoNodes(Node[] allNodes,Node[] nodes, MultiHashMap<String,String> results) {
		List<Node> nodesOut = new ArrayList<Node>();
		//System.out.println("Number of nodes: "+nodes.length);
		Iterator<String> namesIt = null;
		Iterator<String> urisIt = null;

		for (Node n : nodes) {
			try {
				if(BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().containsKey(n.getUri())){

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

					if(urisIt != null && namesIt != null){
						for (int i = 0; i < childUris.size(); i++) {
							String childURI = urisIt.next();
							String childName = namesIt.next();
							try {
								//need to consider this line of code.  By filtering the brain regions that only appear in the list
								//we are reducing the number of parts per parent node.
								if(BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().containsKey(childURI)){
									if(!findElement(allNodes,childName)){
										Node child = new Node(childURI, childName);
										child.setParent(n);
										childrenNodes.add(child);
										nodesOut.add(child);
									}
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					urisIt = null;
					namesIt = null;

					n.setPartOfNodes(childrenNodes);

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			//System.out.println("brainRegionName: "+brainRegion.getName());
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
	private Set<Node> mergeNodes(Node[] nodeSet1, Node[] nodeSet2) {
		List<Node> list1 = Arrays.asList(nodeSet1);
		List<Node> list2 = Arrays.asList(nodeSet2);
		//faster than iterating over both lists.
		return new HashSet<Node>(ListUtils.union(list1, list2));
	}

	private Set<String> findMissingNodeStrings(List<ConnectionEdge> edges, Set<Node> nodes) {
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

	public List<String> getInitialBamsURIs() {
		return initialBamsURIs;
	}

	/**
	 * Method adds new node to the graph.  If there is no graph
	 * it will create a new one.  If there is a graph present
	 * it will add the node as needed.
	 * @param newNodeName - the name of the brain region for
	 * 						the new node about to be created.
	 */
	public void addNewNode(String newNodeName){
		Node n;
		if(!GraphManager.getInstance().containsNodeWithName(newNodeName)){
			n = new Node("http://brancusi1.usc.edu/brain_parts/"+newNodeName+"/",newNodeName);
			//add the newly created node to the graph.
			GraphManager.getInstance().addNodeAndAnyConnectionEdges(n);
		}
	}
}
