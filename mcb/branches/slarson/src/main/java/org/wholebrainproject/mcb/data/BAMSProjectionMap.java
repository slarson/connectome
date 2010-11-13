package org.wholebrainproject.mcb.data;

import java.io.IOException;
import java.util.HashMap;
/**
 * Class takes care of managing the data map that pretends to 
 * the BAMS projection data.
 * @author ruggero carloz
 *
 */
public class BAMSProjectionMap {

	
	public static HashMap<String, BAMSProjectionData> dataMap;
	private static BAMSProjectionMap instance;

	/**
	 * Constructor instantiates the data map.
	 * @throws IOException
	 */
	private BAMSProjectionMap() throws IOException {
		dataMap = ReadProjectionDataFile.getInstance().getMap();
		
	}
	
	
	public static BAMSProjectionMap  getInstance() throws IOException {
		if(instance == null)
			instance = new BAMSProjectionMap();
		return instance;
	}
	
	/**
	 * Method returns the hash map containing the projection data
	 * from BAMS.
	 * @return
	 */
	public HashMap<String, BAMSProjectionData> getBAMSProjectionMap(){
		return this.dataMap;
	}
}
