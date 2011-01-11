package org.wholebraincatalog.mcb;

import org.wholebrainproject.mcb.graph.GraphManager;

import junit.framework.TestCase;

public class TestGraphManager extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testClearAllNodesAndEdges() {
		GraphManager gm = GraphManager.getInstance();
		gm.clearAllNodesAndEdges();
		assertEquals(0, gm.getGraph().getEdgeCount());
		assertEquals(0, gm.getGraph().getVertexCount());
	}

}
