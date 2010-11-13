package org.wholebrainproject.mcb.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class BAMSProjectionData {
	private HashMap<String,String> NomenclatureSpecies;
	private String BAMSName;
	private String BAMSPage;
	private String NeurolexPage;
	private String NeurolexId;
	private Vector<String> projectsTo;
	private int index;
	
	
	/**
	 * Constructor for the brain regions that have projections.
	 * @param brainRegionName
	 */
	public BAMSProjectionData(String brainRegionName,int index){
		this.BAMSName = brainRegionName;
		this.projectsTo = new Vector<String>();
		this.index = index;
	}
	
	/**
	 * Method returns the index for a given brain region.
	 * @return
	 */
	public int getMyIndex(){
		return this.index;
	}
	/**
	 * Method returns the projection vector for the brain region.
	 * @return
	 */
	public Vector<String> getProjections(){
		return this.projectsTo;
	}
	
	/**
	 * Method adds the brain region name to the list of projections.
	 * @param projection
	 */
	public void addProjection(String projection){
		this.projectsTo.add(projection);
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
	 * Method returns the brain region's BAMS URI.
	 * @return
	 */
	public String getBAMSPage(){
		return this.BAMSPage;
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
