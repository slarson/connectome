/*
 * DeleteEdgeMenuItem.java
 *
 * Created on March 21, 2007, 2:47 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package org.wholebrainproject.mcb.mousemenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.util.BareBonesBrowserLaunch;

import edu.uci.ics.jung.visualization.VisualizationViewer;


public class EdgeOpenReferenceMenuItem extends JMenuItem implements EdgeMenuListener<Edge> {
    private Edge edge;
    private VisualizationViewer visComp;
    
    public EdgeOpenReferenceMenuItem() {
        super("Open reference...");
        this.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	BareBonesBrowserLaunch.openURL(edge.getReferenceURL());
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
    }
    
}
