
import java.util.Iterator;

import org.wholebrainproject.wbc.util.exception.WBCException;

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
 * limitations under the License
 * @date February 22, 2010
 * @author Ruggero Carloz
 * @version 0.0.1
 */
public class Reader{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Node[] data = new Node[3];
		
		//obtain the data from the URLs
		DataReader sCaudoputamen = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Caudoputamen%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A","Caudoputamen");
		DataReader sGlobusPallidus = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Globus_pallidus%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A","Globus pallidus");
		DataReader sCentralNucleusOfAmygdala = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Central_nucleus_of_amygdala%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A", "Central nucleus of amygdala");
		
		data[0] = sCaudoputamen.getNode();
		data[1] = sGlobusPallidus.getNode();
		data[2] = sCentralNucleusOfAmygdala.getNode();
		
		//connect the nodes and build the graph.
        BuildConnections connections = new BuildConnections(data,3);
		
       
	}

}
