package org.wholebraincatalog.mcb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.wholebraincatalog.mcb.graph.ConnectionEdge;
import org.wholebraincatalog.mcb.graph.Edge;
import org.wholebraincatalog.mcb.graph.GraphManager;
import org.wholebraincatalog.mcb.graph.Node;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;


/**
 * Creates and holds the Swing GUI elements like the panels and the buttons.
 *
 */
public class View extends JPanel{

	/**
	 * split that contains graph and instructions.
	 */
	static JSplitPane split_graph_help;
	

	/**
	 * split that contains split_graph_help and option buttons.
	 */
	static JSplitPane split;

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
	

	public View() {
		buildGUI();
	}

	private void buildGUI() {
		ButtonGroup radio = new ButtonGroup();
		JRadioButton none = new JRadioButton("None");

	
		none.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				GraphManager.getInstance().lensNone();
			}
		});

		none.setSelected(true);
		
		JRadioButton hyperView = new JRadioButton("View");
		hyperView.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				GraphManager.getInstance().lensView(e);
			}
		});
		JRadioButton hyperModel = new JRadioButton("Layout");
		hyperModel.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				GraphManager.getInstance().lensLayout(e);
			}
		});

		radio.add(none);
		radio.add(hyperView);
		radio.add(hyperModel);
		
		JMenuBar menubar = new JMenuBar();

		DefaultModalGraphMouse<Node, Edge> graphMouse 
		= GraphManager.getInstance().getGraphMouse();
		JMenu modeMenu = graphMouse.getModeMenu();
		menubar.add(modeMenu);

		Container content = this;
		GraphZoomScrollPane gzsp = GraphManager.getInstance().getGraphZoomScrollPane();
		content.add(gzsp);
		

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);


		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().zoomIn();
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().zoomOut();
			}
		});

		JButton collapse = new JButton("Collapse");
		collapse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().collapse();
			}});

		JButton compressEdges = new JButton("Compress Edges");
		compressEdges.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().compressEdges();

			}});

		JButton expandEdges = new JButton("Expand Edges");
		expandEdges.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().expandEdges();

			}});

		JButton expand = new JButton("Expand");
		expand.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().expand();
			}});

		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().reset();
			}});
		JButton test = new JButton("test");
		test.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().test();
			}
		});
		JButton graph_save = new JButton("Save Image");
		graph_save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e){
				GraphManager.getInstance().saveImage();

			}});

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
		collapseControls.add(test);
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
		split_graph_help = 
			new JSplitPane(JSplitPane.VERTICAL_SPLIT,label,
					GraphManager.getInstance().getVisualizationViewer());
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
	
	public JPanel getMainPanel() {
		return this;
	}
	
	public JSplitPane getSplitPanel() {
		return split;
	}
}
