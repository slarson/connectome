package org.wholebraincatalog.mcb;

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
		String neurotransmitter_suffix = "_n";
		String transmitter_role_suffix = "_t_r";

		for (String brainRegionName : brainRegionNames){

			drb.addQueryTriplet("$" + brainRegionName + region_suffix + 
					" <http://semantic-mediawiki.org/swivt/1.0#page> " + 
					" <http://neurolex.org/wiki/Category:"+
					brainRegionName+">");
			
			drb.addQueryTriplet("$"+brainRegionName+cells_suffix +
					" <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> " +
					"$"+brainRegionName + region_suffix );
			
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
			
			drb.addSelectVariable("$"+ brainRegionName + "_cells_label");
			drb.addSelectVariable("$"+ brainRegionName + "_cells_url");
			drb.addSelectVariable("$"+ brainRegionName + "_neurotransmitter_label");
			drb.addSelectVariable("$"+ brainRegionName + "_transmitter_role_label");

			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
		}
	}
	/**
	 * Method searches for the cell data that corresponds to a given node and 
	 * stores it in the correct node field.
	 * @param data -  nodes.
	 * @param cellResults - cell data to be stored in the nodes.
	 */
	static void storeCellData(Node[] data, 
			MultiHashMap<String, String> cellResults) {

		//Date key to search cellResults.
		String searchKeyCell;
		String searchKeyNeurotransmitter;
		String searchKeyRole;

		//sotre the corresponding data in a stack
		Stack<String> neuroStack = new Stack<String>();
		Stack<String> roleStack = new Stack<String>();
		Stack<String> cellStack = new Stack<String>();

		for(Node node :  data){
			//System.out.println("Node name: "+node.toString());
			node.storeCellData(node.toString()+"_cells_label",new CellData());
			//System.out.println("Node name: "+node.toString());

			//construct key to search cellResults.
			searchKeyCell = "$"+node.toString()+"_cells_label";
			searchKeyNeurotransmitter = "$"+node.toString()+"_neurotransmitter_label";
			searchKeyRole = "$"+node.toString()+"_transmitter_role_label";

			String neurotransmitterStore = null;
			String roleStore = null;

			//make sure cellRusults contains a searchKeyCell.
			if(cellResults.containsKey(searchKeyCell)){
				//obtain all the cells.
				
				for(String cells : cellResults.get("$"+node.toString()+"_cells_label")){
					
					cellStack.push(cells);
				}	
				System.out.println("cellStack size: "+cellStack.size());
				//obtain all the nueurotransmitters.
				for(String neurotransmitter: cellResults.get(
						"$"+node.toString()+"_neurotransmitter_label")){
					System.out.println("neurotransmitter:"+neurotransmitter);
					neurotransmitterStore = neurotransmitter;
					neuroStack.push(neurotransmitterStore);
				}	
				//obtain all the roles.
				for(String role: cellResults.get(
						"$"+node.toString()+"_transmitter_role_label")){
					roleStore = role;
					System.out.println("role:"+role);
					roleStack.push(roleStore);
				}
				//store the cell data in node.
				while( !(neuroStack.empty() && roleStack.empty()) ){
					// neurotransmitter name.
					String neuro_str = neuroStack.pop();
					// role name.
					String role_str = roleStack.pop();
					//cell name.
					String cell_str = cellStack.pop();
					//data corresponding to a given cell.
					NeurotransmitterData neurottransmitterCellData = 
						new NeurotransmitterData(neuro_str,role_str);
					//store the cell data corresponding to the node.
					node.getNodeCellsMap().get(node.toString()+"_cells_label").
					store(cell_str,neurottransmitterCellData);
					System.out.println("cell: "+cell_str+" neurotransmitter: "+
							neurottransmitterCellData.getNeurotransmitter()+
							" role:"+neurottransmitterCellData.getRole());
				}
			}
		}	
	}
}
