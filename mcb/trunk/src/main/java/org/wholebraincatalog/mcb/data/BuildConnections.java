package org.wholebraincatalog.mcb.data;
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


import java.awt.List;
import java.io.InputStream;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebraincatalog.mcb.graph.ConnectionEdge;
import org.wholebraincatalog.mcb.graph.GraphManager;
import org.wholebraincatalog.mcb.graph.Node;
import org.wholebraincatalog.mcb.graph.PartOfEdge;
import org.wholebraincatalog.mcb.util.SparqlQuery;

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


	public static void getDataAndCreateGraph(Graph graph) {
		try {
			String sparqlNif = "http://rdf-stage.neuinfo.org/sparql";
			SparqlQuery bamsReader = new SparqlQuery(sparqlNif);

			String sparqlTalis = "http://api.talis.com/stores/neurolex/services/sparql";
			SparqlQuery cellReader = new SparqlQuery(sparqlTalis);
			
			SparqlQuery brainRegionReader = new SparqlQuery(sparqlTalis);

			String[] brainRegions = { "Globus_pallidus", "Caudoputamen",
					"Central_nucleus_of_amygdala",
					"Substantia_nigra_compact_part", "Ventral_tegmental_area",
					"Prelimbic_area", "Lateral_preoptic_area" };

			String[] brainRegionsCellData = { "Globus_pallidus",
					"Caudoputamen", "Central_nucleus_of_amygdala",
					"Substantia_nigra_pars_compacta", "Ventral_tegmental_area" };

			ConnectionStatementLoader.populate(bamsReader,
					brainRegions);
			CellDataLoader.populate(cellReader,
					brainRegionsCellData);
			BrainRegionDataLoader.populate(brainRegionReader,
					brainRegionsCellData);

			MultiHashMap<String, String> results = bamsReader.runSelectQuery();
			MultiHashMap<String, String> cellResults = cellReader.runSelectQuery();
			MultiHashMap<String, String> brainRegionResults = brainRegionReader.runSelectQuery();
			
			Node[] data = ConnectionStatementLoader.createNodesFromResults(
					brainRegions, results);
			CellDataLoader.storeData(data, cellResults);
			BrainRegionDataLoader.storeData(data, brainRegionResults);

			makeConnections(graph, data);

			for (String key : cellResults.keySet()) {
				System.out.println("key: " + key + ", results: "
						+ cellResults.get(key));
			}

		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}

	}
	/**
	 *  Method creates graph by building connections between nodes.
	 *  @param nodes - array containing the nodes used to make graph.
	 *  @param numberElements - number of nodes to be connected.
	 *  @author Ruggero Carloz
	 */
	private static void makeConnections(Graph graph, Node[] node) {

		for (int i = 0; i < node.length ; i++) {
			for (int j = 0; j < node.length; j++) {
				if (node[i].getRegionToStrengthMap().get(node[j].getVertexName().replace('_', ' ')) != null) {
					String strength = node[i].getRegionToStrengthMap().get(
							node[j].getVertexName().replace('_', ' '));
					String reference = node[i].getReferenceSet().get(
							node[j].getVertexName().replace('_', ' '));
					ConnectionEdge e = new ConnectionEdge(strength, reference);
					graph.addEdge(e, node[i], 
							node[j],
							EdgeType.DIRECTED);
				}
			}
		}	
		subNodesConnection(graph,node);
	}

	private static void subNodesConnection(Graph graph, Node[] node){
		Vector<String> repeats = new Vector<String>();
		
		for(int i = 0; i < node.length; i++){
			if(!node[i].getPartOf().isEmpty()){
				for(String partOf: node[i].getPartOf()){
					if(!repeats.contains(partOf)){
						Node subNode = new Node(partOf);
						graph.addEdge(new PartOfEdge(),
								subNode,node[i], EdgeType.DIRECTED);
						repeats.add(partOf);
					}	
				}
			}
		}

	}

}


