package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureEvent;
import com.daohoangson.uml.structures.StructureListener;


public class Diagram extends JPanel implements StructureListener {
	private static final long serialVersionUID = -3147843723002469900L;
	private LinkedList<Structure> structures = new LinkedList<Structure>();
	private List<Relationship> relationships = new LinkedList<Relationship>();
	
	private Component built[];
	private boolean dependencies[][];
	
	private boolean debuging = false;
	private int border_width = 5;
	private Color border_color = Color.black;
	private int gap_vertical = 25;
	private int gap_horizontal = 10;
	
	public Diagram() {
		super();
		
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(700,400));
		setAlignmentY(Component.TOP_ALIGNMENT);
	}
	
	public void add(Structure structure) {
		structures.add(structure);
		structure.addStructureListener(this);
		structureChanged(new StructureEvent(structure));
	}
	
	synchronized public void paint(Graphics g) {
		super.paint(g);
		
		Iterator<Relationship> itr = relationships.iterator();
		while (itr.hasNext()) {
			Relationship r = itr.next();
			r.draw(g);
		}
	}
	
	Component getComponentOf(Structure structure) {
		if (built != null) {
			for (int i = 0; i < built.length; i++) {
				if (structures.get(i) == structure) 
					return built[i];
			}
		}
		
		return null;
	}
	
	Rectangle getBound(Component c) {
		if (c == null) return new Rectangle();
		
		Rectangle bounce = c.getBounds();
		
		while (c.getParent() != null) {
			c = c.getParent();
			if (c == this) break;
			Rectangle bigger_bounce = c.getBounds();
			bounce.setLocation(bigger_bounce.x + bounce.x, bigger_bounce.y + bounce.y);
		}
		
		return bounce;
	}
	
	private void draw() {
		removeAll();
		
		int n = structures.size();
		built = new Component[n];
		
		for (int dependency_limit = 0; dependency_limit < n; dependency_limit++)
			for (int i = 0; i < n; i++) {
				int dependency_count = 0;
				for (int c = 0; c < n; c++) 
					if (dependencies[i][c])
						dependency_count++;
				
				if (dependency_count == dependency_limit) {
					Component c = build(i);
					if (c != null) add(c);
				}
			}
		
		validate();
		repaint();
	}
	
	private Component build(int i) {
		Component container = null;
		
		if (built[i] == null) {
			Component component = build(structures.get(i));
			built[i] = component;
			List<Component> dependent_components = new LinkedList<Component>();

			int n = structures.size();
			
			for (int dependency_limit = 0; dependency_limit < n; dependency_limit++)
				for (int j = 0; j < n; j++) 
					if (dependencies[j][i]) {
						int dependency_count = 0;
						for (int c = 0; c < n; c++) 
							if (dependencies[j][c])
								dependency_count++;
						
						if (dependency_count == dependency_limit) {
							Component dependent_component = build(j);
							if (dependent_component != null) {
								dependent_components.add(dependent_component);
							}
						}
					}
			
			if (dependent_components.size() > 0) {
				container = wrap(component,dependent_components.toArray(new Component[0]));
			} else {
				container = component;
			}
		}
		
		return container;
	}
	
	JComponent wrap(Component c, Component dc[]) {
		JPanel dependent_container = new JPanel();
		dependent_container.setLayout(new BoxLayout(dependent_container,BoxLayout.X_AXIS));
		for (int i = 0; i < dc.length; i++) {
			if (i > 0) 
				dependent_container.add(Box.createRigidArea(new Dimension(gap_horizontal,0)));
			dependent_container.add(dc[i]);
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
		container.add(c);
		container.add(Box.createRigidArea(new Dimension(0,gap_vertical)));
		container.add(dependent_container);
		
		return container;
	}
	
	JComponent build(Structure s) {
		JComponent c;
		
		JLabel label = new DnDLabel(s,this);
		
		if (s.checkHasChildren()) {
			c = new JPanel();
			c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
			c.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			c.add(label);
			
			Structure children[] = s.getChildren();
			for (int i = 0; i < children.length; i++) {
				c.add(build(children[i]));
			}
			
			c.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(border_color)
							, BorderFactory.createEmptyBorder(border_width, border_width, border_width, border_width)
					)
			);
		} else {
			label.setFont(label.getFont().deriveFont(Font.ITALIC));
			c = label;
		}
		
		return c;
	}

	@Override
	public void structureChanged(StructureEvent e) {
		int n = structures.size();
		
		//update dependencties
		dependencies = new boolean[n][n];
		relationships.clear();
		
		Iterator<Structure> itr = structures.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Structure s = itr.next();
			
			//look for Generalization Relationships
			Structure[] parents = s.getParents();
			for (int j = 0, k = parents.length; j < k; j++) {
				int iparent = structures.indexOf(parents[j]);
				if (iparent != -1) dependencies[i][iparent] = true;
				
				relationships.add(new GeneralizationRelationship(this, s, parents[j]));
			}
			
			Structure container = s.getContainer();
			if (container != null) {
				int icontainer = structures.indexOf(container);
				if (icontainer != -1) dependencies[i][icontainer] = true;
				
				relationships.add(new GeneralizationRelationship(this, s, container));
			}
			
			//look for 0..many Relationships
			Structure[] children = s.getChildren();
			for (int j = 0, k = children.length; j < k; j++) {
				Structure[] types = children[j].getTypeAsStructure();
				for (int l = 0, m = types.length; l < m; l++) {
					int itype = structures.indexOf(types[l]);
					if (itype != -1) dependencies[l][itype] = true;
					
					relationships.add(new MultiplicityRelationship(this, s, types[l]));
				}
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
}
