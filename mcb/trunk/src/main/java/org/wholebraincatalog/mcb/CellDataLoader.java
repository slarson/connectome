package org.wholebraincatalog.mcb;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections15.multimap.MultiHashMap;

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
		String[] brainRegions =  reduceBrainRegionName(brainRegionNames);

		for(String RegionName : brainRegionNames){
			for (String brainRegionName : brainRegions){

				drb.addQueryTriplet("$" + brainRegionName + region_suffix + 
						" <http://semantic-mediawiki.org/swivt/1.0#page> " + 
						" <http://neurolex.org/wiki/Category:"+
						RegionName+">");

				drb.addQueryTriplet("$"+brainRegionName+cells_suffix +
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $" +
						brainRegionName + region_suffix );

				drb.addQueryTriplet("$"+brainRegionName+cells_suffix+
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
						brainRegionName+"_cells_label");

				drb.addQueryTriplet("$"+brainRegionName+cells_suffix+
						" <http://semantic-mediawiki.org/swivt/1.0#page> $"+
						brainRegionName+"_cells_url");

				drb.addQueryTriplet("$"+brainRegionName+cells_suffix
						+" <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role>" +
				" <http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>");

				drb.addQueryTriplet("$"+brainRegionName+cells_suffix +
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter> $" +
						brainRegionName+neurotransmitter_suffix);

				drb.addQueryTriplet("$"+brainRegionName+neurotransmitter_suffix +
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $" +
						brainRegionName+"_neurotransmitter_label");

				drb.addQueryTriplet("$"+brainRegionName+neurotransmitter_suffix +
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> $"+
						brainRegionName+transmitter_role_suffix);

				drb.addQueryTriplet("$"+brainRegionName+transmitter_role_suffix +
						"<http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
						brainRegionName+"_transmitter_role_label");

				drb.addQueryTriplet("$"+brainRegionName+region_suffix +
						" <http://neurolex.org/wiki/Special:URIResolver/Property-3AIs_part_of> $"+
						brainRegionName+part_suffix);

				drb.addQueryTriplet("$"+brainRegionName+part_suffix+" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> $"+
						brainRegionName+"_is_part_of");

				drb.addSelectVariable("$"+ brainRegionName + "_cl");
				drb.addSelectVariable("$"+ brainRegionName + "_cu");
				drb.addSelectVariable("$"+ brainRegionName + "_nl");
				drb.addSelectVariable("$"+ brainRegionName + "_trl");
				drb.addSelectVariable("$"+ brainRegionName + "_ipo");

				//add union between all sets of variables except the last
				if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
					drb.addQueryTriplet("} UNION {");
				}
			}
		}
	}
	/**
	 * Method reduces the name of the brain regions.
	 * @param brainRegionNames - array containing the name regions.
	 * @return - array with reduced brain region names.
	 */
	public static String[] reduceBrainRegionName(String[] brainRegionNames){

		String[] brainRegionsReducedNames = new String[brainRegionNames.length];
		String dumName;
		int i = 0;
		int index = 0;
		for(String brainRegionName: brainRegionNames){
			dumName = brainRegionName.substring(0, 0);
			while(brainRegionName.indexOf('_') != -1){
				index = brainRegionName.indexOf('_')+1;
				dumName+=brainRegionName.charAt(index);
				brainRegionName = brainRegionName.substring(index+1);	
			}
			brainRegionsReducedNames[i] = dumName;
			i++;
		}
		return brainRegionsReducedNames;
	}
	/**
	 * Method searches for the cell data that corresponds to a given node and 
	 * stores it in the correct node field.
	 * @param existingNodes -  nodes.
	 * @param cellResults - cell data to be stored in the nodes.
	 */
	static void storeCellData(Node[] existingNodes, 
			MultiHashMap<String, String> cellResults) {

		for(Node node :  existingNodes){
			String brainRegionName = node.toString();
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
		}	
	}
}
