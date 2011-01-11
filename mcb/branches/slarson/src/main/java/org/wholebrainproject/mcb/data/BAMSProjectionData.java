package org.wholebrainproject.mcb.data;

import java.util.Vector;

/**
 * Data class that store the projection information for
 * a particular brain region.
 * @author ruggero carloz
 *
 */
public class BAMSProjectionData {
	private String BAMSName;
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
	 * Method returns the name of the brain region as it 
	 * appears in BAMS.
	 * @return
	 */
	public String getName(){
		return this.BAMSName;
	}
}
