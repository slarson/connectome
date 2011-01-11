package com.vaadin.jung;

import java.awt.BasicStroke;
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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
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
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.animation.Animate;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Ellipse;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.LineTo;

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

	/**********************************************************************
	 * THIS METHOD HAS BEEN CONVERTED TO GWT
	 **********************************************************************/
	/* (non-Javadoc)
	 * @see java.awt.Graphics#getClipBounds()
	 */
	public Rectangle getClipBounds() {
		if (clip == null) {
			//get the clip bounds to be the entire area of the canvas.
			//a reasonable default clipping rectangle according to:
			//http://www.cc.gatech.edu/grads/h/Hao-wei.Hsieh/Haowei.Hsieh/mm.html#sec1
			clip = new Rectangle(canvas.getAbsoluteLeft(),
					canvas.getAbsoluteTop(),canvas.getWidth(),canvas.getHeight());
		}
		try {
			applyAffineTransformToRect2D(clip, transform.createInverse());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return clip;
	}

	/**********************************************************************
	 * THIS METHOD HAS BEEN CONVERTED TO GWT
	 **********************************************************************/
	/* (non-Javadoc)
	 * @see java.awt.Graphics#clipRect(int, int, int, int)
	 */
	public void clipRect(int x, int y, int width, int height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect, transform);
		
		Rectangle2D clip = getClipBounds();
		clip = clip.createIntersection(_awtRect);

	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect, transform);
		getGwtRectFromAwt(_awtRect,swtRect);
		gc.setClipping(swtRect);		
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#clip(java.awt.Shape)
	 */
	public void clip(Shape s) {
		Rectangle2D clipBds = s.getBounds2D();
		applyAffineTransformToRect2D(clipBds, transform);
		getGwtRectFromAwt(clipBds,swtRect);

		org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
		clip = clip.intersection(swtRect);
				
		gc.setClipping(swtRect);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setClip(java.awt.Shape)
	 */
	public void setClip(Shape clip) {
		if (clip == null) {
			gc.setClipping((org.eclipse.swt.graphics.Rectangle)null);	
		} else {
			Rectangle2D clipBds = clip.getBounds2D();
			applyAffineTransformToRect2D(clipBds, transform);
			getGwtRectFromAwt(clipBds,swtRect);
			gc.setClipping(swtRect);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Graphics#getClip()
	 */
	public Shape getClip() {
		org.eclipse.swt.graphics.Rectangle rect = gc.getClipping();
		Rectangle2D aRect = new Rectangle2D.Double(rect.x,rect.y,rect.width,rect.height);
		try {
			applyAffineTransformToRect2D(aRect,transform.createInverse());
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return aRect;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#getDeviceConfiguration()
	 */
	public GraphicsConfiguration getDeviceConfiguration() {
		return ((Graphics2D)_imgBuffer.getGraphics()).getDeviceConfiguration();
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#getPaint()
	 */
	public Paint getPaint() {
		return getColor();
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#setPaint(java.awt.Paint)
	 */
	public void setPaint(Paint paint) {
		if (paint instanceof Color) {
			setColor((Color)paint);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#getFont()
	 */
	public Font getFont() {
		if (curFont != null) {
			int style = Font.PLAIN;
			FontData[] fd = curFont.getFontData();
			if (fd.length > 0) {
			    style = ((fd[0].getStyle() & SWT.BOLD) != 0)?(style | Font.BOLD):style;	
			    style = ((fd[0].getStyle() & SWT.ITALIC) != 0)?(style | SWT.ITALIC):style;	
			    return new Font(fd[0].getName(),style,fd[0].height);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		StringBuffer buffer = new StringBuffer("name="); //$NON-NLS-1$
		buffer.append(font.getFamily());
		buffer.append(";bold="); //$NON-NLS-1$
		buffer.append(font.isBold());
		buffer.append(";italic="); //$NON-NLS-1$
		buffer.append(font.isItalic());
		buffer.append(";size="); //$NON-NLS-1$
		buffer.append(font.getSize());
		curFont = getFont(buffer.toString());
	}


	/* (non-Javadoc)
	 * @see java.awt.Graphics#translate(int, int)
	 */
	public void translate(int x, int y) {
		transform.translate(x,y);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#translate(double, double)
	 */
	public void translate(double tx, double ty) {
		transform.translate(tx,ty);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#rotate(double)
	 */
	public void rotate(double theta) {
		transform.rotate(theta);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#rotate(double, double, double)
	 */
	public void rotate(double theta, double x, double y) {
		transform.rotate(theta,x,y);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#scale(double, double)
	 */
	public void scale(double sx, double sy) {
		transform.scale(sx,sy);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#shear(double, double)
	 */
	public void shear(double shx, double shy) {
		transform.shear(shx,shy);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#transform(java.awt.geom.AffineTransform)
	 */
	public void transform(AffineTransform Tx) {
		transform.concatenate(Tx);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#setTransform(java.awt.geom.AffineTransform)
	 */
	public void setTransform(AffineTransform Tx) {
		transform = (AffineTransform)Tx.clone();
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#getTransform()
	 */
	public AffineTransform getTransform() {
		return (AffineTransform)transform.clone();
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#clearRect(int, int, int, int)
	 */
	public void clearRect(int x, int y, int width, int height) {
		fillRect(x,y,width,height);	
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#draw(java.awt.Shape)
	 */
	public void draw(Shape s) {
		if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D)s;
			drawRect(r.getX(),r.getY(),r.getWidth(),r.getHeight());
		} else if (s instanceof RoundRectangle2D) {
			RoundRectangle2D rr = (RoundRectangle2D)s;
			drawRoundRect(rr.getX(),rr.getY(),rr.getWidth(),rr.getHeight(),rr.getArcWidth(),rr.getArcHeight());	
		} else if (s instanceof Ellipse2D) {
			Ellipse2D e = (Ellipse2D)s;
			drawOval(e.getX(),e.getY(),e.getWidth(),e.getHeight());
		} else if (s instanceof Arc2D) {
			Arc2D a = (Arc2D)s;
			drawArc(a.getX(),a.getY(),a.getWidth(),a.getHeight(),a.getAngleStart(),a.getAngleExtent());
		} else {
			double[] pts = (double[])_shapeRegister.get(s);
			if (pts == null) {
				pts = shapeToPoly(s);
				_shapeRegister.put(s,pts);	
			}
			drawPoly(pts);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#fill(java.awt.Shape)
	 */
	public void fill(Shape s) {
		if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D)s;
			fillRect(r.getX(),r.getY(),r.getWidth(),r.getHeight());
		} else if (s instanceof RoundRectangle2D) {
			RoundRectangle2D rr = (RoundRectangle2D)s;
			fillRoundRect(rr.getX(),rr.getY(),rr.getWidth(),rr.getHeight(),rr.getArcWidth(),rr.getArcHeight());	
		} else if (s instanceof Ellipse2D) {
			Ellipse2D e = (Ellipse2D)s;
			fillOval(e.getX(),e.getY(),e.getWidth(),e.getHeight());
		} else if (s instanceof Arc2D) {
			Arc2D a = (Arc2D)s;
			fillArc(a.getX(),a.getY(),a.getWidth(),a.getHeight(),a.getAngleStart(),a.getAngleExtent());
		} else {
			double[] pts = (double[])_shapeRegister.get(s);
			if (pts == null) {
				pts = shapeToPoly(s);
				_shapeRegister.put(s,pts);	
			}
			fillPolygon(pts);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawPolyline(int[], int[], int)
	 */
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		int[] ptArray = new int[2*nPoints];
		for(int i=0; i<nPoints; i++) {
			_pt.setLocation(xPoints[i],yPoints[i]);
			transform.transform(_pt,_pt);
			ptArray[2*i] = xPoints[i];
			ptArray[2*i+1] = yPoints[i];
		}
		gc.setLineWidth(getTransformedLineWidth());
		gc.drawPolyline(ptArray);	
	}


	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawPolygon(int[], int[], int)
	 */
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] ptArray = new int[2*nPoints];
		for(int i=0; i<nPoints; i++) {
			_pt.setLocation(xPoints[i],yPoints[i]);
			transform.transform(_pt,_pt);
			ptArray[2*i] = xPoints[i];
			ptArray[2*i+1] = yPoints[i];
		}
		
		gc.drawPolygon(ptArray);
		ptArray =null;
	}
	
	/**
	 * @param fontString
	 * @return
	 */
	public org.eclipse.swt.graphics.Font getFont(String fontString) {
		org.eclipse.swt.graphics.Font cachedFont = (org.eclipse.swt.graphics.Font)_fontRegister.get(fontString);
		if (cachedFont == null) {
			int style = 0;
			style = (fontString.indexOf("bold=true") != -1)?(style | SWT.BOLD):style;	 //$NON-NLS-1$
			style = (fontString.indexOf("italic=true") != -1)?(style | SWT.ITALIC):style;	 //$NON-NLS-1$
			
			String name = fontString.substring(0,fontString.indexOf(";")); //$NON-NLS-1$
			String size = fontString.substring(fontString.lastIndexOf(";")+1,fontString.length()); //$NON-NLS-1$
			
			int sizeInt = 12;
			try {
				sizeInt = Integer.parseInt(size.substring(size.indexOf("=")+1,size.length())); //$NON-NLS-1$
			}
			catch (Exception e) {e.printStackTrace();}
			
			cachedFont = new org.eclipse.swt.graphics.Font(device,name.substring(name.indexOf("=")+1,name.length()),sizeInt,style); //$NON-NLS-1$
			
			_fontRegister.put(fontString,cachedFont);
		}
		return cachedFont;		
	}

	/**
	 * @return
	 */
	protected org.eclipse.swt.graphics.Font getTransformedFont() {
		if (curFont != null) {
			FontData fontData = curFont.getFontData()[0];
			int height = fontData.getHeight();
			_awtRect.setRect(0,0,height,height);
			applyAffineTransformToRect2D(_awtRect,transform);
			height = (int)(_awtRect.getHeight()+_RectXYGap);
			StringBuffer buffer = new StringBuffer("name="); //$NON-NLS-1$
			buffer.append(fontData.getName());
			buffer.append(";bold="); //$NON-NLS-1$
			buffer.append(((fontData.getStyle() & SWT.BOLD) != 0));
			buffer.append(";italic="); //$NON-NLS-1$
			buffer.append(((fontData.getStyle() & SWT.ITALIC) != 0));
			buffer.append(";size="); //$NON-NLS-1$
			buffer.append(height);
			return getFont(buffer.toString());
		}
		return curFont;
	}

	/**
	 * @param pts
	 */
	public void drawPoly(double[] pts) {
		int[] intPts = applyAffineTransformToPoly(pts,transform);
		gc.drawPolyline(intPts);	
		intPts = null;
	}

	/**
	 * @param pts
	 */
	public void fillPolygon(double[] pts) {
		int[] intPts = applyAffineTransformToPoly(pts,transform);
		gc.fillPolygon(intPts);
		intPts = null;
	}

	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		_pt.setLocation(x1,y1);
		transform.transform(_pt,_pt);
		x1 = (int)_pt.getX();
		y1 = (int)_pt.getY();
		_pt.setLocation(x2,y2);
		transform.transform(_pt,_pt);
		x2 = (int)_pt.getX();
		y2 = (int)_pt.getY();
		
		Line l = new Line((int)(x1+_RectXYGap),(int)(y1+_RectXYGap),(int)(x2+_RectXYGap),(int)(y2+_RectXYGap));
		l.setStrokeWidth(getTransformedLineWidth());
		l.setStrokeColor(String.valueOf(curColor.getRGB()));
		canvas.add(l);
	}

	/**
	 * @param img
	 * @param x
	 * @param y
	 */
	public void copyArea(org.eclipse.swt.graphics.Image img, double x, double y) {
		_pt.setLocation(x,y);
		transform.transform(_pt,_pt);
		
		gc.copyArea(img,(int)(_pt.getX()+_RectXYGap),(int)(_pt.getY()+_RectXYGap));
	}

	/**
	 * @param str
	 * @param x
	 * @param y
	 */
	public void drawString(String str, double x, double y) {
		_pt.setLocation(x,y);
		transform.transform(_pt,_pt);
		gc.setFont(getTransformedFont());
		gc.drawString(str,(int)(_pt.getX()+_RectXYGap),(int)(_pt.getY()+_RectXYGap),true);
	}
	
	/**
	 * @param s
	 * @param x
	 * @param y
	 */
	public void drawText(String s, double x, double y) {
		_pt.setLocation(x,y);
		transform.transform(_pt,_pt);
		gc.setFont(getTransformedFont());
		gc.drawText(s,(int)(_pt.getX()+_RectXYGap),(int)(_pt.getY()+_RectXYGap),true);	
	}

	/**
	 * @param s
	 * @param x
	 * @param y
	 * @param flags
	 */
	public void drawText(String s, double x, double y, int flags) {
		_pt.setLocation(x,y);
		transform.transform(_pt,_pt);
		gc.setFont(getTransformedFont());
		gc.drawText(s,(int)(_pt.getX()+_RectXYGap),(int)(_pt.getY()+_RectXYGap),flags);	
	}

	
	/**********************************************************************
	 * THIS METHOD HAS BEEN CONVERTED TO GWT
	 **********************************************************************/
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawRect(double x, double y, double width, double height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		getGwtRectFromAwt(_awtRect,gwtRect);
		gwtRect.setStrokeWidth(getTransformedLineWidth());

		canvas.add(gwtRect);
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void fillRect(double x, double y, double width, double height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		getGwtRectFromAwt(_awtRect,gwtRect);
		
		gwtRect.setFillColor(String.valueOf(curColor.getRGB()));
		canvas.add(gwtRect);
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public void drawRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);		
		x = _awtRect.getX();
		y = _awtRect.getY();
		width = _awtRect.getWidth();
		height = _awtRect.getHeight();
		_awtRect.setRect(0,0,arcWidth,arcHeight);
		applyAffineTransformToRect2D(_awtRect,transform);
		arcWidth = _awtRect.getWidth();
		arcHeight = _awtRect.getHeight();
		gc.setLineWidth(getTransformedLineWidth());
		gc.drawRoundRectangle((int)(x+_RectXYGap),(int)(y+_RectXYGap),(int)(width+_RectXYGap),(int)(height+_RectXYGap),(int)(arcWidth+_RectXYGap),(int)(arcHeight+_RectXYGap));		
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param arcWidth
	 * @param arcHeight
	 */
	public void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);		
		x = _awtRect.getX();
		y = _awtRect.getY();
		width = _awtRect.getWidth();
		height = _awtRect.getHeight();
		_awtRect.setRect(0,0,arcWidth,arcHeight);
		applyAffineTransformToRect2D(_awtRect,transform);
		arcWidth = _awtRect.getWidth();
		arcHeight = _awtRect.getHeight();
		gc.setLineWidth(getTransformedLineWidth());
		gc.fillRoundRectangle((int)(x+_RectXYGap),(int)(y+_RectXYGap),(int)(width+_RectXYGap),(int)(height+_RectXYGap),(int)(arcWidth+_RectXYGap),(int)(arcHeight+_RectXYGap));		
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillPolygon(int[], int[], int)
	 */
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] ptArray = new int[2*nPoints];
		for(int i=0; i<nPoints; i++) {
			_pt.setLocation(xPoints[i],yPoints[i]);
			transform.transform(_pt,_pt);
			ptArray[2*i] = xPoints[i];
			ptArray[2*i+1] = yPoints[i];
		}
		gc.fillPolygon(ptArray);	
		ptArray =null;
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawLine(int, int, int, int)
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		drawLine((double)x1,(double)y1,(double)x2,(double)y2);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#copyArea(int, int, int, int, int, int)
	 */
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);

		_pt.setLocation(dx,dy);
		transform.transform(_pt,_pt);
		gc.copyArea((int)_awtRect.getX(),(int)_awtRect.getY(),(int)_awtRect.getWidth(),(int)_awtRect.getHeight(),(int)_pt.getX(),(int)_pt.getY());
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawString(java.lang.String, int, int)
	 */
	public void drawString(String str, int x, int y) {
		drawString(str,(double)x,(double)y);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics2D#drawString(java.lang.String, float, float)
	 */
	public void drawString(String str, float x, float y) {
		drawString(str,(double)x,(double)y);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawRect(int, int, int, int)
	 */
	public void drawRect(int x, int y, int width, int height) {
		drawRect((double)x,(double)y,(double)width,(double)height);	
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 */
	public void fillRect(int x, int y, int width, int height) {
		fillRect((double)x,(double)y,(double)width,(double)height);	
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawRoundRect(int, int, int, int, int, int)
	 */
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		drawRoundRect((double)x,(double)y,(double)width,(double)height,(double)arcWidth,(double)arcHeight);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillRoundRect(int, int, int, int, int, int)
	 */
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		fillRoundRect((double)x,(double)y,(double)width,(double)height,(double)arcWidth,(double)arcHeight);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawOval(int, int, int, int)
	 */
	public void drawOval(int x, int y, int width, int height) {
		drawOval((double)x,(double)y,(double)width,(double)height);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillOval(int, int, int, int)
	 */
	public void fillOval(int x, int y, int width, int height) {
		fillOval((double)x,(double)y,(double)width,(double)height);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#drawArc(int, int, int, int, int, int)
	 */
	public void drawArc(int x, int y, int width, int height, int startAngle, int extent) {
		drawArc((double)x,(double)y,(double)width,(double)height,(double)startAngle,(double)extent);
	}

	/* (non-Javadoc)
	 * @see java.awt.Graphics#fillArc(int, int, int, int, int, int)
	 */
	public void fillArc(int x, int y, int width, int height, int startAngle, int extent) {
		drawArc((double)x,(double)y,(double)width,(double)height,(double)startAngle,(double)extent);
	}
	
	private String getStrokeRegisterKey(int[]v, int s) {
        StringBuffer keyAsText = new StringBuffer();
        
        keyAsText.append("[Stroke]:"); //$NON-NLS-1$
        keyAsText.append("width="); //$NON-NLS-1$
        keyAsText.append(v[2] + ";"); //$NON-NLS-1$
        keyAsText.append("cap="); //$NON-NLS-1$
        keyAsText.append(v[0] + ";"); //$NON-NLS-1$
        keyAsText.append("join="); //$NON-NLS-1$
        keyAsText.append(v[1] + ";"); //$NON-NLS-1$
        keyAsText.append("dash=["); //$NON-NLS-1$
        
        if (s>0) {
            for (int i=1; i<=s;i++) {
                keyAsText.append(v[2+i] + (i!=s?"|":"")); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        keyAsText.append("]"); //$NON-NLS-1$

        return keyAsText.toString();
	}
	
    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getStroke()
     */
    public Stroke getStroke() {
        int[] iDsh = gc.getLineDash();
        
        int strokeKey[] = new int[3+((iDsh!=null)?iDsh.length:0)];
        
        switch(gc.getLineCap()) {
	    	case SWT.CAP_FLAT:
	    	    strokeKey[0] = BasicStroke.CAP_BUTT;
	    	    break;
	    	case SWT.CAP_ROUND:
	    	    strokeKey[0] = BasicStroke.CAP_ROUND;
	    	    break;
	    	case SWT.CAP_SQUARE:
	    	    strokeKey[0] = BasicStroke.CAP_SQUARE;
	    	    break;
        	default:
        	    break;
	    }
	    switch(gc.getLineJoin()) {
	    	case SWT.JOIN_BEVEL:
	    	    strokeKey[1] = BasicStroke.JOIN_BEVEL;
	    	    break;
	    	case SWT.JOIN_MITER:
	    	    strokeKey[1] = BasicStroke.JOIN_MITER;
	    	    break;
	    	case SWT.JOIN_ROUND:
	    	    strokeKey[1] = BasicStroke.JOIN_ROUND;
	    	    break;
	    	default:
	    	    break;
	    }

        strokeKey[2] = gc.getLineWidth();
        
        if (iDsh!=null) {
            for (int i=1; i<=iDsh.length;i++) {
                strokeKey[2+i] = iDsh[i-1];
            }
        }
        
        String key = getStrokeRegisterKey(strokeKey, iDsh==null?0:iDsh.length);
        
        Stroke s = null;
        if (_strokeRegister.containsKey(key)) {
             s = (Stroke) _strokeRegister.get(key);
        } else {
	        float[] fDsh = null;
	        if (iDsh != null && iDsh.length>0) {
	            fDsh = new float[iDsh.length];
	            for (int i=0, max=iDsh.length;i<max;i++) {
	                fDsh[i] = (float)iDsh[i];
	            }
	        }
	        s = new BasicStroke((float)strokeKey[2], strokeKey[0], strokeKey[1], (float)1, fDsh, (float)0);
	        _strokeRegister.put(key, s);
        }
        return s;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
     */
    public void setStroke(Stroke s) {
        if (s instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) s;
            switch(bs.getEndCap()) {
            	case BasicStroke.CAP_BUTT:
            	    gc.setLineCap(SWT.CAP_FLAT);
            	    break;
            	case BasicStroke.CAP_ROUND:
            	    gc.setLineCap(SWT.CAP_FLAT);
            	    break;
            	case BasicStroke.CAP_SQUARE:
            	    gc.setLineCap(SWT.CAP_FLAT);
            	    break;
	        	default:
	        	    break;
            }
            switch(bs.getLineJoin()) {
	        	case BasicStroke.JOIN_BEVEL:
	        	    gc.setLineJoin(SWT.JOIN_BEVEL);
	        	    break;
	        	case BasicStroke.JOIN_MITER:
	        	    gc.setLineJoin(SWT.JOIN_MITER);
	        	    break;
	        	case BasicStroke.JOIN_ROUND:
	        	    gc.setLineJoin(SWT.JOIN_ROUND);
	        	    break;
	        	default:
	        	    break;
            }
            gc.setLineWidth((int)bs.getLineWidth());
            float[] fDsh = bs.getDashArray();
            int[] iDsh = new int[0];
            if (fDsh != null && fDsh.length>0) {
	            int size = fDsh.length;
	            iDsh = new int[size];
	            for(int i=0;i<size;i++) {
	                iDsh[i] = (int)fDsh[i];
	            }
            }
            gc.setLineDash(iDsh);
            
            int strokeKey[] = new int[3+((iDsh!=null)?iDsh.length:0)];
            
            strokeKey[0] = bs.getEndCap();
            strokeKey[1] = bs.getLineJoin();
            strokeKey[2] = (int)bs.getLineWidth();
            
            if (iDsh!=null) {
                for (int i=1; i<=iDsh.length;i++) {
                    strokeKey[2+i] = iDsh[i-1];
                }
            }

            String key = getStrokeRegisterKey(strokeKey, iDsh==null?0:iDsh.length);
            
            if (!_strokeRegister.containsKey(key)) {
                _strokeRegister.put(key, bs);
            }
        }
        // FIXME: Add Dash rule/Miter Limit as soon as SWT post it
    }

	/**
	 * @return
	 */
	public org.eclipse.swt.graphics.Font getSWTFont() {
		return curFont;	
	}

	/**
	 * @return
	 */
	public org.eclipse.swt.graphics.FontMetrics getSWTFontMetrics() {
		gc.setFont(curFont);
		return gc.getFontMetrics();
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawOval(double x, double y, double width, double height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		
		gc.setLineWidth(getTransformedLineWidth());
		gc.drawOval((int)(_awtRect.getX()),(int)(_awtRect.getY()),(int)(_awtRect.getWidth()),(int)(_awtRect.getHeight()));
	}


	/**********************************************************************
	 * THIS METHOD HAS BEEN CONVERTED TO GWT
	 **********************************************************************/
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void fillOval(double x, double y, double width, double height) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		Ellipse e = new Ellipse((int)(_awtRect.getX()), (int)(_awtRect.getY()), 
				(int)(_awtRect.getWidth()), (int)(_awtRect.getHeight()));
		e.setFillColor(getColor().toString());
		canvas.add(e);
	}


	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param extent
	 */
	public void drawArc(double x, double y, double width, double height, double startAngle, double extent) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		
		gc.setLineWidth(getTransformedLineWidth());
		gc.drawArc((int)(_awtRect.getX()),(int)(_awtRect.getY()),(int)(_awtRect.getWidth()),(int)(_awtRect.getHeight()),(int)(startAngle),(int)(startAngle+extent));
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param extent
	 */
	public void fillArc(double x, double y, double width, double height, double startAngle, double extent) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		gc.drawArc((int)(_awtRect.getX()),(int)(_awtRect.getY()),(int)(_awtRect.getWidth()),(int)(_awtRect.getHeight()),(int)(startAngle),(int)(startAngle+extent));
	}

	/**
	 * @param image
	 * @param x
	 * @param y
	 */
	public void drawImage(org.eclipse.swt.graphics.Image image, double x, double y) {
		org.eclipse.swt.graphics.Rectangle bounds = image.getBounds();
		_awtRect.setRect(x,y,bounds.width,bounds.height);
		applyAffineTransformToRect2D(_awtRect,transform);
		getGwtRectFromAwt(_awtRect,swtRect);
		gc.drawImage(image,0,0,bounds.width,bounds.height,swtRect.x,swtRect.y,swtRect.width,swtRect.height);
	}

	/**
	 * @param image
	 * @param srcX
	 * @param srcY
	 * @param srcW
	 * @param srcH
	 * @param destX
	 * @param destY
	 * @param destW
	 * @param destH
	 */
	public void drawImage(org.eclipse.swt.graphics.Image image, int srcX, int srcY, int srcW, int srcH, double destX, double destY, double destW, double destH) {
		_awtRect.setRect(destX,destY,destW,destH);
		applyAffineTransformToRect2D(_awtRect,transform);
		getGwtRectFromAwt(_awtRect,swtRect);
		gc.drawImage(image,srcX,srcY,srcW,srcH,swtRect.x,swtRect.y,swtRect.width,swtRect.height);
	}

	/**
	 * @param w
	 */
	public void set_lineWidth(double w) {
		_lineWidth = w;			
	}

	/**
	 * @return
	 */
	protected int getTransformedLineWidth() {
		_awtLineRect.setRect(0,0,_lineWidth,_lineWidth);
		applyAffineTransformToRect2D(_awtLineRect,transform);
		return (int)(Math.max(_awtLineRect.getWidth(),1));
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param vertical
	 */
	public void fillGradientRectangle(double x, double y, double width, double height, boolean vertical) {
		_awtRect.setRect(x,y,width,height);
		applyAffineTransformToRect2D(_awtRect,transform);
		getGwtRectFromAwt(_awtRect,swtRect);
		gc.fillGradientRectangle(swtRect.x,swtRect.y,swtRect.width,swtRect.height,vertical);
	}

	/**
	 * @param xOr
	 */
	public void setXORMode(boolean xOr) {
		gc.setXORMode(xOr);	
	}

	/**
	 * @param ch
	 * @return
	 */
	public int getAdvanceWidth(char ch) {
		org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
		gc.setFont(curFont);
		int width = gc.getAdvanceWidth(ch);	
		gc.setFont(scaledFont);
		return width;
	}

	/**
	 * @param ch
	 * @return
	 */
	public int getCharWidth(char ch) {
		org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
		gc.setFont(curFont);
		int width = gc.getCharWidth(ch);
		gc.setFont(scaledFont);
		return width;
	}

	/**
	 * @param str
	 * @return
	 */
	public org.eclipse.swt.graphics.Point stringExtent(String str) {
		org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
		gc.setFont(curFont);		
		org.eclipse.swt.graphics.Point extent = gc.stringExtent(str);	
		gc.setFont(scaledFont);
		return extent;
	}

	/**
	 * @param str
	 * @return
	 */
	public org.eclipse.swt.graphics.Point textExtent(String str) {
		org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
		gc.setFont(curFont);		
		org.eclipse.swt.graphics.Point extent = gc.textExtent(str);	
		gc.setFont(scaledFont);
		return extent;
	}

	/**
	 * @param str
	 * @param flags
	 * @return
	 */
	public org.eclipse.swt.graphics.Point textExtent(String str, int flags) {
		org.eclipse.swt.graphics.Font scaledFont = gc.getFont();
		gc.setFont(curFont);		
		org.eclipse.swt.graphics.Point extent = gc.textExtent(str,flags);	
		gc.setFont(scaledFont);
		return extent;
	}


	/**
	 * @param rect
	 */
	private static void _initPts(Rectangle2D rect) {
        _rectCoord[0] = rect.getX();         
        _rectCoord[1] = rect.getY();
        _rectCoord[2] = rect.getX() + rect.getWidth();  
        _rectCoord[3] = rect.getY();
        _rectCoord[4] = rect.getX() + rect.getWidth();  
        _rectCoord[5] = rect.getY() + rect.getHeight();
        _rectCoord[6] = rect.getX();          
        _rectCoord[7] = rect.getY() + rect.getHeight();
	}
	
	/**
	 * Turns an AWT Rectangle into a GWT rectangle.
	 * 
	 * @param aRect -- the target rectangle
	 * @param gRect -- will hold the values of aRect but as a GWT rectangle
	 */
	public static void getGwtRectFromAwt(Rectangle2D aRect, 
			org.vaadin.gwtgraphics.client.shape.Rectangle gRect) {
		gRect.setX((int)(aRect.getX()));
		gRect.setY((int)(aRect.getY()));
		gRect.setWidth((int)(aRect.getWidth()));
		gRect.setHeight((int)(aRect.getHeight()));
	}
	
	/**
	 * @param pts
	 * @param at
	 * @return
	 */
	public static int[] applyAffineTransformToPoly(double[] pts, AffineTransform at) {
		int[] intPts = new int[pts.length];
		for(int i=0; i<pts.length/2; i++) {
			_pt2D.setLocation(pts[2*i],pts[2*i+1]);
			at.transform(_pt2D,_pt2D);
			intPts[2*i] = (int)(_pt2D.getX());
			intPts[2*i+1] = (int)(_pt2D.getY());
		}
		return intPts;
	}	

    /**
     * @param rect
     * @param at
     */
    public static void applyAffineTransformToRect2D(Rectangle2D rect, AffineTransform at) {
        _initPts(rect);
        at.transform(_rectCoord, 0, _rectCoord, 0, 4);
        double minX = _rectCoord[0], minY = _rectCoord[1], maxX = _rectCoord[0], maxY = _rectCoord[1];
        for (int i=1, max = 4; i<max; i++) {
            minX = (_rectCoord[2*i] < minX)?_rectCoord[2*i]:minX;
            minY = (_rectCoord[2*i+1] < minY)?_rectCoord[2*i+1]:minY;
            maxX = (_rectCoord[2*i] > maxX)?_rectCoord[2*i]:maxX;
            maxY = (_rectCoord[2*i+1] > maxY)?_rectCoord[2*i+1]:maxY;
        }
        rect.setRect(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * @param p
     * @return
     */
    private static double[] fillPolyArrayFromPts(double p[]) {
		for(int i=0,max=_segLst.size(); i<max; i++) {
			Point2D p2 = (Point2D)_segLst.get(i);
			p[2*i]		= (p2.getX() + _RectXYGap);
			p[2*i+1]	= (p2.getY() + _RectXYGap);
		}
		return p;
    }
    
	/**
	 * @param s
	 * @return
	 */
	public static double[] shapeToPoly(Shape s) {
		_segLst.clear();
		_pt2D.setLocation(0,0);
		PathIterator pathIt = s.getPathIterator(at2D, _AffineTransformFlatness);
		while (!pathIt.isDone()) {
			int segType = pathIt.currentSegment(_rectCoord);
			switch (segType) {
				case PathIterator.SEG_MOVETO:
					_pt2D.setLocation(_rectCoord[0],_rectCoord[1]);
					_segLst.add(new Point2D.Double(_rectCoord[0],_rectCoord[1]));
					break;
				case PathIterator.SEG_LINETO:
					_segLst.add(new Point2D.Double(_rectCoord[0],_rectCoord[1]));					
					break;
				case PathIterator.SEG_CLOSE:
					_segLst.add(new Point2D.Double(_pt2D.getX(),_pt2D.getY()));
					break;
			}
			pathIt.next();
		}
		return fillPolyArrayFromPts(new double[2*_segLst.size()]);
	}


    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawImage(java.awt.Image, java.awt.geom.AffineTransform, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawImage(java.awt.image.BufferedImage, java.awt.image.BufferedImageOp, int, int)
     */
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawRenderedImage(java.awt.image.RenderedImage, java.awt.geom.AffineTransform)
     */
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawRenderableImage(java.awt.image.renderable.RenderableImage, java.awt.geom.AffineTransform)
     */
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawString(java.text.AttributedCharacterIterator, int, int)
     */
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawString(java.text.AttributedCharacterIterator, float, float)
     */
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#drawGlyphVector(java.awt.font.GlyphVector, float, float)
     */
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#hit(java.awt.Rectangle, java.awt.Shape, boolean)
     */
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setComposite(java.awt.Composite)
     */
    public void setComposite(Composite comp) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object)
     */
    public void setRenderingHint(Key hintKey, Object hintValue) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getRenderingHint(java.awt.RenderingHints.Key)
     */
    public Object getRenderingHint(Key hintKey) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#setRenderingHints(java.util.Map)
     */
    public void setRenderingHints(Map hints) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#addRenderingHints(java.util.Map)
     */
    public void addRenderingHints(Map hints) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getRenderingHints()
     */
    public RenderingHints getRenderingHints() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getComposite()
     */
    public Composite getComposite() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics2D#getFontRenderContext()
     */
    public FontRenderContext getFontRenderContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#create()
     */
    public Graphics create() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#setPaintMode()
     */
    public void setPaintMode() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#setXORMode(java.awt.Color)
     */
    public void setXORMode(Color c1) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
     */
    public FontMetrics getFontMetrics(Font f) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#drawImage(java.awt.Image, int, int, int, int, int, int, int, int, java.awt.Color, java.awt.image.ImageObserver)
     */
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Graphics#dispose()
     */
    public void dispose() {
        // TODO Auto-generated method stub
    }
}