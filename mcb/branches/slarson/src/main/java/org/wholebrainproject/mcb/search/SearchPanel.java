package org.wholebrainproject.mcb.search;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * A panel to pick from a list of brain regions that are available to put on the
 * connectome browser graph.
 * 
 * @author slarson
 * 
 */
public class SearchPanel extends JPanel {

	private List<String> names;

	private JComboBox strictComboBox;

	/**
	 * {@inheritDoc}
	 */
	public SearchPanel() {
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
		
		injectResources();
		bind();
		decorate();
	}

	protected void injectResources() {

		/*FIXME
		String s = Application.getInstance().getContext().getResourceMap(
				getClass()).getString("names");
		
		// prevent changes; we're sharing the list among several models
		names = Collections.unmodifiableList(Arrays.asList(s.split(",")));
				*/
	}

	protected void bind() {
		strictComboBox.setModel(new ListComboBoxModel<String>(names));
	}

	private void decorate() {
		AutoCompleteDecorator.decorate(strictComboBox);
	}
}
