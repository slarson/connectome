package org.wholebraincatalog.mcb.graph;


import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class EdgeLabeller implements Transformer<Edge, String>{

	public String transform(Edge input) {
		return input.getLabel();
	}

}