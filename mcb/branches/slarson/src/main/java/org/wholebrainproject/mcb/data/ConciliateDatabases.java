package org.wholebrainproject.mcb.data;

import java.util.HashMap;
/**
 * This class creates a map from BAMS to neurolex.
 * @author Ruggero Carloz
 * @date   05-23-2010
 */
public class ConciliateDatabases {
	private HashMap<String,String> conciliatedMap;
	
	public ConciliateDatabases(String[] bamsNames, String[] neurolexNames){
		conciliatedMap = new HashMap<String, String>();
		conciliateNames(bamsNames,neurolexNames);		
	}

	/**
	 * Method stores and maps brain region names from BAMS 
	 * to neurolex.
	 * @param bamsNames
	 * @param neurolexNames
	 */
	private void conciliateNames(String[] bamsNames, String[] neurolexNames) {
		for(String bamsName: bamsNames){
			for(String neurolexName: neurolexNames){
				this.conciliatedMap.put(bamsName, neurolexName);
			}			
		}		
	}
	
	/**
	 * Method returns the HashMap containing the map from 
	 * BAMS to neurolex.
	 * @return conciliatedMap - the map with conciliated data.
	 */
	public HashMap<String,String> getConciliatedMap(){
		return this.conciliatedMap;
	}
}
