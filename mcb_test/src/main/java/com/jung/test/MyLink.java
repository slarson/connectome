package com.jung.test;

/**
 * This class takes care of instantiating the graph's edges.
 * Storing the weight and capacity of a given edge.  The
 * class is not yet implemented.
 * @author ruggero
 *
 */
class MyLink {
	double capacity; // should be private
	double weight; // should be private for good practice
	int id;
	private static int edgeCount;

	public MyLink(double weight, double capacity) {
		this.id = edgeCount++; // The edge id will be given by this
							   // counter.
		this.weight = weight;
		this.capacity = capacity;
	}

	/**
	 * Method returns the id for a given edge.
	 * @return id - the individual id for a given edge.
	 */
	public String toString() { // Always good for debugging
		return "E"+id;
	}
}
