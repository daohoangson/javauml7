package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;

/**
 * Relationship class for Generalization Relationships
 * @author Dao Hoang Son
 * @version 1.0
 *
 * @see <a href="http://en.wikipedia.org/wiki/Class_diagram#Relationships">Wikipedia page</a>
 */
class GeneralizationRelationship extends Relationship {
	private double cfg_arrow_length = 10;
	private double cfg_arrow_side_angle = 0.4;
	private double cfg_arrow_side_length = cfg_arrow_length*Math.cos(cfg_arrow_side_angle);

	GeneralizationRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);
		cfg_color = Color.BLUE;
	}

	@Override
	void draw(Graphics g) {
		drawConnectionLine(g,0,cfg_arrow_length);
	}

	@Override
	protected void __drawConnectionLine(Graphics g, PointSet ps) {
		int x0 = ps.x;
		int y0 = ps.y;
		double delta = ps.delta; //the delta angle between the line and vertical line 
		
		//calculate point 1 (arrow edges)
		double delta1 = delta - cfg_arrow_side_angle;
		int y1 = (int) Math.ceil(y0 + cfg_arrow_side_length*Math.cos(delta1));
		int x1 = (int) Math.ceil(x0 + cfg_arrow_side_length*Math.sin(delta1));
		
		//calculate point 2 (arrow edges)
		double delta2 = delta + cfg_arrow_side_angle;
		int y2 = (int) Math.ceil(y0 + cfg_arrow_side_length*Math.cos(delta2));
		int x2 = (int) Math.ceil(x0 + cfg_arrow_side_length*Math.sin(delta2));
		
		//draw arrow
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1, x0, y0);
		g.drawLine(x2, y2, x0, y0);
	}

}
