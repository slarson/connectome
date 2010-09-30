import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;


public class SetDisjoint {

	/**
	 * This application will compare the brain region 
	 * names that appear in BAMS with the brain region
	 * names that appear in neurolex and return the
	 * brain region names that are present in BAMS 
	 * but nor present in neurolex.
	 * @param args
	 * @author ruggero carloz
	 * @date 08-24-10
	 */
	private static HashMap<Integer,NeurolexPageId> bamsRegions;
	private static HashMap<Integer,brainRegionSynonyms> hashCodeNeurolex;
	private static HashMap<Integer,NeurolexPageId> neurolexNoSynonyms;
	private static HashMap<Integer, NeurolexPageId> data;
	

	public static void main(String[] args) throws IOException {

		bamsRegions = RunQuery.RunBAMSQuery();
		neurolexNoSynonyms = RunQuery.RunNeurolxQueryNoSynonyms();
		hashCodeNeurolex = RunQuery.RunNeurolexQueryHashCode();
		data = RunQuery.RunNeurolexQueryNamePageId();

		expandDataWithSynonyms();

		findMatchesAndWrite();

	}
	
	/**
	 * Method merges the data that belongs to brain regions that
	 * have synonyms with the rest of the data.
	 */
	private static void expandDataWithSynonyms() {
		
		HashMap<Integer, NeurolexPageId> dataDummy = new HashMap<Integer, NeurolexPageId>();

		for(Integer key: data.keySet()){
			if(hashCodeNeurolex.containsKey(key)){
				for(String synonym: hashCodeNeurolex.get(key).getSynonyms()){
					dataDummy.put(getHash(synonym), createNeurolexPageId(synonym,key));					
				}
			}
		}
		for(Integer key: dataDummy.keySet()){
			data.put(key, dataDummy.get(key));
		}
		expandDataWithNoSynonyms();	
	}
	
	/**
	 * Method merges the data that belongs to brain regions that
	 * have no synonyms with the rest of the data that contains
	 * brain regions that have synonyms.
	 */
	private static void expandDataWithNoSynonyms() {

		HashMap<Integer, NeurolexPageId> dataDummy = new HashMap<Integer, NeurolexPageId>();
		
		for(Integer key: data.keySet()){
			if(neurolexNoSynonyms.containsKey(key)){
				dataDummy.put(key, neurolexNoSynonyms.get(key));					
			}
		}
		for(Integer key: dataDummy.keySet()){
			data.put(key, dataDummy.get(key));
		}
		
	}

	/**
	 * Method creates a NeurolexPageId object from the synonyms to be 
	 * stored in data.
	 * @param synonym
	 * @param key
	 * @return
	 */
	private static NeurolexPageId createNeurolexPageId(String synonym, Integer key) {
		return new NeurolexPageId(getHash(synonym),synonym,
				data.get(key).getPage(),data.get(key).getId());

	}
	
	/**
	 * Method gives the hash code of a given word.
	 * @param synonym
	 * @return
	 */
	private static Integer getHash(String synonym) {
		return synonym.replace(" ","").toLowerCase().hashCode();

	}

	/**
	 * Method finds the intersection between the BAMS data and Neurolex
	 * and prints the set to file. 
	 * @throws IOException
	 */
	private static void findMatchesAndWrite() throws IOException{
		File file = new File("/Users/rcarloz/Desktop/regions_matched_with_BAMS.txt");
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream out=new DataOutputStream(fos);

		long start = System.currentTimeMillis();
		out.writeBytes("Brain region name, Source, Species, Neurolex page, Neurolex id \n");
		for(Integer bamsNameHash: bamsRegions.keySet()){
			if(data.containsKey(bamsNameHash)){
				out.writeBytes(getName(bamsRegions.get(bamsNameHash))+","
						+getDescription(bamsRegions.get(bamsNameHash))+","+
						getSpecies(bamsRegions.get(bamsNameHash))+
						","+getPage(data.get(bamsNameHash))+","+getId(data.get(bamsNameHash))+"\n");
			}
		}
		out.close();
		long end = System.currentTimeMillis();
		long total = end - start;
		System.out.println("time to look for matches " + total + " ms");
	}

	/**
	 * Method returns the id of the given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getId(NeurolexPageId neurolexPageId) {
		return neurolexPageId.getId();
	}

	/**
	 * Method return the Neurolex page where the data can be found.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getPage(NeurolexPageId neurolexPageId) {
		return neurolexPageId.getPage();
	}

	/**
	 * Method returns the name species to which the given data belongs too.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getSpecies(NeurolexPageId neurolexPageId) {
		return neurolexPageId.getSpecie();
	}

	/**
	 * Method returns the formated description of a given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getDescription(NeurolexPageId neurolexPageId) {
		
		return neurolexPageId.getDescription().replace(",", "");
	}

	/**
	 * Method returns the formated name of a given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getName(NeurolexPageId neurolexPageId) {
		return neurolexPageId.getName().replace(",","" );
		
	}

}
