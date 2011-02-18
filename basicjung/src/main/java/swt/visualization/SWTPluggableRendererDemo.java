/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Nov 7, 2004
 */
package swt.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.TruePredicate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.importance.VoltageRanker;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedEdge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStringer;
import edu.uci.ics.jung.graph.decorators.ConstantVertexColorFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexStringer;
import edu.uci.ics.jung.graph.decorators.EdgeColorFunction;
import edu.uci.ics.jung.graph.decorators.EdgeFontFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.NumberEdgeValueStringer;
import edu.uci.ics.jung.graph.decorators.NumberVertexValue;
import edu.uci.ics.jung.graph.decorators.NumberVertexValueStringer;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexColorFunction;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.decorators.VertexStrokeFunction;
import edu.uci.ics.jung.graph.predicates.ContainsUserDataKeyVertexPredicate;
import edu.uci.ics.jung.random.generators.BarabasiAlbertGenerator;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.PickedInfo;


/**
 * Shows off some of the capabilities of <code>PluggableRenderer</code>.
 * This code provides examples of different ways to provide and
 * change the various functions that provide property information
 * to the renderer.
 *
 * <p>This demo creates a random mixed-mode graph with random edge
 * weights using <code>TestGraph.generateMixedRandomGraph</code>.
 * It then runs <code>VoltageRanker</code> on this graph, using half
 * of the "seed" vertices from the random graph generation as
 * voltage sources, and half of them as voltage sinks.</p>
 *
 * <p>What the controls do:
 * <ul>
 * <li/>"vertex seed coloring": if checked, the seed vertices are colored blue,
 * and all other vertices are colored red.  Otherwise, all vertices are colored
 * a slightly transparent red (except the currently "picked" vertex, which is
 * colored transparent purple).
 * <li/>"vertex selection stroke highlighting": if checked, the picked vertex
 * and its neighbors are all drawn with heavy borders.  Otherwise, all vertices
 * are drawn with light borders.
 * <li/>"show vertex ranks (voltages)": if checked, each vertex is labeled with its
 * calculated 'voltage'.  Otherwise, vertices are unlabeled.
 * <li/>"vertex degree shapes": if checked, vertices are drawn with a polygon with
 * number of sides proportional to its degree.  Otherwise, vertices are drawn
 * as ellipses.
 * <li/>"vertex voltage size": if checked, vertices are drawn with a size
 * proportional to their voltage ranking.  Otherwise, all vertices are drawn
 * at the same size.
 * <li/>"vertex degree ratio stretch": if checked, vertices are drawn with an
 * aspect ratio (height/width ratio) proportional to the ratio of their indegree to
 * their outdegree.  Otherwise, vertices are drawn with an aspect ratio of 1.
 * <li/>"bold text": if checked, all vertex and edge labels are drawn using a
 * boldface font.  Otherwise, a normal-weight font is used.  (Has no effect if
 * no labels are currently visible.)
 * <li/>"edge weight highlighting": if checked, edges with weight greater than
 * a threshold value are drawn using thick black solid lines, and other edges are drawn
 * using thin gray dotted lines.  (This combines edge stroke and color.) Otherwise,
 * all edges are drawn with thin black solid lines.
 * <li/>"show edge weights": if checked, edges are labeled with their weights.
 * Otherwise, edges are not labeled.
 * <li/>"show undirected edge arrows": if checked, undirected edges are drawn with
 * arrowheads at each end.  Otherwise, no arrows are used for undirected edges.
 * <li/>"show directed edges": if checked, directed edges are shown.  Otherwise,
 * directed edges are not shown.
 * <li/>"show undirected edges": if checked, undirected edges are shown.  Otherwise,
 * undirected edges are not shown.
 * </ul>
 * </p>
 *
 * @author Danyel Fisher, Joshua O'Madadhain
 * @author Lucas Bigeardel
 */
public class SWTPluggableRendererDemo
{
    protected static Button v_color;
    protected static Button e_color;
    protected static Button v_stroke;
    protected static Button e_arrow_pred;
    protected static Button v_shape;
    protected static Button v_size;
    protected static Button v_aspect;
    protected static Button v_labels;
    protected static Button e_labels;
    protected static Button font;
    protected static Button e_show_d;
    protected static Button e_show_u;
    protected static Button v_small;
    protected static Button restart;

