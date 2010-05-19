package org.wholebraincatalog.mcb;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class NodeLabeller extends ToStringLabeller<Node> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeedu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#
	 * getToolTipText(java.lang.Object)
	 */
	public String transform(Node v) {

		Node n = (Node) v;

		String cell;
		String neurotransmitter;
		String role;
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
					data_str += " -cell:"+cell + "  neurotransmitter:" + neurotransmitter + " role:" + role
							+ "- \n";
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