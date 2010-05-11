package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class CellDataLoader {

	public CellDataLoader() {
		
	}
	
	public Node[] getNodes() {
		String sparql = "http://api.talis.com/stores/neurolex/services/sparql";
		DataReaderBetter cellReader = new DataReaderBetter(sparql);
		
		String[] brainRegions = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_the_amygdala", "Substantia_nigra_pars_compacta",
				"Ventral_tegmental_area", "Limbic_lobe", 
				"Lateral_preoptic_nucleus"};
		
		populateCellDataReader(cellReader, brainRegions);
				
		InputStream queryResult = cellReader.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = cellReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Node[] data = createNodesFromResults(brainRegions, results);
		return data;
	}
	

	/**
	 * Populate a data reader for BAMS data.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	private static void populateCellDataReader(DataReaderBetter drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("?" + brainRegionName + "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> " +
					"<http://neurolex.org/wiki/Special:URIResolver/Category-3A" + brainRegionName+"> ");
			
			drb.addSelectVariable("$"+ brainRegionName + "_cells");
			
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
	private static Node[] createNodesFromResults(String[] brainRegions, 
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
