package com.daohoangson.uml.structures;

import java.util.Comparator;

/**
 * Comparator to sort structures by their names
 * 
 * @author Dao Hoang Son
 * @version 1.0
 */
public class StructureNameComparator implements Comparator<Structure> {
	private int direction;

	/**
	 * Constructor. Accept the comparing direction
	 * 
	 * @param direction
	 *            the direction, it should be either 1 or -1
	 */
	public StructureNameComparator(int direction) {
		this.direction = direction;
	}

	public StructureNameComparator() {
		this(1);
	}

	@Override
	public int compare(Structure s1, Structure s2) {
		return direction * s1.getName().compareTo(s2.getName());
	}
}
