package org.wholebraincatalog.mcb;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.wholebrainproject.mcb.graph.Node;
import org.wholebrainproject.mcb.util.SparqlQuery;

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
		SparqlQuery d = 
			new SparqlQuery(sparql);
		variableList = new ArrayList<String>();

		assertNotNull(d);
		assertEquals(sparql, d.getSparqlEndPoint());
	}

	public void testAddQueryTriplet() {
		String triplet1 = "$x <http://someurl/property> $y";
		String triplet2 = "$z <http://someurl/property2> $a";

		SparqlQuery d = new SparqlQuery("http://xyz");

		d.addQueryTriplet(triplet1);
		d.addQueryTriplet(triplet2);

		assertEquals(2, d.getTripletCount());
	}


	private void populateBamsDataReader(SparqlQuery drb, String[] brainRegionNames) {
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

	private void populateNIFDataReader(SparqlQuery drb, String[] brainRegionNames) {
		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + 
					" <http://connectivity.neuinfo.org#sending_structure>  \"" 
					+ brainRegionName.replace("_", " ")+"\"");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#projection_strength> $"+ brainRegionName + "_strength");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#receiving_structure> $" + brainRegionName +"_receiving");
			drb.addQueryTriplet("$" + brainRegionName + " <http://connectivity.neuinfo.org#reference> $"+ brainRegionName +"_reference");

			drb.addSelectVariable("$"+ brainRegionName + "_strength");
			drb.addSelectVariable("$"+ brainRegionName + "_receiving");
			drb.addSelectVariable("$"+ brainRegionName + "_reference");

			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
		}
	}

	public void testParseSPARQLResult3() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		SparqlQuery bamsReader = new SparqlQuery(sparql);

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

		MultiHashMap<String, String> results = null;

		try {
			results = bamsReader.runSelectQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(results);
	}

	public void testParseSPARQLResult() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		SparqlQuery d = 
			new SparqlQuery(sparql);

		d.addQueryTriplet("$s $x <http://neurolex.org/wiki/Category:Globus_pallidus>");
		d.addQueryTriplet("$y <http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> $s");


		d.addSelectVariable("$s");
		d.addSelectVariable("$x");

		MultiHashMap<String, String> results = null;

		try {
			results = d.runSelectQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(results);
		assertNotNull(results.get("$s"));
		//assertEquals("http://neurolex.org/wiki/Special:URIResolver/Category-3AGlobus pallidus", results.get("$s"));
		//assertEquals("http://semantic-mediawiki.org/swivt/1.0#page", results.get("$x"));

	}
	
	/**
	 * Populate a data reader for neurolex data.
	 * @param drb - the data reader to populate
	 * @param brainRegionNames - the names of brain regions to populate it with.
	 */
	private static void populateCellDataReader(SparqlQuery drb, String[] brainRegionNames) {

		for (String brainRegionName : brainRegionNames){
			drb.addQueryTriplet("$" + brainRegionName + "_region " + "$x" + "<http://neurolex.org/wiki/Category:"+
					brainRegionName+">");
			drb.addQueryTriplet("$y" + "<http://neurolex.org/wiki/Special:URIResolver/Property-3ALocated_in> " +
					"$" + brainRegionName+"_region");
			drb.addQueryTriplet("$y" +  "_cells <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> " +
			"<http://neurolex.org/wiki/Special:URIResolver/Category-3APrincipal_neuron_role>");
			drb.addQueryTriplet("$y"+ " <http://neurolex.org/wiki/Special:URIResolver/Property-3ANeurotransmitter>" +
					"$" + brainRegionName+"_role_dum");
			drb.addQueryTriplet("$" + brainRegionName+"_role_dum <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel> " +
					"$" + brainRegionName+"_neurotransmitter");
			drb.addQueryTriplet("$"+brainRegionName+"_role_dum <http://neurolex.org/wiki/Special:URIResolver/Property-3AHas_role> "+
					"$"+brainRegionName+"_role_dum_2");
			drb.addQueryTriplet("$"+brainRegionName+"_role_dum_2 <http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel>"+
					"$"+brainRegionName+"_role");
			
			drb.addSelectVariable("$"+ brainRegionName + "_cells");
			drb.addSelectVariable("$"+ brainRegionName + "_neurotransmitter");
			drb.addSelectVariable("$"+ brainRegionName + "_role");

			//add union between all sets of variables except the last
			if (brainRegionName.equals(brainRegionNames[brainRegionNames.length - 1]) == false) {
				drb.addQueryTriplet("} UNION {");
			}
		}
	}


}
