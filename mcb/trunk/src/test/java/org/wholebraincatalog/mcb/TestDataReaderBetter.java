package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebraincatalog.mcb.DataReaderBetter;

import junit.framework.TestCase;


public class TestDataReaderBetter extends TestCase {

	public List<String> variableList;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDataReaderBetter() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter d = 
			new DataReaderBetter(sparql);
		variableList = new ArrayList<String>();
		
		assertNotNull(d);
		assertEquals(sparql, d.getSparqlEndPoint());
	}

	public void testAddQueryTriplet() {
		String triplet1 = "$x <http://someurl/property> $y";
		String triplet2 = "$z <http://someurl/property2> $a";
		
		DataReaderBetter d = new DataReaderBetter("http://xyz");
		
		d.addQueryTriplet(triplet1);
		d.addQueryTriplet(triplet2);
		
		assertEquals(2, d.getTripletCount());
	}

	public void testRunSelectQuery() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter d = 
			new DataReaderBetter(sparql);
	
		/**
		 * select $s $y $z $r { $s $x <http://neurolex.org/wiki/Category:Globus_pallidus>.
$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $s.
$y <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role>  <http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>.
$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter> $z.
$z <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> $r.
}
		 */
		
		d.addQueryTriplet("$s $x <http://neurolex.org/wiki/Category:Globus_pallidus>");
		d.addQueryTriplet("$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $s");

		variableList = new ArrayList<String>();
		variableList.add("$s");
		variableList.add("$x");
		
		InputStream queryResult = d.runSelectQuery(variableList);
		
		try {
			d.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(queryResult);
	}
	
	public void testParseSPARQLResult() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter d = 
			new DataReaderBetter(sparql);
		
		d.addQueryTriplet("$s $x <http://neurolex.org/wiki/Category:Globus_pallidus>");
		d.addQueryTriplet("$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $s");

		variableList = new ArrayList<String>();
		variableList.add("$s");
		variableList.add("$x");
		
		InputStream queryResult = d.runSelectQuery(variableList);
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = d.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(results);
		assertNotNull(results.get("$s"));
		assertEquals("http://neurolex.org/wiki/Special:URIResolver/Category-3AGlobus pallidus", results.get("$s"));
		assertEquals("http://semantic-mediawiki.org/swivt/1.0#page", results.get("$x"));
	}

}
