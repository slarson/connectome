package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.wholebrainproject.mcb.MultiScaleConnectomeBrowser;
import org.wholebrainproject.mcb.data.BAMSProjectionMap;
import org.wholebrainproject.mcb.data.BuildConnections;
import org.wholebrainproject.mcb.mousemenu.MouseMenus;
import org.wholebrainproject.mcb.mousemenu.PopupVertexEdgeMenuMousePlugin;
import org.wholebrainproject.mcb.util.HyperLinkToolTip;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MagnifyTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;

/**
 * The GraphManager holds the graph model, handles layout, and handles
 * modifications to the graph that occur via user interaction with the GUI. It
 * follows the singleton pattern.
 *
 */
public class GraphManager {

	/**
	 * The singleton instance
	 */
	private static GraphManager instance = null;

	/**
	 * the graph
	 */
	DirectedSparseMultigraph<Node, Edge> graph;

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<Node, Edge> vv;

	/**
	 * graph collapser.
	 */
	CustomGraphCollapser collapser;

	/**
	 * Scales the graph.
	 */
	ScalingControl scaler = new CrossoverScalingControl();


	/**
	 * Global flag to enable / disable rendering the connection edges with
	 * different thicknesses based on their projection strength.
	 */
	boolean showProjectionStrength = false;
	PluggableGraphMouse gm;
	/**
	 * Lens objects
	 */
	LensSupport viewSupport;
	LensSupport modelSupport;
	LensSupport magnifyLayoutSupport;
	LensSupport magnifyViewSupport;

	GraphZoomScrollPane gzsp = null;

	/**
	 * Exclusions set
	 */
	final Set exclusions = new HashSet();

	public static GraphManager getInstance() {
		if (instance == null) {
			instance = new GraphManager();
		}
		return instance;
	}

	/**
	 * Set up the Graph Manager and load necessary data!
	 */
	private GraphManager() {

		// initialize the graph
		graph = new DirectedSparseMultigraph<Node, Edge>();

		// load the data on to the graph
		BuildConnections.getInstance().getDataAndCreateGraphBetter(graph);

		AggregateLayout<Node,Edge> layout =
			new AggregateLayout<Node, Edge>(new CircleLayout<Node, Edge>(graph));
		// initialize the visualization viewer, which will make the graph look
		// the way we want
		vv = initializeVisViewer(layout);

		setGraphMouse();

		// configure the look and feel of the graph
		configureVisViewer(vv);


		collapser = new CustomGraphCollapser(graph, layout, vv, exclusions);
		// collapser = CustomGraphCollapser.getInstance();
		// collapser.setGraph(graph);
		// collapser.setLayout(layout);
		// collapser.setVisualizationViewer(vv);
		// collapser.setExclusions(exclusions);
		/*
		 * for (Node n: graph.getVertices()) { if
		 * (BuildConnections.getInstance()
		 * .getInitialBamsURIs().contains(n.getUri())) { collapse(n); } }
		 */

		// by default, collapse all the edges
		// collapser.collapse();
	}

