package org.wholebrainproject.mcb.graph;


import org.apache.commons.collections15.Transformer;

public class ToolTipEdgeLabeller implements Transformer<Edge, String>{

	public String transform(Edge input) {
		return input.getToolTipLabel();
	}

}