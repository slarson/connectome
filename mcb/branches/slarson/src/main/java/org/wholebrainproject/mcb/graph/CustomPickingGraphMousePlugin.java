package org.wholebrainproject.mcb.graph;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class CustomPickingGraphMousePlugin<V, E> extends
		PickingGraphMousePlugin<V, E> {

	/**
	 * For primary modifiers (default, MouseButton1):
	 * pick a single Vertex or Edge that
     * is under the mouse pointer. If no Vertex or edge is under
     * the pointer, unselect all picked Vertices and edges, and
     * set up to draw a rectangle for multiple selection
     * of contained Vertices.
     * For additional selection (default Shift+MouseButton1):
     * Add to the selection, a single Vertex or Edge that is
     * under the mouse pointer. If a previously picked Vertex
     * or Edge is under the pointer, it is un-picked.
     * If no vertex or Edge is under the pointer, set up
     * to draw a multiple selection rectangle (as above)
     * but do not unpick previously picked elements.
	 * 
	 * @param e the event
	 */
    @SuppressWarnings("unchecked")
    public void mousePressed(MouseEvent e) {
        down = e.getPoint();
        VisualizationViewer<V,E> vv = (VisualizationViewer)e.getSource();
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        PickedState<V> pickedVertexState = vv.getPickedVertexState();
        PickedState<E> pickedEdgeState = vv.getPickedEdgeState();
        if(pickSupport != null && pickedVertexState != null) {
            Layout<V,E> layout = vv.getGraphLayout();
            if(e.getModifiers() == modifiers) {
                rect.setFrameFromDiagonal(down,down);
                // p is the screen point for the mouse event
                Point2D ip = e.getPoint();

                vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
					if (pickedVertexState.isPicked(vertex) == false) {
						pickedVertexState.clear();
						pickedVertexState.pick(vertex, true);
						if (vertex instanceof Node) {
							Collection<Node> nodes = ((Node) vertex)
									.getPartOfNodes(GraphManager.getInstance()
											.getGraph());	
							if (nodes.isEmpty() == false) {
								for (Node n : nodes) {
									pickedVertexState.pick((V) n, true);
								}
							}
						}
					}
                    // layout.getLocation applies the layout transformer so
                    // q is transformed by the layout transformer only
                    Point2D q = layout.transform(vertex);
                    // transform the mouse point to graph coordinate system
                    Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                    offsetx = (float) (gp.getX()-q.getX());
                    offsety = (float) (gp.getY()-q.getY());
                } else if((edge = pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.clear();
                    pickedEdgeState.pick(edge, true);
                } else {
                    vv.addPostRenderPaintable(lensPaintable);
                	pickedEdgeState.clear();
                    pickedVertexState.clear();
                }
                
            } else if(e.getModifiers() == addToSelectionModifiers) {
                vv.addPostRenderPaintable(lensPaintable);
                rect.setFrameFromDiagonal(down,down);
                Point2D ip = e.getPoint();
                vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
                    boolean wasThere = pickedVertexState.pick(vertex, !pickedVertexState.isPicked(vertex));
                    if(wasThere) {
                        vertex = null;
                    } else {

                        // layout.getLocation applies the layout transformer so
                        // q is transformed by the layout transformer only
                        Point2D q = layout.transform(vertex);
                        // translate mouse point to graph coord system
                        Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                        offsetx = (float) (gp.getX()-q.getX());
                        offsety = (float) (gp.getY()-q.getY());
                    }
                } else if((edge = pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.pick(edge, !pickedEdgeState.isPicked(edge));
                }
            }
        }
        if(vertex != null) e.consume();
    }
}
