package com.daohoangson.uml.gui;
import java.awt.Rectangle;

import com.daohoangson.uml.structures.Structure;


public abstract class GeneralRelationship implements Relationship {
	private Diagram diagram;
	private Structure from, to;
	
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
	
	protected PointSet calculatePointSet() {
		Rectangle fromBound = diagram.getBound(diagram.getComponentOf(getFrom()));
		Rectangle toBound = diagram.getBound(diagram.getComponentOf(getTo()));
		PointSet ps = new PointSet();
		
		if (fromBound.y + fromBound.height < toBound.y) {
			//fromBounce is higher than toBounce
			ps.x1 = fromBound.x + fromBound.width/2;
			ps.y1 = fromBound.y + fromBound.height;
			ps.x2 = toBound.x + toBound.width/2;
			ps.y2 = toBound.y;
			ps.delta = Math.atan((ps.x2 - ps.x1)/(double)(ps.y2 - ps.y1));
		} else if (fromBound.y > toBound.y + toBound.height) {
			//fromBounce is lower than toBounce
			ps.x1 = fromBound.x + fromBound.width/2;
			ps.y1 = fromBound.y;
			ps.x2 = toBound.x + toBound.width/2;
			ps.y2 = toBound.y + toBound.height;
			ps.delta = Math.atan((ps.x2 - ps.x1)/(double)(ps.y2 - ps.y1));
		} else if (fromBound.x + fromBound.width < toBound.x) {
			//fromBounce is on the left side
			ps.x1 = fromBound.x + fromBound.width;
			ps.y1 = fromBound.y + fromBound.height/2;
			ps.x2 = toBound.x;
			ps.y2 = toBound.y + toBound.height/2;
			ps.delta = Math.PI*0.5*-1;
		} else {
			//the last chance, fromBounce is on the right side
			ps.x1 = fromBound.x;
			ps.y1 = fromBound.y + fromBound.height/2;
			ps.x2 = toBound.x + toBound.width;
			ps.y2 = toBound.y + toBound.height/2;
			ps.delta = Math.PI*0.5;
		}
		
		return ps;
	}

}

class PointSet {
	int x1, y1, x2, y2;
	double delta;
}
