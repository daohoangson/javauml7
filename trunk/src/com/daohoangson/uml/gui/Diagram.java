package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureNameComparator;
import com.tavanduc.uml.gui.DiagramStructureGroup;
import com.tavanduc.uml.gui.GeneralizationRelationship;
import com.tavanduc.uml.gui.MultiplicityRelationship;
import com.tavanduc.uml.gui.RealizationRelationship;
import com.tavanduc.uml.gui.Relationship;
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
public class Diagram extends JPanel implements StructureListener,
		MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
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
	 * The scroll pane for the diagram
	 * 
	 * @see #getScrollable()
	 */
	private JScrollPane scrollpane = null;
	/**
	 * List of objects which listen to our event.
	 * 
	 * @see #addStructureListener(StructureListener)
	 * @see #removeStructureListener(StructureListener)
	 */
	private Vector<StructureListener> listeners;
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
	 * Specifies we are in the middle of a dragging-moving process
	 */
	private Point flag_drag_moving = null;
	/**
	 * The size factor property
	 * 
	 * @see #setSizeFactor(double)
	 * @see #getSizeFactor()
	 */
	private float size_factor = 1.0f;
	/**
	 * The gap between 2 components vertically
	 */
	private int cfg_gap_vertical = 50;
	/**
	 * The gap between 2 components horizontally
	 */
	private int cfg_gap_horizontal = 50;
	/**
	 * Hold the currently pressed component
	 */
	private DiagramStructureGroup pressedComponent = null;
	/**
	 * Hold the last pressed component
	 */
	private DiagramStructureGroup lastPressedComponent = null;

	/**
	 * Hold the last pressed location
	 */
	private Point pressedLocation = null;

	/**
	 * Constructor. Setup some useful configurations.
	 */
	public Diagram() {
		super();

		setLayout(null); // we will align components by ourselves
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
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
		repaint();

		if (Diagram.debugging) {
			System.err.println("On Focus: " + structure);
		}
	}

	/**
	 * Changes diagram zooming level. Sets the size factor for the diagram and
	 * trigger a re-draw
	 * 
	 * @param size_factor
	 *            the new size factor
	 * 
	 * @see #draw()
	 */
	public void setSizeFactor(float size_factor) {
		// make sure it's visible
		size_factor = (float) Math.max(0.25, size_factor);

		if (this.size_factor != size_factor) {
			this.size_factor = size_factor;
			draw();
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
	 * Get the scroll-able component. Use this for a better user experience.
	 * Anyway, this is optional. You can always
	 * {@linkplain JComponent#add(Component) add} directly this object.
	 * 
	 * @return a JScrollPane object of the diagram
	 */
	public Component getScrollable() {
		if (scrollpane == null) {
			scrollpane = new JScrollPane(this);
			// the diagram is usually big
			// we want it scrolls a little bit faster
			int unitIncrement = 15;
			scrollpane.getVerticalScrollBar().setUnitIncrement(unitIncrement);
			scrollpane.getHorizontalScrollBar().setUnitIncrement(unitIncrement);
			// mouse wheel is for zooming
			scrollpane.setWheelScrollingEnabled(false);
			// disable keyboard scrolling
			InputMap im = scrollpane
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			im.put(KeyStroke.getKeyStroke("UP"), "none");
			im.put(KeyStroke.getKeyStroke("DOWN"), "none");
			im.put(KeyStroke.getKeyStroke("LEFT"), "none");
			im.put(KeyStroke.getKeyStroke("RIGHT"), "none");
		}

		return scrollpane;
	}

	/**
	 * Gets all structures in the diagram.
	 * 
	 * @return array containing structures in the diagram
	 */
	public Structure[] getStructures() {
		Structure[] array = structures.toArray(new Structure[0]);
		Arrays.sort(array, new StructureNameComparator());
		return array;
	}

	public float getSizeFactor() {
		return size_factor;
	}

	/**
	 * Calculates the bound of the component. The result is relative to the
	 * diagram itself (bypass all parents if any)
	 * 
	 * @param c
	 *            the component. Should be in the components tree
	 * @return the found bounds
	 */
	private Rectangle getBoundsFor(Component c) {
		if (c == null) {
			return new Rectangle();
		}

		Rectangle bounds = c.getBounds();

		while (c.getParent() != null) {
			c = c.getParent();
			if (c == this) {
				break;
			}
			Rectangle bigger_bounce = c.getBounds();
			bounds.setLocation(bigger_bounce.x + bounds.x, bigger_bounce.y
					+ bounds.y);
		}

		return bounds;
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
		synchronized (built) {
			Iterator<Entry<Structure, Component>> entries = built.entrySet()
					.iterator();
			while (entries.hasNext()) {
				Entry<Structure, Component> entry = entries.next();
				Structure entry_structure = entry.getKey();
				Component entry_component = entry.getValue();

				boundsCache.put(entry_structure, getBoundsFor(entry_component));
			}
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

		if (boundsCache != null && structure != null) {
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
	 * @see #paint(Graphics)
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
	 * @see #saveImage(ImageObserver)
	 */
	public void startClipping(ImageObserver observer) {
		this.observer = observer;
		image_rect = new Rectangle();
	}

	@Override
	public Component add(Component comp) {
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
		return super.add(comp);
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
				synchronized (structures) {
					structures.add(structure);
				}
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
			synchronized (structures) {
				structures.remove(structure);
			}
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
		// TODO: Need to find a better way
		Iterator<Structure> itr = new LinkedList<Structure>(structures)
				.iterator();
		while (itr.hasNext()) {
			itr.next().dispose();
		}
		structures.clear();
		setAutoDrawing(true);
		draw();
	}

	/**
	 * Tries its best to bring the requested structure into the viewport
	 * 
	 * @param structure
	 *            the structure needs bringing to visible area
	 * @param always
	 *            set this to true to change the view port all the time.
	 *            Otherwise, it will only be changed it the structure is not
	 *            visible
	 */
	public void ensureStructureIsVisible(Structure structure, boolean always) {
		Rectangle rect = getBoundsFor(structure);

		if (rect != null) {
			Rectangle visible = getVisibleRect();

			if (!always) {
				// check if the structure is not visible before doing anything
				if (rect.x > visible.x
						&& rect.x + rect.width < visible.x + visible.width
						&& rect.y > visible.y
						&& rect.y + rect.height < visible.y + visible.height) {
					// it's still visible, keep the current view port
					return;
				}
			}

			Point structureLocation = rect.getLocation();
			Dimension structureSize = rect.getSize();
			Dimension visibleSize = visible.getSize();
			int x, y;
			if (structureSize.width < visibleSize.width) {
				x = structureLocation.x
						- (visibleSize.width - structureSize.width) / 2;
			} else {
				x = structureLocation.x;
			}
			if (structureSize.height < visibleSize.height) {
				y = structureLocation.y
						- (visibleSize.height - structureSize.height) / 2;
			} else {
				y = structureLocation.y;
			}
			rect.setLocation(x, y);
			rect.setSize(visible.getSize());
			scrollRectToVisible(rect);
		}
	}

	/**
	 * A shortcut to {@link #ensureStructureIsVisible(Structure, boolean)} with
	 * always set to true
	 * 
	 * @param structure
	 *            the target structure
	 */
	public void ensureStructureIsVisible(Structure structure) {
		ensureStructureIsVisible(structure, true);
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

		synchronized (relationships) {
			Iterator<Relationship> itr = relationships.iterator();
			while (itr.hasNext()) {
				Relationship relationship = itr.next();
				if (onFocusStructure == null
						|| relationship.getFrom() == onFocusStructure) {
					relationship.draw(g, size_factor);
				}
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
					System.err.println("Copy clipping area result: " + result);
				}

				image_rect = null;
			}

			image = null;

			synchronized (observer) {
				if (observer != null) {
					// notify the observer so it can do the job
					boolean result = observer.imageUpdate(painted,
							ImageObserver.ALLBITS, 0, 0, 0, 0);
					observer = null;

					if (Diagram.debugging) {
						System.err.println("Trigger the observer result: "
								+ result);
					}
				} else {
					// no observer?
					// what a waste of painting procedure!
					if (Diagram.debugging) {
						System.err.println("No observer registered!!!!");
					}
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

		synchronized (structures) {
			synchronized (relationships) {
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

						relationships.add(new GeneralizationRelationship(this,
								s, container));
					}

					// look for 0..many Relationships
					Structure[] children = s.getChildren();
					for (int j = 0, k = children.length; j < k; j++) {
						Structure[] types = children[j].getTypeAsStructure();
						for (int l = 0, m = types.length; l < m; l++) {
							relationships.add(new MultiplicityRelationship(
									this, s, types[l]));
						}
					}

					i++;
				}
			}
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

		// remove all built components
		removeAll();
		built = new Hashtable<Structure, Component>();
		Dimension diagram_size = new Dimension();
		int gap_horizontal = (int) (cfg_gap_horizontal * size_factor);
		int gap_vertical = (int) (cfg_gap_vertical * size_factor);

		// start building all components
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
						Dimension d = c.getPreferredSize();
						c.setLocation(diagram_size.width + gap_horizontal,
								0 + gap_vertical);
						diagram_size.width += d.width + 2 * gap_horizontal;
						diagram_size.height = Math.max(diagram_size.height,
								d.height + 2 * gap_vertical);
					}
				}
			}
		}

		setPreferredSize(diagram_size);
		setSize(diagram_size);

		validate();
		repaint();

		if (Diagram.debugging) {
			System.err.println("Diagram drawing procedure completed");
		}
	}

	private Container build(int i) {
		Container container = null;
		Structure structure = structures.get(i);

		if (built.get(structure) == null) {
			// this structure hasn't been built
			// build it now
			Component component = new DiagramStructureGroup(structure,
					new Dimension((int) (cfg_gap_vertical * size_factor),
							(int) (cfg_gap_horizontal * size_factor)),
					size_factor);
			if (component instanceof Container) {
				container = (Container) component;
				synchronized (built) {
					built.put(structure, container);
				}
				add(container);

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
								Container c = build(j);
								if (c != null) {
									container.add(c);
								}
							}
						}
					}
				}
			}
		}

		return container;
	}

	/**
	 * Adds an object to this structure listeners list
	 * 
	 * @param listener
	 *            the object who needs to listen to this
	 */
	synchronized public void addStructureListener(StructureListener listener) {
		if (listeners == null) {
			listeners = new Vector<StructureListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes an object from this structure listeners list
	 * 
	 * @param listener
	 *            the object who is listening to this
	 */
	synchronized public void removeStructureListener(StructureListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Triggers the changed event
	 * 
	 * @param structure
	 *            the structure involved in the event
	 */
	@SuppressWarnings("unchecked")
	protected void fireChanged(Structure structure) {
		if (listeners != null && listeners.size() > 0) {
			StructureEvent e = new StructureEvent(structure,
					StructureEvent.CHILDREN);

			Vector<StructureListener> targets;
			synchronized (this) {
				targets = (Vector<StructureListener>) listeners.clone();
			}

			for (int i = 0; i < targets.size(); i++) {
				StructureListener listener = targets.elementAt(i);
				listener.structureChanged(e);
			}
		}
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

		fireChanged((Structure) e.getSource());
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
		if (e.getSource() == this) {
			// something here?
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				flag_drag_moving = e.getLocationOnScreen();
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		} else {
			// start moving component
			if (e.getButton() == MouseEvent.BUTTON1) {
				Object s = e.getSource();
				if (s instanceof DiagramStructureGroup) {
					lastPressedComponent = null;
					pressedComponent = (DiagramStructureGroup) s;
					pressedLocation = e.getLocationOnScreen();
					requestFocus(); // prepare to use arrow keys later
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == this) {
			if (image_rect != null) {
				// mouse released and we are in the middle of clipping process
				// send a saving image request now
				saveImage(observer);
			}

			if (flag_drag_moving != null) {
				flag_drag_moving = null;
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		} else {
			// moving component finished
			lastPressedComponent = pressedComponent;
			pressedComponent = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getSource() == this) {
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
					image_rect.setBounds(Math.min(x1, x2), Math.min(y1, y2),
							Math.abs(x2 - x1), Math.abs((y2 - y1)));
				}
				// and display the clipping area now
				repaint();

				if (Diagram.debugging) {
					System.err.println("Clipping. Location: " + image_rect.x
							+ "," + image_rect.y + ". Size: "
							+ image_rect.width + "x" + image_rect.height);
				}
			} else if (flag_drag_moving != null) {
				// dragging-moving around
				Point newP = e.getLocationOnScreen();
				int dx = newP.x - flag_drag_moving.x;
				int dy = newP.y - flag_drag_moving.y;
				// revert to make it more... natural
				dx *= -1;
				dy *= -1;
				if (scrollpane != null) {
					scrollpane.getHorizontalScrollBar()
							.setValue(
									scrollpane.getHorizontalScrollBar()
											.getValue()
											+ dx);
					scrollpane.getVerticalScrollBar().setValue(
							scrollpane.getVerticalScrollBar().getValue() + dy);

					if (Diagram.debugging) {
						System.err.println("Moving around. "
								+ "Horizontal: "
								+ scrollpane.getHorizontalScrollBar()
										.getValue() + ". Vertical: "
								+ scrollpane.getVerticalScrollBar().getValue());
					}
				} else {
					if (Diagram.debugging) {
						System.err.println("Moving arround. "
								+ "FAIL because of no scroll bar");
					}
				}
				flag_drag_moving = newP;
			}
		} else {
			if (pressedComponent != null) {
				// moving component
				Point oldP = pressedLocation;
				Point newP = e.getLocationOnScreen();
				Point oldL = pressedComponent.getLocationOnScreen();
				pressedComponent.setLocationRelative(newP.x - oldP.x, newP.y
						- oldP.y);
				Point newL = pressedComponent.getLocationOnScreen();
				pressedLocation.setLocation(oldP.x + newL.x - oldL.x, oldP.y
						+ newL.y - oldL.y);

				Rectangle old_v = getVisibleRect();
				scrollRectToVisible(pressedComponent.getBounds());
				Rectangle new_v = getVisibleRect();
				if (!new_v.equals(old_v)) {
					// we scrolled a little bit
					// update the location
					pressedLocation.setLocation(pressedLocation.x - new_v.x
							+ old_v.x, pressedLocation.y - new_v.y + old_v.y);
				}

				repaint();

				if (Diagram.debugging) {
					System.err.println("Moving component: " + pressedComponent
							+ ".\nNew location: "
							+ pressedComponent.getLocation().x + ","
							+ pressedComponent.getLocation().y);
				}
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int r = e.getWheelRotation();
		float dsf;
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			// unit scrolling
			// jump small step each
			dsf = 0.1f;
		} else {
			// block scrolling
			// should jump larger step
			dsf = 0.5f;
		}
		dsf *= -r;

		// calculate the zooming point
		Dimension old_s = getPreferredSize();
		if (old_s.width == 0 || old_s.height == 0) {
			return;
		}
		Point old_p = e.getPoint();
		Rectangle old_v = getVisibleRect();
		double xp = (double) old_p.x / old_s.width;
		double yp = (double) old_p.y / old_s.height;
		int dx = old_p.x - old_v.x;
		int dy = old_p.y - old_v.y;

		// resize
		setSizeFactor(getSizeFactor() + dsf);

		// make sure the zooming point is near the mouse
		Dimension new_s = getPreferredSize();
		if (new_s.width == 0 || new_s.height == 0) {
			return;
		}
		Point new_p = new Point((int) (xp * new_s.width),
				(int) (yp * new_s.height));
		Rectangle new_v = new Rectangle(new Point(new_p.x - dx, new_p.y - dy),
				old_v.getSize());
		scrollRectToVisible(new_v);

		if (Diagram.debugging) {
			System.err.println("Middle wheel zooming. "
					+ "Coordinate percents: " + xp + ", " + yp);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int kc = e.getKeyCode();

		if (lastPressedComponent != null) {
			int dx = 0;
			int dy = 0;

			switch (kc) {
			case KeyEvent.VK_UP:
				dy = -1;
				break;
			case KeyEvent.VK_DOWN:
				dy = 1;
				break;
			case KeyEvent.VK_LEFT:
				dx = -1;
				break;
			case KeyEvent.VK_RIGHT:
				dx = 1;
				break;
			}

			if (dx != 0 || dy != 0) {
				int step;
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
					// holding CTRL
					step = 5;
				} else {
					step = 15;
				}
				lastPressedComponent.setLocationRelative(step * dx, step * dy);

				ensureStructureIsVisible(lastPressedComponent.getStructure(),
						false);

				if (Diagram.debugging) {
					System.err.println("Moving component: "
							+ lastPressedComponent + "\nNew location: "
							+ lastPressedComponent.getLocation().x + ","
							+ lastPressedComponent.getLocation().y);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}