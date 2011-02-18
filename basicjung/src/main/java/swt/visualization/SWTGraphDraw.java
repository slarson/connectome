/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package swt.visualization;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.EdgeColorFunction;
import edu.uci.ics.jung.graph.decorators.EdgeThicknessFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.VertexColorFunction;
import edu.uci.ics.jung.graph.filters.Filter;
import edu.uci.ics.jung.graph.filters.SerialFilter;
import edu.uci.ics.jung.graph.filters.impl.DropSoloNodesFilter;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.StatusCallback;

/**
 * A Swing-only component for drawing graphs. Allows a series of manipulations
 * to access and show graphs, to set their various colors and lines, and to
 * dynamically change values. This is a good starting place for getting a graph
 * up quickly.
 *
 * @author Danyel Fisher
 * @author Lucas Bigeardel
 */
public class SWTGraphDraw implements StatusCallback {

        protected Graph graph;
        private final SWTSettableRenderer originalRenderer;
        SWTRenderer swtRenderer;
        Layout layout;
        SWTVisualizationViewer visualizationViewer;
        Filter mainFilter;

        List allFilters = new LinkedList();
        List sliders    = new LinkedList();

        /**
         * Creates a graph drawing environment that draws this graph object. By
         * default, uses the Spring layout, the Fade renderer and the
         * AbstractSettable renderer, the Drop Solo Nodes filter, and no adjustable
         * filters at all.
         *
         * @param g
         */
        public SWTGraphDraw(Composite parent, Graph g) {
                StringLabeller sl       = StringLabeller.getLabeller(g);
                graph                           = g;
                layout                          = new SpringLayout(g);
                originalRenderer        = new SWTSettableRenderer(sl);
                visualizationViewer = new SWTVisualizationViewer(parent, layout, originalRenderer);
                swtRenderer                     = originalRenderer;

                visualizationViewer.setTextCallback(this);

                mainFilter = DropSoloNodesFilter.getInstance();
        }

        public void setBackground(Color bg) {
                visualizationViewer.setBackground(SWTUtils.getSWTColor(bg));
        }

        public void callBack(String status) {}

        /**
         * A method to set the renderer.
         *
         * @param re the new renderer
         */
        public void setRenderer(SWTRenderer re) {
                swtRenderer = re;
                visualizationViewer.setRenderer(swtRenderer);
        }

        public void resetRenderer() {
                this.swtRenderer = originalRenderer;
                visualizationViewer.setRenderer(swtRenderer);
        }

        public SWTRenderer getRender() {
                return originalRenderer;
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param c the new edge color
         */
        public void setEdgeColor(Color c) {
                originalRenderer.setEdgeColor(c);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param ecf the new <code>EdgeColorFunction</code>
         */
        public void setEdgeColorFunction(EdgeColorFunction ecf) {
                originalRenderer.setEdgeColorFunction(ecf);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param i the thickness of the edge
         */
        public void setEdgeThickness(int i) {
                originalRenderer.setEdgeThickness(i);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param etf the new <code>EdgeThicknessFunction</code>
         */
        public void setEdgeThicknessFunction(EdgeThicknessFunction etf) {
                originalRenderer.setEdgeThicknessFunction(etf);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param vertexColor the new foreground color of the vertices
         */
        public void setVertexForegroundColor(Color vertexColor) {
                originalRenderer.setVertexForegroundColor(vertexColor);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param vertexColor
         *            the new picked color of the vertices
         */
        public void setVertexPickedColor(Color vertexColor) {
                originalRenderer.setVertexPickedColor(vertexColor);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param vertexColor
         *            the background color of the vertex that is to be set
         */
        public void setVertexBGColor(Color vertexColor) {
                originalRenderer.setVertexBGColor(vertexColor);
        }

        /**
         * A passthrough to the function at <code>originalRenderer</code>.
         *
         * @param vcf
         *            the new <code>VertexColorFunction</code>
         */
        public void setVertexColorFunction(VertexColorFunction vcf) {
                originalRenderer.setVertexColorFunction(vcf);
        }

        /**
         * Dynamically chooses a new GraphLayout.
         *
         * @param l
         *            the new graph layout algorithm
         */
        public void setGraphLayout(Layout l) {
                this.layout = l;
                visualizationViewer.setGraphLayout(l);
        }

        /**
         * Removes all the filters, deleting the sliders that drive them.
         */
        public void removeAllFilters() {
                //toolbar.removeAll();
                sliders.clear();
                allFilters.clear();
                mainFilter = new SerialFilter(allFilters);
        }

        /**
         * Adds a Filter that doesn't slide.
         *
         * @param f
         */
        public void addStaticFilter(Filter f) {
                allFilters.add(f);
                mainFilter = new SerialFilter(allFilters);
        }

        public Layout getGraphLayout() {
                return layout;
        }

        public void restartLayout() {
                visualizationViewer.restart();
        }

        public void stop() {
                visualizationViewer.stop();
        }

    /**
     * @return Returns the visualizationViewer.
     */
    public SWTVisualizationViewer getVisualizationViewer() {
        return visualizationViewer;
    }

    /**
     * @param visualizationViewer The visualizationViewer to set.
     */
    public void setVisualizationViewer(SWTVisualizationViewer visualizationViewer) {
        this.visualizationViewer = visualizationViewer;
    }
}
