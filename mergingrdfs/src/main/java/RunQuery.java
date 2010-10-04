import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;
//import org.wholebrainproject.mcb.util.SparqlQuery;





public class RunQuery {
	public static MultiHashMap<String,String> hashMap = new MultiHashMap();
	private static HashMap<Integer, brainRegionSynonyms> mapNeurolexHashCode = new HashMap();
	private static HashMap<Integer,NeurolexPageId> mapNeurolexHashCodeNoSynonyms = 
		new HashMap<Integer,NeurolexPageId>();
	private static HashMap<Integer,NeurolexPageId> completeData = new HashMap<Integer,NeurolexPageId>();
	private static HashMap<Integer,NeurolexPageId> bamsData = 
		new HashMap<Integer,NeurolexPageId>();
	/**
	 * This method returns the brain region names that appear in 
	 * the BAMS document located in the talis store.
	 * @return Vector<String> - the list containing the brain region names.
	 */
	public static HashMap<Integer,NeurolexPageId> RunBAMSQuery(){
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);

		String nameVar = "$name";
		String descriptionVar = "$description";
		String speciesVar = "$species";
		String markerVar = "$marker";
		int limit =10000;
		int offset = 0;
		q.setFlagNeurolexData(false);
		q.setFlagBAMSData(true);
		while(offset <= 50000){
			// add query triplets
			q.addQueryTriplet("$x" + " <http://brancusi1.usc.edu/RDF/name>" + nameVar);
			q.addQueryTriplet("$x" + " <http://brancusi1.usc.edu/RDF/species>" + speciesVar);
			q.addQueryTriplet("$x" + " <http://brancusi1.usc.edu/RDF/nomenclature> $z" );
			q.addQueryTriplet("$marker" + " <http://brancusi1.usc.edu/RDF/name>" + descriptionVar);
			q.addSelectVariable(nameVar);
			q.addSelectVariable(descriptionVar);
			q.addSelectVariable(speciesVar);
			q.addSelectVariable(markerVar);
			q.setCurrentLimitAndOffset(limit,offset);

			//add union between all sets of variables except the last
			addToBAMSData(q.runSelectQueryBAMS());
			q.resetVariables();
			offset=offset+10000;
		}
		q.setFlagBAMSData(false);

