package org.wholebraincatalog.mcb;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class EdgeLabeller extends ToStringLabeller<Edge>{

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
	 */
	@Override
	public String transform(Edge v) {
		String out = "";
		String reference = v.getReference();
		out += "<a href=\"http://" + reference + "\">" + reference + "</a>";
		return out;
	}

}