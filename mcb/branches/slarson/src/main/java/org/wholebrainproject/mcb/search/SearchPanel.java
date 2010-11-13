package org.wholebrainproject.mcb.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.wholebrainproject.mcb.View;
import org.wholebrainproject.mcb.data.BAMSToNeurolexMap;

/**
 * A panel to pick from a list of brain regions that are available to put on the
 * connectome browser graph.
 * 
 * @author slarson
 * 
 */
public class SearchPanel extends JDialog {

	private List<String> names;

	private JComboBox strictComboBox;

	/**
	 * {@inheritDoc}
	 */
	
	JButton btnOK = new JButton("OK");
	JButton btnCancel = new JButton("Cancel");
	
	
	public SearchPanel(JFrame owner) 
	{
		super(owner);
		setModal(true);
		setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints;

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
		
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridwidth = GridBagConstraints.NONE;
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		buttonConstraints.insets = new Insets(1, 3, 3, 3);
		
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

	protected void injectResources() {
		try {
			Map<String, URI> namesMap = BAMSToNeurolexMap.getInstance().getNamesMap();
			names = new ArrayList(namesMap.keySet());
			Collections.sort(names);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void bind() {
		strictComboBox.setModel(new ListComboBoxModel<String>(names));
	}

	private void decorate() {
		AutoCompleteDecorator.decorate(strictComboBox);
	}
}
