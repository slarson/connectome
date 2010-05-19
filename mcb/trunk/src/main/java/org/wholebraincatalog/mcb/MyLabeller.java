package org.wholebraincatalog.mcb;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class MyLabeller extends ToStringLabeller{

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
	 */
	@Override
	public String transform(Object v) {
		return ((Edge)v).getReference();
	}

}