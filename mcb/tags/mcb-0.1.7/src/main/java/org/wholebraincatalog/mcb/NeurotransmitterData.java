package org.wholebraincatalog.mcb;
/**
 * This class store the neurotransmitter data for a particular 
 * cell.
 * @author - Ruggero Carloz
 * @date - May 13, 2010
 *
 */
public class NeurotransmitterData {

	private String neurotransmitter;
	private String role;
	
	public NeurotransmitterData(String neurotransmitter, String role){
		this.role = role;
		this.neurotransmitter = neurotransmitter;
	}
	
	/**
	 * Method returns the name of the neurotransmitter.
	 * @return neurotransmitter.
	 */
	public String getNeurotransmitter(){
		return this.neurotransmitter;
	}
	
	/**
	 * Method returns the role of the neurotransmitter.
	 * @return role
	 */
	public String getRole(){
		return this.role;
	}
}
