package com.daohoangson.uml.gui;

import com.daohoangson.uml.structures.Structure;

public class RealizationRelationship extends GeneralizationRelationship {

	RealizationRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);

		dash = step / 2;
	}

}
