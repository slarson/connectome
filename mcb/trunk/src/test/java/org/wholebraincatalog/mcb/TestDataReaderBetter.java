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
import org.wholebraincatalog.mcb.SparqlQuery;

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

	public void testRunSelectQuery() {
		String sparql = "http://api.talis.com/stores/neurolex-dev1/services/sparql";
		SparqlQuery d = 
			new SparqlQuery(sparql);

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
		SparqlQuery bamsReader = new SparqlQuery(sparql);

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
		SparqlQuery bamsReader = new SparqlQuery(sparql);

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

	public void testParseSPARQLResultFromNIF() {
		String sparql = "http://rdf-stage.neuinfo.org/sparql";
		SparqlQuery bamsReader = new SparqlQuery(sparql);

		String[] brainRegions = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_amygdala", "Substantia_nigra_compact_part",
				"Ventral_tegmental_area", "Prelimbic_area", 
		"Lateral_preoptic_area"};

		populateNIFDataReader(bamsReader, brainRegions);

		InputStream queryResult = bamsReader.runSelectQuery();

		MultiHashMap<String, String> results = null;

		try {
			results = bamsReader.parseSPARQLResult(queryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//for (String key : results.keySet()) {
		//System.out.println("key: " + key + ", result: " + results.get(key));
		//}

		Node[] data = createNodesFromResults(brainRegions, results);

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
		SparqlQuery d = 
			new SparqlQuery(sparql);

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

	}

	public void testParseSPARQLResult5() {
		String sparqlNif = "http://rdf-stage.neuinfo.org/sparql";
		SparqlQuery bamsReader = new SparqlQuery(sparqlNif);

		String sparqlTalis = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery cellReader = new SparqlQuery(sparqlTalis);

		String[] brainRegions = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_amygdala", "Substantia_nigra_compact_part",
				"Ventral_tegmental_area", "Prelimbic_area", 
		"Lateral_preoptic_area"};

		String[] brainRegionsCellData = {"Globus_pallidus", "Caudoputamen", 
				"Central_nucleus_of_amygdala", "Substantia_nigra_pars_compacta",
				"Ventral_tegmental_area", "Prelimbic_area", 
		"Lateral_preoptic_area"};
		populateNIFDataReader(bamsReader, brainRegions);
		populateCellDataReader(cellReader,brainRegionsCellData);		

		InputStream connectivityQueryResult = bamsReader.runSelectQuery();
		InputStream cellQueryResult = cellReader.runSelectQuery();

		MultiHashMap<String, String> results = null;
		MultiHashMap<String, String> cellResults = null;

		try {
			results = bamsReader.parseSPARQLResult(connectivityQueryResult);
			cellResults = cellReader.parseSPARQLResult(cellQueryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String key : cellResults.keySet()) {
		  System.out.println("key: " + key + ", results: " + cellResults.get(key));
		}

		//assertNotNull(results);
		//assertNotNull(results.get("$s"));
		Node[] data = createNodesFromResults(brainRegions, results);

		storeCellData(data,cellResults);

		//System.out.println("cellResutls :"+cellResults);
	}

	/**
	 * Method searches for the cell data that corresponds to a given node and 
	 * stores it in the correct node field.
	 * @param data -  nodes.
	 * @param cellResults - cell data to be stored in the nodes.
	 */
	private void storeCellData(Node[] data, 
			MultiHashMap<String, String> cellResults) {

		//Date key to search cellResults.
		String searchKeyCell;
		String searchKeyNeurotransmitter;
		String searchKeyRole;
		
		//sotre the corresponding data in a stack
		Stack<String> neuroStack = new Stack<String>();
		Stack<String> roleStack = new Stack<String>();
		Stack<String> cellStack = new Stack<String>();
		
		for(Node node :  data){
			//System.out.println("Node name: "+node.toString());
			node.storeCellData(node.toString()+"_cells",new CellData());
			//System.out.println("Node name: "+node.toString());

			//construct key to search cellResults.
			searchKeyCell = "$"+node.toString()+"_cells";
			searchKeyNeurotransmitter = "$"+node.toString()+"_neurotransmitter";
			searchKeyRole = "$"+node.toString()+"_role";

			String neurotransmitterStore = null;
			String roleStore = null;

			//make sure cellRusults contains a searchKeyCell.
			if(cellResults.containsKey(searchKeyCell)){
				//obtain all the cells.
				for(String cells : cellResults.get("$"+node.toString()+"_cells")){
					cellStack.push(cells);
				}	
				//obtain all the nueurotransmitters.
				for(String neurotransmitter: cellResults.get(
								"$"+node.toString()+"_neurotransmitter")){
					System.out.println("neurotransmitter:"+neurotransmitter);
					neurotransmitterStore = neurotransmitter;
					neuroStack.push(neurotransmitterStore);
				}	
				//obtain all the roles.
				for(String role: cellResults.get(
								"$"+node.toString()+"_role")){
					roleStore = role;
					System.out.println("role:"+role);
					roleStack.push(roleStore);
				}
				//store the cell data in node.
				while( !(neuroStack.empty() && roleStack.empty()) ){
						// neurotransmitter name.
						String neuro_str = neuroStack.pop();
						// role name.
						String role_str = roleStack.pop();
						//cell name.
						String cell_str = cellStack.pop();
						//data corresponding to a given cell.
						NeurotransmitterData neurottransmitterCellData = 
							new NeurotransmitterData(neuro_str,role_str);
						//store the cell data corresponding to the node.
						node.getNodeCellsMap().get(node.toString()+"_cells").
						store(cell_str,neurottransmitterCellData);
						System.out.println("cell: "+cell_str+" neurotransmitter: "+
								neurottransmitterCellData.getNeurotransmitter()+
								" role:"+neurottransmitterCellData.getRole());
					}
				}
			}	
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
