
import java.util.HashMap;

import org.apache.commons.collections15.multimap.MultiHashMap;
//import org.wholebrainproject.mcb.util.SparqlQuery;
public class RunQuery {
	private static MultiHashMap<Integer,NeurolexPageId> bamsDataProjections = 
		new MultiHashMap<Integer,NeurolexPageId>();

	private static HashMap<Integer, brainRegionSynonyms> mapNeurolexHashCode = 
		new HashMap<Integer, brainRegionSynonyms>();
	private static void addToBAMSDataProjections(
			MultiHashMap<Integer, NeurolexPageId> runSelectQueryBAMSProjections) {
		for(Integer key: runSelectQueryBAMSProjections.keySet()){
			for(NeurolexPageId data:runSelectQueryBAMSProjections.get(key)){
				bamsDataProjections.put(key, data);
			}
		}

	}
	/**
	 * This method returns the brain region names that appear in 
	 * the neurolex document located in the talis store.
	 * @return HashMap<Integer,brainRegionSynonyms> - the list containing the brain region names.
	 */
	public static HashMap<Integer,NeurolexPageId> RunBAMSQuery() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		//q.addPrefixMapping("bams_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3A>");
		q.setFlagNeurolexData(false);

		
		String nameVar = "$name";
		String uriVar = "$uri";
		
		// add query triplets
		q.addQueryTriplet("$uri <http://brancusi1.usc.edu/RDF/nomenclature> $nodeid ");
		q.addQueryTriplet("$nodeid <http://brancusi1.usc.edu/RDF/name>\"Swanson-1998\"");
		q.addQueryTriplet("$uri <http://brancusi1.usc.edu/RDF/name> $name");
		q.addSelectVariable(uriVar);
		q.addSelectVariable(nameVar);
		
		
		//add union between all sets of variables except the last
		return q.runSelectQueryNeurolexHashCode();

	}
	
	public static MultiHashMap<Integer, NeurolexPageId> RunBAMSProjectionQuery() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);

		String brainRegionAVar = "$brainRegionA";
		String brainRegionANameVar = "$brainRegionAName";
		String brainRegionBVar = "$brainRegionB";
		String brainRegionBNameVar = "$brainRegionBName";

		//int limit =10000;
		//int offset = 0;

		q.setFlagNeurolexData(false);
		q.setFlagBAMSData(false);

		//while(offset <= 50000){
			// This is where it gets tricky.  If you get the nomenclature first the you can get a hold of
			// the talis uri for a particular description.  Then you can use that 'pointer' to 
			// obtain the description the uri point to.  The other way will not work.
			q.addQueryTriplet("$nodeId <http://brancusi1.usc.edu/RDF/class1> $brainRegionA" );
			q.addQueryTriplet("$brainRegionA <http://brancusi1.usc.edu/RDF/name> $brainRegionAName");
			q.addQueryTriplet("$nodeId <http://brancusi1.usc.edu/RDF/class2> $brainRegionB");
			q.addQueryTriplet("$brainRegionB <http://brancusi1.usc.edu/RDF/name> $brainRegionBName");
			q.addSelectVariable(brainRegionAVar);
			q.addSelectVariable(brainRegionANameVar);
			q.addSelectVariable(brainRegionBVar);
			q.addSelectVariable(brainRegionBNameVar);

			//q.setCurrentLimitAndOffset(limit,offset);

			addToBAMSDataProjections(q.runSelectQueryBAMSProjections());
			//offset=offset+10000;
			//q.resetVariables();
		//}
		return bamsDataProjections;
	}
	/**
	 * This method returns the brain region names that appear in 
	 * the neurolex document located in the talis store.
	 * @return HashMap<Integer,brainRegionSynonyms> - the list containing the brain region names.
	 */
	public static HashMap<Integer,brainRegionSynonyms> RunNeurolexQuery() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		//q.addPrefixMapping("bams_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3A>");

		String synVar;
		String nameVar;
		int limit =10000;
		int offset = 0;
		q.setFlagNeurolexData(false);
		q.setFlagBAMSData(true);
		while(offset <= 50000){
			synVar = "$synonym";
			nameVar = "$name";
			// add query triplets
			q.addQueryTriplet("$x <http://www.w3.org/2000/01/rdf-schema#label> " +
			"$name");
			q.addQueryTriplet("$x <http://neurolex.org/wiki/Special:URIResolver/Property-3ASynonym> "+
					synVar);

			q.addSelectVariable(nameVar);
			q.addSelectVariable(synVar);
			q.setCurrentLimitAndOffset(limit,offset);

			//add union between all sets of variables except the last
			addToMultiHashMapHashCode(q.runSelectQueryNeurolex());
			q.resetVariables();
			offset=offset+10000;
		}
		q.setFlagNeurolexData(false);
		
		return mapNeurolexHashCode;
	}
	
	/**
	 * Method updates the multihashmap with the new data.
	 * @param obtainedMap - the multihashmap with the new data
	 * @param hashMap - the multihashmap storing all data in search
	 */
	public static void addToMultiHashMapHashCode(HashMap<Integer,brainRegionSynonyms> obtainedMap){
		for(Integer obtainedMapKey: obtainedMap.keySet()){
			mapNeurolexHashCode.put(obtainedMapKey, obtainedMap.get(obtainedMapKey));

		}
	}
}
