package org.wholebraincatalog.mcb;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebraincatalog.mcb.graph.Node;
import org.wholebraincatalog.mcb.util.SparqlQuery;

public class CellDataLoader {

	/**
	 * TARGET QUERY FOR A SINGLE BRAIN REGION (e.g. Globus Pallidus):
	 * 
	 * select $Globus_pallidus_cells_label 
	 * 		  $Globus_pallidus_cells_url
	 *        $Globus_pallidus_neurotransmitter_label 
	 *        $Globus_pallidus_transmitter_role_label 
	 *
	 *{$Globus_pallidus_rigion 
	 * <http://semantic-mediawiki.org/swivt/1.0#page> 
	 * <http://neurolex.org/wiki/Category:Globus_pallidus> . 
	 * 
	 * $Globus_pallidus_cells 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> 
	 * $Globus_pallidus_rigion . 
	 * 
	 * $Globus_pallidus_cells 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> 
	 * $Globus_pallidus_cells_label . 
	 * 
	 * $Globus_pallidus_cells 
	 * <http://semantic-mediawiki.org/swivt/1.0#page>
	 * $Globus_pallidus_cells_url .
	 * 
	 * $Globus_pallidus_cells 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> 
	 * <http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role> . 
	 * 
	 * $Globus_pallidus_cells 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter> 
	 * $Globus_pallidus_neurotransmitter . 
	 * 
	 * $Globus_pallidus_neurotransmitter 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> 
	 * $Globus_pallidus_neurotransmitter_label . 
	 * 
	 * $Globus_pallidus_neurotransmitter 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> 
	 * $Globus_pallidus_transmitter_role . 
	 * 
	 * $Globus_pallidus_transmitter_role 
	 * <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> 
	 * $Globus_pallidus_transmitter_role_label . }
	 */

	/**
	 * This class takes care of getting the cell data for each node 
	 * and takes care of incorporating the data into the nodes.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	static void populateCellDataReader(SparqlQuery drb, String[] brainRegionNames) {

		String region_suffix = "_r";
		String cells_suffix = "_c";
		String part_suffix = "_p";
		String neurotransmitter_suffix = "_n";
		String transmitter_role_suffix = "_t_r";
		String brainRegionSufixName = null;

		for(String RegionName : brainRegionNames){

			if(brainRegionSufixName == null)
				brainRegionSufixName =  reduceBrainRegionName(RegionName);

			drb.addQueryTriplet("$" + brainRegionSufixName + region_suffix + 
					" <http://semantic-mediawiki.org/swivt/1.0#page> " + 
					" <http://neurolex.org/wiki/Category:"+
					RegionName+">");

			drb.addQueryTriplet("$"+brainRegionSufixName+cells_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $" +
					brainRegionSufixName + region_suffix );

			drb.addQueryTriplet("$"+brainRegionSufixName+cells_suffix+
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
					brainRegionSufixName+"_cl");

			drb.addQueryTriplet("$"+brainRegionSufixName+cells_suffix+
					" <http://semantic-mediawiki.org/swivt/1.0#page> $"+
					brainRegionSufixName+"_cu");

			drb.addQueryTriplet("$"+brainRegionSufixName+cells_suffix
					+" <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role>" +
			" <http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>");

			drb.addQueryTriplet("$"+brainRegionSufixName+cells_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter> $" +
					brainRegionSufixName+neurotransmitter_suffix);

			drb.addQueryTriplet("$"+brainRegionSufixName+neurotransmitter_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $" +
					brainRegionSufixName+"_nl");

			drb.addQueryTriplet("$"+brainRegionSufixName+neurotransmitter_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> $"+
					brainRegionSufixName+transmitter_role_suffix);

			drb.addQueryTriplet("$"+brainRegionSufixName+transmitter_role_suffix +
					"<http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
					brainRegionSufixName+"_trl");

			drb.addQueryTriplet("$"+brainRegionSufixName+part_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3AIs_part_of> $"+
					brainRegionSufixName+region_suffix);

			drb.addQueryTriplet("$"+brainRegionSufixName+part_suffix+" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
					brainRegionSufixName+"_ipo");

			drb.addSelectVariable("$"+ brainRegionSufixName + "_cl");
			drb.addSelectVariable("$"+ brainRegionSufixName + "_cu");
			drb.addSelectVariable("$"+ brainRegionSufixName + "_nl");
			drb.addSelectVariable("$"+ brainRegionSufixName + "_trl");
			drb.addSelectVariable("$"+ brainRegionSufixName + "_ipo");

			//add union between all sets of variables except the last
			if (RegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
			brainRegionSufixName = null;
		}

	}
	/**
	 * Method reduces the name of the brain regions.
	 * @param brainRegionName - full name of the brain region.
	 * @return reducedName - reduced name of the brain region.
	 */
	public static String reduceBrainRegionName(String brainRegionName){

		String reducedName;
		int index = 0;
		
		//obtain first letter of brain region.
		reducedName = brainRegionName.substring(0, 1);
		
		//loop and obtain the first letter of brain region after 
		// the underscore.
		while(brainRegionName.indexOf('_') != -1){
			index = brainRegionName.indexOf('_')+1;
			reducedName+=brainRegionName.charAt(index);
			brainRegionName = brainRegionName.substring(index);	
		}
		
		return reducedName.toLowerCase();
	}
	/**
	 * Method searches for the cell data that corresponds to a given node and 
	 * stores it in the correct node field.
	 * @param existingNodes -  nodes.
	 * @param cellResults - cell data to be stored in the nodes.
	 */
	static void storeCellData(Node[] existingNodes, 
			MultiHashMap<String, String> cellResults) {
		String brainRegionName = null;
		
		for(Node node :  existingNodes){

			if(brainRegionName == null)
				brainRegionName=  reduceBrainRegionName(node.toString());
			
			Collection<String> cells = 
				cellResults.get("$" + brainRegionName + "_cl");
			Collection<String> cellUrls = 
				cellResults.get("$" + brainRegionName + "_cu");
			Collection<String> transmitters = 
				cellResults.get("$" + brainRegionName + "_nl");
			Collection<String> roles = 
				cellResults.get("$" + brainRegionName + "_trl");
			Collection<String> partOf = 
				cellResults.get("$" + brainRegionName + "_ipo");

			node.setCellInfo(cells, cellUrls, transmitters, roles, partOf);
			
			brainRegionName = null;
		}	
	}
}
