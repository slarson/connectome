package org.wholebraincatalog.mcb;

public class CellData {
	
	private String neurotransmitter;
	private String role;
	
	public CellData(String neurotransmitter, String role){
		this.neurotransmitter = neurotransmitter;
		this.role = neurotransmitter;
	}
	
	public String getNeurotransmitter(){
		return this.neurotransmitter;
	}
	
	public String getRole(){
		return this.role;
	}

}
