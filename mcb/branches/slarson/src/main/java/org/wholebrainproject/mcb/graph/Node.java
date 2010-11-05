package org.wholebrainproject.mcb.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections15.Factory;
import org.wholebrainproject.mcb.data.BAMSToNeurolexData;
import org.wholebrainproject.mcb.data.BAMSToNeurolexMap;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

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
 * The class is used to create the vertices for the graph.  This class is implemented 
 * by class Multi-Scale Connectome Browser.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.1
 */
public class Node implements Factory{

	/**
	 * Name of vertex.
	 */
	private String name = null;

	/**
	 * String containing the pages URIs.
	 */
	private TreeSet<String> uris;

	/**
	 * The URI for this object
	 */
	private String uri = null;

	private HashMap<String,String> regionToStrength;

	private HashMap<String, String> referenceMap;

	List<String> cells = null;
	List<String> cellUrls = null;
	List<String> neurotransmitters = null;
	List<String> roles = null;
	Collection<Node> partOf = null;

	private String reference = null;

	/**
	 * Indicates if this node has been collapsed
	 */
	private boolean collapsed = true;

	/**
	 * Indicates if this has had its part of nodes added to the graph.
	 */
	private boolean partOfsAdded = false;

	private Node parent = null;

	/**
	 * Constructor.
	 * @param uri - the URI that can be used to query for this brain region
	 * @param vertexName - name of node.
	 **/
	public Node(String uri, String vertexName){
		this.name = vertexName;
		this.uri = uri;
		createURITreeSet();
		createRegionToStrengthMap();
		createReferenceMap();
	}

	public void setCellInfo(Collection<String> cells, Collection<String> cellUrls, 
			Collection<String> neurotransmitters, Collection<String> roles) {
		this.cells = new ArrayList<String>();
		if (cells != null) this.cells.addAll(cells);
		this.cellUrls = new ArrayList<String>();
		if (cellUrls != null) this.cellUrls.addAll(cellUrls);
		this.neurotransmitters = new ArrayList<String>();
		if (neurotransmitters != null) this.neurotransmitters.addAll(neurotransmitters);
		this.roles = new ArrayList<String>();
		if (roles != null) this.roles.addAll(roles);

	}


	public void setPartOfNodes(Collection<Node> partOfNodes) {
		this.partOf = partOfNodes;
	}

	public int getUniqueCellCount() {
		return new HashSet<String>(this.cells).size();
	}


	public int getCellCount() {
		if (this.cells != null) {
			return this.cells.size();
		}
		return 0;
	}

	public String getCellName(int index) {
		return this.cells.get(index);
	}

	public String getCellUrl(int index) {
		return this.cellUrls.get(index);
	}

	/**
	 * Returns an http:// dereferencable URL that can be used to get more
	 * information about this node.
	 * @return
	 * @throws IOException 
	 */
	public String getMoreDetailURL()  {
		String NeurolexUri = null;
		//System.out.println(this.uri);
		try{
			if(BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().containsKey(uri) ){
				for(BAMSToNeurolexData data: BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().get(uri)){
					return data.getNeurolexPage();	
				}
			}
			else{
				System.out.println("The URI you are requesting is not present in the data.");
			}
		}
		catch(IOException e){
			System.err.println("Error: Class Node method getMoreDetailURL.");
		}
		return "http://neurolex.org/wiki/Category:" + this.name.toLowerCase();
	}

	public String getNeurotransmitter(int index) {
		return this.neurotransmitters.get(index);
	}

	public String getRole(int index) {
		return this.roles.get(index);
	}

	public Collection<Node> getPartOfNodes(){
		return this.partOf;
	}

	/**
	 * Get the Nodes that point into this Node via a PartOfEdge from the graph.
	 * @param graph - the graph where this node has been added
	 * @return - all the nodes that point into this node
	 */
	@SuppressWarnings("unchecked")
	public Collection<Node> getPartOfNodes(Graph<Node,Edge> graph) {
		List<Node> out = new ArrayList<Node>();	
		if(graph.getInEdges(this)== null)
			return out;
		for (Edge e : graph.getInEdges(this)) {	
			if (e instanceof PartOfEdge) {				
				Pair<Node> p = graph.getEndpoints(e);
				out.add(p.getFirst());
			}
		}
		return out;
	}

	/**
	 * For this node, return a tree graph that hangs under
	 * this node that can be used to display part-of relations
	 * and cells that are associated with this node.  Makes
	 * this node the root of the tree.
	 * @return
	 */
	public Tree<Node, Edge> getPartOfTree(Graph<Node,Edge> graph) {
		Tree<Node,Edge> treeGraph = 
			new DelegateTree<Node,Edge>();

		treeGraph.addVertex(this);
		for (Node n : getPartOfNodes(graph)) {
			Collection<Edge> edges = graph.getOutEdges(n);
			//assuming only one edge
			Edge e = edges.iterator().next();
			treeGraph.addEdge(e, this, n);
		}

		return treeGraph;
	}

