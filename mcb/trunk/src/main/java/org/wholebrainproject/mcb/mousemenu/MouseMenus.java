/*
 * MouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package org.wholebrainproject.mcb.mousemenu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.Node;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * @author Dr. Greg M. Bernstein
 */
public class MouseMenus {
    
    public static class EdgeMenu extends JPopupMenu {        
        // private JFrame frame; 
        public EdgeMenu() {
            super("Edge Menu");
            // this.frame = frame;
            //this.add(new DeleteEdgeMenuItem<Edge>());
            this.add(new EdgeOpenReferenceMenuItem());
            this.addSeparator();
            //this.add(new WeightDisplay());
            //this.add(new CapacityDisplay());
            this.addSeparator();
            //this.add(new EdgePropItem(frame));           
        }
        
    }
    
    public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<Edge>,
            MenuPointListener {
        Edge edge;
        VisualizationViewer visComp;
        Point2D point;
        
        public void setEdgeAndView(Edge edge, VisualizationViewer visComp) {
            this.edge = edge;
            this.visComp = visComp;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }
        
        public  EdgePropItem(final JFrame frame) {            
            super("Edit Edge Properties...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	/*
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, edge);
                    dialog.setLocation((int)point.getX()+ frame.getX(), (int)point.getY()+ frame.getY());
                    dialog.setVisible(true);
                    */
                }
                
            });
        }
        
    }
    public static class WeightDisplay extends JMenuItem implements EdgeMenuListener<Edge> {
        public void setEdgeAndView(Edge e, VisualizationViewer visComp) {
            //this.setText("Weight " + e + " = " + e.getWeight());
        }
    }
    
    public static class CapacityDisplay extends JMenuItem implements EdgeMenuListener<Edge> {
        public void setEdgeAndView(Edge e, VisualizationViewer visComp) {
            //this.setText("Capacity " + e + " = " + e.getCapacity());
        }
    }
    
    public static class VertexMenu extends JPopupMenu {
        public VertexMenu() {
            super("Vertex Menu");
            //TODO: add name of vertex here
            this.addSeparator();
            this.add(new ShowHideNodePartsMenuItem()); 
            this.add(new NodeMoreDetailsMenuItem());
        }
    }
    
}
