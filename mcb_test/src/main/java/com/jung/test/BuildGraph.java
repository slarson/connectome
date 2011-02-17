package com.jung.test;



import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Color;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;


/**
 * This class is the driver of the test application.  It takes care of the
 * visualization of the graph by implementing the class BasicVisualizationServer.
 * The graph is rendered in a circle layout by swt.
 * @author ruggero
 *
 */

public class BuildGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicGraphCreation sgv = new BasicGraphCreation(); //We create our graph in here

		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<MyNode, MyLink> layout = new CircleLayout<MyNode, MyLink>(sgv.g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space

		// Setup up a new vertex to paint transformer to change the node color.
		Transformer<MyNode,Paint> vertexPaint = new Transformer<MyNode,Paint>() {
			public Paint transform(MyNode i) {
				return Color.GREEN;
			}
		};

		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<MyNode, MyLink> vv =
			new BasicVisualizationServer<MyNode, MyLink>(layout);

		// Label the vertecies of the graph.
		VertexLabelAsShapeRenderer<MyNode, MyLink> vlasr =
			new VertexLabelAsShapeRenderer<MyNode, MyLink>(vv.getRenderContext());
		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
		vv.getRenderContext().setVertexShapeTransformer(vlasr);
		vv.getRenderContext().setVertexLabelTransformer(new NodeLabeller());

		// Set the vertex transformer to change the vertices color.
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);

	}

}
