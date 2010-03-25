package org.wholebraincatalog.mcb;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.Vertex;

public class MyLabeller extends ToStringLabeller{

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
	 */
	@Override
	public String transform(Object v) {
		
		if(v instanceof Graph) {
			return ((Edge)v).getReference();
		}
		return super.transform(v);
	}

}