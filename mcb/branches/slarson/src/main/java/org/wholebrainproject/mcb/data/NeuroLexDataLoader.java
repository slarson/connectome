package org.wholebrainproject.mcb.data;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BrainRegionNameShortener;
import org.wholebrainproject.mcb.util.SparqlQuery;

public class NeuroLexDataLoader {

	/**
	 * TARGET QUERY FOR A SINGLE BRAIN REGION (e.g. Globus Pallidus):
	 *
	 * select $Globus_pallidus_cells_label
	 *                $Globus_pallidus_cells_url
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
	 * @param query - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	public static MultiHashMap<String,String>  populate(Node[] brainRegionNames) {

		String region_suffix = "_r";
		String cells_suffix = "_c";
		String neurotransmitter_suffix = "_n";
		String transmitter_role_suffix = "_t_r";
		String brainRegionSufixName = null;

		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery query = new SparqlQuery(sparqlNif);

		query.addPrefixMapping("swivt", "<http://semantic-mediawiki.org/swivt/1.0#>");
		query.addPrefixMapping("nlx_prop", "<http://neurolex.org/wiki/Special:URIResolver/Property-3A>");
		query.addPrefixMapping("nlx_cat", "<http://neurolex.org/wiki/Special:URIResolver/Category-3A>");

		for(Node RegionName : brainRegionNames){

			try {
				if(BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap().containsKey(RegionName.getUri())){

					if(brainRegionSufixName == null)
						brainRegionSufixName =  BrainRegionNameShortener.reduceName(RegionName.toString());

					if(RegionName.toString().equalsIgnoreCase("midbrain-hindbrain, motor, extrapyramidal")){
						query.addQueryTriplet("$" + brainRegionSufixName + region_suffix +
								" swivt:page " +
								" <http://neurolex.org/wiki/Category:"+"basal_ganglia"+">");
					}
					else if(RegionName.toString().equalsIgnoreCase("Anterior hypothalamic area")){
						System.out.println("current brain region name: "+RegionName.toString());
						query.addQueryTriplet("$" + brainRegionSufixName + region_suffix +
								" swivt:page " +
								"<http://neurolex.org/wiki/Category:anterior_nucleus_of_hypothalamus>");
					}
					else{
						query.addQueryTriplet("$" + brainRegionSufixName + region_suffix +
								" swivt:page " +
								"<"+RegionName.getMoreDetailURL()+">");

					}

					//System.out.println(RegionName.getMoreDetailURL());

					query.addQueryTriplet("$"+brainRegionSufixName+cells_suffix +
							" nlx_prop:Located_in $" +
							brainRegionSufixName + region_suffix );

					query.addQueryTriplet("$"+brainRegionSufixName+cells_suffix+
							" nlx_prop:Label $"+
							brainRegionSufixName+"_cl");

					query.addQueryTriplet("$"+brainRegionSufixName+cells_suffix+
							" swivt:page $"+
							brainRegionSufixName+"_cu");

					query.addQueryTriplet("$"+brainRegionSufixName+cells_suffix
							+" nlx_prop:Has_role" + " nlx_cat:Principal_neuron_role");

					query.addQueryTriplet("$"+brainRegionSufixName+cells_suffix +
							" nlx_prop:Neurotransmitter $" +
							brainRegionSufixName+neurotransmitter_suffix);

					query.addQueryTriplet("$"+brainRegionSufixName+neurotransmitter_suffix +
							" nlx_prop:Label $" +
							brainRegionSufixName+"_nl");

					query.addQueryTriplet("$"+brainRegionSufixName+neurotransmitter_suffix +
							" nlx_prop:Has_role $"+
							brainRegionSufixName+transmitter_role_suffix);

					query.addQueryTriplet("$"+brainRegionSufixName+transmitter_role_suffix +
							" nlx_prop:Label $"+
							brainRegionSufixName+"_trl");

					//filter to avoid getting two entries per cell
					query.addQueryTriplet("FILTER regex(str($"+ brainRegionSufixName+
							cells_suffix +"), \"Category\")");

					query.addSelectVariable("$"+ brainRegionSufixName + "_cl");
					query.addSelectVariable("$"+ brainRegionSufixName + "_cu");
					query.addSelectVariable("$"+ brainRegionSufixName + "_nl");
					query.addSelectVariable("$"+ brainRegionSufixName + "_trl");

					//add union between all sets of variables except the last
					if (RegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
						query.addQueryTriplet("} UNION {");
					}
					brainRegionSufixName = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return query.runSelectQuery();
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
				brainRegionName =  BrainRegionNameShortener.reduceName(node.toString());

			Collection<String> cells =
				cellResults.get("$" + brainRegionName + "_cl");
			Collection<String> cellUrls =
				cellResults.get("$" + brainRegionName + "_cu");
			Collection<String> transmitters =
				cellResults.get("$" + brainRegionName + "_nl");
			Collection<String> roles =
				cellResults.get("$" + brainRegionName + "_trl");
			//System.out.println("NeuroLexDataLoader roles: "+roles.toString());
			node.setCellInfo(cells, cellUrls, transmitters, roles);

			brainRegionName = null;
		}
	}
}

