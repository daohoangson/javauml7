package com.daohoangson.uml.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	private Component[] built = null;
	/**
	 * An array of all dependencies among structures.
	 * Get updated in {@link #structureChanged(StructureEvent)}
	 */
	private boolean[][] dependencies = null;
	/**
	 * The pending image path to save
	 */
	private String imagepath = null;
	/**
	 * The pending image object to save
	 */
	private Image image = null;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debuging = false;
	public boolean cfg_draw_on_change = true;
	/**
	 * The gap between 2 components vertically
	 */
	private int cfg_gap_vertical = 25;
	/**
	 * The gap between 2 components horizontally
	 */
	private int cfg_gap_horizontal = 25;
	
	/**
	 * Constructor. Setup some useful configurations.
	 */
	public Diagram() {
		super();
		
		setLayout(new FlowLayoutTopAligned(cfg_gap_horizontal,cfg_gap_vertical));
		setPreferredSize(new Dimension(700,400));
		setAlignmentY(Component.TOP_ALIGNMENT);
	}
	
	/**
	 * Get the scrollable component.
	 * Use this for a better user experience. Anyway, this is
	 * optional. You can always {@linkplain JComponent#add(Component)} directly
	 * this object.
	 * @return a JScrollPane object of the diagram
	 */
	public Component getScrollable() {
		return new JScrollPane(this);
	}
	
	public Structure[] getStructures() {
		return structures.toArray(new Structure[0]);
	}
	
	/**
	 * Generate an image of the diagram to the specified path.
	 * Only support JPG and PNG format. Use the correct extension 
	 * in the path to select image format. <br />
	 * The trick here is done by calling drawing methods targeting
	 * the {@link Graphics} instance of the image but not of the
	 * diagram. Call {@link Component#repaint()} and let them do the drawing
	 * part and then call it again to make sure the on-screen visualization
	 * is correct. <br/>
	 * The image quality is not perfect. There is some missing lines, I have
	 * no idea :(
	 * @param imagepath the new image path. Existing file will be overwritten
	 * @return true if the imagepath is valid (with correct extension)
	 * @throws IOException
	 * 
	 * @see {@link #paint(Graphics)}
	 */
	public boolean saveImage(String imagepath) throws IOException {
		String ext = imagepath.substring(imagepath.length() - 4).toLowerCase();
		if (!ext.equals(".jpg") && !ext.equals(".png")) return false;
		
		this.imagepath = imagepath;
		int width = getSize().width;
		int height = getSize().height;
		image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		
		repaint(0,0,width,height);
		
		return true;
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
	 * Draws relationships (only this, at this moment).
	 */
	public void paint(Graphics g) {
		if (image != null) {
			g = image.getGraphics();
		}
		
		super.paint(g);
		
		// TODO: Figure out a cleaner way to do this without causing con-current exception
		Iterator<Relationship> itr = new LinkedList<Relationship>(relationships).iterator();
		while (itr.hasNext()) {
			itr.next().draw(g);
		}
		
		if (image != null && imagepath != null) {
			File file = new File(imagepath);
			try {
				ImageIO.write((RenderedImage) image, imagepath.substring(imagepath.length() - 3).toLowerCase(), file);
			} catch (IOException e) {
				//ignore
			}
			imagepath = null;
			image = null;
			repaint();
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
	public void draw() {
		removeAll();
		
		int n = structures.size();
		built = new Component[n];
		int width = 0;
		int height = 0;
		
		for (int dependency_limit = 0; dependency_limit < n; dependency_limit++)
			for (int i = 0; i < n; i++) {
				int dependency_count = 0;
				for (int c = 0; c < n; c++) 
					if (dependencies[i][c])
						dependency_count++;
				
				if (dependency_count == dependency_limit) {
					Component c = build(i);
					if (c != null) {
						add(c);
						
						Dimension d = c.getPreferredSize();
						width += d.width + 2*cfg_gap_horizontal;
						height = Math.max(height, d.height + 2*cfg_gap_vertical);
					}
				}
			}
		
		setPreferredSize(new Dimension(width,height));
		if (debuging) System.err.println(getPreferredSize());

		setSize(getPreferredSize());
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
	Component build(Structure s) {
		JComponent c;
		
		JLabel label = new DnDLabel(s);
		
		if (s.checkHasChildren()) {
			c = new DnDPanel(s,label);
			
			Structure children[] = s.getChildren();
			for (int i = 0; i < children.length; i++) {
				c.add(build(children[i]));
			}
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
	JComponent wrap(Component c, Component[] dc) {
		JPanel dependent_container = new JPanel();
		dependent_container.setLayout(new FlowLayoutTopAligned(cfg_gap_horizontal, cfg_gap_vertical));
		for (int i = 0; i < dc.length; i++) {
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
		synchronized (relationships) {
			relationships.clear();
		}
		
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
					if (itype != -1) dependencies[i][itype] = true;
					
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
		
		if (cfg_draw_on_change) draw();
	}
}
