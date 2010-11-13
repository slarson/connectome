package org.wholebrainproject.mcb.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
/**
 * Class takes care of treading the file containing the data
 * that pretends to projections between brain regions.
 * @author ruggero carloz
 *
 */
public class ReadProjectionDataFile {
	private static ReadProjectionDataFile instance;
	public HashMap<String, BAMSProjectionData> dataMap = new HashMap<String, BAMSProjectionData>();

	public ReadProjectionDataFile() throws IOException{
		populateData();
	}

	public static ReadProjectionDataFile getInstance() throws IOException {
		if(instance == null)
			instance = new ReadProjectionDataFile();
		return instance;
	}

	/**
	 * Method populates the hash map with the data that is 
	 * contained in the file BAMSProjections.csv
	 * @throws IOException
	 */
	public  HashMap<String, BAMSProjectionData> populateData() throws IOException {
		String line;
		//File file = new File("src/main/resources/BAMSBrainRegionMatchedWithNeurolex.csv");
		HashMap<Integer,BAMSProjectionData> projectionMap;
		InputStream fis = null;
		BufferedReader br = null;
		DataInputStream in = null;

		try{
			fis = ReadDataFile.class.getResourceAsStream("/BAMSProjections.csv");
			in = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(in));
			projectionMap = readFirstLine(br.readLine());
			do{ 
				line = br.readLine();
				if(line != null && line.contains(",")){
					BAMSProjectionData data = buildDataProjections(line,projectionMap);
					dataMap.put(data.getName(), data);
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
	 * Method updates the projection data for the brain regions.
	 * @param line
	 * @param projectionMap
	 * @return
	 */
	private BAMSProjectionData buildDataProjections(String line,HashMap<Integer,BAMSProjectionData> projectionMap) {
		String name = line.substring(0, line.indexOf(','));
		line =  line.substring(line.indexOf(',')+1);
		int currentIndex = 1;
		String currentValue;
		BAMSProjectionData currentData = findData(name,projectionMap.values());

		while(line.indexOf(',') != -1){
			currentValue = line.substring(0, line.indexOf(','));
			if(currentValue.equalsIgnoreCase("1")){
				currentData.addProjection(projectionMap.get(currentIndex).getName());
				//System.out.println("currentData: "+currentData.getName()+" projectos to: "+projectionMap.get(currentIndex).getName());
			}
			line =  line.substring(line.indexOf(',')+1);
			currentIndex++;
		}

		return currentData;
	}
	/**
	 * Method finds the given brain region in the map.
	 * @param name
	 * @return
	 */
	private BAMSProjectionData findData(String name,Collection<BAMSProjectionData> collection) {
		for(BAMSProjectionData data: collection){
			if(data.getName().equalsIgnoreCase(name))
				return data;
		}
		return null;
	}

	/**
	 * Method obtains the brain regions that have projections and
	 * assigns them an index.
	 * @param line
	 */
	private HashMap<Integer,BAMSProjectionData> readFirstLine(String line) {
		String currentName;
		BAMSProjectionData data;
		HashMap<Integer,BAMSProjectionData> map = new HashMap<Integer,BAMSProjectionData>();
		int count=0;
		while(line.contains(",")){	
			if(line.indexOf(',') != -1){
				currentName = line.substring(0, line.indexOf(','));
				data = new BAMSProjectionData(currentName,count);
				map.put(count,data);	
			}
			else
				break;
			count++;
			line =  line.substring(line.indexOf(',')+1);
		}
		data = new BAMSProjectionData(line,count);
		map.put(count,data);	
		return map;
	}


	/**
	 * Method returns the map of projections.
	 * @return
	 */
	public HashMap<String, BAMSProjectionData> getMap(){
		return dataMap;
	}

}
