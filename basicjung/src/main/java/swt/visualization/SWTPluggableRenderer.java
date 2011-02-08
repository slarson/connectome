/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package swt.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;

import samples.preview_new_graphdraw.CoordinateUtil;
import samples.preview_new_graphdraw.Coordinates;
import swt.contrib.AwtG2DWrapper;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ConstantDirectionalEdgeValue;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeColorFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeFontFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexColorFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexFontFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexStringer;
import edu.uci.ics.jung.graph.decorators.ConstantVertexStrokeFunction;
import edu.uci.ics.jung.graph.decorators.DirectionalEdgeArrowFunction;
import edu.uci.ics.jung.graph.decorators.EdgeArrowFunction;
import edu.uci.ics.jung.graph.decorators.EdgeColorFunction;
import edu.uci.ics.jung.graph.decorators.EdgeFontFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.VertexColorFunction;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.decorators.VertexStrokeFunction;
import edu.uci.ics.jung.graph.predicates.EdgePredicate;
import edu.uci.ics.jung.graph.predicates.SelfLoopEdgePredicate;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.visualization.ArrowFactory;
import edu.uci.ics.jung.visualization.PickedInfo;

/**
 * <p>A renderer with all sorts of buttons to press and dials to turn.
 * Using the appropriate methods, the user can override the default
 * properties/behaviors for vertex color, stroke, shape, label, label font,
 * and label centering; and for edge color, stroke, label, arrows, label font,
 * label positioning, and drawing.
 * </p>
 * <p>Notes on these decorators:
 * <ul>
 * <li/>The decorators are all orthogonal; changing one does not change the behavior of any
 * other (unless your decorator implementations depend on one another).
 * <li/>The default properties apply to all vertices, but the decorators allow these
 * properties to be specified for each individual vertex/edge.  See the documentation for
 * each of these decorators for specific instructions on their use.
 * <li/>Implementations of these decorator interfaces are provided that allow the user
 * to specify a single (constant) property to apply to all vertices/edges.
 * <li/>There are additional interfaces and classes that allow the size and aspect ratio
 * of the vertex shape to be independently manipulated, and that provide factory methods
 * for generating various standard shapes; see <code>SettableVertexShapeFunction</code>,
 * <code>AbstractVertexShapeFunction</code>, <code>VertexShapeFactory</code>, and the
 * sample <code>samples.graph.PluggableRendererDemo</code>.
 * <li/>This class provides default <code>Stroke</code> implementations for dotted and
 * dashed lines: the <code>DOTTED</code> and <code>DASHED</code> static constants,
 * respectively.
 * <li/>The <code>EdgeArrowPredicate</code> specifies the edges for which arrows
 * should be drawn; the <code>EdgeArrowFunction</code> specifies the shapes of
 * the arrowheads for those edges that pass the <code>EdgeArrowPredicate</code>.
 * <li/>If the specified vertex inclusion <code>Predicate</code> indicates that
 * vertex <code>v</code> is not to be drawn, none of its incident edges will be drawn either.
 * </ul>
 *
 * <p>By default, self-loops are drawn as circles.  To modify this behavior, override
 * <code>drawSelfLoop</code>.</p>
 *
 * <p>By default, undirected edges are drawn as straight lines, directed edges are
 * drawn as bent lines, and parallel edges are drawn on top
 * of one another.  To modify these behaviors, set the control offsets for the edges
 * appropriately.

 * <p>Arrowheads are drawn so that the point of the arrow is at the boundary of the
 * vertex shapes's bounding box (for non-self-loops) and at the top of the loop for
 * self-loops.  To modify these behaviors, override <code>drawSelfLoopArrowhead</code>
 * and/or <code>drawArrowhead</code>
 *
 * <p>Setting a stroke width other than 1, or using transparency,
 * may slow down rendering of the visualization.
 * </p>
 *
 * @author Danyel Fisher
 * @author Joshua O'Madadhain
 * @author Lucas Bigeardel
 */
