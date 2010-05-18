package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class CellDataLoader {


	/**
	 * Populate a data reader for neurolex data.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	static void populateCellDataReader(DataReaderBetter drb, String[] brainRegionNames) {

		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + "_region " + "$x" + "<http://neurolex.org/wiki/Category:"+
					brainRegionName+">");
			drb.addQueryTriplet("$y" + "<http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> " +
					"$" + brainRegionName+"_region");
			drb.addQueryTriplet("$y" +  "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> " +
			"<http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>");
			drb.addQueryTriplet("$y"+ " <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter>" +
					"$" + brainRegionName+"_role_dum");
			drb.addQueryTriplet("$" + brainRegionName+"_role_dum <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> " +
					"$" + brainRegionName+"_neurotransmitter");
			drb.addQueryTriplet("$"+brainRegionName+"_role_dum <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> "+
					"$"+brainRegionName+"_role_dum_2");
			drb.addQueryTriplet("$"+brainRegionName+"_role_dum_2 <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel>"+
					"$"+brainRegionName+"_role");
			
			drb.addSelectVariable("$"+ brainRegionName + "_cells");
			drb.addSelectVariable("$"+ brainRegionName + "_neurotransmitter");
			drb.addSelectVariable("$"+ brainRegionName + "_role");

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
			node.storeCellData(node.toString()+"_cells",new CellData());
			//System.out.println("Node name: "+node.toString());

			//construct key to search cellResults.
			searchKeyCell = "$"+node.toString()+"_cells";
			searchKeyNeurotransmitter = "$"+node.toString()+"_neurotransmitter";
			searchKeyRole = "$"+node.toString()+"_role";

			String neurotransmitterStore = null;
			String roleStore = null;

			//make sure cellRusults contains a searchKeyCell.
			if(cellResults.containsKey(searchKeyCell)){
				//obtain all the cells.
				for(String cells : cellResults.get("$"+node.toString()+"_cells")){
					cellStack.push(cells);
				}	
				//obtain all the nueurotransmitters.
				for(String neurotransmitter: cellResults.get(
						"$"+node.toString()+"_neurotransmitter")){
					System.out.println("neurotransmitter:"+neurotransmitter);
					neurotransmitterStore = neurotransmitter;
					neuroStack.push(neurotransmitterStore);
				}	
				//obtain all the roles.
				for(String role: cellResults.get(
						"$"+node.toString()+"_role")){
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
					node.getNodeCellsMap().get(node.toString()+"_cells").
					store(cell_str,neurottransmitterCellData);
					System.out.println("cell: "+cell_str+" neurotransmitter: "+
							neurottransmitterCellData.getNeurotransmitter()+
							" role:"+neurottransmitterCellData.getRole());
				}
			}
		}	
	}
}
