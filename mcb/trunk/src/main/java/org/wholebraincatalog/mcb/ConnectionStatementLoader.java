package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;

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
		
		populateNIFDataReader(bamsReader, brainRegions);
				
		InputStream queryResult = bamsReader.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Node[] data = createNodesFromResults(brainRegions, results);
		return data;
	}
	
	static void populateNIFDataReader(SparqlQuery drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://connectivity.neuinfo.org#sending_structure>  \"" 
					+ brainRegionName.replace("_", " ")+"\"");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#projection_strength> $"+ brainRegionName + "_strength");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#receiving_structure> $" + brainRegionName +"_receiving");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#reference> $"+ brainRegionName +"_reference");
			
			drb.addSelectVariable("$"+ brainRegionName + "_strength");
			drb.addSelectVariable("$"+ brainRegionName + "_receiving");
			drb.addSelectVariable("$"+ brainRegionName + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
		}
	}

	/**
	 * Populate a data reader for BAMS data.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	private static void populateBamsDataReader(SparqlQuery drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#" + brainRegionName+">");
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#projection_Strength> $"+ brainRegionName + "_strength");
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $" + brainRegionName +"_receiving");
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://ncmir.ucsd.edu/BAMS#reference> $"+ brainRegionName +"_reference");
			
			drb.addSelectVariable("$"+ brainRegionName + "_strength");
			drb.addSelectVariable("$"+ brainRegionName + "_receiving");
			drb.addSelectVariable("$"+ brainRegionName + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
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
	static Node[] createNodesFromResults(String[] brainRegions, 
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
