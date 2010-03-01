package org.wholebraincatalog.mcb;

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
 * The class is used to create the edges for the graph.  This class is implemented 
 * by Multi-Scale Connectome Browser.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.1
 */
public class Edge implements Factory{
	/**
	 * Possible edge data
	 */
	private String name = " ";

	/**
	 * Default constructor
	 */
	public Edge() {}

	/**
	 * Constructor
	 */
	public Edge(Node node1, Node node2){
		
	}
	
	/**
	 * Method makes the edges value printable.
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}
	
	 /**
	  * Possible method implemented to create an edge.  Method not 
	  * used in this implementation.
	  * @see org.apache.commons.collections15.Factory#create()
	  */
	public Object create() {
		// TODO Auto-generated method stub
		return new Edge();
	}
}