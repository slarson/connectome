package org.wholebrainproject.mcb;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

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
			System.out.println("Add Button pressed");
			pnlSearch.setVisible(true);
		}
		else if ( sourceButton == btnClear)
		{
			System.out.println("Clear Button Pressed");
		}
			
	}
	
	public ToolBar() 
	{
		pnlSearch = new SearchPanel();
		
		toolBarListener = createListener();
		
		
		add(btnAdd);
		btnAdd.addMouseListener(toolBarListener);
		
		add(btnClear);
		btnClear.addMouseListener(toolBarListener);
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
