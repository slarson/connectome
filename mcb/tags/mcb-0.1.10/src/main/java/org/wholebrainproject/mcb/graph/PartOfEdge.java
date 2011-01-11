package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Font;

/**
 * Edge that defines a part of relationship between two brain regions.
 * Is directional.  Indicates that the brain region being pointed at contains
 * the brain region doing the pointing.
 * @author slarson
 *
 */
public class PartOfEdge implements Edge {

	public String getLabel() {
		return "part of";
	}
	
	public String getMoreDetailsURL() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Number getCloseness() {
		return 0.5f;
	}

	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReferenceURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public BasicStroke getStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInferenceChain() {
		// TODO Auto-generated method stub
		return null;
	}
}
