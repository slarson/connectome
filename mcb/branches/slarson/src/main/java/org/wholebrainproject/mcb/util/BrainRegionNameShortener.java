package org.wholebrainproject.mcb.util;

import java.util.HashMap;
import java.util.Map;

public class BrainRegionNameShortener {

	/**
	 * Stores a map between brain region names to their short variable names used
	 * in SPARQL queries
	 */
	private static Map<String,String> brainRegionsToShortVariableNames = null;
	
	/**
	 * Method reduces the name of the brain regions for the purposes of more
	 * efficient SPARQL queries.
	 * @param brainRegionName - full name of the brain region.
	 * @return reducedName - reduced name of the brain region.
	 */
	public static String reduceName(String brainRegionName){
		if (brainRegionsToShortVariableNames == null) {
			brainRegionsToShortVariableNames = new HashMap<String,String>();
		}
		
		String reducedName;
		int index = 0;
		
		//obtain first letter of brain region.
		reducedName = brainRegionName.substring(0, 1).toLowerCase();
			
		if (brainRegionsToShortVariableNames.containsKey(brainRegionName)) {
			return brainRegionsToShortVariableNames.get(brainRegionName);
		} else {
			if (brainRegionsToShortVariableNames.containsValue(reducedName) == false) {
				brainRegionsToShortVariableNames.put(brainRegionName,
						reducedName);
			} else {
				String reducedNameIncremented = reducedName;
				int i = 0;
				while (brainRegionsToShortVariableNames
						.containsValue(reducedNameIncremented)) {
					reducedNameIncremented = reducedName + i++;
				}
				brainRegionsToShortVariableNames.put(brainRegionName, 
						reducedNameIncremented);
				reducedName = reducedNameIncremented;
			}
		}
	
		return reducedName;
	}
}
