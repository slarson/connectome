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

	private static final long serialVersionUID = 1L;

	/**
	 * the gui frame
	 **/
	static JFrame mainFrame;

	public static int width = 700;
	public static int height = 600;

	public MultiScaleConnectomeBrowser() {
		try {
			View v = new View();
			getContentPane().add(v.getMainPanel(), BorderLayout.CENTER);
			getContentPane().add(new ToolBar(), BorderLayout.NORTH);

			getContentPane().setPreferredSize(new Dimension(width,height));
			this.setJMenuBar(v.getMainMenuBar());
			//v.launchInstructionPopup();
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


			MultiScaleConnectomeBrowser app = new MultiScaleConnectomeBrowser();


			//ReadProjectionData.getInstance();
			//BAMSToNeurolexMap.getInstance().getBAMSToNeurolexMap();
			Properties p = getProperties();
			mainFrame = new JFrame(
					 p.getProperty("application.name") + " version " +
					 p.getProperty("application.version"));
			mainFrame.setSize(width, height);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			mainFrame.add(app);
			mainFrame.pack();
			mainFrame.setVisible(true);

		} catch (Exception e) {
			System.out.println("Unrecoverable error!");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
