package org.wholebrainproject.mcb.graph;


import org.apache.commons.collections15.Transformer;

public class ToolTipEdgeLabeller implements Transformer<Edge, String>{

	public String transform(Edge input) {
		//FIXME bring back good label
		System.out.println("EDGE : "+input.getLabel());
		
		return input.getToolTipLabel();
	}

}