	/**
	 * Add this node to the graph. If it has any connections to any other nodes
	 * on the graph, add an edge.
	 *
	 * @param n
	 */
	public void addNodeAndAnyConnectionEdges(Node n) {
		Collection<Node> nodes = graph.getVertices();
		if (nodes.contains(n) == false) {
			// add the node directly
			graph.addVertex(n);

			// attempt to add any related edges
			try {
				Map<Node, Edge> edgesToAdd = BAMSProjectionMap.getInstance()
						.getEdgesBetween(n, nodes);
				for (Node toAdd : edgesToAdd.keySet()) {
					graph.addEdge(edgesToAdd.get(toAdd), toAdd, n);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		refreshScreen();
	}

	/**
	 * Gets rid of all nodes and edges in the graph.
	 */
	public void clearAllNodesAndEdges() {
		List<Edge> edges = new ArrayList<Edge>();
		for (Edge e : graph.getEdges()) {
			edges.add(e);
		}
		// done in two loops to avoid concurrent modification exception
		for (Edge e : edges) {
			graph.removeEdge(e);
		}

		List<Node> nodes = new ArrayList<Node>();
		for (Node n : graph.getVertices()) {
			nodes.add(n);
		}
		// done in two loops to avoid concurrent modification exception
		for (Node n : nodes) {
			graph.removeVertex(n);
		}
		refreshScreen();
	}

	/**
	 * A refresh command to update the screen relative to the state of the graph
	 */
	public void refreshScreen() {
		AggregateLayout layout =
			new AggregateLayout<Node, Edge>(new CircleLayout<Node, Edge>(graph));
		vv = initializeVisViewer(layout);
		setGraphMouse();
		configureVisViewer(vv);
		getVisualizationViewer().validate();
		getVisualizationViewer().repaint();
		getVisualizationViewer().updateUI();
		Container parent = this.getGraphZoomScrollPane().getParent();
		parent.remove(this.getGraphZoomScrollPane());
		gzsp = null;
		parent.add(this.getGraphZoomScrollPane());
		parent.validate();
		parent.repaint();
	}

	/**
	 * Set various configurations on the visualization viewer to make the graph
	 * look and react the way we want it to.
	 */
	private void configureVisViewer(VisualizationViewer<Node,Edge> vv) {
		VertexLabelAsShapeRenderer vlasr =
			new VertexLabelAsShapeRenderer(vv.getRenderContext());

		vv.getRenderContext().setVertexShapeTransformer(vlasr);

		vv.getRenderContext().setVertexLabelTransformer(new NodeLabeller());

		vv.getRenderContext().setVertexLabelRenderer(
				new DefaultVertexLabelRenderer(Color.RED));

		vv.getRenderer().getVertexLabelRenderer().setPosition(
				Renderer.VertexLabel.Position.CNTR);
		vv.getRenderContext().setEdgeLabelTransformer(new EdgeLabeller());
		vv.getRenderContext().setEdgeLabelRenderer(
				new DefaultEdgeLabelRenderer(Color.RED));

		// uses a gradient edge if unpicked, otherwise uses picked selection
		GradientEdgePaintTransformer<Node, Edge> edgeDrawPaint = new GradientEdgePaintTransformer<Node, Edge>(
				Color.BLUE, Color.GREEN, vv);

		vv.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaint);

		vv.getRenderContext().setEdgeFontTransformer(
				new Transformer<Edge, Font>() {
					public Font transform(Edge input) {
						return input.getFont();
					}
				});

		vv.getRenderContext().setEdgeLabelClosenessTransformer(
				new Transformer<Context<Graph<Node, Edge>, Edge>, Number>() {
					/**
					 * @see Transformer#transform(Object)
					 */
					public Number transform(
							Context<Graph<Node, Edge>, Edge> context) {
						Graph<Node, Edge> graph = context.graph;
						Edge e = context.element;
						return e.getCloseness();
					}
				});
		vv.getRenderContext().setEdgeArrowTransformer(new ArrowTransform());

		// Setup up a new vertex to paint transformer to change node color.
		vv.getRenderContext().setVertexFillPaintTransformer(
				new Transformer<Node, Paint>() {
					public Paint transform(Node input) {
						return Color.WHITE;
					}
				});

		final PredicatedParallelEdgeIndexFunction<Node, Edge> eif = PredicatedParallelEdgeIndexFunction
				.getInstance();

		eif.setPredicate(new Predicate() {
			public boolean evaluate(Object e) {
				return exclusions.contains(e);
			}
		});

		vv.getRenderContext().setParallelEdgeIndexFunction(eif);

		vv.setBackground(Color.LIGHT_GRAY);

		// ***** add a listener for USE CLASS NodeLabeller*****
		vv.setVertexToolTipTransformer(new ToolTipNodeLabeller());
		// vv.setVertexToolTipTransformer(new ToStringLabeller());

		vv.getRenderContext().setEdgeStrokeTransformer(
				new Transformer<Edge, Stroke>() {
					/**
					 * Transforms the input by ignoring it and returning the
					 * stored constant instead.
					 *
					 * @param input
					 *            the input object which is ignored
					 * @return the stored constant
					 */
					public BasicStroke transform(Edge input) {
						if (showProjectionStrength)
							return input.getStroke();
						return new BasicStroke(2.5f);
					}
				});

		// paint the inside of the arrow green
		vv.getRenderContext().setArrowFillPaintTransformer(
				new Transformer<Edge, Paint>() {
					public Paint transform(Edge input) {
						// BufferedImage img = new BufferedImage(40, 50,
						// BufferedImage.TYPE_4BYTE_ABGR);
						// Graphics2D imageGraphics = img.createGraphics();
						// Paint something here using the given graphics object
						// imageGraphics.setColor(Color.GREEN);

						return Color.GREEN;
					}
				});
		// paint the perimeter of the 'arrow' green.
		vv.getRenderContext().setArrowDrawPaintTransformer(
				new Transformer<Edge, Paint>() {
					public Paint transform(Edge input) {
						// BufferedImage img = new BufferedImage(40, 50,
						// BufferedImage.TYPE_4BYTE_ABGR);
						// Graphics2D imageGraphics = img.createGraphics();
						// Paint something here using the given graphics object
						// imageGraphics.setColor(Color.GREEN);

						return Color.GREEN;
					}
				});

		vv.setEdgeToolTipTransformer(new ToolTipEdgeLabeller());

	}

	/**
	 * Initialize the visualization viewer which drives how the graph nodes and
	 * edges will look and react.
	 */
	private VisualizationViewer initializeVisViewer(Layout layout) {


		Dimension preferredSize = new Dimension(
				MultiScaleConnectomeBrowser.width,
				MultiScaleConnectomeBrowser.height);
	    VisualizationModel<Node, Edge> visualizationModel =
	    	//new DefaultVisualizationModel<Node,Edge>(new StaticLayout<Node,Edge>(graph));
	    	new DefaultVisualizationModel<Node, Edge>(layout, preferredSize);
		VisualizationViewer<Node,Edge> vv =
			new VisualizationViewer<Node, Edge>(visualizationModel,
				preferredSize) {
			// Override create tool tip method to use HyperLinkTooltip
			public JToolTip createToolTip() {
				JToolTip tip = new HyperLinkToolTip();
				tip.setComponent(this);
				return tip;
			}
		};
		return vv;
	}

	/**
	 * Render connection arcs differently based on their projection strengths
	 *
	 * @param showProjectionStrengths
	 */
	public void setShowProjectionStrength(boolean showProjectionStrengths) {
		this.showProjectionStrength = showProjectionStrengths;
		refreshScreen();
	}

	public boolean getShowProjectionStrengths() {
		return this.showProjectionStrength;
	}

	private void setGraphMouse() {
		/*
		 * //the regular graph mouse for the normal view final
		 * DefaultModalGraphMouse<Node,Edge> graphMouse = new
		 * DefaultModalGraphMouse<Node,Edge>();
		 *
		 * vv.setGraphMouse(graphMouse); this.viewSupport = new
		 * MagnifyImageLensSupport<Node,Edge>(vv); this.modelSupport = new
		 * LayoutLensSupport<Node,Edge>(vv);
		 *
		 *
		 *
		 * graphMouse.addItemListener(modelSupport.getGraphMouse().getModeListener
		 * ());
		 * graphMouse.addItemListener(viewSupport.getGraphMouse().getModeListener
		 * ());
		 */

		gm = new PluggableGraphMouse();

		gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK));
		gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0,
				1.1f, 0.9f));
		gm.add(new CustomPickingGraphMousePlugin());
		gm.add(new AnimatedPickingGraphMousePlugin());

		// Trying out our new popup menu mouse plugin...
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
		// Add some popup menus for the edges and vertices to our mouse plugin.
		JPopupMenu edgeMenu = new MouseMenus.EdgeMenu();
		JPopupMenu vertexMenu = new MouseMenus.VertexMenu();
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);
		gm.add(myPlugin); // Add our new plugin to the mouse

		this.magnifyViewSupport = new ViewLensSupport<Node, Edge>(
				vv,
				new MagnifyShapeTransformer(vv, vv.getRenderContext()
						.getMultiLayerTransformer().getTransformer(Layer.VIEW)),
				new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(
						1.f, 6.f, .2f)));
		this.magnifyLayoutSupport = new LayoutLensSupport<Node, Edge>(vv,
				new MagnifyTransformer(vv, vv.getRenderContext()
						.getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT)),
				new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(
						1.f, 6.f, .2f)));
		vv.setGraphMouse(gm);
	}

	public GraphZoomScrollPane getGraphZoomScrollPane() {
		if (gzsp == null) {
			gzsp = new GraphZoomScrollPane(vv);
			gzsp.setPreferredSize(new Dimension(
					MultiScaleConnectomeBrowser.width + 20,
					MultiScaleConnectomeBrowser.height + 20));
		}
		return gzsp;
	}

	private VisualizationViewer<Node, Edge> getVisualizationViewer() {
		return vv;
	}

	public DirectedSparseMultigraph<Node, Edge> getGraph() {
		return graph;
	}

	public void lensNone(MouseEvent e) {
		System.out.println("Calling lensNone()");
		if (magnifyViewSupport != null) {
			System.out.println("deactivating magnifyViewSupport");
			magnifyViewSupport.deactivate();
			magnifyViewSupport = null;
			this.magnifyViewSupport = new ViewLensSupport<Node, Edge>(
					vv,
					new MagnifyShapeTransformer(vv, vv.getRenderContext()
							.getMultiLayerTransformer().getTransformer(
									Layer.VIEW)),
					new ModalLensGraphMouse(
							new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
			gm.add(new CustomPickingGraphMousePlugin());
			gm.add(new AnimatedPickingGraphMousePlugin());
		}
		if (magnifyLayoutSupport != null) {
			magnifyLayoutSupport.deactivate();
		}
	}

	public void lensView(MouseEvent e) {
		magnifyViewSupport.activate(true);
	}

	public void lensLayout(MouseEvent e) {
		magnifyLayoutSupport.activate(true);
	}

	private VisualizationViewer.GraphMouse getGraphMouse() {
		return getVisualizationViewer().getGraphMouse();
	}

	public void zoomIn() {
		scaler.scale(vv, 1.1f, vv.getCenter());
	}

	public void zoomOut() {
		scaler.scale(vv, 1 / 1.1f, vv.getCenter());
	}

	/**
	 * Saves an image of the current graph window and a .ppt with that image
	 * embedded in it to the local disk.
	 */
	public void saveImage() {
		File file;
		int value;
		int indx;
		String str;
		int width = vv.getWidth();
		int height = vv.getHeight();
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		// paint graph
		vv.paint(graphics);
		graphics.dispose();

		try {
			JFileChooser file_chooser = new JFileChooser();
			value = file_chooser.showSaveDialog(null);
			// close save window if user presses cancel
			if (value == JFileChooser.CANCEL_OPTION) {
				file_chooser = null;
				return;
			}
			file = file_chooser.getSelectedFile();
			// check that file does not contain a '.'
			if (file.getName().contains(".")) {
				System.out.println("File name: " + file.getName()
						+ " must not contain character '.'");
				return;
			}

			// power point slide generator.
			SlideShow slideShow = new SlideShow();
			Slide slide = slideShow.createSlide();
			str = file.getAbsolutePath() + ".ppt";
			// Capture the screen shot of the area of the screen defined by the
			// rectangle
			ImageIO.write(bi, "jpg", file);
			FileOutputStream out = new FileOutputStream(str);
			indx = slideShow.addPicture(file, Picture.JPEG);
			Picture pict = new Picture(indx);
			pict.setAnchor(new java.awt.Rectangle(80, 100, 700, 350));
			slide.addShape(pict);
			System.out.println("Writing file: " + file.getAbsolutePath());
			slideShow.write(out);
			out.close();
			file.deleteOnExit();
			file_chooser = null;

		} catch (IOException e2) {
			System.out.println(e2);
			e2.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void showBrainParts(Node node) {
		System.out.println("calling showBrainParts()");
		if (node.getPartOfNodes().isEmpty() == false) {
			if (node.isCollapsed()) {
				collapser.expand(node);
			}
		}
		// TODO pop up message saying there are no parts for this region.
	}

	/**
	 * Collapse a specific node
	 *
	 * @param node
	 *            - node to collapse
	 */
	public void collapse(Node node) {
		if (node.isCollapsed() == false) {
			collapser.collapse(node);
		}
	}

	private void expand() {
		collapser.expand();
	}

	/**
	 * Collapse all nodes
	 */
	private void collapse() {
		collapser.collapse();
	}

	private void compressEdges() {
		collapser.compressEdges();
	}

	private void expandEdges() {
		collapser.expandEdges();
	}

	public void reset() {
		collapser.reset();
	}

	private void test() {
		collapser.test();
	}

	/**
	 * Method takes a string and checks if the string matches the
	 * name of any of the nodes in the current graph.
	 * @param nodeNameToCheck - The string to compare the node names
	 * 							too.
	 * @return 				  - true if the string matches any of the
	 * 							graph's nodes name, else return false.
	 */
	public boolean containsNodeWithName(String nodeNameToCheck){
		for(Node currentNode:graph.getVertices()){
			if(currentNode.getName().toLowerCase().equalsIgnoreCase(nodeNameToCheck))
				return true;
		}
		return false;

	}

}
