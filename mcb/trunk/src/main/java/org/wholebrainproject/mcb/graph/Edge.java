package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Font;

/**
 * Common interface for all edges. 
 */
public interface Edge {
	
	public BasicStroke getStroke();

	public String getLabel();

	public String getToolTipLabel();

	public String getMoreDetailsURL();

	public Font getFont() ;

	public Number getCloseness();

	public String getReferenceURL();

}
