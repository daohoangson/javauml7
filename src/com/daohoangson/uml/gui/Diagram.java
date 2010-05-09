package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import com.tranvietson.uml.structures.StructureException;
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
public class Diagram extends JPanel implements StructureListener,
		MouseMotionListener, MouseListener {
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
	/**
	 * A list of bounds for built components. It will be null at the beginning
	 * of each painting request and it will be updated by
	 * {@link #getBoundsCache()}
	 * 
	 * @see #paint(Graphics)
	 */
	private Hashtable<Structure, Rectangle> boundsCache = null;
	/**
	 * An array of bounds extracted from {@link #boundsCache}. We store it in
	 * array form to improve the performance (actually, I'm not really sure
	 * about the performance)
	 */
	private Rectangle[] boundsCacheArray = null;
	/**
	 * Hold the on focus structure. It will be treated differently (if this is
	 * set)
	 * 
	 * @see #paint(Graphics)
	 */
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
	 * Hold the rectangle awaiting to be saved
	 * 
	 * @see #startClipping(ImageObserver)
	 * @see #mouseDragged(MouseEvent)
	 */
	private Rectangle image_rect = null;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debugging = false;
	/**
	 * Should the diagram be drawn if the structures are changed or not.
	 * 
	 * @see #structureChanged(StructureEvent)
	 */
	private boolean flag_auto_draw = true;
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

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Sets the on focus structure
	 * 
	 * @param structure
	 *            the structure that need focusing
	 * 
	 * @see #onFocusStructure
	 */
	public void setFocus(Structure structure) {
		onFocusStructure = structure;
		draw();
	}

	/**
	 * Get the scroll-able component. Use this for a better user experience.
	 * Anyway, this is optional. You can always
	 * {@linkplain JComponent#add(Component) add} directly this object.
	 * 
	 * @return a JScrollPane object of the diagram
	 */
	public Component getScrollable() {
		JScrollPane sp = new JScrollPane(this);

		return sp;
	}

	/**
	 * Tries its best to bring the requested structure into the viewport
	 * 
	 * @param structure
	 *            the structure needs to be viewed
	 */
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
	 * Calculates the bound of the component. The result is relative to the
	 * diagram itself (bypass all parents if any)
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

	/**
	 * Builds cache of bounds for built components. This should be cached
	 * because bounds calculation involves a lot of things.
	 * 
	 * @see #getBoundsFor(Component)
	 * @see #boundsCache
	 */
	private void getBoundsCache() {
		if (built == null) {
			return;
		}

		if (Diagram.debugging) {
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

	/**
	 * Gets the bounds for a structure. Makes use of the {@link #boundsCache}
	 * 
	 * @param structure
	 *            the one needs bound
	 * @return the bounds or null if it's can not be calculated
	 */
	public Rectangle getBoundsFor(Structure structure) {
		if (boundsCache == null) {
			// cache is empty
			// this will be ran one time (and ready to be used intensively!)
			getBoundsCache();
		}

		if (boundsCache != null) {
			return boundsCache.get(structure);
		} else {
			return null;
		}
	}

	/**
	 * Gets bounds for all built structures. Makes use of the
	 * {@link #boundsCache}
	 * 
	 * @return an array of bounds
	 */
	public Rectangle[] getBoundsForAll() {
		if (boundsCache == null) {
			if (Diagram.debugging) {
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
		repaint();
	}

	/**
	 * Takes a part of the diagram and generates image.<br/>
	 * It's quite similar to the saving image procedure but this clipping
	 * process allow user to select a specific area instead of saving the whole
	 * diagram
	 * 
	 * @param observer
	 *            the observer which get notified when image is built
	 * 
	 * @see {@link #saveImage(ImageObserver)}
	 */
	public void startClipping(ImageObserver observer) {
		this.observer = observer;
		image_rect = new Rectangle();
	}

	/**
	 * Add a new structure to the diagram. This method only adds globally unique
	 * structure
	 * 
	 * @param structure
	 *            the one to be added
	 * @return true if it can be added
	 * 
	 * @see Structure#checkIsUniqueGlobally()
	 */
	public boolean add(Structure structure) {
		if (structure.checkIsUniqueGlobally()) {
			if (!structures.contains(structure)) {
				structures.add(structure);
				structure.addStructureListener(this);
				structureChanged(new StructureEvent(structure,
						StructureEvent.ACTIVE));
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	/**
	 * Removes a structure from the diagram.
	 * 
	 * @param structure
	 *            the one needs removing
	 * @return true if the removal is completed
	 */
	public boolean remove(Structure structure) {
		if (structures.contains(structure)) {
			structures.remove(structure);
			structure.removeStructureListener(this);
			structureChanged(new StructureEvent(structure,
					StructureEvent.ACTIVE));

			return true;
		}

		return false;
	}

	/**
	 * Clears the diagram. Removes all structures added. Actually, this methods
	 * will call {@link Structure#dispose()} to dispose it completely.
	 */
	public void clear() {
		setAutoDrawing(false);
		Iterator<Structure> itr = new LinkedList<Structure>(structures)
				.iterator();
		while (itr.hasNext()) {
			try {
				itr.next().dispose();
			} catch (StructureException e) {
				// simply ignore
				if (Diagram.debugging) {
					e.printStackTrace();
				}
			}
		}
		structures.clear();
		setAutoDrawing(true);
		draw();
	}

	/**
	 * Does custom painting procedures.
	 * <ul>
	 * <li>Draws relationships</li>
	 * <li>Redirect painting to image instead of on screen component. Notify the
	 * observer to save the image if any</li>
	 * </ul>
	 * 
	 * @see #saveImage(ImageObserver)
	 * @see #startClipping(ImageObserver)
	 */
	@Override
	public void paint(Graphics g) {
		if (image != null) {
			// wow, saving image. Paint to the image instead of the
			// standard graphic itself
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
			// we are in a saving image request
			Image painted = image;

			if (image_rect != null) {
				// this is a clipping request instead of full capturing
				// we will first create new image (with the proper size)
				// then copy the requested area to the image
				// before pushing it to the observer
				painted = new BufferedImage(image_rect.width,
						image_rect.height, BufferedImage.TYPE_3BYTE_BGR);
				boolean result = painted.getGraphics().drawImage(image, 0, 0,
						image_rect.width, image_rect.height, image_rect.x,
						image_rect.y, image_rect.x + image_rect.width,
						image_rect.y + image_rect.height, null);
				if (Diagram.debugging) {
					System.err.println("Copying clipping area: " + result);
				}

				image_rect = null;
			}

			image = null;

			synchronized (observer) {
				if (observer != null) {
					// notify the observer so it can do the job
					observer.imageUpdate(painted, ImageObserver.ALLBITS, 0, 0,
							0, 0);
					observer = null;
				}
			}

			repaint();
		} else if (image_rect != null) {
			// we are in the middle of clipping selecting process
			// draw the clipping area
			Color original = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(image_rect.x, image_rect.y, image_rect.width,
					image_rect.height);
			g.setColor(original);
		}
	}

	/**
	 * Turns the auto-drawing on change feature on or off
	 * 
	 * @param b
	 *            true to enable, false to disable
	 * 
	 * @see #flag_auto_draw
	 */
	public void setAutoDrawing(boolean b) {
		if (b && !flag_auto_draw) {
			draw();
		}
		flag_auto_draw = b;
	}

	/**
	 * Primary drawing method. Remove all existing components. Re-build them in
	 * order of dependencies.
	 */
	public void draw() {
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

		if (Diagram.debugging) {
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

		removeAll();

		built = new Hashtable<Structure, Component>();
		int width = 0;
		int height = 0;
		int built_count = 0;

		for (int dependency_limit = 0; dependency_limit < n; dependency_limit++) {
			for (int j = 0; j < n; j++) {
				int dependency_count = 0;
				for (int c = 0; c < n; c++) {
					if (dependencies[j][c]) {
						dependency_count++;
					}
				}

				if (dependency_count == dependency_limit) {
					Component c = build(j);
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
	private Component build(Structure s) {
		JComponent c;

		JLabel label = new DiagramStructureName(s);

		if (s.checkCanHaveChildren()) {
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
	private JComponent wrap(Component c, Component[] dc) {
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

	@Override
	public void structureChanged(StructureEvent e) {
		switch (e.getType()) {
		case StructureEvent.ACTIVE:
			Structure structure = (Structure) e.getSource();
			if (!structure.getActive()) {
				remove(structure);
			}
			break;
		}

		if (flag_auto_draw) {
			draw();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (image_rect != null) {
			// the mouse is being dragged around
			// update the clipping area
			if (image_rect.getLocation().equals(new Point())) {
				image_rect.setLocation(e.getPoint());
			} else {
				int x1 = image_rect.getLocation().x;
				int y1 = image_rect.getLocation().y;
				int x2 = e.getPoint().x;
				int y2 = e.getPoint().y;
				image_rect.setBounds(Math.min(x1, x2), Math.min(y1, y2), Math
						.abs(x2 - x1), Math.abs((y2 - y1)));
			}
			// and display the clipping area now
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (image_rect != null) {
			// mouse released and we are in the middle of clipping process
			// send a saving image request now
			saveImage(observer);
		}
	}
}