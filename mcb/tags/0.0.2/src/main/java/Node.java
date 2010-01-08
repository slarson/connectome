import org.apache.commons.collections15.Factory;

/**
 * The class is used to create the vertices for the graph.  This class is implemented 
 * by class Multi-Scale Connectome Browser.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.2
 */
public class Node implements Factory{

	// Name of vertex.
	String name;

	/*
	 * Constructor.
	 * @param val name of the vertex.
	 */
	public Node(String val){
		this.name = val; 
	}

	/*
	 * Method makes the edges value printable.
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name+" ";
	}

	public Object create() {
		// TODO Auto-generated method stub
		return new Node("W");
	}
}
