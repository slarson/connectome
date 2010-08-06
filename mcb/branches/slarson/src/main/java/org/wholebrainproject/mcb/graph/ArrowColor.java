package org.wholebrainproject.mcb.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

public class ArrowColor implements Transformer<Edge,Paint> {

	public Paint transform(Edge input) {
		BufferedImage img = new BufferedImage(40, 50, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D imageGraphics = img.createGraphics();
        // Paint something here using the given graphics object
        imageGraphics.setColor(Color.RED);
        imageGraphics.create().fillOval(40, 50, 40, 50);
       
        return imageGraphics.getPaint();
	}
}