	/**
	 * Create and add the nodes that are listed as part of this Node.
	 * Also perform {@link #setParent(Node)} on these nodes.
	 * @param graph - the graph to use to add the nodes to.
	 */
	public void addPartOfNodes(Graph<Node, Edge> graph) {
		if (partOfsAdded == false) {
			for (Node partOf : getPartOfNodes()) {
				Node subNode = partOf;
				subNode.setParent(this);
				graph.addEdge(new PartOfEdge(), subNode, this,
						EdgeType.DIRECTED);
			}
			partOfsAdded = true;
		}
	}

	/**
	 * This method instantiates the tree to store URIs.
	 */
	public void createURITreeSet(){
		uris = new TreeSet<String>();
	}

	/**
	 * Initializes a map that stores the strength of the 
	 * connection from this node to target brain regions
	 */
	private void createRegionToStrengthMap(){
		regionToStrength = new HashMap<String,String>();
	}

	private void createReferenceMap(){
		referenceMap = new HashMap<String, String>();
	}
	/**
	 * Stores the strength of the connection to the target brain region
	 * @param targetBrainRegion - the name of the region that this node projects to
	 * @param strength - the strength of the projection to that region
	 */
	public void store(String targetBrainRegion, String strength){
		regionToStrength.put(targetBrainRegion, strength);
		uris.add(targetBrainRegion);
	}

	public void store(Collection<String> targetBrainRegions, Collection<String> strengths) {
		if (targetBrainRegions == null || strengths == null) {
			throw new IllegalArgumentException("Can't pass null arguments!");
		}
		if (targetBrainRegions.size() != strengths.size()) {
			throw new IllegalArgumentException("Can't store lists of different" +
					" size! targetBrainRegions size: " + targetBrainRegions.size() + 
					", strengths size: " + strengths.size());
		}

		List<String> targetBrainRegionsList = new ArrayList<String>();
		targetBrainRegionsList.addAll(targetBrainRegions);
		List<String> strengthsList = new ArrayList<String>();
		strengthsList.addAll(strengths);

		for (int i = 0; i < targetBrainRegions.size(); i++) {
			store(targetBrainRegionsList.get(i), strengthsList.get(i));
		}
	}



	/**
	 * This method returns the number of connections in a node.
	 * @return numberOfConnections - number of connections in node.
	 */
	public int getNumberOfConnections(){
		return regionToStrength.keySet().size();
	}

	/**
	 * This method returns a TreeSet containing the name for receiving
	 * nodes.
	 * @return uris - TreeSet. 
	 */
	public TreeSet<String> getTree(){
		return uris;
	}

	/**
	 * Returns a map between the brain regions that this node
	 * targets and the strength values for those connections
	 * @return
	 */
	public HashMap<String,String> getRegionToStrengthMap(){
		return regionToStrength;
	}

	public HashMap<String,String> getReferenceSet(){
		return referenceMap;
	}


	/**
	 * This method returns the current node.
	 * @return this - current node.
	 */
	public Node getNode(){
		return this;
	}

	/**
	 * This method gives the name of node.
	 * @return vertexName -  name of current node.
	 */
	public String getName() {
		return this.name.replace('_', ' ');
	}

	public boolean isCollapsed() {
		return this.collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	/**
	 * Method return name of node used as default.
	 * @return name - name of node
	 */
	public String toString() {
		return this.name ;
	}

	public void addReference(String node, String reference){
		referenceMap.put(node, reference);
	}

	public void addReference(Collection<String> node, Collection<String> reference) {
		if (node == null || reference == null) {
			throw new IllegalArgumentException("Can't pass null arguments!");
		}
		if (node.size() != reference.size()) {
			throw new IllegalArgumentException("Can't store lists of different" +
					" size! nodes size: " + node.size() + 
					", references size: " + reference.size());
		}

		List<String> nodesList = new ArrayList<String>();
		nodesList.addAll(node);
		List<String> referencesList = new ArrayList<String>();
		referencesList.addAll(reference);

		for (int i = 0; i < nodesList.size(); i++) {
			addReference(nodesList.get(i), referencesList.get(i));
		}
	}

	public Object create() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProjectingCellsRoleString() {
		boolean inhibitory = false;
		boolean excitatory = false;
		for (String role : this.roles) {
			if (role.contains("Inhibitory")) inhibitory = true;
			if (role.contains("Excitatory")) excitatory = true;
		}
		if (inhibitory && excitatory) return "excitatory and inhibitory";
		if (inhibitory) return "inhibitory";
		if (excitatory) return "excitatory";
		return "";
	}

	public String getProjectingCellsRoleAbbrevString() {
		boolean inhibitory = false;
		boolean excitatory = false;
		for (String role : this.roles) {
			if (role.contains("Inhibitory")) inhibitory = true;
			if (role.contains("Excitatory")) excitatory = true;
		}
		if (inhibitory && excitatory) return "+/-";
		if (inhibitory) return "-";
		if (excitatory) return "+";
		return "";
	}

	public void setParent(Node n) {
		this.parent = n;
	}

	public Node getParent() {
		return this.parent;
	}

	public boolean hasPartOfParent() {
		return (getParent() != null);
	}

	public boolean hasParts() {
		return (getPartOfNodes() != null && getPartOfNodes().isEmpty() == false);
	}

	public String getUri() {
		return this.uri;
	}



}
