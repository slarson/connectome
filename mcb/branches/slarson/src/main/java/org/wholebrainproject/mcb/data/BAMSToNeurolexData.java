package org.wholebrainproject.mcb.data;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class BAMSToNeurolexData {
	private HashMap<String,String> NomenclatureSpecies;
	private String BAMSName;
	private String NeurolexPage;
	private String NeurolexId;
	
	public BAMSToNeurolexData(String Name,String NeurolexPage, String Id){
		this.BAMSName = Name;
		this.NeurolexPage = NeurolexPage;
		this.NeurolexId = Id;
		this.NomenclatureSpecies = new HashMap();		
	}
	
	/**
	 * Method maps the nomenclature to the species the nomenclature
	 * comes from.
	 * @param Nomenclature
	 * @param Species
	 */
	public void addNomenclatureSpecies(String Nomenclature, String Species){
		this.NomenclatureSpecies.put(Nomenclature, Species);
	}
	
	/**
	 * Method returns the set of keys in the MultiHash map.
	 * @return
	 */
	public Collection<String> getKeyMap(){
		return this.NomenclatureSpecies.keySet();
	}
	
	/**
	 * Method returns the species given a nomenclature.
	 * @param key
	 * @return
	 */
	public String getValue(String key){
		return this.NomenclatureSpecies.get(key);
	}
	
	/**
	 * Method returns a collection containing all species 
	 * @return
	 */
	public Collection<String> getAllSpecies(){
		return this.NomenclatureSpecies.values();
	}
	/**
	 * Method return the neurolex page of the brain region.
	 * @return NeurolexPage
	 */
	public String getNeurolexPage(){
		return this.NeurolexPage;
	}

	/**
	 * Method returns the neurolex id that belongs to the 
	 * brain region.
	 * @return
	 */
	public String getId(){
		return this.NeurolexId;
	}
	
	/**
	 * Method returns the name of the brain region as it 
	 * appears in BAMS.
	 * @return
	 */
	public String getName(){
		return this.BAMSName;
	}
}
