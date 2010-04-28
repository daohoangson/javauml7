package com.daohoangson.uml.gui;

import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;


public class GeneralizationRelationship extends GeneralRelationship {
	private double arrow_length = 10;
	private double arrow_side_angle = 0.4;
	private double arrow_side_length = arrow_length*Math.cos(arrow_side_angle);

	public GeneralizationRelationship(Diagram diagram, Structure from, Structure to) {
		super(diagram, from, to);
	}

	@Override
	public void draw(Graphics g) {
		//calculate and choose main connection line
		PointSet ps = calculatePointSet();
		int x1 = ps.x1;
		int y1 = ps.y1;
		int x2 = ps.x2;
		int y2 = ps.y2;
		double delta = ps.delta; //the delta angle between the line and vertical line 
		
		//calculate point 4 (arrow edges)
		double delta4 = delta - arrow_side_angle;
		int y4 = (int) Math.ceil(y2 + arrow_side_length*Math.cos(delta4));
		int x4 = (int) Math.ceil(x2 + arrow_side_length*Math.sin(delta4));
		
		//calculate point 5 (arrow edges)
		double delta5 = delta + arrow_side_angle;
		int y5 = (int) Math.ceil(y2 + arrow_side_length*Math.cos(delta5));
		int x5 = (int) Math.ceil(x2 + arrow_side_length*Math.sin(delta5));
		
		//calculate the joint point of arrow and connection line
		int y3 = (int) (y2 + arrow_length*Math.cos(delta));
		int x3 = (int) (x2 + arrow_length*Math.sin(delta));
		
		//draw arrow and connection line
		g.drawLine(x1, y1, x3, y3);
		g.drawLine(x4, y4, x5, y5);
		g.drawLine(x4, y4, x2, y2);
		g.drawLine(x5, y5, x2, y2);
	}

}
