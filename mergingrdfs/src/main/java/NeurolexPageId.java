import java.util.HashMap;
import java.util.Vector;

/**
 * Class instantiates the data that pretends to a given brain region.
 * The brain region name is the 'key' that is used in a multi-hash-map
 * and the NeurolexPageId is the value.
 * @author ruggero carloz
 * @date 9/22/2010
 *
 */
public class NeurolexPageId {

	private static NeurolexPageId instance = null;
	private String page;
	private String id;
	private String name;
	private Integer hash;
	private HashMap<String,String> source;
	private Vector<String> species;

	private String description;
	private String specie;

	/**
	 * Constructor instantiates the data that pretends to a given brain region.
	 * The brain region name is the 'key' that is used in a multi-hash-map
	 * and the NeurolexPageId is the value.  Data is obtained from Neurolex.
	 * @param Name
	 * @param Page
	 * @param Id
	 */
	public NeurolexPageId(Integer Hash,String Name,String Page, String Id, String Species){
		this.page = Page;
		this.id = Id;
		this.name = Name;
		this.specie = Species;
		this.hash = Hash;
	}
	
	/**
	 * Constructor instantiates the data that pretends to a given brain region.
	 * The brain region name is the 'key' that is used in a multi-hash-map
	 * and the NeurolexPageId is the value.  Data is obtained from BAMS.
	 * @param Hash
	 * @param Name
	 * @param Description
	 * @param Species
	 */
	public NeurolexPageId(Integer Hash,String Name, String Species){
		this.name = Name;
		this.specie = Species;
		this.hash = Hash;
		this.source = new HashMap<String,String>();
	}
	/**
	 * Method returns the instance of the class.
	 * @return NeurolexPageId 
	 */
	public static NeurolexPageId getInstance(){
		if(instance == null)
			System.err.println("ERROR: No prior instance created.");
		return instance;

	}

	/**
	 * Method returns the hash of the given brain region name.
	 * @return Integer
	 */
	public Integer getHash(){
		return this.hash;
	}
	/**
	 * Method returns the page of the given brain region.
	 * @return page
	 */
	public String getPage(){
		return this.page;
	}

	/**
	 * Method returns the id of the given brain region.
	 * @return id
	 */
	public String getId(){
		return this.id;
	}	

	/**
	 * Method returns the species from where the data was collected.
	 * @return species
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Method returns vector that stores the sources.
	 * @return source
	 */
	public HashMap<String,String> getSource(){
		return this.source;
	}

	/**
	 * Method adds source to data base.
	 * @param source
	 */
	public void addSource(String source,String species){
		this.source.put(source,species);
	}

	/**
	 * Method returns the species to which the data belongs to.
	 * @return
	 */

	public Vector<String> getSpecies(){
		return this.species;
	}

	/**
	 * Method adds species to data base.
	 * @param species
	 */
	public void addSpecies(String species){
		this.species.add(species);
	}

	/**
	 * Method returns the description for a given brain region.
	 * @return
	 */
	public String getDescription(){
		return this.description;
	}

	/**
	 * Method returns the species to which the brain region
	 * data belongs to.
	 * @return
	 */
	public String getSpecie(){
		return this.specie;
	}
}
