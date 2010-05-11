package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

		d.addSelectVariable("$s");
		d.addSelectVariable("$x");
		
		InputStream queryResult = d.runSelectQuery();
		
		try {
			d.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(queryResult);
	}
	
	public void testParseSPARQLResult2() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter bamsReader = new DataReaderBetter(sparql);
		
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#Globus_pallidus>");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#projection_Strength> $gp_strength");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $gp_receiving");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#reference> $gp_reference");
		
		
		bamsReader.addSelectVariable("$gp_strength");
		bamsReader.addSelectVariable("$gp_receiving");
		bamsReader.addSelectVariable("$gp_reference");
		
		InputStream queryResult = bamsReader.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(results);
	}
	
	private void populateBamsDataReader(DataReaderBetter drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + " <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#" + brainRegionName+">");
			drb.addQueryTriplet("$" + brainRegionName + " <http://ncmir.ucsd.edu/BAMS#projection_Strength> $"+ brainRegionName + "_strength");
			drb.addQueryTriplet("$" + brainRegionName + " <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $" + brainRegionName +"_receiving");
			drb.addQueryTriplet("$" + brainRegionName + " <http://ncmir.ucsd.edu/BAMS#reference> $"+ brainRegionName +"_reference");
			
			drb.addSelectVariable("$"+ brainRegionName + "_strength");
			drb.addSelectVariable("$"+ brainRegionName + "_receiving");
			drb.addSelectVariable("$"+ brainRegionName + "_reference");
			
			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
		}
	}
	
	private Node[] createNodesFromResults(String[] brainRegions, 
			MultiHashMap<String, String> results) {
		List<Node> nodeList = new ArrayList<Node>();
		for (String brainRegion : brainRegions) {
			Node n = new Node(brainRegion);
			n.store(results.get("$"+ brainRegion + "_receiving"), 
					results.get("$"+ brainRegion + "_strength"));
			n.addReference(results.get("$"+ brainRegion + "_receiving"), 
					results.get("$"+ brainRegion + "_reference"));
			nodeList.add(n);
		}
		Node[] nodes = new Node[nodeList.size()];
		return nodeList.toArray(nodes);
	}
	
	public void testParseSPARQLResult4() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter bamsReader = new DataReaderBetter(sparql);
		
		String[] brainRegions = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_amygdala", "Substantia_nigra_compact_part",
				"Ventral_tegmental_area", "Prelimbic_area", 
				"Lateral_preoptic_area"};
		
		populateBamsDataReader(bamsReader, brainRegions);
				
		InputStream queryResult = bamsReader.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String key : results.keySet()) {
			System.out.println("key: " + key + ", result: " + results.get(key));
		}
		
		Node[] data = createNodesFromResults(brainRegions, results);
		
	}
	
	public void testParseSPARQLResult3() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter bamsReader = new DataReaderBetter(sparql);
		
		//globus pallidus
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#Globus_pallidus>");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#projection_Strength> $gp_strength");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $gp_receiving");
		bamsReader.addQueryTriplet("$gp <http://ncmir.ucsd.edu/BAMS#reference> $gp_reference");
		
		bamsReader.addSelectVariable("$gp_strength");
		bamsReader.addSelectVariable("$gp_receiving");
		bamsReader.addSelectVariable("$gp_reference");
		
		bamsReader.addQueryTriplet("} UNION {");
		
		bamsReader.addQueryTriplet("$cp <http://ncmir.ucsd.edu/BAMS#sending_Structure>  <http://ncmir.ucsd.edu/BAMS#Caudoputamen>");
		bamsReader.addQueryTriplet("$cp <http://ncmir.ucsd.edu/BAMS#projection_Strength> $cp_strength");
		bamsReader.addQueryTriplet("$cp <http://ncmir.ucsd.edu/BAMS#receiving_Structure> $cp_receiving");
		bamsReader.addQueryTriplet("$cp <http://ncmir.ucsd.edu/BAMS#reference> $cp_reference");
		
		bamsReader.addSelectVariable("$cp_strength");
		bamsReader.addSelectVariable("$cp_receiving");
		bamsReader.addSelectVariable("$cp_reference");
		
		InputStream queryResult = bamsReader.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = bamsReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(results);
	}
	
	public void testParseSPARQLResult() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		DataReaderBetter d = 
			new DataReaderBetter(sparql);
		
		d.addQueryTriplet("$s $x <http://neurolex.org/wiki/Category:Globus_pallidus>");
		d.addQueryTriplet("$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $s");

		
		d.addSelectVariable("$s");
		d.addSelectVariable("$x");
		
		InputStream queryResult = d.runSelectQuery();
		
		MultiHashMap<String, String> results = null;
		
		try {
			results = d.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(results);
		assertNotNull(results.get("$s"));
		//assertEquals("http://neurolex.org/wiki/Special:URIResolver/Category-3AGlobus pallidus", results.get("$s"));
		//assertEquals("http://semantic-mediawiki.org/swivt/1.0#page", results.get("$x"));
		
		URLDecoder dx = new URLDecoder();
		String s = "select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Globus_pallidus%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}";
		String s2 = "select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Caudoputamen%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}";
		String s3 = "select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Central_nucleus_of_amygdala%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}";
		String s4 = "select+%24structure+%24reference+%24oReceive+{+%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23sending_Structure%3E++%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23Substantia_nigra_compact_part%3E.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23projection_Strength%3E+%24oReceive.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23receiving_Structure%3E+%24structure.%0D%0A%24s+%3Chttp%3A%2F%2Fncmir.ucsd.edu%2FBAMS%23reference%3E+%24reference%0D%0A}";
		try {
			System.out.println(dx.decode(s, "UTF-8"));
			System.out.println(dx.decode(s2, "UTF-8"));
			System.out.println(dx.decode(s3, "UTF-8"));
			System.out.println(dx.decode(s4, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
