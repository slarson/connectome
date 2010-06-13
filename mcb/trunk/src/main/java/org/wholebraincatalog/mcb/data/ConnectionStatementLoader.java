package org.wholebraincatalog.mcb.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebraincatalog.mcb.graph.Node;
import org.wholebraincatalog.mcb.util.SparqlQuery;

public class ConnectionStatementLoader {

	public ConnectionStatementLoader() {
		
	}
	
	public Node[] getNodes() {

		String sparql = "http://rdf-stage.neuinfo.org/sparql";
		SparqlQuery bamsReader = new SparqlQuery(sparql);
		
		String[] brainRegions = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_amygdala", "Substantia_nigra_compact_part",
				"Ventral_tegmental_area", "Prelimbic_area", 
				"Lateral_preoptic_area"};
		
		populate(bamsReader, brainRegions);
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.runSelectQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Node[] data = createNodesFromResults(brainRegions, results);
		return data;
	}
	
	public static void populate(SparqlQuery query, String[] brainRegionNames) {

		query.addPrefixMapping("nif_cnxn", "<http://connectivity.neuinfo.org#>");
		
		for (String brainRegionName : brainRegionNames){
			query.addQueryTriplet("$" + brainRegionName + 
					" nif_cnxn:sending_structure  \"" 
					+ brainRegionName.replace("_", " ")+"\"");
			query.addQueryTriplet("$" + brainRegionName + " nif_cnxn:projection_strength $"+ brainRegionName + "_strength");
			query.addQueryTriplet("$" + brainRegionName + " nif_cnxn:receiving_structure $" + brainRegionName +"_receiving");
			query.addQueryTriplet("$" + brainRegionName + " nif_cnxn:reference $"+ brainRegionName +"_reference");
			
			query.addSelectVariable("$"+ brainRegionName + "_strength");
			query.addSelectVariable("$"+ brainRegionName + "_receiving");
			query.addSelectVariable("$"+ brainRegionName + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				query.addQueryTriplet("} UNION {");
			}
		}
	}

	/**
	 * Populate a data reader for BAMS data.
	 * @param query - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	private static void populateBamsDataReader(SparqlQuery query, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			query.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#" + brainRegionName+">");
			query.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#projection_Strength> $"+ brainRegionName + "_strength");
			query.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $" + brainRegionName +"_receiving");
			query.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#reference> $"+ brainRegionName +"_reference");
			
			query.addSelectVariable("$"+ brainRegionName + "_strength");
			query.addSelectVariable("$"+ brainRegionName + "_receiving");
			query.addSelectVariable("$"+ brainRegionName + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				query.addQueryTriplet("} UNION {");
			}
		}
	}
	
	/**
	 * Create Node elements from the results of getting info from the BAMS 
	 * brain regions
	 * @param brainRegions
	 * @param results
	 * @return
	 */
	public static Node[] createNodesFromResults(String[] brainRegions, 
			MultiHashMap<String, String> results) {
		List<Node> nodeList = new ArrayList<Node>();
		for (String brainRegion : brainRegions) {
			Node n = new Node(brainRegion);
			n.store(results.get("$"+ brainRegion + "_receiving"), 
					results.get("$"+ brainRegion + "_strength"));
			n.addReference(results.get("$"+ brainRegion + "_receiving"), 
					results.get("$"+ brainRegion + "_reference"));
			nodeList.add(n);
		}
		Node[] nodes = new Node[nodeList.size()];
		return nodeList.toArray(nodes);
	}
	
}
