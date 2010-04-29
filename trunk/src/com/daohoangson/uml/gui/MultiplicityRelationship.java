package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;

/**
 * Relationship class for Multiplicity Relationships
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Class_diagram#Relationships">Wikipedia page</a>
 */
public class MultiplicityRelationship extends Relationship {
	private double cfg_diamon_length = 15;
	private double cfg_diamon_side_angle = 0.4;
	private double cfg_diamon_side_length = cfg_diamon_length/2*Math.cos(cfg_diamon_side_angle);

	public MultiplicityRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);
		cfg_color = Color.RED;
	}

	@Override
	public void draw(Graphics g) {
		drawConnectionLine(g,0,cfg_diamon_length);		
	}

	@Override
	protected void __drawConnectionLine(Graphics g,PointSet ps) {
		int x0 = ps.x;
		int y0 = ps.y;
		double delta = ps.delta; //the delta angle between the line and vertical line 
		
		//calculate point 1 (diamon edges)
		double delta1 = delta - cfg_diamon_side_angle;
		int x1 = (int) Math.ceil(x0 + cfg_diamon_side_length*Math.sin(delta1));
		int y1 = (int) Math.ceil(y0 + cfg_diamon_side_length*Math.cos(delta1));
		
		//calculate point 2 (diamon edges)
		double delta2 = delta + cfg_diamon_side_angle;
		int x2 = (int) Math.ceil(x0 + cfg_diamon_side_length*Math.sin(delta2));
		int y2 = (int) Math.ceil(y0 + cfg_diamon_side_length*Math.cos(delta2));
		
		//calculate the joint point of diamon and connection line
		int x3 = (int) Math.ceil(x0 + cfg_diamon_length*Math.sin(delta));
		int y3 = (int) Math.ceil(y0 + cfg_diamon_length*Math.cos(delta));
		
		//draw diamon
		g.drawLine(x0, y0, x1, y1);
		g.drawLine(x0, y0, x2, y2);
		g.drawLine(x3, y3, x1, y1);
		g.drawLine(x3, y3, x2, y2);
	}

}
