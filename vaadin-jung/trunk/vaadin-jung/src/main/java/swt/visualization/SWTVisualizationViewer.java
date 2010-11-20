/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package swt.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import swt.contrib.AwtG2DWrapper;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PickedInfo;
import edu.uci.ics.jung.visualization.StatusCallback;

/**
 * 
 * @author lbigeard
 */
public class SWTVisualizationViewer extends Composite implements PickedInfo {
    
	protected StatusCallback statusCallback;
	Thread relaxer;
	boolean suspended;
	boolean manualSuspend;
	protected SWTRenderer renderer;
	protected Layout layout;
	
	protected double offsetx = 0.0;
	protected double offsety = 0.0;
	protected double scalex = 1.0;
	protected double scaley = 1.0;
	
	protected Map renderingHints = new HashMap();
	//	private Graph graph;

	public final static String VERTEX_PICKED_KEY	= "VertexPicked"; //$NON-NLS-1$
	public final static String VERTEX_SELECTED_KEY	= "VertexSelected"; //$NON-NLS-1$
	
	static Vertex picked;
	static Vertex verticesSelection;
	static Edge edgeSelection;
	static Point delta; 
	private AsynchronousRedraw asynchRedraw;
	GC gcStatic = new GC(this);
	

	public SWTVisualizationViewer(Composite parent, Layout l, SWTRenderer r) {
		super(parent, SWT.NONE);
		
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL_BOTH));
		
		addControlListener(new VisualizationListener(this));
		
		renderer = r;
		r.setPickedKey(this);
		layout = l;
		setSize(600, 600);
		layout.initialize(new Dimension(600, 600));
		suspended = true;
		manualSuspend = false;
		asynchRedraw = new AsynchronousRedraw();
		init();
		initListeners();
	}
	
	private void initListeners(){
		Listener graphListener = new Listener() {
	        public void handleEvent(Event e) {
	            switch(e.type) {
		            case SWT.MouseDown:
		            {
		    		    Point p = new Point((int)(e.x/scalex+offsetx), (int)(e.y/scaley+offsety));
		    			Vertex v = layout.getVertex(p.x, p.y);
		    			if (v == null) {
		    				//System.out.println("Bad pick!"); //$NON-NLS-1$
		    				return;
		    			}
		    			picked = v;
		    			pick(picked, true);
		    			layout.forceMove(picked, (int)p.x, (int)p.y);
		    			Display.getDefault().asyncExec(asynchRedraw);
		    			break;
		            }
		            case SWT.MouseUp:
		            {
		    			if (picked == null)
		    				return;
		    			pick(picked, false);
		    			picked = null;
		    			Display.getDefault().asyncExec(asynchRedraw);
		    			break;
		    		}
		            case SWT.MouseMove:
		            {
		    			if (picked == null)
		    				return;
		    			layout.forceMove(picked, (int)(e.x/scalex+offsetx), (int)(e.y/scaley+offsety));
		    			Display.getDefault().asyncExec(asynchRedraw);
		    			//			drawSpot( e.getX(), e.getY() );
		    			break;
		    		}
	            }
	        }
	    };
	    addListener(SWT.MouseDown, graphListener);
	    addListener(SWT.MouseUp, graphListener);
	    addListener(SWT.MouseMove, graphListener);
	}
	
	/**
	 * UNTESTED.
	 * @param v
	 */
	public void setRenderer(SWTRenderer r) {
		this.renderer = r;
		r.setPickedKey(this);
		setVisible(true);
		layout();
		Display.getDefault().asyncExec(asynchRedraw);
	}

	/**
	 * @param v
	 */
	public void setGraphLayout(Layout v) {
		suspend();
		v.initialize(this.layout.getCurrentSize());
		this.layout = v;
		layout.restart();
		prerelax();
		unsuspend();
	}

	/**
	 * starts a visRunner thread without prerelaxing
	 */
	public synchronized void restartThreadOnly() {
	    if (visRunnerIsRunning ) {
	        throw new FatalException("Can't init while a visrunner is running"); //$NON-NLS-1$
	    }
		relaxer = new VisRunner();
		relaxer.start();
	}

	
	/**
	 * Pre-relaxes and starts a visRunner thread
	 */
	public synchronized void init() {
	    if (visRunnerIsRunning ) {
	        throw new FatalException("Can't init while a visrunner is running"); //$NON-NLS-1$
	    }
		prerelax();
		relaxer = new VisRunner();
		relaxer.start();
	}

	/**
	 * Restarts layout, then calls init();
	 */
	public synchronized void restart() {
	    if (visRunnerIsRunning ) {
	        throw new FatalException("Can't restart while a visrunner is running"); //$NON-NLS-1$
	    }
		layout.restart();
		init();
		Display.getDefault().asyncExec(asynchRedraw);
	}
	/** 
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		layout.resize(new Dimension(getSize().x, getSize().y));
	}

	/**
	 *
	 */
	public void prerelax() {
		suspend();
		if (layout.isIncremental()) {
			// then increment layout for half a second
			long timeNow = System.currentTimeMillis();
			while (System.currentTimeMillis() - timeNow < 500)
				layout.advancePositions();
		}
		unsuspend();
	}

	public void start() {
		suspended = false;
		synchronized (pauseObject) {
			pauseObject.notify();
		}
	}

	public void suspend() {
		manualSuspend = true;
	}

	public void unsuspend() {
		manualSuspend = false;
		synchronized (pauseObject) {
			pauseObject.notify();
		}
	}
	public Object pauseObject = new String("PAUSE OBJECT"); //$NON-NLS-1$

	public boolean isPicked(Vertex v) {
		Boolean picked = (Boolean) v.getUserDatum(getVisKey());
		return ((picked != null) && (picked == Boolean.TRUE));
	}

	/**
	 * @param picked
	 * @param b
	 */
	protected void pick(Vertex picked, boolean b) {
		if (b)
			layout.lockVertex(picked);
		else
			layout.unlockVertex(picked);
		Object key = getVisKey();
		if (picked.getUserDatum(key) != null) {
			picked.setUserDatum(
				key,
				b ? Boolean.TRUE : Boolean.FALSE,
				UserData.REMOVE);
		} else {
			picked.addUserDatum(
				key,
				b ? Boolean.TRUE : Boolean.FALSE,
				UserData.REMOVE);
		}
	}

	public static final String VIS_KEY = "edu.uci.ics.jung.visualization"; //$NON-NLS-1$

	protected Object key = null;

	protected Object getVisKey() {
		if (key == null)
			key = new Pair(this, VIS_KEY);
		return key;
	}


	long[] relaxTimes = new long[5];
	long[] paintTimes = new long[5];
	int relaxIndex = 0;
	int paintIndex = 0;
	double paintfps, relaxfps;

	boolean stop = false;

	boolean visRunnerIsRunning = false; 
	
	protected class VisRunner extends Thread {
		public VisRunner() {
			super("Relaxer Thread"); //$NON-NLS-1$
		}

		public void run() {
		    visRunnerIsRunning = true;
		    try {
				while (!layout.incrementsAreDone() && !stop) {
	
					synchronized (pauseObject) {
						while ((suspended || manualSuspend) && !stop) {
							try {
								pauseObject.wait();
							} catch (InterruptedException e) {
							}
						}
					}
					long start = System.currentTimeMillis();
					layout.advancePositions();
					long delta = System.currentTimeMillis() - start;
	
					if (stop)
						return;
	
					String status = layout.getStatus();
					if (statusCallback != null && status != null) {
						statusCallback.callBack(status);
					}
	
					if (stop)
						return;
	
					relaxTimes[relaxIndex++] = delta;
					relaxIndex = relaxIndex % relaxTimes.length;
					relaxfps = average(relaxTimes);
	
					if (stop)
						return;
	
					Display.getDefault().asyncExec(asynchRedraw);
	
					if (stop)
						return;
	
					try {
						sleep(20);
					} catch (InterruptedException ie) {
					}
				}
		    } finally {
				visRunnerIsRunning = false;
		    }
		}
	}
	
	/**
	 * Returns a flag that says whether the visRunner thread is running. If
	 * it is not, then you may need to restart the thread (with 
	 * @return
	 */
	public boolean isVisRunnerRunning() {
	    return visRunnerIsRunning;
	}
	
	class AsynchronousRedraw  implements Runnable {
        public void run() {
            redraw();
        }
    }
	
    public void redraw() {
        if (!isDisposed()) {
			Rectangle r = getClientArea();
			if (r.width != 0 && r.height != 0) { 
		        Image img = new Image(Display.getDefault(), r.width, r.height);
		        GC gcBuffer = new GC(img);
		        AwtG2DWrapper g2d = new AwtG2DWrapper(gcBuffer, getDisplay());
		        paintComponent(g2d, r);
		        gcStatic.drawImage(img, 0, 0);
		        gcBuffer.dispose();
		        g2d.dispose();
		        img.dispose();
			}
        }
    }
    
	/*
	 * Add a double buffer to avoid the clipping when redrawing the graph 
	 *
	 */
	protected void paintComponent(AwtG2DWrapper g2d, Rectangle r) {
	    start();
		long start = System.currentTimeMillis();

		AffineTransform oldXform = g2d.getTransform();
		AffineTransform newXform = new AffineTransform(oldXform);
		newXform.scale(scalex, scaley);
		newXform.translate(-offsetx, -offsety);
		g2d.setTransform(newXform);
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g2d.setPaint(new GradientPaint(0.0f, 0.0f, Color.darkGray, (float) r.width, (float) r.height, Color.white));
	    g2d.fillRect(0, 0, r.width, r.height);
		
		drawGraph(g2d);
		
		g2d.setTransform(oldXform);
		
		long delta = System.currentTimeMillis() - start;
		paintTimes[paintIndex++] = delta;
		paintIndex = paintIndex % paintTimes.length;
		//paintfps = average(paintTimes);		
		//System.out.println(paintfps + " fps");
	}

	private void drawGraph(AwtG2DWrapper g2d) {
		for (Iterator iter = layout.getVisibleEdges().iterator();iter.hasNext();) {
			Edge e = (Edge) iter.next();
			Vertex v1 = (Vertex) e.getIncidentVertices().iterator().next();
			Vertex v2 = e.getOpposite(v1);
			renderer.paintEdge(g2d, e, (int) layout.getX(v1), (int) layout.getY(v1), (int) layout.getX(v2), (int) layout.getY(v2));
		}
		for (Iterator iter = layout.getVisibleVertices().iterator();iter.hasNext();) {
			Vertex v = (Vertex) iter.next();
			renderer.paintVertex(g2d, v, (int) layout.getX(v), (int) layout.getY(v));
		}
		if (verticesSelection != null){
			renderer.paintVertex(g2d, verticesSelection, (int) layout.getX(verticesSelection), (int) layout.getY(verticesSelection));
		}
	}
	
	/**
	 * Returns the double average of a number of long values.
	 * @param paintTimes	an array of longs
	 * @return the average of the doubles
	 */
	protected double average(long[] paintTimes) {
		double l = 0;
		for (int i = 0; i < paintTimes.length; i++) {
			l += paintTimes[i];
		}
		return l / paintTimes.length;
	}

	private class VisualizationListener extends ControlAdapter {
		private SWTVisualizationViewer vv;
		public VisualizationListener(SWTVisualizationViewer vv) {
			this.vv = vv;
		}
		public void controlMoved(ControlEvent e) {
			super.controlMoved(e);
		}
		public void controlResized(ControlEvent e) {		
			Rectangle rect = ((Composite)e.widget).getBounds();
			setSize(rect.width, rect.height);
			vv.layout.resize(new Dimension(rect.width, rect.height));
			Display.getDefault().asyncExec(asynchRedraw);
		}
	}
	
	/**
	 * @param scb
	 */
	public void setTextCallback(StatusCallback scb) {
		this.statusCallback = scb;
	}

	/**
	 * 
	 */
	public synchronized void stop() {
		manualSuspend = false;
		suspended = false;
		stop = true;
		synchronized (pauseObject) {
			pauseObject.notifyAll();
		}
	}
}