

/**
 * Class instantiates the data that pretends to a given brain region.
 * The brain region name is the 'key' that is used in a multi-hash-map
 * and the NeurolexPageId is the value.
 * @author ruggero carloz
 * @date 9/22/2010
 *
 */
public class NeurolexPageId{

	private String name;
	private String bamsUri;
	private int myIndex;
	private String projectsToName;
	private String projectsToUri;
	private int[] indexArray;

	/** 
	 * Constructor instantiates that data that pretends to a given brain region.
	 * @param Uri
	 * @param Name
	 * @param index
	 */
	public NeurolexPageId(String Uri, String Name){
		this.bamsUri = Uri;
		this.name = Name;
		
	}
	
	/**
	 * Constructor for BAMS projections.
	 * @param Uri
	 * @param Name
	 * @param ProjectsTo
	 * @param ProjectsToUri
	 */
	public NeurolexPageId(String Uri, String Name, String ProjectsTo, String ProjectsToUri){
		this.bamsUri = Uri;
		this.name = Name;
		this.projectsToName = ProjectsTo;
		this.projectsToUri = ProjectsToUri;
	}
	
	/**
	 * Method returns the brain region's uri to which the brain region
	 * projects to.
	 * @return
	 */
	public String getProjectsToUri(){
		return projectsToUri;
	}
	
	/**
	 * Method returns the brain region's name to which the brain region
	 * projects to.
	 * @return
	 */
	public String getProjectsToName(){
		return projectsToName;
	}
	/**
	 * Method returns the index of the object
	 * @return
	 */
	public int myIndex(){
		return myIndex;
	}
	
	/**
	 * Method sets the index of the given object.
	 * @param index
	 */
	public void setMyIndex(int index){
		this.myIndex = index;
	}
	/**
	 * Method returns the BAMS URI for a given brain region that is 
	 * present in BAMS.
	 * @return
	 */
	public String getBAMSUri(){
		return this.bamsUri;
	}

	/**
	 * Method returns the species from where the data was collected.
	 * @return species
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Method initializes the projection index array.
	 * @param total
	 */
	public void createIndexArray(int total){
		indexArray = new int[total];
		for(int i = 0; i < total;i++)
			indexArray[i] = 0;
	}
	
	/**
	 * Method updates the projection index array given an index.
	 * @param index
	 */
	public void updateIndexArray(int index){
		indexArray[index] = 1;
	}
	
	/**
	 * Method returns the projection index array.
	 * @return
	 */
	public int[] getIndexArray(){
		return indexArray;
	}
}
