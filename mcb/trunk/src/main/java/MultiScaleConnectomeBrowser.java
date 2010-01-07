import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;



import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;



/**
 * This application creates a simple graph with zoom capabilities.
 * This application will be used as a template for the development of Multi-Sacale Connectome Browser.
 * @date    January 6, 2010
 * @author  Ruggero Carloz
 * @version 0.0.2
 */
public class MultiScaleConnectomeBrowser{
	


	public static void main(String args[]){
		
		Node n1,n2,n3,n4;
        
		//Generic graph.
		Graph<Node, Link> gr = new DirectedSparseMultigraph<Node, Link>();
		
		
		
		//Creating the nodes. 
		n1 = new Node("Cortex");
		n2 = new Node("Basal Ganglia");
		n3 = new Node("Thalamus");
		n4 = new Node("Brain Stem");
		
		
		//Connecting nodes with directed edges.
		gr.addEdge(new Link(),n1,n2, EdgeType.DIRECTED);
		gr.addEdge(new Link(),n3,n1, EdgeType.DIRECTED);
		gr.addEdge(new Link(),n2,n3, EdgeType.DIRECTED);
		gr.addEdge(new Link(),n4,n3, EdgeType.DIRECTED);
		

	
		// Object foe visualization of graph.
		Layout<Node, Link> layout = new CircleLayout(gr);	

	
        // set the size.
		layout.setSize(new Dimension(400,400));
		
		VisualizationViewer<Node,Link> bvs = new VisualizationViewer<Node,Link>(layout);
	
		// Add node and edge labels.
		bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		
		// Create a graph mouse.
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		bvs.setGraphMouse(gm);
	
		
		String str = "Controls: Use mouse wheel to zoom in and out.\n"+
		" Under Menu use TRANSFORMING to move graph (click left mouse button and drag) \n" +
		"   and PICKING to manipulate graph nodes. (click left mouse button on red nodes and drag).\n" +
		" In PICKING mode, you can also drag a box around multiple nodes \n" +
		"   (click left mouse button outside a node and drag a box around multiple nodes)";
	
		// Graphs GUI
		JTextArea label = new JTextArea(str);
		label.setEnabled(false);
		JFrame frame =  new JFrame("Multi-Scale Connectome Browser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		

		frame.getContentPane().add(bvs);
		JMenuBar mb = new JMenuBar();
		JMenu mm = gm.getModeMenu();
		mm.setText("Menu");
		mm.setPreferredSize(new Dimension(80,20));
		mb.add(mm);
		frame.setJMenuBar(mb);
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		
		// Splitting the window in two parts.
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,label,bvs);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(100);
	
		frame.add(split);
		frame.setSize(800, 600);
		frame.pack();
		frame.setVisible(true);	
	}
}
