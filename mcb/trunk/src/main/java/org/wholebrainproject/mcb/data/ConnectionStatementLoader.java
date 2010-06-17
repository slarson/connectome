package org.wholebrainproject.mcb.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.SparqlQuery;

public class ConnectionStatementLoader {

	public ConnectionStatementLoader() {
		
	}
	
	public Node[] getNodes() {

		String sparql = "http://rdf-stage.neuinfo.org/sparql";
		SparqlQuery bamsReader = new SparqlQuery(sparql);
		
		/**
		String[] brainRegions = {"Cerebral_cortex", "Thalamus", 
				"Brainstem","Basal_ganglia"};
		**/
		String[] brainRegionsCellData = { "Interbrain",
				"Epithalamus", "Dorsal_thalamus","Ventral_thalamus",
				"bed_nucleus_of_the_accessory_olfactory_tract",
				"Pallidum","basal_nucleus",
				"Interstitial_nucleus_of_the_posterior_limb_of_the_anterior_commissure",
				"Striatum"};
		MultiHashMap<String, String> brainRegionResults = bamsReader.runSelectQuery();
		Node[] data = ConnectionStatementLoader.createNodesFromResults(
				brainRegionsCellData, brainRegionResults);
		populate(bamsReader, data);
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.runSelectQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Node[] dataFinal = createNodesFromResults(brainRegionsCellData, results);
		return dataFinal;
	}
	
	public static void populate(SparqlQuery query, Node[] brainRegionNames) {
		String[] brainRegionsCellData = { "Brainstem","Thalamus",
				"Cerebral_cortex","Basal_ganglia" };
		
		query.addPrefixMapping("nif_cnxn", "<http://connectivity.neuinfo.org#>");
		
		for (Node brainRegionName : brainRegionNames){
			System.out.println("brainRegionName: "+brainRegionName);
			query.addQueryTriplet("$" + brainRegionName.toString() + 
					" nif_cnxn:sending_structure  \"" 
					+ brainRegionName.toString().replace("_", " ")+"\"");
			query.addQueryTriplet("$" + brainRegionName.toString() + " nif_cnxn:projection_strength $"+ brainRegionName.toString() + "_strength");
			query.addQueryTriplet("$" + brainRegionName.toString() + " nif_cnxn:receiving_structure $" + brainRegionName.toString() +"_receiving");
			query.addQueryTriplet("$" + brainRegionName.toString() + " nif_cnxn:reference $"+ brainRegionName.toString() +"_reference");
			
			query.addSelectVariable("$"+ brainRegionName.toString() + "_strength");
			query.addSelectVariable("$"+ brainRegionName.toString() + "_receiving");
			query.addSelectVariable("$"+ brainRegionName.toString() + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.toString().equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
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
			System.out.println("brainRegionName: "+brainRegionName);
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
			System.out.println("brainRegion value: "+brainRegion);
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
	public static void createNodesFromResultsData(Node[] data,
			MultiHashMap<String, String> results){
		int i = 0;
		int length = data.length;
		for(String name: results.values()){
			if(i >= length)
				break;
			Node node = data[i];
			System.out.println("node.toString(): "+name);
			node.store(results.get("$"+ name + "_receiving"), 
					results.get("$"+ name + "_strength"));
			node.addReference(results.get("$"+ name + "_receiving"), 
					results.get("$"+ name + "_reference"));
		}
		
	}
	/**
	 * Create Node elements from the results of getting info from the BAMS 
	 * brain regions
	 * @param brainRegions
	 * @param results
	 * @return
	 */
	public static Node[] createNodesFromResultsNeurolex(String[] brainRegions, 
			MultiHashMap<String, String> results) {
		List<Node> nodeList = new ArrayList<Node>();
		for (String brainRegion : results.values()) {
			brainRegion = brainRegion.replace(", ", "-");
			brainRegion = brainRegion.replace(" [", "-");
			brainRegion = brainRegion.replace(' ','-');
			brainRegion = brainRegion.replace(']',' ');
			if(brainRegion.contains("("))
				brainRegion = brainRegion.replace("(", "-");
			brainRegion = brainRegion.substring(0, brainRegion.length()-1);
			System.out.println("brainRegion value: "+brainRegion);
			Node n = new Node(brainRegion);
			nodeList.add(n);
		}
		
		Node[] nodes = new Node[nodeList.size()];
		return nodeList.toArray(nodes);
	}
	
	public static void createNodesFromResultsNeuro(Node[] data, 
			MultiHashMap<String, String> results) {
		List<Node> nodeList = new ArrayList<Node>();
		int length = data.length;
		int i = 0;
		for (String brainRegion : results.keySet()) {
			System.out.println("brainRegion value: "+brainRegion);
			data[i].store(results.get(brainRegion + "_receiving"), 
					results.get(brainRegion + "_strength"));
			data[i].addReference(results.get(brainRegion + "_receiving"), 
					results.get(brainRegion + "_reference"));
			i++;
		}
		
	}
}
