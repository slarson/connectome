package org.wholebrainproject.mcb;

import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * Main entrance point to the application.
 *
 */
public class MultiScaleConnectomeBrowser extends JApplet{
	
	/**
	 * the gui frame
	 */
	static JFrame f;

	public MultiScaleConnectomeBrowser() {
		try {
			//setSize(500,600);
			View v = new View();
			getContentPane().add(v.getMainPanel());
			//getContentPane().setPreferredSize(new Dimension(500,900));
			this.setJMenuBar(v.getMainMenuBar());
			v.launchInstructionPopup();

		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/*
	 * Driver for application
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		try {
		
			f = new JFrame(
					"Multi-Scale Connectome Browser version-0.2.0-alpha");
			//f.setSize(500, 600);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			View v = new View();
			f.getContentPane().add(v.getMainPanel());
			f.setJMenuBar(v.getMainMenuBar());
			f.pack();
			f.setVisible(true);
			v.launchInstructionPopup();
		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
