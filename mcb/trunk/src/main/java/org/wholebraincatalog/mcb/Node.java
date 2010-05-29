package org.wholebraincatalog.mcb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.multimap.MultiHashMap;

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
	private String name;
	
	/**
	 * String containing the pages URIs.
	 */
	private TreeSet<String> uris;

	private HashMap<String,String> regionToStrength;
	
	private HashMap<String, String> referenceMap;
	
	List<String> cells = null;
	List<String> cellUrls = null;
	List<String> neurotransmitters = null;
	List<String> roles = null;
	List<String> partOf = null;
	
	private String reference = null;
	
	/**
	 * Constructor.
	 * @param vertexName - name of node.
	 **/
	public Node(String vertexName){
		this.name = vertexName;
		createURITreeSet();
		createRegionToStrengthMap();
		createReferenceMap();
	}
	
	public void setCellInfo(Collection<String> cells, Collection<String> cellUrls, 
			Collection<String> neurotransmitters, Collection<String> roles, Collection<String> partOf) {
		this.cells = new ArrayList<String>();
		if (cells != null) this.cells.addAll(cells);
		this.cellUrls = new ArrayList<String>();
		if (cellUrls != null) this.cellUrls.addAll(cellUrls);
		this.neurotransmitters = new ArrayList<String>();
		if (neurotransmitters != null) this.neurotransmitters.addAll(neurotransmitters);
		this.roles = new ArrayList<String>();
		if (roles != null) this.roles.addAll(roles);
		this.partOf = new ArrayList<String>();
		if(partOf != null) this.partOf.addAll(partOf);
	}
	
	public int getCellCount() {
		return this.cells.size();
	}
	
	public String getCellName(int index) {
		return this.cells.get(index);
	}
	
	public String getCellUrl(int index) {
		return this.cellUrls.get(index);
	}
	
	public String getNeurotransmitter(int index) {
		return this.neurotransmitters.get(index);
	}
	
	public String getRole(int index) {
		return this.roles.get(index);
	}
	
	public String getPartOf(int index){
		return this.partOf.get(index);
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
	public String getVertexName() {
		return this.name;
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
	
	/**
	 * Method creates a new node.  Method not used in this 
	 * implementation.
	 * @see org.apache.commons.collections15.Factory#create()
	 */
	public Object create() {
		return new Node("");
	}

}
