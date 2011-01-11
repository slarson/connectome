package org.wholebrainproject.mcb.data;

import java.util.HashMap;
/**
 * This class creates a map from BAMS to neurolex.
 * @author Ruggero Carloz
 * @date   05-23-2010
 */
public class ConciliateDatabases {
	
	private HashMap<String,String> conciliatedMap;
	public static ConciliateDatabases instance = null;
	
	public static ConciliateDatabases getInstance(){
		if(instance == null)
			instance =  new ConciliateDatabases();
		return instance;
	}
	
	/**
	 * Method creates the data base that will store the map 
	 * between data bases. 
	 */
	public void createConciliatedMap(){
		this.conciliatedMap = new HashMap<String, String>();
	}
	
	/**
	 * Method maps the BAMS URL to the neurolex URL.
	 * @param bamsNames
	 * @param neurolexNames
	 */
	public void mapBAMSdataToNeurolex(String bamsNames, String neurolexNames){
		this.conciliatedMap.put(bamsNames, neurolexNames);
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
	 * Method returns the neurolex URL that mapped from 
	 * BAMS to neurolex.
	 * @return conciliatedMap - the map with conciliated data.
	 */
	public String  getConciliatedMap(String key){
		return this.conciliatedMap.get(key);
	}
}
