package org.wholebrainproject.mcb.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class BAMSToNeurolexMap {

	private File file = new File("/Users/rcarloz/Desktop/BAMSBrainRegionMatchedWithNeuroLex.txt");
	private FileInputStream fis = null;
	private BufferedReader br = null;
	private DataInputStream in = null;
	public static MultiHashMap<String, BAMSToNeurolexData> dataMap;
	private static BAMSToNeurolexMap instance;

	private BAMSToNeurolexMap() throws IOException{
		dataMap = new MultiHashMap();
		populateData();
	}
	
	public static BAMSToNeurolexMap getInstance() throws IOException{
		if(instance == null)
			instance = new BAMSToNeurolexMap();
		return instance;
	}

	/**
	 * Method populates the hash map with the data that is 
	 * contained in the file BAMSBrainregionsmatchedwithNeuroLex.csv
	 * @throws IOException
	 */
	private void populateData() throws IOException {
		String line;
		try{
			fis = new FileInputStream(file);
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));

			do{ 
				line = br.readLine();
				if(line != null && line.contains(",")){
					BAMSToNeurolexData data = buildDataRelation(line);
					dataMap.put(data.getNeurolexPage(), data);
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
	
	/**
	 * Method returns the multi hash map containing the data maped
	 * from BAMS to Neurolex.
	 * @return
	 */
	public MultiHashMap<String, BAMSToNeurolexData> getMap(){
		return this.dataMap;
	}
}
