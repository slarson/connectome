package com.vaadin.test;

import com.vaadin.Application;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
/**
 * Vaadin application intended to be implemented by GWT wrapper
 * in order port mcb test to the browser natively.
 * @author ruggero
 *
 */
public class VaadinJung extends Application{
	TabSheet tabSheet = new TabSheet();

	public void init(){
		//set the title for the application window.
		Window mainWindow = new Window("Vaadin Jung");
		setMainWindow(mainWindow);
		mainWindow.addComponent(tabSheet);

		//make the application close and start fresh when
		//window is closed.
		getMainWindow().addListener(new CloseListener(){
			public void windowClose(CloseEvent e) {
				e.getWindow().removeAllComponents();
				removeWindow(e.getWindow());
				close();
			}
		});

	}
}
