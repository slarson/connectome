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

import swt.contrib.AwtG2DWrapper;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.PickedInfo;

/**
 * This abstract class structures much of the annoying
 * bits of Renderers, allowing the user to simply override
 * the important methods and move on.
 * 
 * @author danyelf
 */
public abstract class SWTAbstractRenderer implements SWTRenderer {

	private PickedInfo pickedInfo;
//	private SelectedInfo selectedInfo;

	abstract public void paintEdge(AwtG2DWrapper g, Edge e, int x1, int y1, int x2, int y2);
	abstract public void paintVertex(AwtG2DWrapper g, Vertex v, int x, int y);

	public void setPickedKey(PickedInfo pk) {
		this.pickedInfo = pk;
	}
	
//	public void setSelectedKey(SelectedInfo sk) {
//		this.selectedInfo = sk;
//	}

//	protected boolean isSelected(Vertex v) {
//		return selectedInfo.isSelected(v);
//	}
	
	protected boolean isPicked(Vertex v) {
		return pickedInfo.isPicked(v);
	}

	public int wiggleRoomX() {
		return 0;
	}

	public int wiggleRoomY() {
		return 0;
	}

}
