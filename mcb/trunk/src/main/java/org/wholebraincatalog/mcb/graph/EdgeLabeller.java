package org.wholebraincatalog.mcb.graph;


import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class EdgeLabeller extends ToStringLabeller<ConnectionEdge>{

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
	 */
	@Override
	public String transform(ConnectionEdge v) {
		String out = "";
		String reference = v.getReference();
		out += "<a href=\"http://" + reference + "\">" + reference + "</a>";
		return out;
	}

}