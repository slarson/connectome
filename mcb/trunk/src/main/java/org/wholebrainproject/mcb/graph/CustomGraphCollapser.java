package org.wholebrainproject.mcb.graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;

public class CustomGraphCollapser extends GraphCollapser{

	Graph<Node,Edge> originalGraph; 
	AggregateLayout<Node, Edge> layout; 
	VisualizationViewer<Node, Edge> vv;
	Set exclusions;
	
	public CustomGraphCollapser(Graph<Node,Edge> originalGraph, 
			AggregateLayout<Node, Edge> layout, 
			VisualizationViewer<Node, Edge> vv, Set exclusions) {
		
		super(originalGraph);
		this.originalGraph = originalGraph;
		this.layout = layout;
		this.vv = vv;
		this.exclusions = exclusions;
	}
	

	public void collapse() {
		Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
		if(picked.size() > 1) {
			Graph<Node,Edge> inGraph = layout.getGraph();
			Graph<Node,Edge> clusterGraph = getClusterGraph(inGraph, picked);

			Graph<Node,Edge> g = collapse(layout.getGraph(), clusterGraph);
			double sumx = 0;
			double sumy = 0;
			for(Object v : picked) {
				Point2D p = (Point2D)layout.transform((Node) v);
				sumx += p.getX();
				sumy += p.getY();
			}
			Point2D cp = new Point2D.Double(sumx/picked.size(), sumy/picked.size());
			vv.getRenderContext().getParallelEdgeIndexFunction().reset();
			layout.setGraph(g);
			layout.setLocation((Node) clusterGraph, cp);
			vv.getPickedVertexState().clear();
			vv.repaint();
		}
	}

	public void expand() {
		Collection<Node> picked = 
			new HashSet<Node>(vv.getPickedVertexState().getPicked());
		for(Object v : picked) {
			if(v instanceof Graph<?,?>) {

				Graph g = expand(layout.getGraph(), (Graph)v);
				vv.getRenderContext().getParallelEdgeIndexFunction().reset();
				//applyTreeLayout((DirectedGraph)v);
				
				layout.setGraph(g);
				
			}
			((Node)v).setCollapsed(false);
			vv.getPickedVertexState().clear();
			vv.repaint();
		}
	}

	public void compressEdges() {
		Collection<Node> picked = vv.getPickedVertexState().getPicked();
		if(picked.size() == 2) {
			Pair<Node> pair = new Pair<Node>(picked);
			Graph<Node,Edge> graph = layout.getGraph();
			Collection<Edge> edges = new HashSet(graph.getIncidentEdges(pair.getFirst()));
			edges.retainAll(graph.getIncidentEdges(pair.getSecond()));
			exclusions.addAll(edges);
			vv.repaint();
		}
	}

	public void expandEdges() {
		Collection picked = vv.getPickedVertexState().getPicked();
		if(picked.size() == 2) {
			Pair pair = new Pair(picked);
			Graph graph = layout.getGraph();
			Collection edges = new HashSet(graph.getIncidentEdges(pair.getFirst()));
			edges.retainAll(graph.getIncidentEdges(pair.getSecond()));
			exclusions.removeAll(edges);
			vv.repaint();
		}
	}
	

	/**
	 * Method collapses the nodes that are part of a particular brain region.
	 */
	private void collapseSubGraph(){

		Collection<Node> picked = null;

		for(Node node: originalGraph.getVertices()){

			if(node.getPartOf()!= null && !node.getPartOf().isEmpty())
				picked = getPickedNodes(node);
			else if(node.getPartOf() ==  null)
				continue;

			if(picked != null && picked.size() > 1) {
				Graph<Node,Edge> inGraph = layout.getGraph();
				Graph<Node,Edge> clusterGraph = getClusterGraph(inGraph, picked);
				Graph<Node,Edge> g = collapse(layout.getGraph(), clusterGraph);
				
				double sumx = 0;
				double sumy = 0;
				for(Node v : picked) {
					Point2D p = (Point2D)layout.transform(v);
					sumx += p.getX();
					sumy += p.getY();
				}
				Point2D cp = new Point2D.Double(sumx/picked.size(), sumy/picked.size());
				vv.getRenderContext().getParallelEdgeIndexFunction().reset();
				//vv.getRenderContext().setVertexLabelTransformer(new NodeLabeller());
				layout.setGraph(g);
				//layout.setLocation(clusterGraph, cp);
				vv.getPickedVertexState().clear();
				vv.repaint();
			}
			picked = null;
		}
	}
	


	public void test() {
		///temporary hack for demo purposes
		for (Node n : originalGraph.getVertices()) {
			//if (n.getVertexName().startsWith("Glo")) {
				applyTreeLayoutNode(n);
			//}
		}
	}

	public void applyTreeLayout(DirectedGraph<Node,Edge> tree) {
		if (tree == null) throw new IllegalArgumentException();
		Tree<Node, Edge> subGraph;
		try {
			subGraph = new DelegateTree<Node,Edge>(tree);
			Collection<Node> picked = subGraph.getVertices();
			Point2D center = new Point2D.Double();
			double x = 0;
			double y = 0;
			for (Node vertex : picked) {
				Point2D p = layout.transform(vertex);
				x += p.getX();
				y += p.getY();
			}
			x /= picked.size();
			y /= picked.size();
			center.setLocation(x, y);

			Layout<Node, Edge> subLayout =
				new TreeLayout<Node, Edge>(subGraph);
			subLayout.setInitializer(vv.getGraphLayout());
			subLayout.setSize(new Dimension(100, 100));
			layout.put(subLayout, center);
			vv.setGraphLayout(layout);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void applyTreeLayoutNode(Node n) {

		Tree<Node, Edge> subGraph;
		try {
			subGraph = n.getChildTree(); //get the tree graph from the Node
			Collection<Node> picked = subGraph.getVertices();
			Point2D center = new Point2D.Double();
			double x = 0;
			double y = 0;
			for (Node vertex : picked) {
				originalGraph.addVertex(vertex);
				Point2D p = layout.transform(vertex);
				x += p.getX();
				y += p.getY();
			}
			layout.setGraph(originalGraph);
			x /= picked.size();
			y /= picked.size();
			center.setLocation(x, y);

			Layout<Node, Edge> subLayout =
				new TreeLayout<Node, Edge>(subGraph);
			subLayout.setInitializer(vv.getGraphLayout());
			
			layout.put(subLayout, center);
			vv.setGraphLayout(layout);
			vv.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reset() {
		layout.setGraph(originalGraph);
		exclusions.clear();
		collapseSubGraph();
		vv.repaint();
	}
	
	/**
	 * Given a node this method returns the children of the node.
	 * @param node - the node used to check for its children.
	 * @return pickedNodes - collection containing the children of the node and
	 * 						 the node itself.
	 */
	private Collection<Node> getPickedNodes(Node node){
		Collection<Node> pickedNodes = new Vector<Node>();
		for(String subNode : node.getPartOf()){
			for(Node currentNode: originalGraph.getVertices()){
				if(subNode.equals(currentNode.getName().replace('_', ' '))){
					pickedNodes.add(currentNode);
				}
			}
			pickedNodes.add(node);
		}
		return pickedNodes;
	}
}
