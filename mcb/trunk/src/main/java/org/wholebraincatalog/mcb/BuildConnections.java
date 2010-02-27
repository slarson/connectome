package org.wholebraincatalog.mcb;
/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
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
import edu.uci.ics.jung.visualization.util.PredicatedParallelEdgeIndexFunction;


/**
 * A demo that shows how collections of vertices can be collapsed
 * into a single vertex. In this demo, the vertices that are
 * collapsed are those mouse-picked by the user. Any criteria
 * could be used to form the vertex collections to be collapsed,
 * perhaps some common characteristic of those vertex objects.
 * 
 * Note that the collection types don't use generics in this
 * demo, because the vertices are of two types: String for plain
 * vertices, and Graph<String,Number> for the collapsed vertices.
 * 
 * @author Tom Nelson
 * @author Ruggero Carloz - adaptation 
 * 
 */
@SuppressWarnings("serial")
public class BuildConnections extends JApplet {

    String instructions =
        "<html>Use the mouse to select multiple vertices"+
        "<p>either by dragging a region, or by shift-clicking"+
        "<p>on multiple vertices."+
        "<p>After you select vertices, use the Collapse button"+
        "<p>to combine them into a single vertex."+
        "<p>Select a 'collapsed' vertex and use the Expand button"+
        "<p>to restore the collapsed vertices."+
        "<p>The Restore button will restore the original graph."+
        "<p>If you select 2 (and only 2) vertices, then press"+
        "<p>the Compress Edges button, parallel edges between"+
        "<p>those two vertices will no longer be expanded."+
        "<p>If you select 2 (and only 2) vertices, then press"+
        "<p>the Expand Edges button, parallel edges between"+
        "<p>those two vertices will be expanded."+
        "<p>You can drag the vertices with the mouse." +
        "<p>Use the 'Picking'/'Transforming' combo-box to switch"+
        "<p>between picking and transforming mode.</html>";
    /**
     * the graph
     */
    Graph graph;

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer vv;
    
    Layout layout;
    static JSplitPane split;
    GraphCollapser collapser;

    public BuildConnections(Node[] nodes, int numberElements) {
        
        // create a simple graph for the demo
        graph = new DirectedSparseMultigraph();
        
      //construct graph by making graph connections.
		makeConnections(nodes, numberElements);
		
        collapser = new GraphCollapser(graph);
        
        layout = new FRLayout(graph);

        Dimension preferredSize = new Dimension(400,400);
        final VisualizationModel visualizationModel = 
            new DefaultVisualizationModel(layout, preferredSize);
        vv =  new VisualizationViewer(visualizationModel, preferredSize);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction());
        
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
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        
        Container content = getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        content.add(gzsp);
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        /**String str = "Controls: Use mouse wheel to zoom in and out.\n"+
		" Under Menu use TRANSFORMING to move graph (click left mouse button and drag) \n" +
		"   and PICKING to manipulate graph nodes. (click left mouse button on red nodes and drag).\n" +
		" In PICKING mode, you can also drag a box around multiple nodes \n" +
		"   (click left mouse button outside a node and drag a box around multiple nodes)";
	
		// Graphs GUI
		JTextArea label = new JTextArea(str);
		label.setEnabled(false);
		// Splitting the window in two parts.
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,label,vv);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(100);**/
		
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
        
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog((JComponent)e.getSource(), instructions, "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });

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
        controls.add(collapseControls);
        controls.add(modeBox);
        controls.add(help);
        content.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * a demo class that will create a vertex shape that is either a
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
	 * Method creates graph by building connections between nodes.
	 * @param nodes - array containing the nodes used to make graph.
	 * @param numberElements - number of nodes to be connected.
	 */
	private void makeConnections(Node[] node, int numberElements) {
	
		for(int i = 0; i < numberElements ; i++){
			for(int j = 0; j < numberElements ; j++){
				//check that node[i] has a sending structure to node[j]
				if(node[i].getNode().getTree().contains(node[j].getVertexName())){
					graph.addEdge(new Edge(),node[i].getVertexName(),node[j].getVertexName(), EdgeType.DIRECTED);
				}
			}
		}		
	}

    /**
     * a driver for this demo
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	
    	Node[] data = new Node[7];
		
		//obtain the data from the URLs
		DataReader sCaudoputamen = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Caudoputamen%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A","Caudoputamen");
		DataReader sGlobusPallidus = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Globus_pallidus%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A","Globus pallidus");
		DataReader sCentralNucleusOfAmygdala = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24t+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Central_nucleus_of_amygdala%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24t%0D%0A}%0D%0A", "Central nucleus of amygdala");
		DataReader sSubstantiaNigraCompactPart = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24oSend+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Substantia_nigra_compact_part%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24oSend%0D%0A}","Substantia nigra compact part");
		DataReader sVentralTegmentalArea = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24oSend+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Ventral_tegmental_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24oSend%0D%0A}","Ventral tegmental area");
		DataReader sPrelimbicArea = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24oSend+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Prelimbic_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24oSend%0D%0A}","Prelimbic area");
		DataReader sLateralPreopticArea = new DataReader("http://api.talis.com/stores/neurolex-dev1/services/sparql?query=select+%24oSend+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Lateral_preoptic_area%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24oSend%0D%0A}","Lateral preoptic area");

		data[0] = sCaudoputamen.getNode();
		data[1] = sGlobusPallidus.getNode();
		data[2] = sCentralNucleusOfAmygdala.getNode();
		data[3] = sSubstantiaNigraCompactPart.getNode();
		data[4] = sVentralTegmentalArea.getNode();
		data[5] = sPrelimbicArea.getNode();
		data[6] = sLateralPreopticArea.getNode();
        JFrame f = new JFrame("Multi-Scale Connectome Browser");

        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new BuildConnections(data, 7));
        f.pack();
        f.setVisible(true);
    }
}


