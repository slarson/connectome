package org.wholebrainproject.mcb.data;

import java.io.IOException;
import java.util.HashMap;

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
	 * Method returns the multi hash map containing the data maped
	 * from BAMS to Neurolex.
	 * @return
	 */
	public HashMap<String, BAMSProjectionData> getBAMSProjectionMap(){
		return this.dataMap;
	}
}
