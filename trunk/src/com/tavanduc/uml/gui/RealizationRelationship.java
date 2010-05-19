package com.tavanduc.uml.gui;

import java.awt.Graphics;

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
	}

	@Override
	public void draw(Graphics g, float size_factor) {
		cfg_dash_length = (int) (3 * size_factor);

		super.draw(g, size_factor);
	}
}
