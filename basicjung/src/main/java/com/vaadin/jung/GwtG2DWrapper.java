package com.vaadin.jung;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Circle;

/**
 * This wrapper is currently still in development and has a bunch of compile errors.
 *
 * It is in the process of being adapted from an SWT wrapper into a GWT wrapper.
 *
 * In order to accomplish this, the low-level canvas drawing calls that work for
 * SWT must be remapped onto low-level canvas drawing calls for the GWT-Graphics
 * library.  So all the methods should stay, but their implementations should
 * be GWT-graphics implementations.
 *
 * As of 1/2011, <a href="http://code.google.com/p/gwt-graphics/wiki/Manual">here</a> is the manual for GWT-graphcs.
 *
 * As of 1/2011, examples of low-level calls in the GWT-Graphics libraries can be
 * found <a href="http://dev.vaadin.com/svn/incubator/gwt-graphics-examples/src/com/virtuallypreinstalled/hene/gwtgraphicsexamples/client/GWTGraphicsExamples.java">here</a>
 *
 * One of the differences between SWT/AWT and GWT and the library we are using is that
 * GWT-graphics is a vector-graphics library.  Consequently concepts like
 * <a href="http://www.cc.gatech.edu/grads/h/Hao-wei.Hsieh/Haowei.Hsieh/mm.html#sec1">"clipping"</a>
 * may be tricky to map.
 *
 * On the other hand, the contract that these methods must obey is well specified in
 * Java's API, which is easily google-able.
 */
public class GwtG2DWrapper extends Graphics2D {

	//this is the "canvas" equivalent in GWT
	//the comments for this class include an example of use
	private DrawingArea canvas;

	private static Rectangle clip = null;

	//some of these are still useful to keep as holders of numbers.
	private static Point _pt = new Point();
	private static Rectangle2D _awtRect = new Rectangle2D.Double();
	private static Rectangle2D _awtLineRect = new Rectangle2D.Double();
	private static org.vaadin.gwtgraphics.client.shape.Rectangle gwtRect = new org.vaadin.gwtgraphics.client.shape.Rectangle(0,0,0,0);
	private static BufferedImage _imgBuffer = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);

	private static AffineTransform transform = new AffineTransform();
	private static AffineTransform at2D = new AffineTransform();

	//private org.eclipse.swt.graphics.Font curFont;

	/**
	 * the current foreground color that items will be drawn in.
	 */
	private static Color curColor = Color.black;

	private static Color curBackgroundColor = Color.white;

	private static double _lineWidth = 1.0;
	private static double _AffineTransformFlatness = 0.000000001;
    public static double _RectXYGap = 0.5;

    private static HashMap _fontRegister	= new HashMap();
	private static HashMap _colorRegister	= new HashMap();
	private static HashMap _shapeRegister	= new HashMap();
	private static HashMap _strokeRegister	= new HashMap();


	private static Point2D _pt2D = new Point2D.Double();
	private static ArrayList _segLst = new ArrayList();
	private static double[] _rectCoord = new double[8];

	public GwtG2DWrapper(DrawingArea canvas) {

		super();
		this.canvas = canvas;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getColor()
	 */
	public Color getColor() {
		return curColor;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 */
	public void setColor(Color c) {
		curColor = c;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#setBackground(java.awt.Color)
	 */
	public void setBackground(Color c) {
		curBackgroundColor = c;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#getBackground()
	 */
	public Color getBackground() {
		return curBackgroundColor;
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clip(Shape s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Shape s) {
		//Rectangle rect = new Rectangle(100,100, 100, 50);
		//canvas.add(rect);
		Circle circle = new Circle(100, 100, 50);
		circle.setFillColor("red");
		canvas.add(circle);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fill(Shape s) {
		// TODO Auto-generated method stub

	}

	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stroke getStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotate(double theta, double x, double y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(double sx, double sy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setComposite(Composite comp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaint(Paint paint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStroke(Stroke s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shear(double shx, double shy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transform(AffineTransform Tx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void translate(double tx, double ty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub

	}

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public Shape getClip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClip(Shape clip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub

	}

	/**********************************************************************
	 * THIS METHOD HAS BEEN CONVERTED TO GWT
	 **********************************************************************/

}