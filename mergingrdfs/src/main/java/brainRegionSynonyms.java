import java.util.Vector;

/**
 * Class pretends to the synonyms that some brain regions 
 * have and that are present in neurolex.  This data is 
 * implemented when comparing brain region names from 
 * BAMS and the synonyms in neurolex.
 * @author ruggero carloz
 * @date 9-30-2010
 *
 */
public class brainRegionSynonyms {

	private Vector<String> synonyms;
	private String name;

	/**
	 * Constructor stores the name of the brain region
	 * and instantiates the vector where the synonyms
	 * are stored.
	 * @param Name
	 */
	public brainRegionSynonyms(String Name){
		this.synonyms = new Vector<String>();
	}

	/**
	 * Method adds the synonym to the data base.
	 * @param Synonym
	 */
	public void addSynonym(String Synonym){
		this.synonyms.add(Synonym);
	}
	
	/**
	 * Method returns the vector containing the 
	 * synonyms of the brain region.
	 * @return
	 */
	public Vector<String> getSynonyms(){
		return this.synonyms;
	}


}
