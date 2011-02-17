package com.jung.test;

/**
 * This class instantiates the vertices.  Each vertex is given
 * an id.
 * @author ruggero
 */
class MyNode {
	int id; // good coding practice would have this as private
	public MyNode(int id) {
		this.id = id;
	}

	/**
	 * Method returns the node's id.
	 * @return id - the vertex identification.
	 */
	public String toString() { // Always a good idea for debuging
		return "V"+id; // JUNG2 makes good use of these.
	}
}