package org.wholebrainproject.mcb.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.wholebrainproject.mcb.data.BuildConnections;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;

/**
 * Extends the default graph collapser for our purposes.
 * @author slarson
 *
 */
public class CustomGraphCollapser extends GraphCollapser{

	Graph<Node,Edge> originalGraph; 
	AggregateLayout<Node, Edge> layout; 
	VisualizationViewer<Node, Edge> vv;
	Set exclusions;

	private static CustomGraphCollapser instance = null;

	public CustomGraphCollapser(){
		super(null);
	}

	public CustomGraphCollapser(Graph<Node,Edge> originalGraph, 
			AggregateLayout<Node, Edge> layout, 
			VisualizationViewer<Node, Edge> vv, Set exclusions) {

		super(originalGraph);
		this.originalGraph = originalGraph;
		this.layout = layout;
		this.vv = vv;
		this.exclusions = exclusions;
	}

	public void setGraph(Graph<Node,Edge> originalGraph){
		//this.setGraph(originalGraph);
		this.originalGraph = originalGraph;

	}

	public void setLayout(AggregateLayout<Node, Edge> layout){
		this.layout = layout;

	}

	public void setVisualizationViewer(VisualizationViewer<Node, Edge> vv){
		this.vv = vv;

	}

	public void setExclusions(Set exclusions){
		this.exclusions = exclusions;
	}

	public static CustomGraphCollapser getInstance(){
		if (instance == null) {
			instance = new CustomGraphCollapser();

		}
		return instance;
	}

	/**
	 * Collapse all the part of nodes into their parent nodes.
	 */
	@SuppressWarnings("unchecked")
	public void collapse() {
		// loop over all nodes
		for (Node n : originalGraph.getVertices()) {
			collapse(n);
		}
	}

	public void initialCollapse(Graph graph){

		ArrayList<Node> picked = new ArrayList<Node>(graph.getVertexCount());
		Collection<Node> nodes = graph.getVertices();

		for(Node currentNode: nodes){
			if(currentNode.getPartOfNodes() != null){
				if(!(currentNode.getPartOfNodes().isEmpty())){
					picked.add(currentNode);

					for(Node childNode: currentNode.getPartOfNodes()){
						picked.add(childNode);					
					}
					Graph<Node, Edge> clusterGraph = getClusterGraph(graph, picked);
					// collapses the original graph by creating a new graph
					// without the nodes in clusterGraph. Makes Node n be the
					// stand in node for the clusterGraph.
					Graph<Node, Edge> g = collapse(layout.getGraph(), clusterGraph, currentNode);

					// calculate a center point for the new node by averaging the
					// positions of its constituents.
					double sumx = 0;
					double sumy = 0;
					for (Object v : picked) {
						Point2D p = (Point2D) layout.transform((Node)v);
						sumx += p.getX();
						sumy += p.getY();
					}
					Point2D cp = new Point2D.Double(sumx / picked.size(), sumy
							/ picked.size());
					vv.getRenderContext().getParallelEdgeIndexFunction().reset();
					layout.setGraph(g);
					layout.setLocation((Node)currentNode, cp);
					currentNode.setCollapsed(true);
					// vv.getPickedVertexState().clear();
					vv.repaint();
					picked.clear();
				}
			}
		}
	}
	public void collapse(Node n) {

		// get part of nodes for this node, adding itself
		ArrayList<Node> picked = new ArrayList<Node>(n.getPartOfNodes());
		picked.add(n);

		if (picked.size() > 1) {

			Graph<Node, Edge> inGraph = originalGraph;
			Graph<Node, Edge> clusterGraph = getClusterGraph(inGraph, picked);

			// collapses the original graph by creating a new graph
			// without the nodes in clusterGraph. Makes Node n be the
			// stand in node for the clusterGraph.
			Graph<Node, Edge> g = collapse(layout.getGraph(), clusterGraph, n);

			// calculate a center point for the new node by averaging the
			// positions of its constituents.
			double sumx = 0;
			double sumy = 0;
			for (Object v : picked) {
				Point2D p = (Point2D) layout.transform((Node) v);
				sumx += p.getX();
				sumy += p.getY();
			}
			Point2D cp = new Point2D.Double(sumx / picked.size(), sumy
					/ picked.size());
			vv.getRenderContext().getParallelEdgeIndexFunction().reset();
			layout.setGraph(g);
			layout.setLocation(n, cp);
			n.setCollapsed(true);
			// vv.getPickedVertexState().clear();
			vv.repaint();
		}
	}

