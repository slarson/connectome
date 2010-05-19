package org.wholebraincatalog.mcb;



import java.util.List;

import org.apache.commons.collections15.multimap.MultiHashMap;
/**
 * This class stores the data that pretends to each cell.
 * @author Ruggero Carloz
 * @date May 13, 2010
 *
 */
public class CellData {

	// The key pretends to the cell's name the value to the
	// role of the neurotransmitter of the cell.
	private MultiHashMap<String, NeurotransmitterData> neurotransmitterData;
	
	
	public CellData(){
		neurotransmitterData = new MultiHashMap<String,NeurotransmitterData>();
	}
	
	/**
	 * Method returns a list of NeurotransmitterData which, as the name of the 
	 * class states, contains the neurotransmitter of the cell.
	 * @param cellKey - The key to retrive the data.
	 * @return List<NeurotransmitterData> - List of NeurotransmitterData.
	 */
	public List<NeurotransmitterData> getNeurotransmitter(String cellKey){
		return (List<NeurotransmitterData>) this.neurotransmitterData.get(cellKey);
	}
	
	/**
	 * Method stores the neurotransmitter data associated to the cell.
	 * @param cellKey -  The key to store the data.
	 * @param neurotransmitterData -  The cell's data.
	 */
	public void store(String cellKey, NeurotransmitterData neurotransmitterData){
		this.neurotransmitterData.put(cellKey, neurotransmitterData);
	}
	
	public MultiHashMap<String, NeurotransmitterData> getNeurotransmitterData(){
		return this.neurotransmitterData;
	}
	

}
