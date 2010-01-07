
import org.apache.commons.collections15.Factory;

/**
 * The class is used to create the edges for the graph.  This class is implemented 
 * by Multi-Scale Connnectome Browser.
 * @date    December 10, 2009
 * @author  Ruggero Carloz
 * @version 0.0.2
 */
public class Link implements Factory{

	double capacity;
	double weight;
	double value;

	/*
	 * Constructor
	 */
	public Link(double weight,double capacity){
		this.value++;
		this.weight = weight;
		this.capacity = capacity;
	}
	/*
	 * Method makes the edges value printable.
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return " ";
	}

	public Object create() {
		// TODO Auto-generated method stub
		return new Link(1.0,2.0);
	}
}