	Graph<Node,Edge> createGraph() throws InstantiationException, IllegalAccessException {
		return (Graph<Node,Edge>)originalGraph.getClass().newInstance();
	}

	/**
	 * Does a collapse.  Rewrites the superclasses {@link #collapse(Graph, Graph)} method
	 * to prevent a Graph from being a vertex.
	 * @param inGraph - the original graph, containing all the nodes
	 * @param clusterGraph - the subgraph that will get collapsed to a single node
	 * @param repNode - the representative node to replace the subgraph with.
	 * @return
	 */
	public Graph<Node,Edge> collapse(Graph<Node,Edge> inGraph, Graph<Node,Edge> clusterGraph, Node repNode) {

		if(clusterGraph.getVertexCount() < 2) return inGraph;

		Graph<Node,Edge> graph = inGraph;
		try {
			graph = createGraph();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Collection<Node> cluster = clusterGraph.getVertices();

		// add all vertices in the delegate, unless the vertex is in the
		// cluster.
		for(Node v : inGraph.getVertices()) {
			if(cluster.contains(v) == false) {
				graph.addVertex(v);
			}
		}
		// add the clusterGraph as a single representative node
		graph.addVertex(repNode);

		//add all edges from the inGraph, unless both endpoints of
		// the edge are in the cluster
		for(Edge e : (Collection<Edge>)inGraph.getEdges()) {
			Pair<Node> endpoints = inGraph.getEndpoints(e);
			// don't add edges whose endpoints are both in the cluster
			if(cluster.containsAll(endpoints) == false) {

				if(cluster.contains(endpoints.getFirst())) {
					graph.addEdge(e, repNode, endpoints.getSecond(), inGraph.getEdgeType(e));

				} else if(cluster.contains(endpoints.getSecond())) {
					graph.addEdge(e, endpoints.getFirst(), repNode, inGraph.getEdgeType(e));

				} else {
					graph.addEdge(e,endpoints.getFirst(), endpoints.getSecond(), inGraph.getEdgeType(e));
				}
			}
		}

		for (Node n : inGraph.getVertices()) {
			BuildConnections.connectNodesIfEdgeIsAppropriate(graph, repNode, n);
			BuildConnections.connectNodesIfEdgeIsAppropriate(graph, n, repNode);
		}

		return graph;
	}

	/**
	 * Take the current graph node and expand it into its children.  Remove the
	 * higher level node from the graph completely and replace it with its
	 * children nodes.
	 * @param node
	 */
	public void expand(Node node) { 
		Point2D p = (Point2D) layout.transform((Node) node);
		Double x = p.getX();
		Double y = p.getY();
		
		System.out.println("Colling Expand: "+node.toString());
		// get part of nodes for this node
		ArrayList<Node> picked = new ArrayList<Node>(node.getPartOfNodes());

		Graph<Node, Edge> clusterGraph = getClusterGraph(originalGraph, picked);

		Graph<Node,Edge> g = expand(layout.getGraph(), clusterGraph);
		
		//take out the node that is being expanded because we want to 
		//break the node into its consitutents
		g.removeVertex(node);
		Collection<Edge> edges = g.getIncidentEdges(node);

		if (edges != null) {
			for (Edge e : edges) {
				g.removeEdge(e);
			}
		}
		// calculate a center point for the new node by averaging the
		// positions of its constituents.
		double sumx = 0;
		double sumy = 0;
		int i = 2;
		layout.setGraph(g);
		for (Object v : picked) {
			i+=2;
			sumx += x+i+2;
			sumy += y+i+2;
			Point2D cp = new Point2D.Double(x, y);
			layout.setLocation((Node) v, cp);
		}
		node.setCollapsed(false);
		//applyTreeLayoutNode(node);
		//vv.getPickedVertexState().clear();
		//vv.repaint();
	}


	public Graph getClusterGraph(Graph inGraph, Collection picked) {
		Graph clusterGraph;
		try {
			clusterGraph = createGraph();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		for(Object v : picked) {
			clusterGraph.addVertex(v);
			Collection edges = inGraph.getIncidentEdges(v);
			if (edges == null) continue;
			for(Object edge : edges) {
				Pair endpoints = inGraph.getEndpoints(edge);
				Object v1 = endpoints.getFirst();
				Object v2 = endpoints.getSecond();
				if(picked.containsAll(endpoints)) {
					clusterGraph.addEdge(edge, v1, v2, inGraph.getEdgeType(edge));
				}
			}
		}
		return clusterGraph;
	}


	public void expand() {
		Collection<Node> picked = new HashSet<Node>(vv.getPickedVertexState()
				.getPicked());
		for (Node v : picked) {
			expand(v);
		}
	}

	public void compressEdges() {
		Collection<Node> picked = vv.getPickedVertexState().getPicked();
		if (picked.size() == 2) {
			Pair<Node> pair = new Pair<Node>(picked);
			Graph<Node, Edge> graph = layout.getGraph();
			Collection<Edge> edges = new HashSet(graph.getIncidentEdges(pair
					.getFirst()));
			edges.retainAll(graph.getIncidentEdges(pair.getSecond()));
			exclusions.addAll(edges);
			vv.repaint();
		}
	}

	public void expandEdges() {
		Collection picked = vv.getPickedVertexState().getPicked();
		if (picked.size() == 2) {
			Pair pair = new Pair(picked);
			Graph graph = layout.getGraph();
			Collection edges = new HashSet(graph.getIncidentEdges(pair
					.getFirst()));
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

			if(node.getPartOfNodes()!= null && !node.getPartOfNodes().isEmpty())
				picked = getPickedNodes(node);
			else if(node.getPartOfNodes() ==  null)
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

	/**
	 * Make the children of this node arrange themselves into a tree.
	 * @param n
	 */
	public void applyTreeLayoutNode(Node n) {
		System.out.println("calling applyTreeLayout");
		try {
			//get the tree graph from the Node
			Tree<Node, Edge> treeGraph = n.getPartOfTree(originalGraph); 

			//calculate the position of the center of the tree by averaging
			//the positions of its constituents
			Collection<Node> picked = treeGraph.getVertices();
			Point2D center = new Point2D.Double();
			
			double x = 0;
			double y = 0;
			for (Node vertex : picked) {
				Point2D p = layout.transform(vertex);
				x += p.getX();
				y += p.getY();
			}
			//layout.setGraph(originalGraph);
			x /= picked.size();
			y /= picked.size();
			center.setLocation(x, y);
			
			center.setLocation(layout.transform(n).getX(), 
					layout.transform(n).getY());

			//create a new sublayout that is a TreeLayout based on the treeGraph
			TreeLayout<Node, Edge> subLayout =
				new TreeLayout<Node, Edge>(treeGraph, 50);
			subLayout.setInitializer(vv.getGraphLayout());

			//place the sublayout at the computed location.
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
		for(Node subNode : node.getPartOfNodes()){
			for(Node currentNode: originalGraph.getVertices()){
				if(subNode.getUri().equals(currentNode.getUri())){
					pickedNodes.add(currentNode);
				}
			}
			pickedNodes.add(node);
		}
		return pickedNodes;
	}

}
