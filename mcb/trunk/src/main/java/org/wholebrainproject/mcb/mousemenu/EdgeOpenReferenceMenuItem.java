/*
 * DeleteEdgeMenuItem.java
 *
 * Created on March 21, 2007, 2:47 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package org.wholebrainproject.mcb.mousemenu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.util.BareBonesBrowserLaunch;

/**
 * A class to implement the deletion of an edge from within a 
 * PopupVertexEdgeMenuMousePlugin.
 * @author Dr. Greg M. Bernstein
 */
public class EdgeOpenReferenceMenuItem extends JMenuItem implements EdgeMenuListener<Edge> {
    private Edge edge;
    private VisualizationViewer visComp;
    
    /** Creates a new instance of DeleteEdgeMenuItem */
    public EdgeOpenReferenceMenuItem() {
        super("More details...");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	BareBonesBrowserLaunch.openURL(edge.getReferenceURL());
                //visComp.getPickedEdgeState().pick(edge, false);
                //visComp.getGraphLayout().getGraph().removeEdge(edge);
                visComp.repaint();
            }
        });
    }

    /**
     * Implements the EdgeMenuListener interface to update the menu item with info
     * on the currently chosen edge.
     * @param edge 
     * @param visComp 
     */
    public void setEdgeAndView(Edge edge, VisualizationViewer visComp) {
        this.edge = edge;
        this.visComp = visComp;
        //this.setText("Delete Edge " + edge.toString());
    }
    
}
