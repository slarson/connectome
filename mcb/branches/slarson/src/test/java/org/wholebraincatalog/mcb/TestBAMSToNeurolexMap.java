package org.wholebraincatalog.mcb;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.wholebrainproject.mcb.data.BAMSToNeurolexMap;

import junit.framework.TestCase;

public class TestBAMSToNeurolexMap extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetNamesMap() {
		try {
			Map<String,URI> namesMap = BAMSToNeurolexMap.getInstance().getNamesMap();
			assertNotNull(namesMap);
			
			for(String s: namesMap.keySet()) {
				System.out.println(s + ": " + namesMap.get(s));
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
