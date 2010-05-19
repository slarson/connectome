package org.wholebraincatalog.mcb;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
/**
 * This extend the ToStringLabeller class and it is implemented 
 * to display the data that pretends to the cells in the node.
 * @author Ruggero Carloz
 * @date 05-19-2010
 *
 */
public class NodeLabeller extends ToStringLabeller<Node> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeedu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#
	 * getToolTipText(java.lang.Object)
	 */
	public String transform(Node v) {

		Node n = (Node) v;

		//the cell data.
		String cell;
		String neurotransmitter;
		String role;
		
		//trying to format string.
		String data_str = n.getVertexName().replace("_", " ");
		StringBuilder builder =null;
		String newline = System.getProperty("line.separator");
		
		for (String key : n.getNodeCellsMap().keySet()) {
			for (String cellName : ((Node) v).getNodeCellsMap().get(key)
					.getNeurotransmitterData().keySet()) {
				cell = cellName;
				for (NeurotransmitterData data : ((Node) v).getNodeCellsMap()
						.get(key).getNeurotransmitter(cellName)) {
					neurotransmitter = data.getNeurotransmitter();
					role = data.getRole();
					//cell data to be displayed.
					data_str += " -cell:"+cell + "  neurotransmitter:" + neurotransmitter + " role:" + role
							+ "- \n";
					//trying to format the output data.
					builder = new StringBuilder(data_str);
					builder.append(newline);
					builder.append("\n");
					data_str = builder.toString(); 
				}
			}
		}
		return data_str;

	}

}