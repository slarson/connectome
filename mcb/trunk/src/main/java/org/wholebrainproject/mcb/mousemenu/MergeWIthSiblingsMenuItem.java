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
import org.wholebrainproject.mcb.util.BareBonesBrowserLaunch;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A menu item to show or hide the parts of a node.
 */
@SuppressWarnings("serial")
public class MergeWIthSiblingsMenuItem extends JMenuItem implements VertexMenuListener<Node> {
    private Node node;
    private VisualizationViewer visComp;
    
    /** Creates a new instance of DeleteVertexMenuItem */
    public MergeWIthSiblingsMenuItem() {
        super("Merge with siblings");
        setToolTipText("");
        this.addActionListener(new ActionListener(){
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
            	//FIXME can't hide brain parts of this node, need to look up its 
            	//parent and perform it there.
            	GraphManager.getInstance().hideBrainParts(node);
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
     }
    
}
