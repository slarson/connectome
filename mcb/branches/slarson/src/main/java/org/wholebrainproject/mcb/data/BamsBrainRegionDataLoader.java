package org.wholebrainproject.mcb.data;

import java.util.Collection;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BrainRegionNameShortener;
import org.wholebrainproject.mcb.util.SparqlQuery;


public class BamsBrainRegionDataLoader {
	/**
	 * TARGET QUERY FOR A SINGLE BRAIN REGION (e.g. Globus Pallidus):
	 * select DISTINCT $gp_cl $gp_cu $gp_nl $gp_trl $gp_ipo 
	 * {$gp_r <http://semantic-mediawiki.org/swivt/1.0#page> 
	 * <http://neurolex.org/wiki/Category:Globus_pallidus> . 
	 * $gp_p <http://neurolex.org/wiki/Special:URIResolver/Property-3AIs_part_of> $gp_r . 
	 * $gp_p <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $gp_ipo }
	 */

	/**
	 * This class takes care of getting the cell data for each node 
	 * and incorporating the data into the nodes.
	 * @param query - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	public static void populate(SparqlQuery query, String[] brainRegionNames) {

		String region_suffix = "_r";
		String part_suffix = "_p";
		String[] brainRegionsCellData = { "Cortex",
				"brainstem", "thalamus","basal-ganglia" };
		
		String brainRegionSufixName = null;

		//query.addPrefixMapping("swivt", "<http://semantic-mediawiki.org/swivt/1.0#>");
		//query.addPrefixMapping("nlx_prop", "<http://neurolex.org/wiki/Special:URIResolver/Property-3A>");

		for(String RegionName : brainRegionNames){

			if(brainRegionSufixName == null)
				brainRegionSufixName =  BrainRegionNameShortener.reduceName(RegionName);

			query.addQueryTriplet("$a"+" <http://brancusi1.usc.edu/RDF/class1> "+
					RegionName);

			query.addQueryTriplet("$a"+" <http://brancusi1.usc.edu/RDF/class2> "+
					"$childStructure");

			query.addQueryTriplet("$childStructure"+
					" <http://brancusi1.usc.edu/RDF/name> " +
					"$childStructureName"+RegionName );

			query.addSelectVariable("$childStructureName"+RegionName);

			//add union between all sets of variables except the last
			if (RegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				query.addQueryTriplet("} UNION {");
			}
			brainRegionSufixName = null;
		}

	}
	
	/**
	 * Method searches for the cell data that corresponds to a given node and 
	 * stores it in the correct node field.
	 * @param existingNodes -  nodes.
	 * @param cellResults - cell data to be stored in the nodes.
	 */
	public static void storeData(Node[] existingNodes, 
			MultiHashMap<String, String> cellResults) {
		String brainRegionName = null;

		for(Node node :  existingNodes){

			if(brainRegionName == null)
				brainRegionName=  BrainRegionNameShortener.reduceName(node.toString());

			Collection<String> partOf = 
				cellResults.get("$" + brainRegionName + "_ipo");

			//node.setPartOfNodes(partOf);

			brainRegionName = null;
		}	
	}
}


