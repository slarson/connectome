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

		String[] brainRegions = {"Cerebral_cortex", "Thalamus", 
				"Brainstem","Basal_ganglia"};
		String[] brainRegionsCellData = { "Globus_pallidus",
				"Caudoputamen", "Central_nucleus_of_the_amygdala",
				"Substantia_nigra_pars_compacta", "Ventral_tegmental_area" };

		populate(bamsReader, brainRegions);

		MultiHashMap<String, String> results = null;


		try {
			results = bamsReader.runSelectQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Node[] data = createNodesFromResults(brainRegions, results,brainRegionsCellData);
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
			MultiHashMap<String, String> results,
			String[] neuroLexData) {
		String neuroLex = null;
		Node n;
		List<Node> nodeList = new ArrayList<Node>();
		for (String brainRegion : brainRegions) {
			for(String neuroName: neuroLexData){
				System.out.println("current brainRegion: "+brainRegion);
				if(strringIsFormated(brainRegion,neuroName)){
					//System.out.println("neuroName: "+neuroName);
					neuroLex = neuroName;
					break;
				}					
			}		
			
			if(neuroLex == null){
				n = new Node(brainRegion,brainRegion);
			}
			else{
				n = new Node(brainRegion,neuroLex);
				neuroLex = null;
			}	
			
			n.store(results.get("$"+ brainRegion+ "_receiving"), 
					results.get("$"+ brainRegion + "_strength"));
			n.addReference(results.get("$"+ brainRegion + "_receiving"), 
					results.get("$"+ brainRegion + "_reference"));
			nodeList.add(n);

		}
		Node[] nodes = new Node[nodeList.size()];
		return nodeList.toArray(nodes);
	}

	private static boolean strringIsFormated(String brainRegion,
			String neuroName) {
		
		int endIndex = getIndex(brainRegion, neuroName);
		
		String brainRegionSubString = brainRegion.substring(0, endIndex);
		String neurolexSubString = neuroName.substring(0, endIndex);
		
		System.out.println("brainRegionSubString: "+brainRegionSubString);
		System.out.println("neurolexSubString: "+neurolexSubString);
		return brainRegionSubString.equals(neurolexSubString);	
	}

	private static int getIndex(String brainRegion, String neuroName) {
		if(brainRegion.length() < neuroName.length())
			return brainRegion.length()/2;
		else
			return neuroName.length()/2;
		
	}

}
