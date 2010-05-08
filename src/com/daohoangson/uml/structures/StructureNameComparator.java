package com.daohoangson.uml.structures;

import java.util.Comparator;

/**
 * Comparator to sort structures. The structures are arranged by structure name
 * from upper lever down: Interface > Class > Property > Method > Argument
 * 
 * @author Dao Hoang Son
 * 
 * @see Structure#getChildren()
 */
public class StructureNameComparator implements Comparator<Structure> {
	private int direction;

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