		return bamsData;
	}

	private static void addToBAMSData(
			HashMap<Integer, NeurolexPageId> dataBams) {
		for(Integer key: dataBams.keySet()){
			bamsData.put(key, dataBams.get(key));
		}

	}

	/**
	 * This method returns the brain region names that appear in 
	 * the neurolex document located in the talis store.
	 * @return HashMap<Integer,brainRegionSynonyms> - the list containing the brain region names.
	 */
	public static HashMap<Integer,brainRegionSynonyms> RunNeurolexQueryHashCode() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		//create query
		// create prefixes
		//q.addPrefixMapping("bams_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3A>");

		String synVar;
		String nameVar;
		int limit =10000;
		int offset = 0;
		q.setFlagNeurolexData(true);
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
			addToMultiHashMapHashCode(q.runSelectQueryNeurolexHashCode());
			q.resetVariables();
			offset=offset+10000;
		}
		q.setFlagNeurolexData(false);
		return mapNeurolexHashCode;
	}

	public static HashMap<Integer,NeurolexPageId> RunNeurolxQueryNoSynonyms() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);
		HashMap<Integer,NeurolexPageId> dumHashMap;
		String nameVar;
		String pageVar;
		String idVar;
		String synVar;

		int limit =10000;
		int offset = 0;
		q.setFlagNeurolexData(true);
		while(offset <= 50000){

			nameVar = "$name";
			pageVar = "$page";
			idVar = "$id";
			synVar = "$synonym";
			// add query triplets
			q.addQueryTriplet("$x <http://www.w3.org/2000/01/rdf-schema#label> $name");
			q.addQueryTriplet("$x <http://semantic-mediawiki.org/swivt/1.0#page> $page");
			q.addQueryTriplet("$x <http://neurolex.org/wiki/Special:URIResolver/Property-3AId> $id");
			q.addQueryTriplet("OPTIONAL { $x <http://neurolex.org/wiki/Special:URIResolver/Property-3ASynonym> "+
					synVar+"}");
			q.addQueryTriplet("FILTER (!bound("+synVar+"))");

			q.addSelectVariable(nameVar);
			q.addSelectVariable(pageVar);
			q.addSelectVariable(idVar);
			//q.addSelectVariable(synVar);
			q.setCurrentLimitAndOffset(limit,offset);

			//add union between all sets of variables except the last
			dumHashMap = q.runSelectQueryNeurolexHashCodeNoSynonym();

			if(dumHashMap != null)
				addToMultiHashMapHashCodeNoSynonyms(dumHashMap);

			q.resetVariables();
			offset=offset+10000;

		}
		q.setFlagNeurolexData(false);
		//System.out.println("Final size of hashMap: "+hashMap.size());
		return mapNeurolexHashCodeNoSynonyms;
	}

	/**
	 * Method updates the multihashmap with the new data.
	 * @param obtainedMap - the multihashmap with the new data
	 * @param hashMap - the multihashmap storing all data in search
	 */
	public static void addToMultiHashMap(MultiHashMap<String,String> obtainedMap){
		for(String obtainedMapKey: obtainedMap.keySet()){
			Collection<String> obtainedMapValues = obtainedMap.get(obtainedMapKey);
			for(String obtainedMapValue: obtainedMapValues){
				hashMap.put(obtainedMapKey, obtainedMapValue);
			}
		}
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
	/**
	 * Method updates the multihashmap with the new data.
	 * @param obtainedMap - the multihashmap with the new data
	 * @param hashMap - the multihashmap storing all data in search
	 */
	public static void addToMultiHashMapHashCodeNoSynonyms(HashMap<Integer,NeurolexPageId> obtainedMap){
		for(Integer obtainedMapKey: obtainedMap.keySet()){
			if(!mapNeurolexHashCodeNoSynonyms.containsKey(obtainedMapKey))
				mapNeurolexHashCodeNoSynonyms.put(obtainedMapKey,obtainedMap.get(obtainedMapKey));
		}
	}

	private static void addToCompleteDataHashMap(
			HashMap<Integer, NeurolexPageId> runSelectQueryNeurolexHashCode) {
		for(Integer obtainedMulKey: runSelectQueryNeurolexHashCode.keySet()){
			if(!completeData.containsKey(obtainedMulKey)){
				completeData.put(obtainedMulKey, runSelectQueryNeurolexHashCode.get(obtainedMulKey));
			}
		}
	}



	public static HashMap<Integer, NeurolexPageId> RunNeurolexQueryNamePageId() {
		String sparqlNif = "http://api.talis.com/stores/neurolex/services/sparql";
		SparqlQuery q = new SparqlQuery(sparqlNif);	
		/**q.addPrefixMapping("name_rdf", "<http://www.w3.org/2000/01/rdf-schema#>");
		q.addPrefixMapping("page_rdf", "<http://semantic-mediawiki.org/swivt/1.0#page>");
		q.addPrefixMapping("id_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3AId>");
		q.addPrefixMapping("source_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3AAbbrevSource>");
		q.addPrefixMapping("species_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3ASpecies>");
		q.addPrefixMapping("label_rdf", "<http://neurolex.org/wiki/Special:URIResolver/Property-3ALabel>");
		 **/
		String nameVar = "$name";
		//String synonymVar = "$synonym";
		String pageVar = "$page";
		String idVar = "$id";

		int limit =10000;
		int offset = 0;

		q.setFlagNeurolexData(true);
		while(offset <= 50000){

			q.addQueryTriplet("$x <http://www.w3.org/2000/01/rdf-schema#label> $name");
			q.addQueryTriplet("$x <http://semantic-mediawiki.org/swivt/1.0#page> $page");
			q.addQueryTriplet("$x <http://neurolex.org/wiki/Special:URIResolver/Property-3AId> $id");


			q.addSelectVariable(nameVar);
			q.addSelectVariable(pageVar);
			q.addSelectVariable(idVar);


			q.setCurrentLimitAndOffset(limit,offset);

			//add union between all sets of variables except the last
			addToCompleteDataHashMap(q.runSelectQueryNeurolexNamePageId());
			offset=offset+10000;
			q.resetVariables();
		}
		q.setFlagNeurolexData(false);
		return completeData;
	}


}
