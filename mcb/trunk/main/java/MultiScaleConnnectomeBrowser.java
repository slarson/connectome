import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;



import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;



/**
 * This application creates a simple graph with zoom capabilities.
 * This application will be used as a template for future applications.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.2
 */
public class MultiScaleConnnectomeBrowser{
	


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
		gr.addEdge(new Link(0.0,48),n1,n2, EdgeType.DIRECTED);
		gr.addEdge(new Link(0.0,48),n3,n1, EdgeType.DIRECTED);
		gr.addEdge(new Link(0.0,48),n2,n3, EdgeType.DIRECTED);
		gr.addEdge(new Link(0.0,48),n4,n3, EdgeType.DIRECTED);
		

	
		// Object foe visualization of graph.
		Layout<Node, Link> layout = new CircleLayout(gr);	

	
        // set the size.
		layout.setSize(new Dimension(300,300));
		
		VisualizationViewer<Node,Link> bvs = new VisualizationViewer<Node,Link>(layout);
		//bvs.setPreferredSize(new Dimension(350,350));
	
		// Add node and edge labels.
		bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		
		// Create a graph mouse.
		EditingModalGraphMouse gm = new EditingModalGraphMouse(bvs.getRenderContext(),new Node("w"),new Link(2.0,3.0));
		gm.getPopupEditingPlugin();
		bvs.setGraphMouse(gm);
		
		// Graphs GUI
		JFrame frame =  new JFrame("Multi-Scale Connnectome Browser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(bvs);
		JMenuBar mb = new JMenuBar();
		JMenu mm = gm.getModeMenu();
		mm.remove(2);
		mm.setText("Menu");
		//mm.setIcon(null);
		mm.setPreferredSize(new Dimension(80,20));
		mb.add(mm);
		frame.setJMenuBar(mb);
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		frame.pack();
		frame.setVisible(true);
		
		
	}
}