public class SWTPluggableRenderer extends SWTAbstractRenderer implements PickedInfo
{

    private final static float[] dotting = {1.0f, 3.0f};
    /**
     * A stroke for a dotted line: 1 pixel width, round caps, round joins, and an
     * array of {1.0f, 3.0f}.
     */
    public final static Stroke DOTTED = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 1.0f, dotting, 0f);

    private final static float[] dashing = {5.0f};
    /**
     * A stroke for a dashed line: 1 pixel width, square caps, beveled joins, and an
     * array of {5.0f}.
     */
    public final static Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL, 1.0f, dashing, 0f);

    /**
     * Specifies the offset for the edge labels.
     */
    protected int LABEL_OFFSET = 10;

    protected VertexColorFunction vertexColorFunction =
        new ConstantVertexColorFunction(this, Color.BLACK, Color.RED, Color.ORANGE);
    protected Predicate vertexIncludePredicate = TruePredicate.getInstance();
    protected VertexStrokeFunction vertexStrokeFunction =
        new ConstantVertexStrokeFunction(1.0f);
    protected VertexShapeFunction vertexShapeFunction =
        new EllipseVertexShapeFunction(
                new ConstantVertexSizeFunction(8),
                new ConstantVertexAspectRatioFunction(1.0f));
    protected VertexStringer vertexStringer =
        new ConstantVertexStringer(null);
    protected VertexFontFunction vertexFontFunction =
        new ConstantVertexFontFunction(new Font("Helvetica", Font.PLAIN, 12)); //$NON-NLS-1$
    protected boolean centerVertexLabel = false;

    protected EdgeColorFunction edgeColorFunction =
        new ConstantEdgeColorFunction(Color.BLACK);
    protected EdgeStringer edgeStringer =
        new ConstantEdgeStringer(null);
    protected EdgeStrokeFunction edgeStrokeFunction =
        new ConstantEdgeStrokeFunction(1.0f);
    protected EdgeArrowFunction edgeArrowFunction =
        new DirectionalEdgeArrowFunction(10, 8, 4);
    protected Predicate edgeArrowPredicate ; //Graph.DIRECTED_EDGE;
    protected Predicate edgeIncludePredicate = TruePredicate.getInstance();
    protected EdgeFontFunction edgeFontFunction =
        new ConstantEdgeFontFunction(new Font("Helvetica", Font.PLAIN, 12)); //$NON-NLS-1$
    protected NumberEdgeValue edgeLabelClosenessFunction =
        new ConstantDirectionalEdgeValue(0.5, 0.75);
    protected NumberEdgeValue edgeControlOffsetFunction =
        new ConstantDirectionalEdgeValue(0, 20);

    protected final static EdgePredicate self_loop = SelfLoopEdgePredicate.getInstance();

    public SWTPluggableRenderer()
    {
    }

    /**
     * Sets the <code>EdgeArrowFunction</code> that specifies the
     * <code>Shape</code> of the arrowheads for each edge.
     * The same shape will be used for both ends of an undirected
     * edge.  The default arrow-drawing implementations assume that arrows
     * are drawn with their base on the y-axis, pointed left (in the negative
     * x-direction), centered on the x-axis.
     * Note that the <code>EdgeArrowFunction</code> must return a valid shape
     * for any edge for which the <code>EdgeArrowPredicate</code>
     * returns <code>true</code>.
     * <br>Default: wedge arrows for undirected edges, notched arrows for directed edges
     * (<code>DirectionalEdgeArrowFunction</code>)
     * @see EdgeArrowFunction
     * @see EdgeArrowPredicate
     * @see ArrowFactory
     */
    public void setEdgeArrowFunction(EdgeArrowFunction eaf)
    {
        this.edgeArrowFunction = eaf;
    }

    /**
     * Sets the <code>EdgeArrowPredicate</code> that specifies whether
     * arrowheads should be drawn for each edge.  If the predicate evaluates
     * to <code>true</code> for a specified edge, arrows should be drawn
     * for that edge.
     * <br>Default: only directed edges have arrows (<code>Graph.DIRECTED_EDGE</code> instance)
     * @see EdgeArrowPredicate
     * @see EdgeArrowFunction
     */
    public void setEdgeArrowPredicate(Predicate p)
    {
        this.edgeArrowPredicate = p;
    }

    /**
     * Sets the <code>EdgeColorFunction</code> that specifies the color to
     * draw each edge.
     * <br>Default: Color.BLACK
     * @see java.awt.Color
     */
    public void setEdgeColorFunction(EdgeColorFunction ecf)
    {
        this.edgeColorFunction = ecf;
    }

    /**
     * Sets the <code>NumberEdgeValue</code> that specifies the offset to use
     * for the control point for each edge.  This can be used to distinguish
     * between edge types (as in the default behavior) or to handle the
     * drawing of parallel edges: if each parallel edge is given a different
     * control offset, then they won't be drawn on top of one another.
     * <br>Default: undirected edges 0, directed edges 20
     * @param nev
     */
    public void setEdgeControlOffsetFunction(NumberEdgeValue nev)
    {
        this.edgeControlOffsetFunction = nev;
    }

    /**
     * Sets the <code>EdgeFontFunction</code> that specifies the font
     * to use for drawing each edge label.  This can be used (for example) to
     * emphasize (or to de-emphasize) edges that have a specific property.
     * <br>Default: 12-point Helvetica
     * @see EdgeFontFunction
     */
    public void setEdgeFontFunction(EdgeFontFunction eff)
    {
        this.edgeFontFunction = eff;
    }

    /**
     * Sets the <code>Predicate</code> that specifies whether each
     * edge should be drawn; only those edges for which this
     * predicate returns <code>true</code> will be drawn.  This can be
     * used to selectively display only those edges that have a
     * specific property, such as a particular decoration or value, or
     * only those edges of a specific type (such as directed edges,
     * or everything except self-loops).
     * <br>Default: all edges drawn (<code>TruePredicate</code> instance)
     * @see org.apache.commons.collections.Predicate
     */
    public void setEdgeIncludePredicate(Predicate p)
    {
        this.edgeIncludePredicate = p;
    }

    /**
     * Sets the <code>NumberEdgeValue</code> that specifies where to draw
     * the label for each edge.  A value of 0 draws the label on top of
     * the edge's first vertex; a value of 1.0 draws the label on top
     * of the edge's second vertex; values between 0 and 1 split the
     * difference (i.e., a value of 0.5 draws the label halfway in between
     * the two vertices).  The effect of values outside the range [0,1]
     * is undefined.  This function is not used for self-loops.
     * <br>Default: 0.5 for undirected edges, 0.75 for directed edges
     * (<code>ConstantDirectionalEdgeValue</code>)
     * @see edu.uci.ics.jung.graph.decorators.NumberEdgeValue
     */
    public void setEdgeLabelClosenessFunction(NumberEdgeValue nev)
    {
        this.edgeLabelClosenessFunction = nev;
    }

    /**
     * Sets the <code>EdgeStringer</code> that specifies the label to
     * draw for each edge.
     * <br>Default: no labels
     * (<code>ConstantEdgeStringer</code>)
     * @see edu.uci.ics.jung.graph.decorators.EdgeStringer
     */
    public void setEdgeStringer(EdgeStringer es)
    {
        this.edgeStringer = es;
    }

    /**
     * Sets the <code>EdgeStrokeFunction</code> that specifies the
     * <code>Stroke</code> to use when drawing each edge.
     * <br>Default: 1-pixel-width basic stroke
     * (<code>ConstantEdgeStrokeFunction</code>)
     * @see java.awt.Stroke
     * @see EdgeStrokeFunction
     */
    public void setEdgeStrokeFunction(EdgeStrokeFunction esf)
    {
        this.edgeStrokeFunction = esf;
    }

    /**
     * <p>Sets the <code>VertexColorFunction</code> which specifies the
     * foreground (border and text) and background (fill) color for each vertex.</p>
     * <p>If users want the <code>VertexColorFunction</code> implementation
     * to highlight selected vertices, they should take this
     * PluggableRenderer instance as a constructor parameter, and call
     * the <code>isPicked</code> method on it to identify selected vertices.</p>
     * <p>Default: black borders, red foreground (selected vertex is orange).</p>
     * @see VertexColorFunction
     * @see ConstantVertexColorFunction
     */
    public void setVertexColorFunction(VertexColorFunction vcf)
    {
        this.vertexColorFunction = vcf;
    }

    /**
     * Sets the <code>VertexFontFunction</code> that specifies the font
     * to use for drawing each vertex label.  This can be used (for example) to
     * emphasize (or to de-emphasize) vertices that have a specific property.
     * <br>Default: 12-point Helvetica
     * @see VertexFontFunction
     */
    public void setVertexFontFunction(VertexFontFunction vff)
    {
        this.vertexFontFunction = vff;
    }

    /**
     * Sets the <code>Predicate</code> that specifies whether each
     * vertex should be drawn; only those vertices for which this
     * predicate returns <code>true</code> will be drawn.  This can be
     * used to selectively display only those vertices that have a
     * specific property, such as a particular decoration or value.
     * <br>Default: all vertices drawn (<code>TruePredicate</code> instance)
     * @see org.apache.commons.collections.Predicate
     */
    public void setVertexIncludePredicate(Predicate p)
    {
        this.vertexIncludePredicate = p;
    }

    /**
     * Specifies whether vertex labels are drawn centered on the vertex
     * position (<code>true</code>) or offset to one side (<code>false</code>).
     * <br>Default: offset
     */
    public void setVertexLabelCentering(boolean b)
    {
        centerVertexLabel = b;
    }

    /**
     * Sets the <code>VertexShapeFunction</code>,
     * which specifies the <code>Shape</code> for each vertex.
     * Users that wish to independently change the size and
     * aspect ratio of a vertex's shape should take a look
     * at the <code>SettableVertexShapeFunction</code>
     * interface and the <code>AbstractVertexShapeFunction</code>
     * abstract class.
     * <br>Default: 8-pixel-diameter circle
     * (<code>EllipseVertexShapeFunction</code>)
     * @see java.awt.Shape
     * @see VertexShapeFunction
     */
    public void setVertexShapeFunction(VertexShapeFunction vsf)
    {
        this.vertexShapeFunction = vsf;
    }

    /**
     * Sets the <code>VertexStringer</code> that specifies the label to
     * draw for each vertex.
     * <br>Default: no labels
     * (<code>ConstantVertexStringer</code>)
     * @see edu.uci.ics.jung.graph.decorators.VertexStringer
     */
    public void setVertexStringer(VertexStringer vs)
    {
        this.vertexStringer = vs;
    }

    /**
     * Sets the <code>VertexStrokeFunction</code> which
     * specifies the <code>Stroke</code> to use when drawing
     * each vertex border.
     * <br>Default: 1-pixel-width basic stroke.
     * @see java.awt.Stroke
     * @see VertexStrokeFunction
     */
    public void setVertexStrokeFunction(VertexStrokeFunction vsf)
    {
        this.vertexStrokeFunction = vsf;
    }


    /**
     * Paints <code>e</code>, whose endpoints are at <code>(x1,y1)</code>
     * and <code>(x2,y2)</code>, on the graphics context <code>g</code>.
     * Uses the color and stroke specified by this instance's
     * <code>EdgeColorFunction</code> and <code>EdgeStrokeFunction</code>,
     * respectively.  (If the color is unspecified, the existing
     * color for the graphics context is used; the same applies to stroke.)
     * The details of the actual rendering are delegated to
     * <code>drawSelfLoop</code> or <code>drawSimpleEdge</code>,
     * depending on the type of the edge.
     * Note that <code>(x1, y1)</code> is the location of
     * e.getEndpoints.getFirst() and <code>(x2, y2)</code> is the location of
     * e.getEndpoints.getSecond().
     *
     */
    public void paintEdge(AwtG2DWrapper g2d, Edge e, int x1, int y1, int x2, int y2)
    {
        if (!edgeIncludePredicate.evaluate(e))
            return;

        // don't draw edge if either incident vertex is not drawn
        Pair endpoints = e.getEndpoints();
        Vertex v1 = (Vertex)endpoints.getFirst();
        Vertex v2 = (Vertex)endpoints.getSecond();
        if (!vertexIncludePredicate.evaluate(v1) ||
            !vertexIncludePredicate.evaluate(v2))
            return;


        // save color and stroke for later restoration
        Color new_color = edgeColorFunction.getEdgeColor(e);

        Color old_fg_color = g2d.getColor();
        Color old_bg_color = g2d.getBackground();

        if (new_color != null)
            g2d.setColor(new_color);
        	g2d.setBackground(new_color);

        Stroke new_stroke = edgeStrokeFunction.getStroke(e);
        Stroke old_stroke = g2d.getStroke();
        if (new_stroke != null)
            g2d.setStroke(new_stroke);

        if (self_loop.evaluate(e))
            drawSelfLoop(g2d, e, x1, y1);
        else
            drawSimpleEdge(g2d, e, x1, y1, x2, y2);

        // restore color and stroke
        if (old_stroke != null)
            g2d.setStroke(old_stroke);

        if (new_color != null) {
            g2d.setColor(old_fg_color);
            g2d.setBackground(old_bg_color);
        }
    }

    /**
     * Draws the self-loop <code>e</code>, whose vertex is at
     * <code>(x,y)</code>, on the graphics context <code>g</code>. The
     * self-loop is drawn as a circle centered at the top of the vertex
     * shape's bounding box, whose diameter is the
     * height of the vertex shape's bounding box.  Delegates the
     * work of drawing the arrowhead (if any) to
     * <code>drawSelfLoopArrowhead</code>,
     * but specifies that the location of any such arrowhead is at the top
     * of the circle.  Draws the associated label, if any, centered above
     * the circle.
     */
    protected void drawSelfLoop(AwtG2DWrapper g, Edge e, int x, int y)
    {
        Vertex v = (Vertex)e.getEndpoints().getFirst();
        Shape s = vertexShapeFunction.getShape(v);
        int width = (int)s.getBounds().getWidth();
        int height = (int)s.getBounds().getHeight();
        g.drawOval(x - width/2, y - height, width, height);

        if (edgeArrowPredicate.evaluate(e))
            drawSelfLoopArrowhead(g, e, x, y, height);

        String label = edgeStringer.getLabel(e);
        if (label != null)
        {
            Font prev = g.getFont();
            g.setFont(edgeFontFunction.getFont(e));
            FontMetrics fm = g.getFontMetrics();
            int h_offset = -(g.stringExtent(label).x / 2);
            int v_offset = (fm.getAscent() / 2) + 5;
            g.drawString(label, x + h_offset , y - height - v_offset);
            g.setFont(prev);
        }
    }

    /**
     * Draws an arrowhead for the self-loop <code>e</code> whose vertex is at
     * <code>(x,y)</code>, on the graphics context <code>g</code>, and whose
     * vertex's shape's height is <code>height</code>.  Uses the arrow shape
     * specified by this instance's <code>EdgeArrowFunction</code>.
     */
    protected void drawSelfLoopArrowhead(AwtG2DWrapper g, Edge e, int x, int y, int height)
    {
        Shape arrow = edgeArrowFunction.getArrow(e);
        AffineTransform at = new AffineTransform();
        at.translate(x + arrow.getBounds2D().getWidth()/2, y - height);
        g.fill(at.createTransformedShape(arrow));
    }

    /**
     * Draws the non-self-loop <code>e</code>, whose endpoints are at <code>(x1,y1)</code>
     * and <code>(x2,y2)</code>, on the graphics context <code>g</code>.  Delegates the
     * work of drawing the arrowheads (if any) to <code>drawArrowhead</code>.
     * Delegates the drawing of the label (if any) to <code>labelEdge</code>.
     */
    protected void drawSimpleEdge(AwtG2DWrapper g, Edge e, int x1, int y1, int x2, int y2)
    {
        float control_offset = edgeControlOffsetFunction.getNumber(e).floatValue();
        float xoff = x2 - x1;
        float yoff = y2 - y1;
        double theta = Math.atan2(yoff, xoff) - Math.PI/2;
        Pair endpoints = e.getEndpoints();
        Shape s2 = vertexShapeFunction.getShape((Vertex)endpoints.getSecond());
        double theta_offset;

        if (control_offset == 0)
        {
            g.drawLine(x1, y1, x2, y2);
            theta_offset = 0;
        }
        else
        {
            float distance = (float)CoordinateUtil.distance(new Coordinates(x1,y1), x2, y2);
            theta_offset = Math.atan2(control_offset, distance/2);
            GeneralPath edge_path = new GeneralPath();
            edge_path.moveTo(0, 0);
            edge_path.lineTo(control_offset, distance/2);
            edge_path.lineTo(0, distance);
            AffineTransform at = new AffineTransform();
            at.translate(x1,y1);
            at.rotate(theta);
            g.draw(at.createTransformedShape(edge_path));
        }

        if (edgeArrowPredicate.evaluate(e))
        {
            drawArrowhead(g, e, x2, y2, xoff, yoff, s2, theta + theta_offset);
            if (e instanceof UndirectedEdge)
            {
                Shape s1 = vertexShapeFunction.getShape((Vertex)endpoints.getFirst());
                drawArrowhead(g, e, x1, y1, xoff, yoff, s1, theta + theta_offset + Math.PI);
            }
        }

        String label = edgeStringer.getLabel(e);
        if (label != null)
            labelEdge(g, e, label, x1, x2, y1, y2);
    }

    /**
     * Draws an arrowhead for the specified edge.  The point of the arrowhead is placed
     * at the intersection of the vertex shape's bounding box and the edge.  The arrowhead
     * shape is specified by this instance's <code>EdgeArrowFunction</code>.
     *
     * @param g     the graphics context on which to draw
     * @param e     the edge whose arrowhead is to be drawn
     * @param x     the x-coordinate of the first endpoint of the edge
     * @param y     the y-coordinate of the first endpoint of the edge
     * @param delta_x   the horizontal distance between the edge's first and second endpoints
     * @param delta_y   the vertical distance between the edge's first and second endpoints
     * @param vertex_shape  the shape for the second endpoint's vertex
     * @param theta the angle of the incoming edge line (may not be the same as the angle
     * between the two endpoints)
     */
    protected void drawArrowhead(AwtG2DWrapper g, Edge e, int x, int y, double delta_x, double delta_y, Shape vertex_shape, double theta)
    {
        Shape arrow = edgeArrowFunction.getArrow(e);
        theta += Math.PI/2;

        // calculate offset from center of vertex bounding box;
        // create coordinates for source and dest centered at dest
        // (since vertex shape will be centered at dest)
        Coordinates source = new Coordinates(-delta_x, -delta_y);
        Coordinates dest = new Coordinates(0,0);
        Coordinates c1 = CoordinateUtil.getClosestIntersection(source, dest, vertex_shape.getBounds2D());
        if (c1 == null) // can happen if source and dest are the same
            return;
        double bounding_box_offset = CoordinateUtil.distance(c1, dest);

        // transform arrowhead into dest coordinate space
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(theta);
        at.translate(-bounding_box_offset, 0);
        g.fill(at.createTransformedShape(arrow));
    }

    /**
     * Labels the specified non-self-loop edge with the specified label.
     * Uses the font specified by this instance's
     * <code>EdgeFontFunction</code>.  (If the font is unspecified, the existing
     * font for the graphics context is used.)  Positions the
     * label between the endpoints according to the coefficient returned
     * by this instance's edge label closeness function.
     */
    protected void labelEdge(AwtG2DWrapper g2d, Edge e, String label, int x1, int x2, int y1, int y2)
    {
        Font prev = g2d.getFont();
        Font new_font = edgeFontFunction.getFont(e);
        if (new_font != null)
            g2d.setFont(new_font);

        int half_lineHeight = g2d.getSWTFontMetrics().getHeight() / 2;

        int distX = x2 - x1;
        int distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);

        double closeness = edgeLabelClosenessFunction.getNumber(e).doubleValue();

        int posX = (int) (x1 + (closeness) * distX);
        int posY = (int) (y1 + (closeness) * distY);

        int xDisplacement = (int) (LABEL_OFFSET * (distY / totalLength));
        int yDisplacement = (int) (LABEL_OFFSET * (-distX / totalLength));

        g2d.drawString(label, posX + xDisplacement, posY + yDisplacement + half_lineHeight);

        if (prev != null)
            g2d.setFont(prev);
    }

    /**
     * Paints the vertex <code>v</code> at the location <code>(x,y)</code>
     * on the graphics context <code>g_gen</code>.  The vertex is painted
     * using the shape returned by this instance's <code>VertexShapeFunction</code>,
     * and the foreground and background (border) colors provided by this
     * instance's <code>VertexColorFunction</code>.  Delegates drawing the
     * label (if any) for this vertex to <code>labelVertex</code>.
     */
    public void paintVertex(AwtG2DWrapper g2d, Vertex v, int x, int y)
    {
        if (!vertexIncludePredicate.evaluate(v)) return;

        Stroke old_stroke = g2d.getStroke();

        Stroke new_stroke = vertexStrokeFunction.getStroke(v);

        if (new_stroke != null)
            g2d.setStroke(new_stroke);

        Color old_fg_color = g2d.getColor();
        Color old_bg_color = g2d.getBackground();

        Shape s = vertexShapeFunction.getShape(v);
        g2d.translate(x,y);
        g2d.setBackground(vertexColorFunction.getBackColor(v));
        g2d.setColor(vertexColorFunction.getForeColor(v));
        g2d.fill(s);
        g2d.setBackground(vertexColorFunction.getBackColor(v));
        g2d.setColor(vertexColorFunction.getForeColor(v));
        g2d.draw(s);
        g2d.translate(-x,-y);

        if (old_stroke != null)
            g2d.setStroke(old_stroke);

        g2d.setColor(old_fg_color);
        g2d.setBackground(old_bg_color);

        String label = vertexStringer.getLabel(v);
        if (label != null)
            labelVertex(g2d, v, label, x, y);
    }

    /**
     * Labels the specified vertex with the specified label.
     * Uses the font specified by this instance's
     * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
     * font for the graphics context is used.)  If vertex label centering
     * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    protected void labelVertex(AwtG2DWrapper g2d, Vertex v, String label, int x, int y)
    {
        Font old = g2d.getFont();
        Font new_font = vertexFontFunction.getFont(v);
        if (new_font != null)
            g2d.setFont(new_font);

        int h_offset;
        int v_offset;
        if (centerVertexLabel)
        {
            // calculate width of string
            org.eclipse.swt.graphics.FontMetrics fm = g2d.getSWTFontMetrics();
            h_offset = -(g2d.stringExtent(label).x / 2);
            v_offset = fm.getAscent() / 2;
        }
        else
        {
            Rectangle2D bounds = vertexShapeFunction.getShape(v).getBounds2D();
            h_offset = (int)(bounds.getWidth() / 2) + 5;
            v_offset = (int)(bounds.getHeight() / 2) + 5;
        }
        g2d.drawString(label, x + h_offset, y + v_offset);

        if (old != null)
            g2d.setFont(old);
    }

    /**
     * @see SWTAbstractRenderer#isPicked()
     */
    public boolean isPicked(Vertex v)
    {
        return super.isPicked(v);
    }

	public boolean isPicked(ArchetypeVertex arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPicked(ArchetypeEdge arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}