package org.wholebrainproject.mcb;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.wholebrainproject.mcb.util.HyperLinkToolTip;

/**
 * Main entrance point to the application.
 *
 */
public class MultiScaleConnectomeBrowser {
	
	/**
	 * the gui frame
	 */
	static JFrame f;

	/*
	 * Driver for application
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		try {
		
			f = new JFrame(
					"Multi-Scale Connectome Browser version-0.2.0-alpha");
			f.setSize(500, 900);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			View v = new View();
			f.getContentPane().add(v.getMainPanel());
			f.add(v.getSplitPanel());						
			f.pack();
			f.setVisible(true);

		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
