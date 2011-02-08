package com.vaadin.jung;
class MyLink {
	double capacity; // should be private
	double weight; // should be private for good practice
	int id;
	private static int edgeCount;

	public MyLink(double weight, double capacity) {
		this.id = edgeCount++; // This is defined in the outer class.
		this.weight = weight;
		this.capacity = capacity;
	}
	public String toString() { // Always good for debugging
		return "E"+id;
	}
}
