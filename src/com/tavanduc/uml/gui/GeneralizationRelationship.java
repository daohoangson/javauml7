package com.tavanduc.uml.gui;

import java.awt.Graphics;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

/**
 * Relationship class for Generalization Relationships
 * 
 * @author Dao Hoang Son
 * @version 1.1
 * 
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Class_diagram#Relationships">Wikipedia</a>
 */
public class GeneralizationRelationship extends Relationship {
	/**
	 * The length of the arrow
	 */
	private double cfg_arrow_length;
	/**
	 * The angle for the arrow (from the core line)
	 */
	private double cfg_arrow_side_angle = 0.4;
	/**
	 * The length of the arrow sides
	 */
	private double cfg_arrow_side_length;

	public GeneralizationRelationship(Diagram diagram, Structure from,
			Structure to) {
		super(diagram, from, to);
	}

	@Override
	public void draw(Graphics g, float size_factor) {
		cfg_arrow_length = 10 * size_factor;
		cfg_arrow_side_length = cfg_arrow_length
				* Math.cos(cfg_arrow_side_angle);
		cfg_distance = (int) (5 * size_factor);

		drawConnectingPath(g, 0, cfg_arrow_length);
	}

	/**
	 * Draws the arrow
	 */
	@Override
	protected void drawEndPoints(Graphics g, PointSet ps) {
		int x0 = ps.x4;
		int y0 = ps.y4;
		double delta = ps.delta2;

		// calculate point 1 (arrow edges)
		double delta1 = delta - cfg_arrow_side_angle;
		int x1 = (int) Math.ceil(x0 - cfg_arrow_side_length * Math.sin(delta1));
		int y1 = (int) Math.ceil(y0 + cfg_arrow_side_length * Math.cos(delta1));

		// calculate point 2 (arrow edges)
		double delta2 = delta + cfg_arrow_side_angle;
		int x2 = (int) Math.ceil(x0 - cfg_arrow_side_length * Math.sin(delta2));
		int y2 = (int) Math.ceil(y0 + cfg_arrow_side_length * Math.cos(delta2));

		// draw arrow
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1, x0, y0);
		g.drawLine(x2, y2, x0, y0);
	}

}
