package org.wholebrainproject.mcb.data;

import java.io.IOException;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class BAMSToNeurolexMap {

	
	public static MultiHashMap<String, BAMSToNeurolexData> dataMap;
	private static BAMSToNeurolexMap instance;

	private BAMSToNeurolexMap() throws IOException {
		dataMap = ReadDataFile.getInstance().getMap();
		
	}
	
	public static BAMSToNeurolexMap getInstance() throws IOException {
		if(instance == null)
			instance = new BAMSToNeurolexMap();
		return instance;
	}
	
	/**
	 * Method returns the multi hash map containing the data maped
	 * from BAMS to Neurolex.
	 * @return
	 */
	public MultiHashMap<String, BAMSToNeurolexData> getBAMSToNeurolexMap(){
		return this.dataMap;
	}
}
