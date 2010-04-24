package com.daohoangson.uml;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tranvietson.uml.Structure;
import com.tranvietson.uml.StructureEvent;
import com.tranvietson.uml.StructureListener;

public class Diagram implements StructureListener {
	private LinkedList<Structure> structures = new LinkedList<Structure>();
	private Rectangle positions[] = null;
	private boolean built[];
	private boolean dependencies[][];
	private JPanel panel;
	private boolean debuging = false;
	
	public Diagram() {
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setPreferredSize(new Dimension(400,200));
	}
	
	public Component getDisplay() {
		return panel;
	}
	
	public void add(Structure structure) {
		structures.add(structure);
		structure.addStructureListener(this);
		structureChanged(new StructureEvent(structure));
	}
	
	private void draw() {
		panel.removeAll();
		
		int n = structures.size();
		built = new boolean[n];
		positions = new Rectangle[n];
		
		for (int dependency_limit = 0; dependency_limit < n; dependency_limit++)
			for (int i = 0; i < n; i++) {
				int dependency_count = 0;
				for (int c = 0; c < n; c++) 
					if (dependencies[i][c])
						dependency_count++;
				
				if (dependency_count == dependency_limit) {
					JComponent c = build(i);
					if (c != null) panel.add(c);
				}
			}
		
		panel.validate();
	}
	
	private JComponent build(int i) {
		JComponent container = null;
		
		if (!built[i]) {
			built[i] = true;
			JComponent component = build(structures.get(i));
			
			JPanel dependent_container = new JPanel();
			dependent_container.setLayout(new BoxLayout(dependent_container,BoxLayout.X_AXIS));
			int n = structures.size();
			for (int j = 0; j < n; j++) {
				if (dependencies[j][i]) {
					JComponent dependent_component = build(j);
					if (dependent_component != null)
						dependent_container.add(dependent_component);
				}
			}
			
			if (dependent_container.getComponentCount() > 0) {
				container = new JPanel();
				container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
				container.add(component);
				container.add(dependent_container);
			} else {
				container = component;
			}
		}
		
		return container;
	}
	
	private JComponent build(Structure s) {
		JComponent c;
		
		if (s.hasChildren()) {
			c = new JPanel();
			c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
			c.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			c.add(new JLabel(s.toString()));
			
			Structure children[] = s.getChildren();
			for (int i = 0; i < children.length; i++) {
				c.add(build(children[i]));
			}
			
			c.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createCompoundBorder(
									BorderFactory.createEmptyBorder(5, 10, 5, 10)
									, BorderFactory.createLineBorder(Color.BLACK)
							)
							, BorderFactory.createEmptyBorder(5, 10, 5, 10)
					)
			);
		} else {
			c = new JLabel(s.toString());
		}
		
		return c;
	}
	
	public Structure getStructure(int x, int y) {
		Point p = new Point(x,y);
		
		if (positions != null) {
			for (int i = 0; i < positions.length; i++) {
				if (positions[i].contains(p)) {
					log("Found " + structures.get(i) + " at " + x + "," + y + ".");
					return structures.get(i);
				}
			}
		}
		
		log("Found nothing at " + x + "," + y + ".");
		return null;
	}

	@Override
	public void structureChanged(StructureEvent e) {
		int n = structures.size();
		dependencies = new boolean[n][n];
		Iterator<Structure> itr = structures.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Structure s = itr.next();
			Structure[] p = s.getParents();
			for (int j = 0, k = p.length; j < k; j++) {
				int f = structures.indexOf(p[j]);
				if (f != -1) dependencies[i][f] = true;
			}
			i++;
		}
		
		if (debuging) {
			System.err.print("Dependencies: ");
			for (int k = 0; k < n; k++) {
				System.err.print("[");
				for(int j = 0; j < n; j++) 
					System.err.print(dependencies[k][j]?1:0);
				System.err.print("]");
			}
			System.err.println();
		}
		
		draw();
	}
	
	private void log(String message) {
		System.err.println(message);
	}
}
