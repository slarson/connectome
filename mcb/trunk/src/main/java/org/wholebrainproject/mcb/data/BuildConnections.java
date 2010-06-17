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


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.ConnectionEdge;
import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.Node;
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
			NeuroLexDataLoader.populate(cellReader,
					brainRegionsCellData);
			BrainRegionDataLoader.populate(brainRegionReader,
					brainRegionsCellData);

			MultiHashMap<String, String> results = bamsReader.runSelectQuery();
			MultiHashMap<String, String> cellResults = cellReader.runSelectQuery();
			MultiHashMap<String, String> brainRegionResults = brainRegionReader.runSelectQuery();
			
			Node[] data = ConnectionStatementLoader.createNodesFromResults(
					brainRegions, results);
			NeuroLexDataLoader.storeData(data, cellResults);
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
				Node x = node[i];
				Node y = node[j];
				if (x.getRegionToStrengthMap().get(y.getName()) != null) {
					String strength = x.getRegionToStrengthMap().get(y.getName());
					String reference = x.getReferenceSet().get(y.getName());
					ConnectionEdge e = new ConnectionEdge(strength, reference);

					graph.addEdge(e, x, y, EdgeType.DIRECTED);
				}
			}
		}	
		subNodesConnection(graph,node);
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
		Vector<String> repeats = new Vector<String>();
		
		for(int i = 0; i < node.length; i++){
			if(!node[i].getPartOf().isEmpty()){
				for(String partOf: node[i].getPartOf()){
					if(!repeats.contains(partOf)){
						node[i].addPartOfNodes(graph);
						repeats.add(partOf);
					}	
				}
			}
		}

	}

}


