package com.daohoangson.uml.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.daohoangson.uml.structures.Structure;


public abstract class GeneralRelationship implements Relationship {
	private Diagram diagram;
	private Structure from, to;
	protected Color color = Color.BLACK;
	
	public GeneralRelationship(Diagram diagram, Structure from, Structure to) {
		this.diagram = diagram;
		this.from = from;
		this.to = to;
	}
	
	public Structure getFrom() {
		return from;
	}
	
	public Structure getTo() {
		return to;
	}
	
	protected void drawConnectionLine(Graphics g, double start_length, double end_length) {
		Rectangle fromBound = diagram.getBound(diagram.getComponentOf(getFrom()));
		Rectangle toBound = diagram.getBound(diagram.getComponentOf(getTo()));
		int x1, y1, x2, y2, x3, y3, x4, y4;
		double delta;
		Color original_color = g.getColor();
		g.setColor(color);
		
		if (fromBound.equals(toBound)) {
			//fromBound is similar to toBound
			//this happens sometimes
			x2 = fromBound.x + fromBound.width;
			y2 = fromBound.y + fromBound.height/3;
			x1 = (int) (x2 + Math.max(Math.max(start_length, end_length),15) + 5);
			y1 = y2;
			delta = Math.PI*0.5;
			
			int xx = x2;
			int yy = fromBound.y + fromBound.height*2/3;
			g.drawLine(xx, yy, x1, yy);
			g.drawLine(x1, yy, x1, y1);
		} else if (fromBound.y + fromBound.height < toBound.y) {
			//fromBound is higher than toBound
			x1 = fromBound.x + fromBound.width/2;
			y1 = fromBound.y + fromBound.height;
			x2 = toBound.x + toBound.width/2;
			y2 = toBound.y;
			delta = Math.atan((x2 - x1)/(double)(y2 - y1)) + Math.PI;
		} else if (fromBound.y > toBound.y + toBound.height) {
			//fromBound is lower than toBound
			x1 = fromBound.x + fromBound.width/2;
			y1 = fromBound.y;
			x2 = toBound.x + toBound.width/2;
			y2 = toBound.y + toBound.height;
			delta = Math.atan((x2 - x1)/(double)(y2 - y1));
		} else if (fromBound.x + fromBound.width < toBound.x) {
			//fromBound is on the left side
			x1 = fromBound.x + fromBound.width;
			y1 = fromBound.y + fromBound.height/2;
			x2 = toBound.x;
			y2 = toBound.y + toBound.height/2;
			delta = Math.PI*0.5*-1;
		} else {
			//the last chance, fromBound is on the right side
			x1 = fromBound.x;
			y1 = fromBound.y + fromBound.height/2;
			x2 = toBound.x + toBound.width;
			y2 = toBound.y + toBound.height/2;
			delta = Math.PI*0.5;
		}
		
		x3 = (int) Math.ceil(x1 - start_length*Math.sin(delta));
		y3 = (int) Math.ceil(y1 - start_length*Math.cos(delta));
		
		x4 = (int) Math.ceil(x2 + end_length*Math.sin(delta));
		y4 = (int) Math.ceil(y2 + end_length*Math.cos(delta));
		
		g.drawLine(x3, y3, x4, y4);
		__drawConnectionLine(g, new PointSet(x2,y2,delta));
		g.setColor(original_color);
	}

	abstract protected void __drawConnectionLine(Graphics g, PointSet ps);
}

class PointSet {
	int x, y;
	double delta;
	
	PointSet(int x, int y, double delta) {
		this.x = x;
		this.y = y;
		this.delta = delta;
	}
}
