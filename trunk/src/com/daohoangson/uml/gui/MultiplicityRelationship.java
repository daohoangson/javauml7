package com.daohoangson.uml.gui;

import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;

/**
 * Relationship class for Multiplicity Relationships
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Class_diagram#Relationships">Wikipedia</a>
 */
class MultiplicityRelationship extends Relationship {
	/**
	 * The length of the diamond shape
	 */
	private double cfg_diamond_length = 15;
	/**
	 * The angle for the diamond sides
	 */
	private double cfg_diamond_side_angle = 0.4;
	/**
	 * The length of the diamond sides
	 */
	private double cfg_diamond_side_length = cfg_diamond_length / 2
			* Math.cos(cfg_diamond_side_angle);

	MultiplicityRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);
	}

	@Override
	void draw(Graphics g) {
		drawConnectionLine(g, 0, cfg_diamond_length);
	}

	/**
	 * Draws diamond shape
	 */
	@Override
	protected void __drawConnectionLine(Graphics g, PointSet ps) {
		int x0 = ps.x4;
		int y0 = ps.y4;
		double delta = ps.delta2; // the delta angle between the line and
		// vertical line

		// calculate point 1 (diamond edges)
		double delta1 = delta - cfg_diamond_side_angle;
		int x1 = (int) Math.ceil(x0 - cfg_diamond_side_length
				* Math.sin(delta1));
		int y1 = (int) Math.ceil(y0 + cfg_diamond_side_length
				* Math.cos(delta1));

		// calculate point 2 (diamond edges)
		double delta2 = delta + cfg_diamond_side_angle;
		int x2 = (int) Math.ceil(x0 - cfg_diamond_side_length
				* Math.sin(delta2));
		int y2 = (int) Math.ceil(y0 + cfg_diamond_side_length
				* Math.cos(delta2));

		// calculate the joint point of diamond and connection line
		int x3 = (int) Math.ceil(x0 - cfg_diamond_length * Math.sin(delta));
		int y3 = (int) Math.ceil(y0 + cfg_diamond_length * Math.cos(delta));

		// draw diamond
		g.drawLine(x0, y0, x1, y1);
		g.drawLine(x0, y0, x2, y2);
		g.drawLine(x3, y3, x1, y1);
		g.drawLine(x3, y3, x2, y2);
	}

}
