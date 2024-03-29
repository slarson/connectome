package org.wholebrainproject.mcb.graph;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
/**
 * This extend the ToStringLabeller class and it is implemented 
 * to display the data that pretends to the cells in the node.
 * @author Ruggero Carloz
 * @date 05-19-2010
 *
 */
public class ToolTipNodeLabeller extends ToStringLabeller<Node> {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeedu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#
	 * getToolTipText(java.lang.Object)
	 */
	public String transform(Node v) {

		Node n = (Node) v;
		
		//HTML can be used to format the tooltip.
		//http://sourceforge.net/projects/jung/forums/forum/252062/topic/2294542
		String data_str = "<html>";
		data_str += n.getName().replace("_", " ");
		data_str += "<br>";
		
		for (int i = 0; i < n.getCellCount(); i++) {
			data_str += "<a href=\"" + n.getCellUrl(i) + "\">" + 
			n.getCellName(i) + "</a><br>";
			data_str += n.getNeurotransmitter(i) + "<br>";
			data_str += n.getRole(i) + "<br>";
			
		}
		
		data_str += "</html>";
		return data_str;

	}

}