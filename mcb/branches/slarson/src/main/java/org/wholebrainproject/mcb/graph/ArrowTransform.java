package org.wholebrainproject.mcb.graph;

import java.awt.Rectangle;
import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
/**
 * This class handles the shape of the edge's arrows. By default directed edges
 * mark the edge's direction with an arrow.  This class changes the shape of the
 * default arrow to a rectangle.     
 * @author Ruggero Carloz
 * @date 8-12-2010
 */
public class ArrowTransform implements Transformer<Context<Graph<Node,Edge>,Edge>,Shape> {

    public Shape transform(Context<Graph<Node, Edge>, Edge> input) {
        //arrow to be drawn in edge.
        // this functionality might enable us to indicate
        // types of connectivity among brain regions.
        Rectangle rec = new Rectangle(-5,-5,6,10);
        return rec;
    }

}
