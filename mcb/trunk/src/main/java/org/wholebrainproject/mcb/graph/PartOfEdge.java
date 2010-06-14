package org.wholebrainproject.mcb.graph;

/**
 * Edge that defines a part of relationship between two brain regions.
 * Is directional.  Indicates that the brain region being pointed at contains
 * the brain region doing the pointing.
 * @author slarson
 *
 */
public class PartOfEdge extends Edge {

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
}
