import java.util.Vector;


public class brainRegionSynonyms {

	private Vector<String> synonyms;
	private String name;
	
	public brainRegionSynonyms(String Name){
		this.synonyms = new Vector<String>();
	}
	
	public void addSynonym(String Synonym){
		this.synonyms.add(Synonym);
	}
	
	public Vector<String> getSynonyms(){
		return this.synonyms;
	}
	
	
}
