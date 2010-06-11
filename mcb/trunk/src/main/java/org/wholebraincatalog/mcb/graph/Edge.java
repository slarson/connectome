package org.wholebraincatalog.mcb.graph;

import java.awt.BasicStroke;

/**
 * Parent class for all edges.  Abstract because we must be working with a 
 * subtype.
 */
public abstract class Edge {
	
	public BasicStroke getStroke() {
		return new BasicStroke(2.5f);
	}

	public String getLabel() {
		return this.toString();
	}

}
