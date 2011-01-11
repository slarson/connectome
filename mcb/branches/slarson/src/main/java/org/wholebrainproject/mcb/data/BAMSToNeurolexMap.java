package org.wholebrainproject.mcb.data;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.multimap.MultiHashMap;

public class BAMSToNeurolexMap {

	public static MultiHashMap<String, BAMSToNeurolexData> dataMap;
	private static BAMSToNeurolexMap instance;

	private BAMSToNeurolexMap() throws IOException {
		dataMap = ReadDataFile.getInstance().getMap();

	}

	public static BAMSToNeurolexMap getInstance() throws IOException {
		if (instance == null)
			instance = new BAMSToNeurolexMap();
		return instance;
	}

	/**
	 * Method returns the multi hash map containing the data maped from BAMS to
	 * Neurolex.
	 * 
	 * @return
	 */
	public MultiHashMap<String, BAMSToNeurolexData> getBAMSToNeurolexMap() {
		return this.dataMap;
	}

	/**
	 * Get a map with the names of all brain regions mapped to their BAMS URIs
	 * 
	 * @return
	 */
	public Map<String, URI> getNamesMap() {
		Map<String, URI> namesToUris = new HashMap<String, URI>();
		for (String uri : getBAMSToNeurolexMap().keySet()) {
			Collection<BAMSToNeurolexData> bList = getBAMSToNeurolexMap().get(
					uri);
			for (BAMSToNeurolexData b : bList) {
				namesToUris.put(b.getName(), URI.create(uri));
			}
		}
		return namesToUris;
	}
}
