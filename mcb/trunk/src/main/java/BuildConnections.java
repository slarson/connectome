
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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
/* The class creates and displays a graph given a set of nodes to connect.
 * @date   February 23, 2010
 * @author Ruggero Carloz ----
 *
 */

public class BuildConnections {

	private JFrame frame;

	private static Graph<Node,Edge> gr;
	
	public BuildConnections(Node[] nodes, int numberElements){
		//Generic graph.
		gr = new DirectedSparseMultigraph<Node, Edge>();
		
		makeConnections(nodes, numberElements);
	
		// Object foe visualization of graph.
		Layout<Node, Edge> layout = new CircleLayout(gr);	
	
	
		// set the size.
		layout.setSize(new Dimension(400,400));
		
		VisualizationViewer<Node,Edge> bvs = new VisualizationViewer<Node,Edge>(layout);
	
		// Add node and edge labels.
		bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		//bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
	
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
					gr.addEdge(new Edge(),node[i],node[j], EdgeType.DIRECTED);
				}
			}
		}		
	}
	
	/**
	 * Method gives the graph frame.
	 * @return frame - graph frame.
	 */
	public JFrame getFrame(){
		return frame;
	}
	
}