    protected static SWTPluggableRenderer pr;
    protected static VertexColorFunction vcf_constant;
    protected static VertexColorFunction vcf_degree;
    protected static EdgeWeightColorStroke ewcs;
    protected static VertexStrokeHighlight vsh;
    protected static VertexStringer vs;
    protected static VertexStringer vs_none;
    protected static EdgeStringer es;
    protected static EdgeStringer es_none;
    protected static FontHandler ff;
    protected static VertexShapeSizeAspect vssa;
    protected static EdgeDisplayPredicate show_edge;
    protected static VertexDisplayPredicate show_vertex;

    protected final static Object VOLTAGE_KEY = "voltages"; //$NON-NLS-1$
    protected final static Object EDGE_WEIGHT_KEY = "edge_weight"; //$NON-NLS-1$

    protected static NumberEdgeValue edge_weight = new UserDatumNumberEdgeValue(EDGE_WEIGHT_KEY);
    protected static NumberVertexValue voltages = new UserDatumNumberVertexValue(VOLTAGE_KEY);

    private static int DUMP_ID;

    protected static SWTGraphDraw gd;

    public static void main(String[] s ) {
        startFunction();
    }

    public static void startFunction() {
        Display display = new Display ();
        final Shell shell = new Shell (display);
        shell.setLayout (new GridLayout(2, false));
        shell.setSize(800, 600);

        Graph g = getGraph();

        pr = new SWTPluggableRenderer();

        vcf_constant = new ConstantVertexColorFunction(pr, Color.BLACK, new Color(1f,0f,0f,.9f), new Color(1f,0f,1f,.9f));
        vcf_degree = new SeedColor();
        ewcs = new EdgeWeightColorStroke(edge_weight);
        vsh = new VertexStrokeHighlight(pr);
        ff = new FontHandler();
        vs_none = new ConstantVertexStringer(null);
        es_none = new ConstantEdgeStringer(null);
        vssa = new VertexShapeSizeAspect(voltages);
        show_edge = new EdgeDisplayPredicate(true, true);
        show_vertex = new VertexDisplayPredicate(false);

        pr.setVertexColorFunction(vcf_constant);
        pr.setVertexStrokeFunction(vsh);
        pr.setVertexStringer(vs_none);
        pr.setVertexFontFunction(ff);
        pr.setVertexShapeFunction(vssa);
        pr.setVertexIncludePredicate(show_vertex);

        pr.setEdgeColorFunction(ewcs);
        pr.setEdgeStringer(es_none);
        pr.setEdgeFontFunction(ff);
        pr.setEdgeStrokeFunction(ewcs);
        pr.setEdgeIncludePredicate(show_edge);

        addBottomControls(shell);

        gd = new SWTGraphDraw(shell, g);
        gd.setGraphLayout(new FRLayout(g));
        gd.setRenderer( pr );
        gd.setBackground(Color.white);



        shell.addControlListener(new ControlListener() {
            public void controlMoved(ControlEvent e) {}
            public void controlResized(ControlEvent e) {
                gd.getVisualizationViewer().notifyListeners(SWT.Resize, new Event());
            }
        });

        shell.open ();
        while (!shell.isDisposed ()) {
                if (!display.readAndDispatch ()) display.sleep ();
        }
        display.dispose ();
    }

    /**
     * Generates a mixed-mode random graph, runs VoltageRanker on it, and
     * returns the resultant graph.
     */
    public static Graph getGraph()
    {
        Graph g = TestGraphs.generateMixedRandomGraph(edge_weight, 75);

        vs = new NumberVertexValueStringer(voltages);
        es = new NumberEdgeValueStringer(edge_weight);

        // collect the seeds used to define the random graph
        Collection seeds = PredicateUtils.getVertices( (ArchetypeGraph) g, new ContainsUserDataKeyVertexPredicate(BarabasiAlbertGenerator.SEED));
        if (seeds.size() < 2)
            System.out.println("need at least 2 seeds (one source, one sink)"); //$NON-NLS-1$

        // use these seeds as source and sink vertices, run VoltageRanker
        boolean source  = true;
        Set sources             = new HashSet();
        Set sinks               = new HashSet();
        for (Iterator iter = seeds.iterator(); iter.hasNext(); ) {
            if (source) {
                sources.add(iter.next());
            } else {
                sinks.add(iter.next());
            }
            source = !source;
        }
        VoltageRanker vr = new VoltageRanker(edge_weight, voltages, 100, 0.01);
        vr.calculateVoltages(g, sources, sinks);

        return g;
    }

