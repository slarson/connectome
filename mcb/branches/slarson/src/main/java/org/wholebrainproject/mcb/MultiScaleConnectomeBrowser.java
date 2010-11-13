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
	 **/
	static JFrame mainFrame;
	
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
	
	/**
	 * Driver for application
	 * @throws Exception 
	 **/
	public static void main(String[] args) {

		try {
			//ReadProjectionData.getInstance();
			//BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap();
			Properties p = getProperties();
			mainFrame = new JFrame(
					 p.getProperty("application.name") + " version " + 
					 p.getProperty("application.version"));
			mainFrame.setSize(500, 600);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//create new instance of View.  Implicitly kicks off loading of
			//data behind the scenes.
			View v = new View();
			mainFrame.setJMenuBar(v.getMainMenuBar());
			//mainFrame.getContentPane().setPreferredSize(new Dimension(500,900));
			
			mainFrame.setLayout(new BorderLayout());
			mainFrame.add(v.getMainPanel(), BorderLayout.CENTER);
			
			mainFrame.pack();
			mainFrame.setVisible(true);
			v.launchInstructionPopup();
		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
