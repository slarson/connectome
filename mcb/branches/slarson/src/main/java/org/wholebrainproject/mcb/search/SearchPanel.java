package org.wholebrainproject.mcb.search;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.wholebrainproject.mcb.ToolBar.ToolBarButton;
import org.wholebrainproject.mcb.data.BAMSToNeurolexMap;
import org.wholebrainproject.mcb.graph.GraphManager;

/**
 * A panel to pick from a list of brain regions that are available to put on the
 * connectome browser graph.
 *
 * @author slarson
 *
 */
public class SearchPanel extends JDialog {

	private static final long serialVersionUID = 1L;

	private static String currentElement;
	private MouseListener mouseListener = null;

	private List<String> names;

	private JComboBox strictComboBox;

	/**
	 * {@inheritDoc}
	 */

	JButton btnOK = new JButton("OK");
	JButton btnCancel = new JButton("Cancel");


	/**
	 * Method creates the search panel and add the brain regions
	 * from master list to it.
	 * @param owner
	 */
	public SearchPanel(JFrame owner)
	{
		super(owner);
		setModal(true);
		setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints;

		//mouse listener.
		mouseListener = createListener();
		strictComboBox = new JComboBox();

		JLabel strictComboBoxLabel = new JLabel();
		strictComboBoxLabel.setName("strictComboBoxLabel");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		add(strictComboBoxLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		add(strictComboBox, gridBagConstraints);

		//set the coordinates for the buttons used combo box.
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridwidth = GridBagConstraints.NONE;
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.insets = new Insets(1, 3, 3, 3);

		//adding mouse listeners to buttons.
		btnOK.addMouseListener(mouseListener);
		btnCancel.addMouseListener(mouseListener);

		//add buttons to combo box.
		add(btnOK, buttonConstraints);
		add(btnCancel, buttonConstraints);

		Dimension size = new Dimension(500, 250);

		setMinimumSize(size);
		setSize(size);
		setPreferredSize(size);

		injectResources();
		bind();
		decorate();
		repaint();

	}
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
				if ( e.getSource() != null && e.getSource() instanceof JButton)
				{
					executeCommand((JButton) e.getSource());
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
	public void executeCommand(JButton sourceButton)
	{
		//button to add brain region was clicked.
		if ( sourceButton == btnOK)
		{
			setSetElement(strictComboBox.getSelectedItem());
			this.dispose();
		}
		//button to clear the graph was clicked.
		else if ( sourceButton == btnCancel)
		{
			System.out.println("Closing combo box.");
			this.dispose();
		}
	}

	/**
	 * Method sets the variable that stores the selected item from
	 * the combo box.
	 * @param selectedItem
	 */
	private void setSetElement(Object selectedItem) {
		currentElement = (String)selectedItem;

	}

	/**
	 * Method returns the current selected brain region name.
	 * @return currentElement - the name of the brain region
	 * 							selected by the user in the
	 * 							combo box.
	 */
	public static String getSelectedItem(){
		return currentElement;
	}
	/**
	 * Method gets the brain region names form the master list and
	 * adds them to a map.  The list is sorted by alphabetical order.
	 */
	protected void injectResources() {
		try {
			//get brain region names from master list.
			Map<String, URI> namesMap = BAMSToNeurolexMap.getInstance().getNamesMap();

			//put the brain region names in an array.
			names = new ArrayList<String>(namesMap.keySet());
			names.add("");
			//sort the brain region names by name.
			Collections.sort(names);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method adds the brain region names in the master list to the
	 * combo box.
	 */
	protected void bind() {
		//adding the brain region names to the combo box.
		strictComboBox.setModel(new ListComboBoxModel<String>(names));
	}

	/**
	 * Method decorates the combo box that contains the brain region names.
	 */
	private void decorate() {
		AutoCompleteDecorator.decorate(strictComboBox);
	}
}
