package org.wholebrainproject.mcb;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import java.util.Properties;

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
	
	public static Properties getProperties() {
		try {
			Properties mcbProps = new Properties();
			URL url = MultiScaleConnectomeBrowser.class
					.getResource("/mcb.properties");
			mcbProps.load(url.openStream());
			return mcbProps;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Driver for application
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		try {
			Properties p = getProperties();
			f = new JFrame(
					 p.getProperty("application.name") + " version " + 
					 p.getProperty("application.version"));
			//f.setSize(500, 600);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			View v = new View();
			f.setJMenuBar(v.getMainMenuBar());
			//f.getContentPane().setPreferredSize(new Dimension(500,900));
			JFrame mainFrame = f;
			mainFrame.setLayout(new BorderLayout());
			mainFrame.add(v.getMainPanel(), BorderLayout.CENTER);
			
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
