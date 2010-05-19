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

		String cell = "NOTHING";
		String neurotransmitter;
		String role;
		String data_str = n.getVertexName().replace("_", " ");

		for (String key : n.getNodeCellsMap().keySet()) {
			System.out.println(cell);
			for (String cellName : ((Node) v).getNodeCellsMap().get(key)
					.getNeurotransmitterData().keySet()) {
				cell = cellName;
				for (NeurotransmitterData data : ((Node) v).getNodeCellsMap()
						.get(key).getNeurotransmitter(cellName)) {
					neurotransmitter = data.getNeurotransmitter();
					role = data.getRole();
					data_str += cell + " " + neurotransmitter + " " + role
							+ "\n";
				}
			}
		}
		return data_str;

	}

}