package org.wholebraincatalog.mcb;

import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.collections15.Factory;

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

	private HashMap<String,String> keySet;
	
	private HashMap<String, String> referenceMap;
	
	private HashMap<String,CellData> nodeCells;
	
	/**
	 * URL that contains the node sending connections.
	 */
	private String URL = null;
	
	private String reference = null;
	/**
	 * Number of possible connections for node.
	 */
	private int numberOfConnections;
	

	/**
	 * Constructor.
	 * @param URL - the url of a given node.
	 * @param vertexName - name of node.
	 * @param elementNum - the number of URIs in the node.
	 **/
	public Node(String URL, String vertexName, int elementNum){
		this.URL = URL;
		this.name = vertexName;
		this.numberOfConnections = elementNum;
		createURITreeSet();
		createHashMap();
		createReferenceMap();
		createNodeCellsMap();
	}

	private void createNodeCellsMap(){
		nodeCells = new HashMap<String,CellData>();
	}

	/**
	 * Method returns the URL for the node.
	 * @return URL
	 */
	public String getURL(){
		return this.URL;
	}
	
	/**
	 * This method instantiates the tree to store URIs.
	 */
	public void createURITreeSet(){
		uris = new TreeSet<String>();
	}
	
	private void createHashMap(){
		keySet = new HashMap<String,String>();
	}
	
	private void createReferenceMap(){
		referenceMap = new HashMap<String, String>();
	}
	/**
	 * This method stores data in binary tree.
	 * @param str -  data to be stored in tree.
	 */
	public void store(String brainRegion, String strength){
		keySet.put(brainRegion, strength);
		uris.add(brainRegion);
	}
	
	/**
	 * This method returns the number of connections in a node.
	 * @return numberOfConnections - number of connections in node.
	 */
	public int getNumberOfConnections(){
		return numberOfConnections;
	}
	
	/**
	 * This method returns a TreeSet containing the name for receiving
	 * nodes.
	 * @return uris - TreeSet. 
	 */
	public TreeSet<String> getTree(){
		return uris;
	}
	
	public HashMap<String,String> getKeySet(){
		return keySet;
	}
	
	public HashMap<String,String> getReferenceSet(){
		return referenceMap;
	}
	
	public HashMap<String,CellData> getNodeCellsMap(){
		return nodeCells;
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
	/**
	 * Method creates a new node.  Method not used in this 
	 * implementation.
	 * @see org.apache.commons.collections15.Factory#create()
	 */
	public Object create() {
		// TODO Auto-generated method stub
		return new Node("W","",3);
	}
}
