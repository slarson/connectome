package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Font;

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

	public String getToolTipLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMoreDetailsURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	public Number getCloseness() {
		// TODO Auto-generated method stub
		return null;
	}

}
