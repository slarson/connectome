package org.wholebraincatalog.mcb;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ToolTipUI;
import java.awt.*;
 
/**
 * Class for putting hyperlinks into tool tips.  Snagged from: 
 * http://forums.sun.com/thread.jspa?messageID=3093335
 * 
 *
 */
public class HyperLinkToolTip extends JToolTip {
	private JEditorPane theEditorPane;
 
	public HyperLinkToolTip() {
		setLayout(new BorderLayout());
		LookAndFeel.installBorder(this, "ToolTip.border");
		LookAndFeel.installColors(this, "ToolTip.background", "ToolTip.foreground");
		theEditorPane = new JEditorPane();
		theEditorPane.setContentType("text/html");
		theEditorPane.setEditable(false);
		theEditorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					// do whatever you want with the url
					BareBonesBrowserLaunch.openURL(e.getURL().toString());
					System.out.println("clicked on link : " + e.getURL());
				}
			}
		});
		add(theEditorPane);
	}
 
	public void setTipText(String tipText) {
		theEditorPane.setText(tipText);
	}
 
	public void updateUI() {
		setUI(new ToolTipUI() {});
	}
 
	public static void main(String[] args) {
		final JFrame frame = new JFrame(HyperLinkToolTip.class.getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel() {
			public JToolTip createToolTip() {
				JToolTip tip = new HyperLinkToolTip();
				tip.setComponent(this);
				return tip;
			}
		};
		panel.setToolTipText("<html><body><a href=\"http://www.sun.com\">www.sun.com</a></body></html>");
		frame.setContentPane(panel);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setSize(400, 400);
				frame.show();
			}
		});
	}
}
