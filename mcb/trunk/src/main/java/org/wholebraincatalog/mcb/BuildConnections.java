package org.wholebraincatalog.mcb;
/*Copyright (C) 2010 contact@wholebraincatalog.org
 *
 * Whole Brain Catalog is Licensed under the GNU Lesser Public License (LGPL), Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the license at
 *
 * http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The class is used to create the edges for the graph.  This class is implemented 
 * by Multi-Scale Connectome Browser.
 * @date    March 1, 2010
 * @author  Ruggero Carloz
 * @version 0.0.1
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyImageLensSupport;
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;



/**
 * Programs shows how collections of vertices can be collapsed
 * into a single vertex. In this application, the vertices that are
 * collapsed are those mouse-picked by the user. Any criteria
 * could be used to form the vertex collections to be collapsed,
 * perhaps some common characteristic of those vertex objects.
 * 
 * Note that the collection types don't use generics in this
 * application.
 * 
 * @author Tom Nelson
 * @author Ruggero Carloz - adaptation 
 * 
 */
@SuppressWarnings("serial")

public class BuildConnections extends JPanel{

	String instructions ="Use the mouse to select multiple vertices either by dragging a region, or by shift-clicking "+
	"on multiple vertices. After you select vertices, use the Collapse button to combine them \n"+
	"into a single vertex.  Select a 'collapsed' vertex and use the Expand button to restore the "+
	"collapsed vertices.  The Restore button will restore the original graph.  If you select 2 \n" +
	"(and only 2) vertices, then press the Compress Edges button, parallel edges between "+ 
	"those two vertices will no longer be expanded.  If you select 2 (and only 2) vertices, then \n"+
	"press the Expand Edges button, parallel edges between those two vertices will be "+
	"expanded.  You can drag the vertices with the mouse. Use the 'Picking'/'Transforming' \n"+
	"combo-box to switch between picking and transforming mode.  Rest mouse on an edge and a reference "+
	"message will appear.  The edges legent state the connectivite strength \n"+ "" +
	"between brain regions.  Press the 'Save' button under 'Save Image' and give the graph a name.  The graph will be saved as a power point. \n";		
	/**
	 * the graph
	 */
	Graph graph;

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer vv;

	/**
	 * graph layout.
	 */
	Layout layout;

	/**
	 * split that contains graph and instructions.
	 */
	static JSplitPane split_graph_help;

	/**
	 * split that contains split_graph_help and option buttons.
	 */
	static JSplitPane split;

	/**
	 * graph collapser.
	 */
	GraphCollapser collapser;

	/**
	 * used to create temporary file
	 */
	JFileChooser file_chooser;

	/**
	 * the gui frame
	 */
	static JFrame f;	

	/**
	 * Lens objects
	 */
	LensSupport viewSupport;
	LensSupport modelSupport;
	LensSupport magnifyLayoutSupport;
	LensSupport magnifyViewSupport;


	@SuppressWarnings("unchecked")
	public BuildConnections(Node[] nodes, int numberElements) throws IOException {

		// create a simple graph for the demo
		graph = new DirectedSparseMultigraph();

		//construct graph by making graph connections.
		makeConnections(nodes, numberElements);

		collapser = new GraphCollapser(graph);

		layout = new CircleLayout(graph);

		SlideShow slideShow = new SlideShow();
		Slide slide = slideShow.createSlide();

		Dimension preferredSize = new Dimension(800,400);
		final VisualizationModel visualizationModel = 
			new DefaultVisualizationModel(layout, preferredSize);
		vv =  new VisualizationViewer(visualizationModel, preferredSize);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

		
		final PredicatedParallelEdgeIndexFunction eif = PredicatedParallelEdgeIndexFunction.getInstance();
		final Set exclusions = new HashSet();
		eif.setPredicate(new Predicate() {

			public boolean evaluate(Object e) {

				return exclusions.contains(e);
			}});


		vv.getRenderContext().setParallelEdgeIndexFunction(eif);

		vv.setBackground(Color.white);

		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller() {


			/* (non-Javadoc)
			 * @see edu.uci.ics.jung.visualization.decorators.DefaultToolTipFunction#getToolTipText(java.lang.Object)
			 */
			@Override
			public String transform(Object v) {
				if(v instanceof Graph) {
					return ((Graph)v).getVertices().toString();
				}
				return super.transform(v);
			}});

