package org.wholebrainproject.mcb.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.wholebrainproject.mcb.data.BuildConnections;
import org.wholebrainproject.mcb.mousemenu.MouseMenus;
import org.wholebrainproject.mcb.mousemenu.PopupVertexEdgeMenuMousePlugin;
import org.wholebrainproject.mcb.util.HyperLinkToolTip;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.jai.TransformingImageVertexIconRenderer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyImageLensSupport;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;

/**
 * The GraphManager holds the graph model, handles layout, and handles modifications
 * to the graph that occur via user interaction with the GUI.  It follows the singleton pattern.
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
	Graph<Node,Edge> graph;

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<Node,Edge> vv;

	/**
	 * graph layout.
	 */
	AggregateLayout<Node, Edge> layout;

	/**
	 * graph collapser.
	 */
	CustomGraphCollapser collapser;

	/**
	 * Scales the graph.
	 */
	final ScalingControl scaler;

	/**
	 * Lens objects
	 */
	LensSupport viewSupport;
	LensSupport modelSupport;
	LensSupport magnifyLayoutSupport;
	LensSupport magnifyViewSupport;


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

	private GraphManager() {

		graph = new DirectedSparseMultigraph<Node,Edge>();

		BuildConnections.getDataAndCreateGraph(graph);

		layout = 
			new AggregateLayout<Node,Edge>(
					new CircleLayout<Node,Edge>(graph));

		scaler = new CrossoverScalingControl();
		
		Dimension preferredSize = new Dimension(800,400);
		final VisualizationModel<Node,Edge> visualizationModel = 
			new DefaultVisualizationModel<Node,Edge>(layout, preferredSize);
		vv =  new VisualizationViewer<Node,Edge>(visualizationModel, preferredSize) {
			// Override create tool tip method to use HyperLinkTooltip
			public JToolTip createToolTip() {
				JToolTip tip = new HyperLinkToolTip();
				tip.setComponent(this);
				return tip;
			}
		};

		setGraphMouse();

		VertexLabelAsShapeRenderer vlasr = new VertexLabelAsShapeRenderer(vv.getRenderContext());

		vv.getRenderContext().setVertexShapeTransformer(vlasr);

		vv.getRenderContext().setVertexLabelTransformer(new NodeLabeller());

		vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.RED));
		vv.getRenderContext().setEdgeLabelTransformer(new EdgeLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

		// Setup up a new vertex to paint transformer to change node color.
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node,Paint>() {
			public Paint transform(Node input) {
				return Color.WHITE;
			}
		});

		final PredicatedParallelEdgeIndexFunction<Node,Edge> eif =
			PredicatedParallelEdgeIndexFunction.getInstance();

		eif.setPredicate(new Predicate() {

			public boolean evaluate(Object e) {

				return exclusions.contains(e);
			}});


		vv.getRenderContext().setParallelEdgeIndexFunction(eif);

		vv.setBackground(Color.LIGHT_GRAY);

		//***** add a listener for USE CLASS NodeLabeller*****
		vv.setVertexToolTipTransformer(new ToolTipNodeLabeller());
		//vv.setVertexToolTipTransformer(new ToStringLabeller());

		vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Edge, Stroke>() {
			/**
			 * Transforms the input by ignoring it and returning the stored constant instead.
			 *
			 * @param input the input object which is ignored
			 * @return the stored constant
			 */
			public BasicStroke transform(Edge input) {
				return input.getStroke();
			}
		});

		vv.setEdgeToolTipTransformer(new ToolTipEdgeLabeller());
		

		collapser = new CustomGraphCollapser(graph, layout, vv, exclusions);
				
		//by default, collapse all the edges
		collapser.collapse();
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
		 * graphMouse.addItemListener(modelSupport.getGraphMouse().getModeListener
		 * ());
		 * graphMouse.addItemListener(viewSupport.getGraphMouse().getModeListener
		 * ());
		 */

		EditingModalGraphMouse gm = new EditingModalGraphMouse(vv
				.getRenderContext(), null, null);
		
		// Trying out our new popup menu mouse plugin...
		PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
		// Add some popup menus for the edges and vertices to our mouse plugin.
		JPopupMenu edgeMenu = new MouseMenus.EdgeMenu();
		JPopupMenu vertexMenu = new MouseMenus.VertexMenu();
		myPlugin.setEdgePopup(edgeMenu);
		myPlugin.setVertexPopup(vertexMenu);
		gm.remove(gm.getPopupEditingPlugin()); // Removes the existing popup
												// editing plugin
		gm.add(myPlugin); // Add our new plugin to the mouse

		vv.setGraphMouse(gm);
	}

	public GraphZoomScrollPane getGraphZoomScrollPane() {
		return new GraphZoomScrollPane(vv);
	}

	public VisualizationViewer<Node,Edge> getVisualizationViewer() {
		return vv;
	}

	public void lensNone() {
		if(viewSupport != null) {
			viewSupport.deactivate();
		}
		if(modelSupport != null) {
			modelSupport.deactivate();
		}
	}

	public void lensView(ItemEvent e) {
		viewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
	}

	public void lensLayout(ItemEvent e) {
		modelSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
	}

	public VisualizationViewer.GraphMouse getGraphMouse() {
		return vv.getGraphMouse();
	}

	public void zoomIn() {
		scaler.scale(vv, 1.1f, vv.getCenter());
	}

	public void zoomOut() {
		scaler.scale(vv, 1/1.1f, vv.getCenter());
	}

	public void saveImage() {
		File file;
		int value;
		int indx;
		String str;
		int width = vv.getWidth();
		int height = vv.getHeight();
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		//paint graph
		vv.paint(graphics);
		graphics.dispose();

		try {
			JFileChooser file_chooser = new JFileChooser();
			value = file_chooser.showSaveDialog(null);
			//close save window if user presses cancel
			if(value == JFileChooser.CANCEL_OPTION){
				file_chooser = null;
				return;
			}	
			file = file_chooser.getSelectedFile();
			//check that file does not contain a '.'
			if(file.getName().contains(".")){
				System.out.println("File name: "+file.getName()+" must not contain character '.'");
				return;
			}

			//power point slide generator.
			SlideShow slideShow = new SlideShow();
			Slide slide= slideShow.createSlide();
			str = file.getAbsolutePath()+".ppt";
			// Capture the screen shot of the area of the screen defined by the rectangle
			ImageIO.write(bi,"jpg", file);
			FileOutputStream out = new FileOutputStream(str);
			indx = slideShow.addPicture(file, Picture.JPEG);
			Picture pict = new Picture(indx);
			pict.setAnchor(new java.awt.Rectangle(80,100,700,350));
			slide.addShape(pict);
			System.out.println("Writing file: "+file.getAbsolutePath());
			slideShow.write(out);
			out.close();
			file.deleteOnExit();
			file_chooser = null;
			
		} catch (IOException e2) {
			System.out.println(e2);
			e2.printStackTrace();
		}catch(Exception e1){e1.printStackTrace();}
	}
	
	public void showBrainParts(Node node) {
		if (node.getPartOf().isEmpty() == false) {
			if (node.isCollapsed()) {
				collapser.expand(node);
			}
		}
		//TODO pop up message saying there are no parts for this region.
	}

	public void hideBrainParts(Node node) {
		// TODO Auto-generated method stub
		
	}

	public void expand() {
		collapser.expand();
	}

	public void collapse() {
		collapser.collapse();
	}

	public void compressEdges() {
		collapser.compressEdges();
	}

	public void expandEdges() {
		collapser.expandEdges();
	}

	public void reset() {
		collapser.reset();
	}

	public void test() {
		collapser.test();
	}


	
	
}
