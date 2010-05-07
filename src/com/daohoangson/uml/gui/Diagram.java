package com.daohoangson.uml.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
 * Diagram Manager and Swing-based Component. It holds structures and display
 * them itself. Organize structures in tree. Supports relationships. Supports
 * {@linkplain #saveImage(ImageObserver) saving image} also.
 * 
 * @author Dao Hoang Son
 * @version 1.3
 * 
 */
public class Diagram extends JPanel implements StructureListener {
	private static final long serialVersionUID = -3147843723002469900L;
	/**
	 * A list of all global structures.
	 * 
	 * @see Structure#checkIsUniqueGlobally()
	 */
	private List<Structure> structures = new LinkedList<Structure>();
	/**
	 * A list of all relationships among structures.
	 */
	private List<Relationship> relationships = new LinkedList<Relationship>();
	/**
	 * An array of all built component. It will be null at the beginning but
	 * will get updated in {@link #build(int)}
	 */
	private Hashtable<Structure, Component> built = null;
	private Hashtable<Structure, Rectangle> boundsCache = null;
	private Rectangle[] boundsCacheArray = null;
	private Structure onFocusStructure = null;
	/**
	 * An array of all dependencies among structures. Get updated in
	 * {@link #structureChanged(StructureEvent)}
	 */
	private boolean[][] dependencies = null;
	/**
	 * The pending image object to save
	 * 
	 * @see DiagramImageObserver
	 */
	private Image image = null;
	/**
	 * The observer awaiting for image
	 * 
	 * @see DiagramImageObserver
	 */
	private ImageObserver observer = null;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debuging = false;
	/**
	 * Should the diagram be drawn if the structures are changed
	 */
	public boolean cfg_draw_on_change = true;
	/**
	 * The gap between 2 components vertically
	 */
	private int cfg_gap_vertical = 50;
	/**
	 * The gap between 2 components horizontally
	 */
	private int cfg_gap_horizontal = 50;

	/**
	 * Constructor. Setup some useful configurations.
	 */
	public Diagram() {
		super();

		setLayout(new FlowLayoutTopAligned(cfg_gap_horizontal,
				cfg_gap_vertical, true));
		setPreferredSize(new Dimension(700, 400));
		setAlignmentY(Component.TOP_ALIGNMENT);
	}

	public void setFocus(Structure structure) {
		onFocusStructure = structure;
		prepare();
	}

	/**
	 * Get the scroll-able component. Use this for a better user experience.
	 * Anyway, this is optional. You can always
	 * {@linkplain JComponent#add(Component)} directly this object.
	 * 
	 * @return a JScrollPane object of the diagram
	 */
	public Component getScrollable() {
		JScrollPane sp = new JScrollPane(this);

		// TODO: Need a better way to move around
		setAutoscrolls(true);
		MouseMotionListener doScrollRectToVisible = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				((Diagram) e.getSource()).scrollRectToVisible(r);
			}
		};
		addMouseMotionListener(doScrollRectToVisible);

		return sp;
	}

	public void ensureStructureIsVisible(Structure structure) {
		Rectangle rect = getBoundsFor(structure);
		scrollRectToVisible(rect);
	}

	/**
	 * Gets all structures in the diagram.
	 * 
	 * @return array containing structures in the diagram
	 */
	public Structure[] getStructures() {
		return structures.toArray(new Structure[0]);
	}

	/**
	 * Calculate the bound of the component. The result is relative to the
	 * Diagram itself (bypass all parents if any)
	 * 
	 * @param c
	 *            the component. Should be in the components tree
	 * @return
	 */
	private Rectangle getBoundsFor(Component c) {
		if (c == null) {
			return new Rectangle();
		}

		Rectangle bounce = c.getBounds();

		while (c.getParent() != null) {
			c = c.getParent();
			if (c == this) {
				break;
			}
			Rectangle bigger_bounce = c.getBounds();
			bounce.setLocation(bigger_bounce.x + bounce.x, bigger_bounce.y
					+ bounce.y);
		}

		return bounce;
	}

	private void getBoundsCache() {
		if (Diagram.debuging) {
			System.err.println("Generating boundsCache...");
		}

		boundsCache = new Hashtable<Structure, Rectangle>();
		Iterator<Entry<Structure, Component>> entries = built.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Entry<Structure, Component> entry = entries.next();
			Structure entry_structure = entry.getKey();
			Component entry_component = entry.getValue();

			boundsCache.put(entry_structure, getBoundsFor(entry_component));
		}

		boundsCacheArray = null; // reset the array instance
	}

	public Rectangle getBoundsFor(Structure structure) {
		if (boundsCache == null) {
			// cache is empty
			// this will be ran one time (and ready to be used intensively!)
			getBoundsCache();
		}

		return boundsCache.get(structure);
	}

	public Rectangle[] getBoundsForAll() {
		if (boundsCache == null) {
			if (Diagram.debuging) {
				System.err.println("Missed the cache!");
			}
			getBoundsCache();
		}

		if (boundsCacheArray == null) {
			boundsCacheArray = boundsCache.values().toArray(new Rectangle[0]);
		}

		return boundsCacheArray;

	}

	/**
	 * Generate an image of the diagram to the specified path. Only support JPG
	 * and PNG format. Use the correct extension in the path to select image
	 * format. <br />
	 * The trick here is done by calling drawing methods targeting the
	 * {@link Graphics} instance of the image but not of the diagram. Call
	 * {@link Component#repaint()} and let them do the drawing part and then
	 * call it again to make sure the on-screen visualization is correct. <br/>
	 * The image quality is not perfect. There is some missing lines, I have no
	 * idea :(
	 * 
	 * @param observer
	 *            the observer which get notified when image is built
	 * 
	 * @see {@link #paint(Graphics)}
	 */
	public void saveImage(ImageObserver observer) {
		this.observer = observer;
		int width = getSize().width;
		int height = getSize().height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		repaint(0, 0, width, height);
	}

	/**
	 * Add a new structure to the diagram.
	 * 
	 * @param structure
	 */
	public void add(Structure structure) {
		if (structure.checkIsUniqueGlobally()) {
			structures.add(structure);
			structure.addStructureListener(this);
			structureChanged(new StructureEvent(structure));
		}
	}

	public void remove(Structure structure) {
		if (structures.contains(structure)) {
			structures.remove(structure);
			structure.removeStructureListener(this);
			structureChanged(new StructureEvent(structure));
		}
	}

	public void clear() {
		Iterator<Structure> itr = structures.iterator();
		while (itr.hasNext()) {
			itr.next().removeStructureListener(this);
		}
		structures.clear();
		prepare();
	}

	/**
	 * Does custom painting procedures. Draws relationships (only this, at this
	 * moment).
	 */
	@Override
	public void paint(Graphics g) {
		if (image != null) {
			g = image.getGraphics();
		}

		super.paint(g);

		boundsCache = null; // remove the cache because it's useless now

		// TODO: Figure out a cleaner way to do this without causing con-current
		// exception. Well, this seem not to happen any more. Maybe this to-do
		// note need removing?
		Iterator<Relationship> itr = new LinkedList<Relationship>(relationships)
				.iterator();
		while (itr.hasNext()) {
			Relationship relationship = itr.next();
			if (onFocusStructure == null
					|| relationship.getFrom() == onFocusStructure) {
				relationship.draw(g);
			}
		}

		if (image != null) {
			Image painted = image;
			image = null;

			synchronized (observer) {
				if (observer != null) {
					observer.imageUpdate(painted, ImageObserver.ALLBITS, 0, 0,
							0, 0);
					observer = null;
				}
			}

			repaint();
		}
	}

	/**
	 * Primary drawing method. Remove all existing components. Re-build them in
	 * order of dependencies.
	 */
	public void draw() {
		removeAll();

		int n = structures.size();
		built = new Hashtable<Structure, Component>();
		int width = 0;
		int height = 0;
		int built_count = 0;

		for (int dependency_limit = 0; dependency_limit < n; dependency_limit++) {
			for (int i = 0; i < n; i++) {
				int dependency_count = 0;
				for (int c = 0; c < n; c++) {
					if (dependencies[i][c]) {
						dependency_count++;
					}
				}

				if (dependency_count == dependency_limit) {
					Component c = build(i);
					if (c != null) {
						add(c);

						Dimension d = c.getPreferredSize();
						width += d.width;
						height = Math.max(height, d.height);
						built_count++;
					}
				}
			}
		}

		if (built_count > 0) {
			width += built_count * cfg_gap_horizontal;
			width += 2 * cfg_gap_horizontal;
			height += 2 * cfg_gap_vertical;
		}
		setPreferredSize(new Dimension(width, height));
		setSize(getPreferredSize());

		validate();
		repaint();
	}

	/**
	 * Checks and builds a structure from its' id
	 * 
	 * @param i
	 *            the id of the structure in {@link #structures}
	 * @return the built component
	 */
	private Component build(int i) {
		Component container = null;
		Structure structure = structures.get(i);

		if (built.get(structure) == null) {
			Component component = build(structure);
			built.put(structure, component);
			List<Component> dependent_components = new LinkedList<Component>();

			int n = structures.size();

			for (int dependency_limit = 0; dependency_limit < n; dependency_limit++) {
				for (int j = 0; j < n; j++) {
					if (dependencies[j][i]) {
						int dependency_count = 0;
						for (int c = 0; c < n; c++) {
							if (dependencies[j][c]) {
								dependency_count++;
							}
						}

						if (dependency_count == dependency_limit) {
							Component dependent_component = build(j);
							if (dependent_component != null) {
								dependent_components.add(dependent_component);
							}
						}
					}
				}
			}

			if (dependent_components.size() > 0) {
				container = wrap(component, dependent_components
						.toArray(new Component[0]));
			} else {
				container = component;
			}
		}

		return container;
	}

	/**
	 * Builds component for a structure
	 * 
	 * @param s
	 *            the structure
	 * @return the built component
	 */
	Component build(Structure s) {
		JComponent c;

		JLabel label = new DiagramStructureName(s);

		if (s.checkHasChildren()) {
			c = new DiagramStructureGroup(s, label, new Dimension(
					cfg_gap_vertical / 5, cfg_gap_horizontal / 5));

			Structure children[] = s.getChildren();
			for (int i = 0; i < children.length; i++) {
				c.add(build(children[i]));
			}
		} else {
			c = label;
		}

		return c;
	}

	/**
	 * Wraps structure component with its' children's components
	 * 
	 * @param c
	 *            the structure component
	 * @param dc
	 *            the array of children's components
	 * @return the wrapper component
	 */
	JComponent wrap(Component c, Component[] dc) {
		JPanel dependent_container = new JPanel();
		dependent_container.setLayout(new FlowLayoutTopAligned(
				cfg_gap_horizontal, cfg_gap_vertical, false));
		for (int i = 0; i < dc.length; i++) {
			dependent_container.add(dc[i]);
		}
		dependent_container.setPreferredSize(FlowLayoutTopAligned
				.calculateRequiredSize(dependent_container, false,
						cfg_gap_horizontal, cfg_gap_vertical));
		dependent_container.setSize(dependent_container.getPreferredSize());

		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(c);
		container.add(Box.createRigidArea(new Dimension(0, cfg_gap_vertical)));
		container.add(dependent_container);

		return container;
	}

	/**
	 * Looks for relationships among our structures use the appropriate
	 * {@linkplain Relationship} and make them ready to be drawn later
	 */
	private void prepare() {
		int n = structures.size();

		// update dependencies
		dependencies = new boolean[n][n];
		synchronized (relationships) {
			relationships.clear();
		}

		Iterator<Structure> itr = structures.iterator();
		int i = 0;
		while (itr.hasNext()) {
			Structure s = itr.next();

			// look for Realization Relationships
			Structure[] parents = s.getParents();
			for (int j = 0, k = parents.length; j < k; j++) {
				int iparent = structures.indexOf(parents[j]);
				if (iparent != -1) {
					dependencies[i][iparent] = true;
				}

				relationships.add(new RealizationRelationship(this, s,
						parents[j]));
			}

			// look for Generalization Relationships
			Structure container = s.getContainer();
			if (container != null) {
				int icontainer = structures.indexOf(container);
				if (icontainer != -1) {
					dependencies[i][icontainer] = true;
				}

				relationships.add(new GeneralizationRelationship(this, s,
						container));
			}

			// look for 0..many Relationships
			Structure[] children = s.getChildren();
			for (int j = 0, k = children.length; j < k; j++) {
				Structure[] types = children[j].getTypeAsStructure();
				for (int l = 0, m = types.length; l < m; l++) {
					relationships.add(new MultiplicityRelationship(this, s,
							types[l]));
				}
			}

			i++;
		}

		if (Diagram.debuging) {
			System.err.print("Dependencies: ");
			for (int k = 0; k < n; k++) {
				System.err.print("[");
				for (int j = 0; j < n; j++) {
					System.err.print(dependencies[k][j] ? 1 : 0);
				}
				System.err.print("]");
			}
			System.err.println();
		}

		if (cfg_draw_on_change) {
			draw();
		}
	}

	@Override
	public void structureChanged(StructureEvent e) {
		prepare();
	}
}