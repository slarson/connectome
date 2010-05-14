package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class CellDataLoader {

	private Node[] nodes;
	
	public CellDataLoader(Node[] data) {
		this.nodes = data;
	}
	
	/**
	 * Populate a data reader for BAMS data.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	private static void populateCellDataReader(DataReaderBetter drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + "_region " + "$x " + "<http://neurolex.org/wiki/Category:"+
				brainRegionName+">");
			drb.addQueryTriplet("$" + brainRegionName + "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> " +
					"$" + brainRegionName+"_region");
			drb.addQueryTriplet("$" + brainRegionName + "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> " +
					"<http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>");
			drb.addQueryTriplet("$" + brainRegionName + "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter>" +
					"$" + brainRegionName+"_neurotransmitter");
			drb.addQueryTriplet("$" + brainRegionName+"_neurotransmitter <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role>" +
					"$" + brainRegionName+"_role");
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
	 * Create Node elements from the results of getting info from the BAMS 
	 * brain regions
	 * @param brainRegions
	 * @param results
	 * @return
	 */
	private void storeCellData(Node[] data, 
			MultiHashMap<String, String> cellResults) {

		//Date key to search cellResults.
		String searchKeyCell;
		String searchKeyNeurotransmitter;
		String searchKeyRole;
		
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
				for(String cells : cellResults.get("$"+node.toString()+"_cells")){
					if(cellResults.containsKey(searchKeyNeurotransmitter)){
						System.out.println("cells:"+cells);
						for(String neurotransmitter: cellResults.get(
								"$"+node.toString()+"_neurotransmitter")){
							System.out.println("neurotransmitter:"+neurotransmitter);
							neurotransmitterStore = neurotransmitter;
							if(cellResults.containsKey(searchKeyRole)){
								for(String role: cellResults.get(
										"$"+node.toString()+"_role")){
									roleStore = role;
									System.out.println("role:"+role);
									break;
								}
							}
							break;
						}
					}
					NeurotransmitterData neurottransmitterCellData = 
						new NeurotransmitterData(neurotransmitterStore,roleStore);
					node.getNodeCellsMap().get(node.toString()+"_cells").
					store(cells,neurottransmitterCellData);
				}
			}	
		}	
	}
	
}
