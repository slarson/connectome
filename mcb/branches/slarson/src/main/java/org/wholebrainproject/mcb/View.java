package org.wholebrainproject.mcb;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

import org.wholebrainproject.mcb.graph.GraphManager;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;


/**
 * Creates and holds the Swing GUI elements like the panels and the buttons.
 *
 */
public class View extends JPanel{

	private static final long serialVersionUID = 1L;

	/**
	 * split that contains split_graph_help and option buttons.
	 */
	static JSplitPane split;

	static String instructionString ="<html>" +
			"<b>Welcome to the Multi-Scale Connectome Browser " +
			MultiScaleConnectomeBrowser.getProperties().getProperty("application.version")+"!</b>" +
			"<ul><li>Right-click on vertices or edges for more information." +
			"<li>Zoom in and out with your mouse wheel or using the menu options under 'View'" +
			"<li>Pan the graph around by clicking the right mouse button and dragging" +
			"<li>Left-click and drag a node to move it around" +
			"<li>Rest mouse on an edge and a reference message will appear." +
			"<li>Use the 'Save Image' feature under the file menu and give" +
	" the graph a name.  The graph will be saved as a power point.</ul>" +
	"You can recall these instructions from the help menu at any time.</html>";


	public View() {
		//load the graph zoom scroll pane.
		//doing the getInstance() on GraphManager causes data to be loaded.
		GraphZoomScrollPane gzsp = GraphManager.getInstance()
				.getGraphZoomScrollPane();
		gzsp.validate();
		add(gzsp);
	}

	protected Component getLensPanel() {
		JRadioButton none = new JRadioButton("None");

		none.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				System.out.println("none mouseClicked()");
				GraphManager.getInstance().lensNone(e);

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		none.setSelected(true);

		JRadioButton hyperView = new JRadioButton("View");
		hyperView.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				System.out.println("hyperView mouseClicked()");
				GraphManager.getInstance().lensView(e);

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		JRadioButton hyperModel = new JRadioButton("Layout");
		hyperModel.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				System.out.println("hyperMouse mouseClicked()");
				GraphManager.getInstance().lensLayout(e);

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

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


	public Frame findParentFrame(){
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
		JMenu lens = new JMenu("Lens");

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

		final JMenuItem showProjectionStrength = new JMenuItem("Show Projection Strengths");
		showProjectionStrength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean enabled = GraphManager.getInstance().getShowProjectionStrengths();
				GraphManager.getInstance().setShowProjectionStrength(!enabled);
				showProjectionStrength.setText("Hide Projection Strengths");
			}
		});
		showProjectionStrength.setToolTipText("<html>Render the graph with edges that indicate the strength <br>of the connection between brain regions was reported to be</html>");
		edit.add(showProjectionStrength);


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
		lens.add(getLensPanel());
		//TODO: Fix reset functionality and re-enable
		menu.add(edit);
		menu.add(view);
		//menu.add(lens);
		menu.add(help);
		return menu;
	}
}