    /**
     * @param jp
     * @param gd
     */
    protected static void addBottomControls(Composite jp)
    {
        final Composite control_panel = new Composite(jp, SWT.NONE);
        control_panel.setLayout(new GridLayout());
        control_panel.setLayoutData(new GridData(GridData.BEGINNING|GridData.HORIZONTAL_ALIGN_BEGINNING));

        Composite vertex_panel = new Composite(control_panel, SWT.NONE);
        vertex_panel.setLayout(new GridLayout());
        vertex_panel.setLayoutData(new GridData(GridData.BEGINNING|GridData.HORIZONTAL_ALIGN_BEGINNING));

        Composite edge_panel = new Composite(control_panel, SWT.NONE);
        edge_panel.setLayout(new GridLayout());
        edge_panel.setLayoutData(new GridData(GridData.BEGINNING|GridData.HORIZONTAL_ALIGN_BEGINNING));

        Composite both_panel = new Composite(control_panel, SWT.NONE);
        both_panel.setLayout(new GridLayout());
        both_panel.setLayoutData(new GridData(GridData.BEGINNING|GridData.HORIZONTAL_ALIGN_BEGINNING));

        Listener actionListener = new Listener() {
            public void handleEvent(Event e) {
                if (e.type == SWT.Selection) {
                        Button source = (Button)(e.widget);
                        if (source == v_color) {
                            if (source.getSelection()) {
                                pr.setVertexColorFunction(vcf_degree);
                            } else {
                                pr.setVertexColorFunction(vcf_constant);
                            }
                        } else if (source == e_color) {
                            ewcs.setWeighted(source.getSelection());
                        } else if (source == v_stroke) {
                            vsh.setHighlight(source.getSelection());
                        } else if (source == v_labels) {
                            if (source.getSelection()) {
                                pr.setVertexStringer(vs);
                            } else {
                                pr.setVertexStringer(vs_none);
                            }
                        } else if (source == e_labels) {
                            if (source.getSelection()) {
                                pr.setEdgeStringer(es);
                            } else {
                                pr.setEdgeStringer(es_none);
                            }
                        } else if (source == e_arrow_pred) {
                            if (source.getSelection()) {
                                pr.setEdgeArrowPredicate(TruePredicate.getInstance());
                            }
                            //else {
                        //pr.setEdgeArrowPredicate(Graph.DIRECTED_EDGE);
                    //}
                        } else if (source == font) {
                            ff.setBold(source.getSelection());
                        } else if (source == v_shape) {
                            vssa.useFunnyShapes(source.getSelection());
                        } else if (source == v_size) {
                            vssa.setScaling(source.getSelection());
                        } else if (source == v_aspect) {
                            vssa.setStretching(source.getSelection());
                        } else if (source == e_show_d) {
                            show_edge.showDirected(source.getSelection());
                        } else if (source == e_show_u) {
                            show_edge.showUndirected(source.getSelection());
                        } else if (source == v_small) {
                            show_vertex.filterSmall(source.getSelection());
                        }
                        gd.getVisualizationViewer().redraw();
                }
            }
        };

        v_color = new Button(vertex_panel, SWT.CHECK);
        v_color.setText("vertex seed coloring"); //$NON-NLS-1$
        v_color.addListener(SWT.Selection, actionListener);

        v_stroke = new Button(vertex_panel, SWT.CHECK);
        v_stroke.setText("vertex selection stroke highlighting"); //$NON-NLS-1$
        v_stroke.addListener(SWT.Selection, actionListener);

        v_labels = new Button(vertex_panel, SWT.CHECK);
        v_labels.setText("show vertex ranks (voltages)"); //$NON-NLS-1$
        v_labels.addListener(SWT.Selection, actionListener);

        v_shape = new Button(vertex_panel, SWT.CHECK);
        v_shape.setText("vertex degree shapes"); //$NON-NLS-1$
        v_shape.addListener(SWT.Selection, actionListener);

        v_size = new Button(vertex_panel, SWT.CHECK);
        v_size.setText("vertex voltage size"); //$NON-NLS-1$
        v_size.addListener(SWT.Selection, actionListener);

        v_aspect = new Button(vertex_panel, SWT.CHECK);
        v_aspect.setText("vertex degree ratio stretch"); //$NON-NLS-1$
        v_aspect.addListener(SWT.Selection, actionListener);

        v_small = new Button(vertex_panel, SWT.CHECK);
        v_small.setText("filter vertices of degree < " + VertexDisplayPredicate.MIN_DEGREE); //$NON-NLS-1$
        v_small.addListener(SWT.Selection, actionListener);


        e_color = new Button(edge_panel, SWT.CHECK);
        e_color.setText("edge weight highlighting"); //$NON-NLS-1$
        e_color.addListener(SWT.Selection, actionListener);

        e_labels = new Button(edge_panel, SWT.CHECK);
        e_labels.setText("show edge weights"); //$NON-NLS-1$
        e_labels.addListener(SWT.Selection, actionListener);

        e_arrow_pred = new Button(edge_panel, SWT.CHECK);
        e_arrow_pred.setText("show undirected edge arrows"); //$NON-NLS-1$
        e_arrow_pred.addListener(SWT.Selection, actionListener);

        e_show_d = new Button(edge_panel, SWT.CHECK);
        e_show_d.setText("show directed edges"); //$NON-NLS-1$
        e_show_d.addListener(SWT.Selection, actionListener);
        e_show_d.setSelection(true);

        e_show_u = new Button(edge_panel, SWT.CHECK);
        e_show_u.setText("show undirected edges"); //$NON-NLS-1$
        e_show_u.addListener(SWT.Selection, actionListener);
        e_show_u.setSelection(true);

        font = new Button(both_panel, SWT.CHECK);
        font.setText("bold text"); //$NON-NLS-1$
        font.addListener(SWT.Selection, actionListener);

        restart = new Button(both_panel, SWT.PUSH) ;
        restart.setText("Restart Layout"); //$NON-NLS-1$
        restart.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                if (!gd.getVisualizationViewer().isVisRunnerRunning()) {
                    gd.getVisualizationViewer().restart();
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }

    private final static class SeedColor implements VertexColorFunction
    {
        private final static Color DARK_BLUE = new Color(0, 0, 100);
        public Color getForeColor(Vertex v)
        {
            return Color.BLACK;
        }
        public Color getBackColor(Vertex v)
        {
            if (v.containsUserDatumKey(BarabasiAlbertGenerator.SEED))
                return DARK_BLUE;
            else
                return Color.RED;
        }
    }

    private final static class EdgeWeightColorStroke implements EdgeColorFunction, EdgeStrokeFunction {
        protected static final Stroke basic = new BasicStroke(1);
        protected static final Stroke heavy = new BasicStroke(2);
        protected static final Stroke dotted = SWTPluggableRenderer.DOTTED;

        protected boolean weighted = false;
        protected NumberEdgeValue edge_weight;

        public EdgeWeightColorStroke(NumberEdgeValue edge_weight) {
            this.edge_weight = edge_weight;
        }

        public void setWeighted(boolean weighted) {
            this.weighted = weighted;
        }

        public Color getEdgeColor(Edge e) {
            if (weighted) {
                float value;
                if (drawHeavy(e)) {
                    value = 0;
                } else {
                    value = 0.5f;
                }
                return new Color(value, value, value);
            } else {
                return Color.BLACK;
            }
        }

        public Stroke getStroke(Edge e) {
            if (weighted) {
                if (drawHeavy(e)) {
                    return heavy;
                } else {
                    return dotted;
                }
            } else {
                return basic;
            }
        }

        protected boolean drawHeavy(Edge e) {
            double value = edge_weight.getNumber(e).doubleValue();
            if (value > 0.7) {
                return true;
            } else {
                return false;
            }
        }
    }

    private final static class VertexStrokeHighlight implements VertexStrokeFunction {
        protected boolean highlight = false;
        protected Stroke heavy = new BasicStroke(5);
        protected Stroke medium = new BasicStroke(3);
        protected Stroke light = new BasicStroke(1);
        protected PickedInfo pi;

        public VertexStrokeHighlight(PickedInfo pi) {
            this.pi = pi;
        }

        public void setHighlight(boolean highlight) {
            this.highlight = highlight;
        }

        public Stroke getStroke(Vertex v) {
            if (highlight) {
                if (pi.isPicked(v)) {
                    return heavy;
                } else {
                    for (Iterator iter = v.getNeighbors().iterator(); iter.hasNext(); ) {
                        Vertex w = (Vertex)iter.next();
                        if (pi.isPicked(w))
                            return medium;
                    }
                    return light;
                }
            } else {
                return light;
            }
        }
    }

    private final static class FontHandler implements VertexFontFunction, EdgeFontFunction {
        protected boolean bold = false;
        Font f = new Font("Helvetica", Font.PLAIN, 12); //$NON-NLS-1$
        Font b = new Font("Helvetica", Font.BOLD, 12); //$NON-NLS-1$

        public void setBold(boolean b) {
            bold = b;
        }

        public Font getFont(Vertex v) {
            return (bold)?b:f;
        }

        public Font getFont(Edge e){
            return (bold)?b:f;
        }
    }

    private final static class EdgeDisplayPredicate implements Predicate {
        protected boolean show_d;
        protected boolean show_u;

        public EdgeDisplayPredicate(boolean show_d, boolean show_u) {
            this.show_d = show_d;
            this.show_u = show_u;
        }

        public void showDirected(boolean b) {
            show_d = b;
        }

        public void showUndirected(boolean b) {
            show_u = b;
        }

        public boolean evaluate(Object arg0) {
            if (arg0 instanceof DirectedEdge && show_d)
                return true;
            if (arg0 instanceof UndirectedEdge && show_u)
                return true;
            return false;
        }
    }

    private final static class VertexDisplayPredicate implements Predicate {
        protected boolean filter_small;
        protected final static int MIN_DEGREE = 4;

        public VertexDisplayPredicate(boolean filter) {
            this.filter_small = filter;
        }

        public void filterSmall(boolean b) {
            filter_small = b;
        }

        public boolean evaluate(Object arg0) {
            Vertex v = (Vertex)arg0;
            if (filter_small) {
                return (v.degree() >= MIN_DEGREE);
            } else {
                return true;
            }
        }
    }

    /**
     * Controls the shape, size, and aspect ratio for each vertex.
     *
     * @author Joshua O'Madadhain
     */
    private final static class VertexShapeSizeAspect
        extends AbstractVertexShapeFunction
        implements VertexSizeFunction, VertexAspectRatioFunction
    {
        protected boolean stretch = false;
        protected boolean scale = false;
        protected boolean funny_shapes = false;
        protected NumberVertexValue voltages;

        public VertexShapeSizeAspect(NumberVertexValue voltages) {
            this.voltages = voltages;
            setSizeFunction(this);
            setAspectRatioFunction(this);
        }

        public void setStretching(boolean stretch) {
            this.stretch = stretch;
        }

        public void setScaling(boolean scale) {
            this.scale = scale;
        }

        public void useFunnyShapes(boolean use) {
            this.funny_shapes = use;
        }

        public int getSize(Vertex v) {
            if (scale) {
                return (int)(voltages.getNumber(v).doubleValue() * 30) + 20;
            } else {
                return 20;
            }
        }

        public float getAspectRatio(Vertex v) {
            if (stretch) {
                return (float)(v.inDegree() + 1) / (v.outDegree() + 1);
            } else {
                return 1.0f;
            }
        }

        public Shape getShape(Vertex v) {
            if (funny_shapes) {
                if (v.degree() < 5) {
                    int sides = (int)Math.max(v.degree(), 3);
                    return factory.getRegularPolygon(v, sides);
                } else {
                    return factory.getRegularStar(v, v.degree());
                }
            }
            return factory.getEllipse(v);
        }
    }
}
