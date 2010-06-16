package org.wholebrainproject.mcb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

import org.wholebrainproject.mcb.graph.GraphManager;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;


/**
 * Creates and holds the Swing GUI elements like the panels and the buttons.
 *
 */
public class View extends JPanel{

	/**
	 * split that contains split_graph_help and option buttons.
	 */
	static JSplitPane split;

	static String instructionString ="Use the mouse to select multiple vertices either by dragging a region, or by shift-clicking "+
	"on multiple vertices. After you select vertices, use the Collapse button to combine them \n"+
	"into a single vertex.  Select a 'collapsed' vertex and use the Expand button to restore the "+
	"collapsed vertices.  The Restore button will restore the original graph.  If you select 2 \n" +
	"(and only 2) vertices, then press the Compress Edges button, parallel edges between "+ 
	"those two vertices will no longer be expanded.  If you select 2 (and only 2) vertices, then \n"+
	"press the Expand Edges button, parallel edges between those two vertices will be "+
	"expanded.  You can drag the vertices with the mouse. Use the 'Picking'/'Transforming' \n"+
	"combo-box to switch between picking and transforming mode.  Rest mouse on an edge and a reference "+
	"message will appear.  The edges legent state the connectivite strength \n"+ "" +
	"between brain regions.  Press the 'Save' button under 'Save Image' and give the graph a name.  The graph will be saved as a power point. \n" +
	"\n\nYou can recall these instructions from the help menu at any time.";		
	

	public View() {
		buildGUI();
	}

	private void buildGUI() {
		JMenuBar menubar = new JMenuBar();

		VisualizationViewer.GraphMouse graphMouse = GraphManager
				.getInstance().getGraphMouse();
		JMenu modeMenu = ((AbstractModalGraphMouse) graphMouse).getModeMenu();
		menubar.add(modeMenu);

		Container content = this;
		GraphZoomScrollPane gzsp = GraphManager.getInstance()
				.getGraphZoomScrollPane();
		content.add(gzsp);
		
		JComboBox modeBox = ((AbstractModalGraphMouse) graphMouse).getModeComboBox();
		modeBox.addItemListener(((AbstractModalGraphMouse) graphMouse).getModeListener());
		((AbstractModalGraphMouse) graphMouse).setMode(ModalGraphMouse.Mode.PICKING);

		JPanel controls = new JPanel();
		
		controls.add(modeBox);
		controls.add(getLensPanel());
		
		content.add(controls, BorderLayout.SOUTH);

	}
	
	protected Component getLensPanel() {
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
		
		ButtonGroup radio = new ButtonGroup();
		radio.add(none);
		radio.add(hyperView);
		radio.add(hyperModel);
		
		JPanel lensPanel = new JPanel(new GridLayout(2,0));
		lensPanel.setBorder(BorderFactory.createTitledBorder("Lens"));
		lensPanel.add(none);
		lensPanel.add(hyperView);
		lensPanel.add(hyperModel);
		
		
		return lensPanel;
	}

	
	public JPanel getMainPanel() {
		return this;
	}
	
	
	private Frame findParentFrame(){ 
	    Container c = this; 
	    while(c != null){ 
	      if (c instanceof Frame) 
	        return (Frame)c; 

	      c = c.getParent(); 
	    } 
	    return (Frame)null; 
	  } 
	
	public void launchInstructionPopup() {
		JOptionPane.showMessageDialog(findParentFrame(),
			    instructionString,
			    "Instructions",
			    JOptionPane.PLAIN_MESSAGE);
	}

	public JMenuBar getMainMenuBar() {
		JMenuBar menu =  new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu view = new JMenu("View");
		JMenu help = new JMenu("Help");
		
		final JMenuItem saveImage = new JMenuItem("Save image...");
		saveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().saveImage();
			}
		});
		file.add(saveImage);
		
		
		final JMenuItem reset = new JMenuItem("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().reset();
			}
		});
		edit.add(reset);
		
		
		final JMenuItem zoomIn = new JMenuItem("Zoom in");
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().zoomIn();
			}
		});
		zoomIn.setToolTipText("You can also zoom in using the mouse wheel");
		
		final JMenuItem zoomOut = new JMenuItem("Zoom out");
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphManager.getInstance().zoomOut();
			}
		});
		zoomOut.setToolTipText("You can also zoom out using the mouse wheel");
		
		view.add(zoomIn);
		view.add(zoomOut);
		
		final JMenuItem instructions = new JMenuItem("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchInstructionPopup();
			}
		});
		help.add(instructions);
		menu.add(file);
		menu.add(edit);
		menu.add(view);
		menu.add(help);
		return menu;
	}
}
