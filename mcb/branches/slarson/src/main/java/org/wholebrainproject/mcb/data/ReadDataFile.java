package org.wholebrainproject.mcb.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class ReadDataFile {
	private static ReadDataFile instance;
	public MultiHashMap<String, BAMSToNeurolexData> dataMap = new MultiHashMap();
	public ReadDataFile() throws IOException{
		populateData();
	}
	
	public static ReadDataFile getInstance() throws IOException {
		if(instance == null)
			instance = new ReadDataFile();
		return instance;
	}

	/**
	 * Method populates the hash map with the data that is 
	 * contained in the file BAMSBrainregionsmatchedwithNeuroLex.csv
	 * @throws IOException
	 */
	public  MultiHashMap<String, BAMSToNeurolexData> populateData() throws IOException {
		String line;
		File file = new File("src/main/resources/BAMSBrainRegionMatchedWithNeurolex.csv");
		FileInputStream fis = null;
		BufferedReader br = null;
		DataInputStream in = null;
		
		try{
			fis = new FileInputStream(file);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));

			do{ 
				line = br.readLine();
				if(line != null && line.contains(",")){
					BAMSToNeurolexData data = buildDataRelation(line);
					dataMap.put(data.getBAMSPage(), data);
					data = null;
				}

			}while(line != null);

			// dispose all the resources after using them.
			fis.close();
			in.close();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
		return dataMap;
	}
	/**
	 * Method creates the object that will store the data that
	 * pretends to a given brain region.
	 * @param line
	 * @return
	 */
	private static BAMSToNeurolexData buildDataRelation(String line) {

		BAMSToNeurolexData data;

		String name = line.substring(0, line.indexOf(','));
		line =  line.substring(line.indexOf(',')+1);
		String nomenclature = line.substring(0, line.indexOf(','));
		line = line.substring(line.indexOf(',')+1);
		String species = line.substring(0, line.indexOf(','));
		line = line.substring(line.indexOf(',')+1);
		String BAMSUri = line.substring(0, line.indexOf(','));
		line = line.substring(line.indexOf(',')+1);
		String NeurolexUri = line.substring(0,line.indexOf(','));
		line = line.substring(line.indexOf(',')+1);
		String id = line = line.substring(line.indexOf(',')+1);
		
		data = new BAMSToNeurolexData(name,BAMSUri,NeurolexUri,id);
		data.addNomenclatureSpecies(nomenclature, species);

		return data;
	}
	
	public MultiHashMap<String, BAMSToNeurolexData> getMap(){
		return dataMap;
	}

}
