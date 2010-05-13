package com.tavanduc.uml.gui;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

/**
 * Relationship class for Realization Relationships
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Class_diagram#Relationships">Wikipedia</a>
 */
public class RealizationRelationship extends GeneralizationRelationship {

	public RealizationRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);

		cfg_dash_length = cfg_distance / 2;
	}

}
