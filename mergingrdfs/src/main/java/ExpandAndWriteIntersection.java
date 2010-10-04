import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class takes care of merging the data obtained from the 
 * BAMS rdf and the neurolex rdf.  After the date is merged it
 * is compared in order to find the intersection between the 
 * rdf documents.  Once the intersection is found the result
 * is printed to file "regions_matched_with_BAMS_and_neurolex.txt"
 * @author ruggero carloz
 * @date 09-29-2010
 */

public class ExpandAndWriteIntersection {

	private static ExpandAndWriteIntersection instance = null;
	private static HashMap<Integer,NeurolexPageId> bamsRegions;
	private static HashMap<Integer,brainRegionSynonyms> neurolexSynonyms;
	private static HashMap<Integer,NeurolexPageId> neurolexNoSynonyms;
	private static HashMap<Integer, NeurolexPageId> completeData;

	public static ExpandAndWriteIntersection getInstance(){
		if(instance == null){
			instance = new ExpandAndWriteIntersection();
		}
		return instance;
	}

	/**
	 * Method takes care of setting the global variables.
	 * @param bamsData
	 * @param hashCodeNeurolex
	 * @param neurolexWithNoSynonyms
	 * @param data
	 */
	public void setData(HashMap<Integer,NeurolexPageId> bamsData,HashMap<Integer,brainRegionSynonyms> hashCodeNeurolex,
			HashMap<Integer,NeurolexPageId> neurolexWithNoSynonyms,HashMap<Integer, NeurolexPageId> data){
		this.bamsRegions = bamsData;
		this.neurolexSynonyms = hashCodeNeurolex;
		this.neurolexNoSynonyms = neurolexWithNoSynonyms;
		this.completeData = data;
	}

	/**
	 * Method merges the data that belongs to brain regions that
	 * have synonyms with the rest of the data.
	 */
	static void expandDataWithSynonyms() {

		HashMap<Integer, NeurolexPageId> dataDummy = new HashMap<Integer, NeurolexPageId>();

		for(Integer key: completeData.keySet()){
			if(neurolexSynonyms.containsKey(key)){
				for(String synonym: neurolexSynonyms.get(key).getSynonyms()){
					dataDummy.put(getHash(synonym), createNeurolexPageId(synonym,key));					
				}
			}
		}
		for(Integer key: dataDummy.keySet()){
			if(!completeData.containsKey(key))
				completeData.put(key, dataDummy.get(key));
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

		for(Integer key: neurolexNoSynonyms.keySet()){
			if(!completeData.containsKey(key)){
				completeData.put(key, neurolexNoSynonyms.get(key));					
			}
		}
		//for(Integer key: dataDummy.keySet()){
			//completeData.put(key, dataDummy.get(key));
		//}

	}

	/**
	 * Method creates a NeurolexPageId object from the synonyms to be 
	 * stored in data.
	 * @param synonym
	 * @param key
	 * @return
	 */
	private static NeurolexPageId createNeurolexPageId(String synonym, Integer key) {
		//System.out.println("getHash(): "+getHash(synonym)+" synonym: "+synonym+" completeData.get(key).getPage(): "+
		//		completeData.get(key).getPage()+" completeData.get(key).getId(): "+completeData.get(key).getId());
		return new NeurolexPageId(getHash(synonym),synonym,
				completeData.get(key).getPage(),completeData.get(key).getId(),"");

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
	 * Method finds the intersection between the BAMS data and neurolex
	 * and prints the set to file. 
	 * @throws IOException
	 */
	static void findMatchesAndWrite() throws IOException{
		File file = new File("/Users/rcarloz/Desktop/regions_matched_with_BAMS_and_neurolex.txt");
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream out=new DataOutputStream(fos);

		long start = System.currentTimeMillis();
		out.writeBytes("Brain region name, Source, Species, Neurolex page, Neurolex id \n");
		for(Integer bamsNameHash: bamsRegions.keySet()){
			if(completeData.containsKey(bamsNameHash) && completeData.get(bamsNameHash) != null){
				for(String source: bamsRegions.get(bamsNameHash).getSource() ){
				   out.writeBytes(getName(bamsNameHash).replace(",", "")+","+source.replace(",", "")+","+
						getSpecies(bamsNameHash)+","+getPage(bamsNameHash)+","+getId(bamsNameHash)+"\n");
				}
			}
		}
		out.close();
		long end = System.currentTimeMillis();
		long total = end - start;
		System.out.println("time to look for matches " + total + " ms");
	}

	private static Vector<String> getSource(Integer bamsNameHash) {
		
		return bamsRegions.get(bamsNameHash).getSource();
	}

	/**
	 * Method returns the id of the given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getId(Integer key) {
		return completeData.get(key).getId();
	}

	/**
	 * Method return the Neurolex page where the data can be found.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getPage(Integer key) {
		return completeData.get(key).getPage();
	}

	/**
	 * Method returns the name species to which the given data belongs too.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getSpecies(Integer key) {
		return bamsRegions.get(key).getSpecie();
	}

	/**
	 * Method returns the formated description of a given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getDescription(Integer key) {

		return bamsRegions.get(key).getDescription().replace(",", "");
	}

	/**
	 * Method returns the formated name of a given brain region.
	 * @param neurolexPageId
	 * @return
	 */
	private static String getName(Integer key) {
		return bamsRegions.get(key).getName().replace(",","");

	}


}
