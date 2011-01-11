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
	private static boolean numberOfNumenclaturePerBrainRegionInBAMS = true;
	public static void main(String[] args) throws IOException {
		int count = 0;

		long start = System.currentTimeMillis();

		//HashMap<Integer, NeurolexPageId> bamsData = RunQuery.RunBAMSQuery();
		if(numberOfNumenclaturePerBrainRegionInBAMS){
			ExpandAndWriteIntersection.getInstance().getNumberOfNomenclaturesPerBrainRegionInBAMS(RunQuery.RunBAMSQuery());
		}
		else{
			ExpandAndWriteIntersection.getInstance().findingMatches(true);
			ExpandAndWriteIntersection.getInstance().setData(RunQuery.RunBAMSQuery(), RunQuery.RunNeurolexQueryHashCode()
					, RunQuery.RunNeurolxQueryNoSynonyms(), RunQuery.RunNeurolexQueryNamePageId());

			ExpandAndWriteIntersection.getInstance().expandDataWithSynonyms();

			ExpandAndWriteIntersection.getInstance().findMatchesAndWrite();
		}	

		/**for(Integer key: bamsData.keySet())
			System.out.println("name: "+bamsData.get(key).getName());
		 **/
		long end = System.currentTimeMillis();
		long total = end - start;
		System.out.println("time to look for matches " + total + " ms");
		System.out.println("Total: "+count);

	}

}
