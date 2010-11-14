/*
 * DeleteVertexMenuItem.java
 *
 * Created on March 21, 2007, 2:03 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package org.wholebrainproject.mcb.mousemenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenuItem;

import org.wholebrainproject.mcb.graph.GraphManager;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BareBonesBrowserLaunch;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A menu item to show or hide the parts of a node.
 */
@SuppressWarnings("serial")
public class MergeWithSiblingsMenuItem extends JMenuItem implements VertexMenuListener<Node> {
    private Node node;
    private VisualizationViewer visComp;
    
    /** Creates a new instance of DeleteVertexMenuItem */
    public MergeWithSiblingsMenuItem() {
        super("Merge with siblings");
       
        this.addActionListener(new ActionListener(){
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
            	System.out.println("Calling Merge with siblings :"+node.getParent());
            	GraphManager.getInstance().collapse(node.getParent());
                visComp.repaint();
            }
        });
    }

    /**
     * Implements the VertexMenuListener interface.
     * @param v 
     * @param visComp 
     */
    public void setVertexAndView(Node v, VisualizationViewer visComp) {
        this.node = v;
        this.visComp = visComp;
        if (node.hasPartOfParent()) {
        	this.setVisible(true);
        	
        	 Collection<Node> siblings = node.getParent().getPartOfNodes();
             String sibString = "<html>Will merge together the following: <br>";
             for (Node sib : siblings) {
             	sibString += sib.getName() + "<br>";
             }
             sibString = sibString.substring(0, sibString.length()-4);
             sibString += "</html>";
             setToolTipText(sibString);
        } else {
        	this.setVisible(false);
        }
       
     }
    
}
