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

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.wholebrainproject.mcb.graph.Edge;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.BareBonesBrowserLaunch;

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
            this.add(new EdgePropItem(findParentFrame()));           
        }
        
        private Frame findParentFrame(){ 
    	    Container c = this; 
    	    while(c != null){ 
    	      if (c instanceof Frame) 
    	        return (Frame)c; 

    	      c = c.getParent(); 
    	    } 
    	    return (Frame)null; 
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
        
        public  EdgePropItem(final Frame frame) {            
            super("Show inference chain...");
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	JDialog dialog = new JDialog(frame, "Inference chain", true);
                	dialog.setSize(500, 600);
                	JEditorPane theEditorPane = new JEditorPane();
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
            		dialog.add(theEditorPane);
            		theEditorPane.setText(edge.getInferenceChain());
                    dialog.setVisible(true);
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
