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
import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureListener;

/**
 * Diagram Manager and Swing-based Component.
 * @author Dao Hoang Son
 * @version 1.0
 *
 */
public class Diagram extends JPanel implements StructureListener {
	private static final long serialVersionUID = -3147843723002469900L;
	/**
	 * A list of all global structures.
	 * @see Structure#checkIsUniqueGlobally()
	 */
	private List<Structure> structures = new LinkedList<Structure>();
	/**
	 * A list of all relationships among structures.
	 */
	private List<Relationship> relationships = new LinkedList<Relationship>();
	/**
	 * An array of all built component.
	 * It will be null at the beginning but will get updated 
	 * in {@link #build(int)}
	 */
	private Component built[] = null;
	/**
	 * An array of all dependencies among structures.
	 * Get updated in {@link #structureChanged(StructureEvent)}
	 */
	private boolean dependencies[][] = null;
	/**
	 * Determines if we are in debug mode.
	 */
	private boolean debuging = false;
	/**
	 * The width of border for structure's component
	 */
	public int cfg_border_width = 5;
	/**
	 * The color of the border specified from {@link #cfg_border_width}
	 */
	public Color cfg_border_color = Color.black;
	/**
	 * The gap between 2 components vertically
	 */
	public int cfg_gap_vertical = 25;
	/**
	 * The gap between 2 components horizontally
	 */
	public int cfg_gap_horizontal = 10;
	
	/**
	 * Constructor. Setup some useful configurations.
	 */
	public Diagram() {
		super();
		
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(700,400));
		setAlignmentY(Component.TOP_ALIGNMENT);
	}
	
	/**
	 * Add a new structure to the diagram.
	 * @param structure
	 */
	public void add(Structure structure) {
		if (structure.checkIsUniqueGlobally()) {
			structures.add(structure);
			structure.addStructureListener(this);
			structureChanged(new StructureEvent(structure));
		}
	}
	
	/**
	 * Does custom painting procedures.
	 * Draws relationships.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		// TODO: Figure out why this piece of code always cause con-current exception
//		Iterator<Relationship> itr = relationships.iterator();
//		while (itr.hasNext()) {
//			itr.next().draw(g);
//		}
//		
		
		for (int i = 0; i < relationships.size(); i++) {
			relationships.get(i).draw(g);
		}
	}
	
	/**
	 * Looks for the built component for structure
	 * @param structure the one needs component
	 * @return component or null
	 */
	Component getComponentOf(Structure structure) {
		if (built != null) {
			for (int i = 0; i < built.length; i++) {
				if (structures.get(i) == structure) 
					return built[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Calculate the bound of the component.
	 * The result is relative to the Diagram itself 
	 * (bypass all parents if any)
	 * @param c the component. Should be in the components tree
	 * @return
	 */
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
	
	/**
	 * Primary drawing method.
	 * Remove all existing components. 
	 * Re-build them in order of dependencies.
	 */
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
	
	/**
	 * Checks and builds a structure from its' id
	 * @param i the id of the structure in {@link #structures}
	 * @return the built component 
	 */
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
	
	/**
	 * Builds component for a structure
	 * @param s the structure
	 * @return the built component
	 */
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
							BorderFactory.createLineBorder(cfg_border_color)
							, BorderFactory.createEmptyBorder(cfg_border_width, cfg_border_width, cfg_border_width, cfg_border_width)
					)
			);
		} else {
			label.setFont(label.getFont().deriveFont(Font.ITALIC));
			c = label;
		}
		
		return c;
	}
	
	/**
	 * Wraps structure component with its' children's components
	 * @param c the structure component
	 * @param dc the array of children's components
	 * @return the wrapper component
	 */
	JComponent wrap(Component c, Component dc[]) {
		JPanel dependent_container = new JPanel();
		dependent_container.setLayout(new BoxLayout(dependent_container,BoxLayout.X_AXIS));
		for (int i = 0; i < dc.length; i++) {
			if (i > 0) 
				dependent_container.add(Box.createRigidArea(new Dimension(cfg_gap_horizontal,0)));
			dependent_container.add(dc[i]);
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
		container.add(c);
		container.add(Box.createRigidArea(new Dimension(0,cfg_gap_vertical)));
		container.add(dependent_container);
		
		return container;
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
