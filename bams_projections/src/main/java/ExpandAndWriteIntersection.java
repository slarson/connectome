
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.collections15.multimap.MultiHashMap;

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
	//brain regions in BAMS with Swanson-1998 nomenclature
	private static HashMap<Integer,NeurolexPageId> bamsRegions;
	//set containing inresection between BAMS and neurolex ordered by brain region name.
	private static TreeSet<NeurolexPageId> orderedSet = 
		new TreeSet<NeurolexPageId>(new NeurolexComparator());
	//BAMS projections that a subset of neurolex.
	private static MultiHashMap<Integer,NeurolexPageId> filteredBAMSProjections = 
		new MultiHashMap<Integer,NeurolexPageId>();
	//projections that appear in BAMS
	private static MultiHashMap<Integer,NeurolexPageId> bamsProjections;
	//brain regions and their synonyms from Neurolex.
	private static HashMap<Integer,brainRegionSynonyms> neurolexSynonyms;

	public static ExpandAndWriteIntersection getInstance(){
		if(instance == null){
			instance = new ExpandAndWriteIntersection();
		}
		return instance;
	}

	/**
	 * Method takes care of setting the global data structures.
	 * @param bamsData - BAMS brain regions that have Swanson-1998 nomenclature.
	 * @param bamsProjections - projections between BAMS brain regions.
	 * @param neurolexData - brain region names and there corresponding synonyms that appear in neurolex.
	 * @throws Exception 
	 */
	public void setData(HashMap<Integer,NeurolexPageId> bamsData,MultiHashMap<Integer,NeurolexPageId> bamsProjections,
			HashMap<Integer,brainRegionSynonyms> neurolexData) 
	throws Exception{
		this.bamsRegions = bamsData;
		this.bamsProjections = bamsProjections;
		this.neurolexSynonyms = neurolexData;
		
		//map the brain regions uris to projection. 
		setDataElements();
		setWriteProjections();
	}
	
	/**
	 * Method writes the projection data for each brain region 
	 * that appears in BAMSN and Neurolex.
	 * @throws IOException
	 */
	private void setWriteProjections() throws IOException {
		//output streams to write data to file.
		String fileName ="BAMSProjections.txt"; 
		File file = new File("/Users/ruggero/Desktop/"+fileName);
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream out=new DataOutputStream(fos);
		
		//buffer for the output.
		String outputBuffer;
		//find the intersection between BAMS and Nurolex and sort the data.
		sortData();
		
		//write the first row with sorted brain region names.
		for(NeurolexPageId currentBrainRegion: orderedSet){
			out.writeBytes(","+currentBrainRegion.getName());
		}
		out.writeBytes("\n");
		
		for(NeurolexPageId data: orderedSet){
			outputBuffer = data.getName();

			//update the index array if a projection is found with some other 
			// brain region in the ordered set.
			for(NeurolexPageId dataTo : orderedSet){
				if(data.getProjectsToUri().equals(dataTo.getBAMSUri()))
					data.updateIndexArray(dataTo.myIndex());		
			}
			//store projection array data in buffer.
			for(int i: data.getIndexArray()){
				outputBuffer+= ","+i;
			}
			//write buffer to file.
			out.writeBytes(outputBuffer+"\n");
			outputBuffer = null;
		}
		fos.close();
		out.close();
	}

	/**
	 * Method filters only the brain regions and their projections that are present in 
	 * Neurolex..
	 */
	private void sortData() {
		TreeSet<NeurolexPageId> orderedBrainRegions = new TreeSet<NeurolexPageId>(new NeurolexComparator());
		//obtain the projection's keys
		for(Integer key :filteredBAMSProjections.keySet()){
			//obtain the projection data for the keys.
			for(NeurolexPageId data: filteredBAMSProjections.get(key)){
				//obtain the synonyms keys.
				for(Integer neurolexKey: neurolexSynonyms.keySet()){
					//make sure that the brain region with its projections is present 
					// in Neurolex.
					if(data.getName().equals(neurolexSynonyms.get(neurolexKey).getName())){
						orderedBrainRegions.add(data);						
					}
				}
				//make sure that the brain region with its projections does not 
				//have a synonym.  If it does place the brain region in the sorted
				//data structure.
				for(brainRegionSynonyms synonym: neurolexSynonyms.values()){
					for(String synonymName: synonym.getSynonyms()){
						if(data.getName().equals(synonymName)){
							orderedBrainRegions.add(data);	
						}
					}
				}
			}
		}
		setCurrent(orderedBrainRegions);
	}
	/**
	 * Method copies the data to the global sorted list and set the index
	 * for each brain region that is going to be stored.
	 * @param orderedSet2
	 */
	private void setCurrent(
			TreeSet<NeurolexPageId> orderedSet2) {
		//index of brain region.
		int index = 0;
		for(NeurolexPageId data: orderedSet2){
			//initialize the brain regions projections array.
			data.createIndexArray(orderedSet2.size());
			//set the index of the brain region.
			data.setMyIndex(index);
			index++;
			orderedSet.add(data);
		}
	}

	/**
	 * Method maps the brain region's uri to projection.
	 */
	private void setDataElements() {
		for(Integer key: bamsRegions.keySet()){
			if(bamsProjections.containsKey(key)){
				filteredBAMSProjections.putAll(key, bamsProjections.get(key));
			}
		}
	}
}
