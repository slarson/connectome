package org.wholebrainproject.mcb;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.wholebrainproject.mcb.data.BuildConnections;
import org.wholebrainproject.mcb.graph.GraphManager;
import org.wholebrainproject.mcb.search.SearchPanel;

public class ToolBar extends JToolBar
{
	private static final long serialVersionUID = 1L;

	private MouseListener toolBarListener = null;

	private static String currentSelectedBrainRegion = null;
	ToolBarButton btnClear = new ToolBarButton(null, "Clear Regions");
	ToolBarButton btnAdd = new ToolBarButton(null, "Add Region");
	SearchPanel pnlSearch = null;

	/**
	 * Method takes care of the mouse listener for the tool bar.
	 * @return
	 */
	protected MouseListener createListener()
	{
			MouseListener listener = new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
				//pass the event to our actual handler
				if ( e.getSource() != null && e.getSource() instanceof ToolBarButton)
				{
					executeCommand((ToolBarButton) e.getSource());
				}

			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		};

		return listener;
	}

	/**
	 * Method checks which tool bar button was clicked and
	 * acts accordingly.
	 * @param sourceButton
	 */
	public void executeCommand(ToolBarButton sourceButton)
	{
		//button to add brain region was clicked.
		if ( sourceButton == btnAdd)
		{
			showSearch();
		}
		//button to clear the graph was clicked.
		else if ( sourceButton == btnClear)
		{
			//get the graph and clear it.
			GraphManager.getInstance().clearAllNodesAndEdges();
		}

	}

	/**
	 *
	 */
	private void showSearch()
	{
		String currentBrainRegion;
		/*
		Dimension size = new Dimension(500, 70);
		JFrame frame = new JFrame("Search Panel");
		frame.add(pnlSearch);
		frame.setPreferredSize(size);
		frame.setMinimumSize(size);
		frame.setSize(size);
		frame.pack();
		frame.setVisible(true);
		*/

		pnlSearch.setVisible(true);

		currentBrainRegion = SearchPanel.getSelectedItem();
		//make sure the user selected a brain region name from the list.
		if(currentBrainRegion != null){

			//set the global variable that stores the selected brain
			//region name.
			currentSelectedBrainRegion = currentBrainRegion;

			//the name of the new node to be created.
			BuildConnections.getInstance().addNewNode(currentBrainRegion);
		}

		System.out.println(currentSelectedBrainRegion);
	}

	/**
	 * Method instantiates the tool bar with it's appropriate
	 * buttons.
	 */
	public ToolBar()
	{
		//instantiate the seach panel.
		pnlSearch = new SearchPanel(null);

		//create mouse listener.
		toolBarListener = createListener();

		//adding the brain region add button to tool bar.
		add(btnAdd);
		btnAdd.addMouseListener(toolBarListener);

		//adding the clear button to tool bar.
		add(btnClear);
		btnClear.addMouseListener(toolBarListener);

		this.setFloatable(false);
		this.setRollover(true);
	}

	/**
	 * This class takes care of the tool bar buttons that enable
	 * the user to,
	 * 1.- Clear the current graph.
	 * 2.- Add nodes to the graph by specifying a brain region
	 * by pressing the add button.
	 *
	 */
	public class ToolBarButton extends JButton
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor for tool bar button.
		 */
		public ToolBarButton(Icon icon, String text)
		{
			super(icon);

			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setText(text); //dont put a label ont he buttons
			setActionCommand(text);
			setToolTipText(text);
		}
	}

}
