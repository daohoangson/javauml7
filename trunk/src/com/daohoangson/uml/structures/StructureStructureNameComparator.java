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
public class StructureStructureNameComparator implements Comparator<Structure> {

	@Override
	public int compare(Structure s1, Structure s2) {
		int i1 = getPriority(s1);
		int i2 = getPriority(s2);

		if (i1 == i2) {
			return 0;
		} else if (i1 > i2) {
			return -1;
		} else {
			return 1;
		}
	}

	int getPriority(Structure s) {
		String name = s.getStructureName();
		if (name.equals("Interface")) {
			return 10;
		} else if (name.equals("Class")) {
			return 9;
		} else if (name.equals("Property")) {
			return 7;
		} else if (name.equals("Method")) {
			return 5;
		} else if (name.equals("Argument")) {
			return 3;
		}

		return 1;
	}
}