		vv.setEdgeToolTipTransformer(new MyLabeller());
		
		 vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Edge, BasicStroke>() {
			    /**
			     * Transforms the input by ignoring it and returning the stored constant instead.
			     *
			     * @param input the input object which is ignored
			     * @return the stored constant
			     */
			    public BasicStroke transform(Edge input) {
			    	switch (input.getStrength()) {
			    	case EXISTS:
			    		return new BasicStroke(0.5f);
			    	case VERY_LIGHT:
			    		return new BasicStroke(1f);
			    	case LIGHT:
			    		return new BasicStroke(2f);
			    	case MODERATE:
			    		return new BasicStroke(3f);
			    	}
			        return new BasicStroke(2.5f);
			    }
		 });		
		
		//the regular graph mouse for the normal view
		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

		vv.setGraphMouse(graphMouse);

		Container content = this;
		GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
		content.add(gzsp);

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1/1.1f, vv.getCenter());
			}
		});

		JButton collapse = new JButton("Collapse");
		collapse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
				if(picked.size() > 1) {
					Graph inGraph = layout.getGraph();
					Graph clusterGraph = collapser.getClusterGraph(inGraph, picked);

					Graph g = collapser.collapse(layout.getGraph(), clusterGraph);
					double sumx = 0;
					double sumy = 0;
					for(Object v : picked) {
						Point2D p = (Point2D)layout.transform(v);
						sumx += p.getX();
						sumy += p.getY();
					}
					Point2D cp = new Point2D.Double(sumx/picked.size(), sumy/picked.size());
					vv.getRenderContext().getParallelEdgeIndexFunction().reset();
					layout.setGraph(g);
					layout.setLocation(clusterGraph, cp);
					vv.getPickedVertexState().clear();
					vv.repaint();
				}
			}});

		JButton compressEdges = new JButton("Compress Edges");
		compressEdges.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Collection picked = vv.getPickedVertexState().getPicked();
				if(picked.size() == 2) {
					Pair pair = new Pair(picked);
					Graph graph = layout.getGraph();
					Collection edges = new HashSet(graph.getIncidentEdges(pair.getFirst()));
					edges.retainAll(graph.getIncidentEdges(pair.getSecond()));
					exclusions.addAll(edges);
					vv.repaint();
				}

			}});

		JButton expandEdges = new JButton("Expand Edges");
		expandEdges.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Collection picked = vv.getPickedVertexState().getPicked();
				if(picked.size() == 2) {
					Pair pair = new Pair(picked);
					Graph graph = layout.getGraph();
					Collection edges = new HashSet(graph.getIncidentEdges(pair.getFirst()));
					edges.retainAll(graph.getIncidentEdges(pair.getSecond()));
					exclusions.removeAll(edges);
					vv.repaint();
				}

			}});

		JButton expand = new JButton("Expand");
		expand.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
				for(Object v : picked) {
					if(v instanceof Graph) {

						Graph g = collapser.expand(layout.getGraph(), (Graph)v);
						vv.getRenderContext().getParallelEdgeIndexFunction().reset();
						layout.setGraph(g);
					}
					vv.getPickedVertexState().clear();
					vv.repaint();
				}
			}});

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				layout.setGraph(graph);
				exclusions.clear();
				vv.repaint();
			}});
		JButton graph_save = new JButton("Save Image");
		graph_save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
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
					;
				} catch (IOException e2) {
					System.out.println(e2);
					e2.printStackTrace();
				}catch(Exception e1){e1.printStackTrace();}

			}});


		this.viewSupport = new MagnifyImageLensSupport<Number,Number>(vv);
		this.modelSupport = new LayoutLensSupport<Number,Number>(vv);

		graphMouse.addItemListener(modelSupport.getGraphMouse().getModeListener());
		graphMouse.addItemListener(viewSupport.getGraphMouse().getModeListener());

		ButtonGroup radio = new ButtonGroup();
		JRadioButton none = new JRadioButton("None");

		none.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(viewSupport != null) {
					viewSupport.deactivate();
				}
				if(modelSupport != null) {
					modelSupport.deactivate();
				}
			}
		});

		none.setSelected(true);

		JRadioButton hyperView = new JRadioButton("View");
		hyperView.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				viewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		JRadioButton hyperModel = new JRadioButton("Layout");
		hyperModel.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				modelSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		radio.add(none);
		radio.add(hyperView);
		radio.add(hyperModel);
		JMenuBar menubar = new JMenuBar();
		JMenu modeMenu = graphMouse.getModeMenu();
		menubar.add(modeMenu);

		JPanel controls = new JPanel();
		JPanel zoomControls = new JPanel(new GridLayout(2,1));
		zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
		zoomControls.add(plus);
		zoomControls.add(minus);
		controls.add(zoomControls);
		JPanel collapseControls = new JPanel(new GridLayout(3,1));
		collapseControls.setBorder(BorderFactory.createTitledBorder("Picked"));
		collapseControls.add(collapse);
		collapseControls.add(expand);
		collapseControls.add(compressEdges);
		collapseControls.add(expandEdges);
		collapseControls.add(reset);
		JPanel saveFile = new JPanel(new GridLayout(1,3));
		saveFile.setBorder(BorderFactory.createTitledBorder("Save"));
		saveFile.add(graph_save);

		controls.add(collapseControls);
		controls.add(modeBox);
		controls.add(saveFile);
		content.add(controls, BorderLayout.SOUTH);


		JTextArea label = new JTextArea(instructions);
		label.setEnabled(false);

		// Splitting the window in two parts.
		split_graph_help = new JSplitPane(JSplitPane.VERTICAL_SPLIT,label,vv);
		split_graph_help.setOneTouchExpandable(false);
		split_graph_help.setDividerLocation(100);
		// Splitting the window in two parts.
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,split_graph_help,controls);
		split.setOneTouchExpandable(false);
		split.setDividerLocation(500);
		JPanel lensPanel = new JPanel(new GridLayout(2,0));
		lensPanel.setBorder(BorderFactory.createTitledBorder("Lens"));
		lensPanel.add(none);
		lensPanel.add(hyperView);
		lensPanel.add(hyperModel);
		controls.add(lensPanel);


	}
	/**
	 * A demo class that will create a vertex shape that is either a
	 * polygon or star. The number of sides corresponds to the number
	 * of vertices that were collapsed into the vertex represented by
	 * this shape.
	 * 
	 * @author Tom Nelson
	 *
	 * @param <V>
	 */
	class ClusterVertexShapeFunction<V> extends EllipseVertexShapeTransformer<V> {

		ClusterVertexShapeFunction() {
			setSizeTransformer(new ClusterVertexSizeFunction<V>(20));
		}
		@Override
		public Shape transform(V v) {
			if(v instanceof Graph) {
				int size = ((Graph)v).getVertexCount();
				if (size < 8) {   
					int sides = Math.max(size, 3);
					return factory.getRegularPolygon(v, sides);
				}
				else {
					return factory.getRegularStar(v, size);
				}
			}
			return super.transform(v);
		}
	}

	/**
	 * A demo class that will make vertices larger if they represent
	 * a collapsed collection of original vertices
	 * @author Tom Nelson
	 *
	 * @param <V>
	 */
	class ClusterVertexSizeFunction<V> implements Transformer<V,Integer> {
		int size;
		public ClusterVertexSizeFunction(Integer size) {
			this.size = size;
		}

		public Integer transform(V v) {
			if(v instanceof Graph) {
				return 30;
			}
			return size;
		}
	}
	/**
	 *  Method creates graph by building connections between nodes.
	 *  @param nodes - array containing the nodes used to make graph.
	 *  @param numberElements - number of nodes to be connected.
	 *  @author Ruggero Carloz
	 */
	@SuppressWarnings("unchecked")
	private void makeConnections(Node[] node, int numberElements) {

		for(int i = 0; i < numberElements ; i++){
			for(int j = 0; j < numberElements ; j++){
				//check that node[i] has a sending structure to node[j]
				if(node[i].getNode().getTree().contains(node[j].getVertexName())){
					graph.addEdge(new Edge(node[i].getRegionToStrengthMap().get(node[j].getVertexName()),
							node[i].getNode().getReferenceSet().get(node[j].getVertexName())),
							node[i].getVertexName(),node[j].getVertexName(), EdgeType.DIRECTED);

				}
			}
		}		
	}

	/*
	 * Driver for application
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		try {
			Node[] data = new Node[7];

			DataReader sGlobusPallidus = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Globus_pallidus%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Globus pallidus","http://api.talis.com/stores/neurolex/services/sparql?query=select+%24region+%24cellLabel+%24neurotransmitterLabel+%24roleLabel+{+%24region+%24x+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FCategory%3AGlobus_pallidus%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALocated_in%3E+%24region.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E++%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FCategory-3APrincipal_neuron_role%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24cellLabel.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ANeurotransmitter%3E+%24neurotransmitter.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24neurotransmitterLabel.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E+%24role.%0D%0A%24role+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24roleLabel.%0D%0A}");
			// obtain the data from the URLs
			/**DataReader sCaudoputamen = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Caudoputamen%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Caudoputamen");
			DataReader sCentralNucleusOfAmygdala = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Central_nucleus_of_amygdala%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Central nucleus of amygdala");
			DataReader sSubstantiaNigraCompactPart = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Substantia_nigra_compact_part%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Substantia nigra pars compacta","http://api.talis.com/stores/neurolex/services/sparql?query=select+%24region+%24cellLabel+%24neurotransmitterLabel+%24roleLabel+{+%24region+%24x+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FCategory%3ASubstantia_nigra_pars_compacta%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALocated_in%3E+%24region.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E++%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FCategory-3APrincipal_neuron_role%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24cellLabel.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ANeurotransmitter%3E+%24neurotransmitter.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24neurotransmitterLabel.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E+%24role.%0D%0A%24role+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24roleLabel.%0D%0A}");
			DataReader sVentralTegmentalArea = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Ventral_tegmental_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Ventral tegmental area","http://api.talis.com/stores/neurolex/services/sparql?query=select+%24region+%24cellLabel+%24neurotransmitterLabel+%24roleLabel+{+%24region+%24x+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FCategory%3AVentral_tegmental_area%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALocated_in%3E+%24region.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E++%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FCategory-3APrincipal_neuron_role%3E.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24cellLabel.%0D%0A%24cell+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ANeurotransmitter%3E+%24neurotransmitter.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24neurotransmitterLabel.%0D%0A%24neurotransmitter+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3AHas_role%3E+%24role.%0D%0A%24role+%3Chttp%3A%2F%2Fneurolex.org%2Fwiki%2FSpecial%3AURIResolver%2FProperty-3ALabel%3E+%24roleLabel.%0D%0A}");
			DataReader sPrelimbicArea = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Prelimbic_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Prelimbic area");
			DataReader sLateralPreopticArea = new DataReader(
					"http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Lateral_preoptic_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}",
			"Lateral preoptic area");

			// store node data
			data[0] = sCaudoputamen.getNode();**/
			data[1] = sGlobusPallidus.getNode();
			/**data[2] = sCentralNucleusOfAmygdala.getNode();
			data[3] = sSubstantiaNigraCompactPart.getNode();
			data[4] = sVentralTegmentalArea.getNode();
			data[5] = sPrelimbicArea.getNode();
			data[6] = sLateralPreopticArea.getNode();**/

			f = new JFrame(
			"Multi-Scale Connectome Browser version-0.1.5-alpha");
			f.setSize(500, 900);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(new BuildConnections(data, 7));
			f.add(split);
			f.pack();
			f.setVisible(true);


		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}
	}
}


