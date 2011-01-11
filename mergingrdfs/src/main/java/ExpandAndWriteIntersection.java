import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
	private static boolean findingMatches;
	public static String nomenclatureHeaderFrequency = "-- "+
	",Alheid et al."+",Bayer"+",Berman/Jones-1982"+
	",Bleier"+",Bowden-Human-2002"+",Bowden-Macaca-2002"+
	",Craigie"+",Dong-2007"+",Felleman & van Essen"+
	",Geeraedts"+",Gurdjian"+",Hammack -2007"+
	",His-Nomina Anatomica-1895"+",Hof et al.-2000"+",Johnston"+
	",Ju/Swanson"+",Koenig & Klippel"+",Krettek & Price"+
	",Mai-1997"+",McDonald"+",Moga-Fulwiler-Saper"+
	",Paxinos/Franklin-2001"+",Paxinos/Watson-1998"+",Pellegrino"+
	",Swanson-1992"+",Swanson-1998"+",Swanson-2004"+
	",Swanson/Cowan"+",Zeman & Maitland"+",de Groot"+
	",de Olmos - 1985"+",de Olmos -1995";

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
		String fileName = null;

		//decide the name of the file depending on what is it that we
		// want to do.
		if(findingMatches)
			fileName = "BAMSBrainRegionMatchedWithNeurolex.txt";
		else if(!findingMatches)
			fileName = "BAMSBrainRegionNotMatchedWithNeurolex.txt";

		File file = new File("/Users/rcarloz/Desktop/"+fileName);
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream out=new DataOutputStream(fos);

		if(findingMatches)
			out.writeBytes("Brain region name, Source, Species, BAMS page, Neurolex page, Neurolex id \n");
		else if(!findingMatches)
			out.writeBytes("Brain region name,BAMS uri, Source, Species\n");

		for(Integer bamsNameHash: bamsRegions.keySet()){
			if(findingMatches){
				if(completeData.containsKey(bamsNameHash) && completeData.get(bamsNameHash) != null){
					for(String source: getSourceAndSpecies(bamsNameHash).keySet()){
						out.writeBytes(getName(bamsNameHash).replace(",", "")+","+source.replace(",", "")+","+
								getSourceAndSpecies(bamsNameHash).get(source)+","+getPageBAMS(bamsNameHash)
								+","+getPageNeurolex(bamsNameHash)+","+getId(bamsNameHash)+"\n");
					}
				}
			}
			else if(!findingMatches){
				if(!completeData.containsKey(bamsNameHash)){
					for(String source: getSourceAndSpecies(bamsNameHash).keySet()){
						out.writeBytes(getName(bamsNameHash).replace(",", "")+","+getPageBAMS(bamsNameHash)+","+
								source.replace(",", "")+","+getSourceAndSpecies(bamsNameHash).get(source)+"\n");
					}
				}
			}
		}
		fos.close();
		out.close();

	}

	public void getNumberOfNomenclaturesPerBrainRegionInBAMS(HashMap<Integer,NeurolexPageId> bamsData) throws IOException{
		bamsRegions = bamsData;
		for(Integer key : bamsRegions.keySet()){
			for(String data: bamsRegions.get(key).getSource().keySet() ){
				updateNomenclautureFrequency(key,data);
			}
		}
		printNomenclautreFrequency();
	}
	private void printNomenclautreFrequency() throws IOException {
		String dataOut;
		String fileName ="BAMSNomenclatureFrequency.txt"; 
		File file = new File("/Users/rcarloz/Desktop/"+fileName);
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream out=new DataOutputStream(fos);
		out.writeBytes(nomenclatureHeaderFrequency+"\n");
		
		for(Integer key : bamsRegions.keySet()){
			dataOut = getDataOut(key);
			out.writeBytes(dataOut+"\n");
		}
		
		out.close();
		fos.close();
	}

	private String getDataOut(Integer key) {
		String data = bamsRegions.get(key).getName();
		for(Integer isPresent: bamsRegions.get(key).getNomenclatureFrequency()){
			data += ","+isPresent;

		}
		return data;
	}

	private void updateNomenclautureFrequency(Integer key, String data) {

		if(data.hashCode() == "alheid et al.".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(0, 1);
		if(data.hashCode() == "bayer".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(1, 1);
		if(data.hashCode() == "berman/jones-1982".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(2, 1);
		if(data.hashCode() == "bleier".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(3, 1);
		if(data.hashCode() == "bowden-human-2002".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(4, 1);
		if(data.hashCode() == "bowden-macaca-2002".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(5, 1);
		if(data.hashCode() == "craigie".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(6, 1);
		if(data.hashCode() == "dong-2007".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(7, 1);
		if(data.hashCode() == "felleman & van essen".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(8, 1);
		if(data.hashCode() == "geeraedts".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(9, 1);
		if(data.hashCode() == "gurdjian".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(10, 1);
		if(data.hashCode() == "hammack-2007".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(11, 1);
		if(data.hashCode() == "his-nomina anatomica-1895".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(12, 1);
		if(data.hashCode() == "hof et al.-2000".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(13, 1);
		if(data.hashCode() == "johnston".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(14, 1);
		if(data.hashCode() == "ju/swanson".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(15, 1);
		if(data.hashCode() == "koenig & klippel".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(16, 1);
		if(data.hashCode() == "krettek & price".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(17, 1);
		if(data.hashCode() == "mai-1997".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(18, 1);
		if(data.hashCode() == "mcdonald".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(19, 1);
		if(data.hashCode() == "moga-fulwiler-saper".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(20, 1);
		if(data.hashCode() == "paxinos/franklin-2001".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(21, 1);
		if(data.hashCode() == "paxinos/watson-1998".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(22, 1);
		if(data.hashCode() == "pellegrino".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(23, 1);
		if(data.hashCode() == "swanson-1992".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(24, 1);
		if(data.hashCode() == "swanson-1998".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(25, 1);
		if(data.hashCode() == "swanson-2004".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(26, 1);
		if(data.hashCode() == "swanson/cowan".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(27, 1);
		if(data.hashCode() == "zeman & maitland".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(28, 1);
		if(data.hashCode() == "de groot".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(29, 1);
		if(data.hashCode() == "de olmos - 1985".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(30, 1);
		if(data.hashCode() == "de olmos -1995".hashCode())
			bamsRegions.get(key).updateNomenclatureFrequency(31, 1);

	}

	/**
	 * Method gets the uri that belongs to the given BAMS brain region
	 * @param bamsNameHash
	 * @return
	 */
	private static String getPageBAMS(Integer bamsNameHash) {
		return bamsRegions.get(bamsNameHash).getBAMSUri();
	}

	/**
	 * Method returns the hash map containing the data that
	 * pretends to a given key.
	 * @param bamsNameHash
	 * @return
	 */
	private static HashMap<String,String> getSourceAndSpecies(Integer bamsNameHash) {
		return bamsRegions.get(bamsNameHash).getSource() ;
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
	private static String getPageNeurolex(Integer key) {
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

	public void findingMatches(boolean flag){
		this.findingMatches = flag;
	}


}
