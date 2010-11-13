package org.wholebrainproject.mcb.data;

import java.util.Comparator;


public class BrainRegionComparator implements Comparator{

	public int compare(Object o1, Object o2) {
		
		BAMSToNeurolexData component1 = (BAMSToNeurolexData)o1;
		BAMSToNeurolexData component2 = (BAMSToNeurolexData)o2;
		
		return component1.getName().compareTo(component2.getName());
		
	}

}