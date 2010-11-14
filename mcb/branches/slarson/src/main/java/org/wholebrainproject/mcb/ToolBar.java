package org.wholebrainproject.mcb;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.wholebrainproject.mcb.graph.GraphManager;
import org.wholebrainproject.mcb.search.SearchPanel;

public class ToolBar extends JToolBar
{
	
	
	private MouseListener toolBarListener = null;
	
	
	
	ToolBarButton btnClear = new ToolBarButton(null, "Clear Regions");
	ToolBarButton btnAdd = new ToolBarButton(null, "Add Region");
	SearchPanel pnlSearch = null;
	
	
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
	
	
	protected void executeCommand(ToolBarButton sourceButton)
	{
		if ( sourceButton == btnAdd)
		{
			showSearch();
		}
		else if ( sourceButton == btnClear)
		{
			GraphManager.getInstance().clearAllNodesAndEdges();
		}
			
	}
	
	private void showSearch()
	{
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
		
	}
	

	
	public ToolBar() 
	{
		
		pnlSearch = new SearchPanel(null);
		
		
		toolBarListener = createListener();
		
		
		add(btnAdd);
		btnAdd.addMouseListener(toolBarListener);
		
		add(btnClear);
		btnClear.addMouseListener(toolBarListener);
		this.setFloatable(false);
		this.setRollover(true);
	}
	
	class ToolBarButton extends JButton
	{
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
