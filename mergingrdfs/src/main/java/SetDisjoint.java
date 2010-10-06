import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.multimap.MultiHashMap;


public class SetDisjoint {

	/**
	 * This application will compare the brain region 
	 * names that appear in BAMS with the brain region
	 * names that appear in neurolex and return the
	 * intersection of the two sets.  The result is 
	 * a file, regions_matched_with_BAMS.txt, that 
	 * contains the name, source, species, neurolex page,
	 * and neurolex id for a given brain region.
	 * @param args
	 * @author ruggero carloz
	 * @date 08-24-10
	 */

	public static void main(String[] args) throws IOException {
		int count = 0;
		//HashMap<Integer,NeurolexPageId> bamsRegions = RunQuery.RunBAMSQuery();
		//HashMap<Integer,NeurolexPageId> neurolexNoSynonyms = RunQuery.RunNeurolxQueryNoSynonyms();
		//HashMap<Integer,brainRegionSynonyms> hashCodeNeurolex = RunQuery.RunNeurolexQueryHashCode();
		//HashMap<Integer, NeurolexPageId> data = RunQuery.RunNeurolexQueryNamePageId();
		long start = System.currentTimeMillis();
		ExpandAndWriteIntersection.getInstance().setData(RunQuery.RunBAMSQuery(), RunQuery.RunNeurolexQueryHashCode()
			, RunQuery.RunNeurolxQueryNoSynonyms(), RunQuery.RunNeurolexQueryNamePageId());

		ExpandAndWriteIntersection.getInstance().expandDataWithSynonyms();

		ExpandAndWriteIntersection.getInstance().findMatchesAndWrite();
		
		long end = System.currentTimeMillis();
		long total = end - start;
		System.out.println("time to look for matches " + total + " ms");
		
		/**for(Integer key: bamsRegions.keySet()){
			for(String nomenclature: bamsRegions.get(key).getSource()){
				System.out.println("Element name: "+bamsRegions.get(key).getName()+
						" elementSource: "+nomenclature);
				count++;
			}	
		
		}
		System.out.println("Total: "+count);
		**/
	}

}
