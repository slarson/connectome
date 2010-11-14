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

import javax.swing.JMenuItem;

import org.wholebrainproject.mcb.graph.GraphManager;
import org.wholebrainproject.mcb.graph.Node;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A menu item to show or hide the parts of a node.
 */
public class ShowHideNodePartsMenuItem extends JMenuItem implements VertexMenuListener<Node> {
    private Node node;
    private VisualizationViewer visComp;
    
    /** Creates a new instance of DeleteVertexMenuItem */
    public ShowHideNodePartsMenuItem() {
        super();
        this.addActionListener(new ActionListener(){
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
                if (node.isCollapsed()) {
                	GraphManager.getInstance().showBrainParts(node);
                } else {
                	GraphManager.getInstance().collapse(node);
                }
                visComp.getPickedVertexState().pick(node, false);
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
        
        if (this.node.hasParts() == false) {
        	this.setVisible(false);
        } else {
        	this.setVisible(true);
        }
        
        if (this.node.isCollapsed()) {
        	super.setText("Show parts"); 
        } else {
        	super.setText("Hide parts");
        }
        
    }
    
}
