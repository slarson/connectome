package org.wholebraincatalog.mcb;

import java.util.EnumSet;

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
public class ConnectionEdge implements Factory {
	
	enum STRENGTH {
		NOT_PRESENT("not present"),
		NOT_CLEAR("not clear"),
		EXISTS("exists"),
		VERY_LIGHT("very light"),
		LIGHT("light"),
		MODERATE("moderate"),
		STRONG_MODERATE("moderate/strong"),
		STRONG("strong");
		
		String text; 
        
        STRENGTH(String msg) {
                this.text = msg;
        }
        
        public String toString() {
                return this.text;
        }
        
        public static STRENGTH myValueOf(Object s) {
                //return MENU_STRING based on its toString() value
                for (STRENGTH ms : EnumSet.allOf(STRENGTH.class)) {
                        if (ms.toString().equals(s)) {
                                return ms;
                        }
                }
                return null;
        }
	}
	
	/**
	 * Possible edge data
	 */
	private STRENGTH strength;

	private String reference;
	/**
	 * Default constructor
	 */
	public ConnectionEdge(String strength, String reference) {
		this.strength = STRENGTH.myValueOf(strength);
		this.reference = reference;
	}

	
	/**
	 * Method makes the edges value printable.
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return this.strength.toString();
	}
	
	public STRENGTH getStrength() {
		return this.strength;
	}
	
	public String getReference(){
		return this.reference;
	}
	 /**
	  * Possible method implemented to create an edge.  Method not 
	  * used in this implementation.
	  * @see org.apache.commons.collections15.Factory#create()
	  */
	public Object create() {
		// TODO Auto-generated method stub
		return new ConnectionEdge("s","");
	}
}