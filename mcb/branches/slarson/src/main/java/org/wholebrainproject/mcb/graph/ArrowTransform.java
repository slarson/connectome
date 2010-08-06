package org.wholebrainproject.mcb.graph;




import java.awt.Shape;

import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.util.ArrowFactory;

public class ArrowTransform implements Transformer<Context<Graph<Node,Edge>,Edge>,Shape>{

	public Shape transform(Context<Graph<Node, Edge>, Edge> input) {
		Graph g = GraphManager.getInstance().getGraph();
		ArrowFactory arrow = new ArrowFactory();
        return arrow.getNotchedArrow(10, 10, 0);
	}

	
}
