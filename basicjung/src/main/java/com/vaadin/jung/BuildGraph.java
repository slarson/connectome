package com.vaadin.jung;

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Color;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;


public class BuildGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicGraphCreation sgv = new BasicGraphCreation(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		Layout<MyNode, MyLink> layout = new CircleLayout<MyNode, MyLink>(sgv.g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		// Setup up a new vertex to paint transformer...
		Transformer<MyNode,Paint> vertexPaint = new Transformer<MyNode,Paint>() {
			public Paint transform(MyNode i) {
				return Color.GREEN;
			}
		};


		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<MyNode, MyLink> vv =
			new BasicVisualizationServer<MyNode, MyLink>(layout);

		VertexLabelAsShapeRenderer<MyNode, MyLink> vlasr =
			new VertexLabelAsShapeRenderer<MyNode, MyLink>(vv.getRenderContext());
		vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
		vv.getRenderContext().setVertexShapeTransformer(vlasr);
		vv.getRenderContext().setVertexLabelTransformer(new NodeLabeller());


		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);

	}

}
