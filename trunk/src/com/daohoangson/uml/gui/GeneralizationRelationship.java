package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;


public class GeneralizationRelationship extends GeneralRelationship {
	private double arrow_length = 10;
	private double arrow_side_angle = 0.4;
	private double arrow_side_length = arrow_length*Math.cos(arrow_side_angle);

	public GeneralizationRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);
		color = Color.BLUE;
	}

	@Override
	public void draw(Graphics g) {
		drawConnectionLine(g,0,arrow_length);
	}

	@Override
	protected void __drawConnectionLine(Graphics g, PointSet ps) {
		int x0 = ps.x;
		int y0 = ps.y;
		double delta = ps.delta; //the delta angle between the line and vertical line 
		
		//calculate point 1 (arrow edges)
		double delta1 = delta - arrow_side_angle;
		int y1 = (int) Math.ceil(y0 + arrow_side_length*Math.cos(delta1));
		int x1 = (int) Math.ceil(x0 + arrow_side_length*Math.sin(delta1));
		
		//calculate point 2 (arrow edges)
		double delta2 = delta + arrow_side_angle;
		int y2 = (int) Math.ceil(y0 + arrow_side_length*Math.cos(delta2));
		int x2 = (int) Math.ceil(x0 + arrow_side_length*Math.sin(delta2));
		
		//draw arrow
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1, x0, y0);
		g.drawLine(x2, y2, x0, y0);
	}

}
