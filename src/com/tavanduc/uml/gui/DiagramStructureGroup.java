package com.tavanduc.uml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.daohoangson.uml.gui.StructureBased;
import com.daohoangson.uml.structures.Structure;

/**
 * Grouping for several {@linkplain Structure structure}s
 * 
 * @author Dao Hoang Son
 * @version 1.2
 * 
 */
public class DiagramStructureGroup extends JPanel implements MouseListener,
		StructureBased {
	private static final long serialVersionUID = -6330448983826031865L;
	/**
	 * The corresponding structure of the panel
	 */
	private Structure structure;
	/**
	 * The heading component for the panel. Usually, it should be a
	 * {@link DiagramStructureName} of the same {@link #structure}
	 */
	private Component head;
	/**
	 * The first property's component
	 */
	private Component property_first;
	/**
	 * The first method's component
	 */
	private Component method_first;
	/**
	 * The width of border for structure's component
	 */
	private int cfg_border_width = 5;
	/**
	 * The incremental block size for the component. The component actual size
	 * will always be a number of times of this block size
	 * 
	 * @see #getPreferredSize()
	 */
	private Dimension block_size;
	/**
	 * Components which get managed by this component
	 */
	private List<DiagramStructureGroup> dependent_components = new LinkedList<DiagramStructureGroup>();
	/**
	 * Hold the last calculated width for dependent components
	 */
	private int dependent_components_width = 0;
	/**
	 * The pseudo location for this component and its dependent components (they
	 * will be center aligned)
	 */
	private Point pseudoLocation;

	/**
	 * Constructor (the one and only)
	 * 
	 * @param structure
	 *            the primary one
	 * @param head
	 *            the heading component
	 * @param block_size
	 *            the block size
	 * 
	 * @see #block_size
	 */
	public DiagramStructureGroup(Structure structure, Dimension block_size,
			float size_factor) {
		super();

		this.structure = structure;
		this.block_size = block_size;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setBorder(getForeground());

		head = new DiagramStructureName(structure, size_factor);
		add(head);

		Structure children[] = structure.getChildren();
		for (int i = 0; i < children.length; i++) {
			add(new DiagramStructureName(children[i], size_factor));
		}
	}

	@Override
	public String toString() {
		return "DnDPanel of " + structure;
	}

	/**
	 * Gets the structure in associate with this component
	 * 
	 * @return the structure
	 */
	@Override
	public Structure getStructure() {
		return structure;
	}

	/**
	 * Gets the structure name of the structure
	 * 
	 * @return the structure name of the structure
	 */
	public String getStructureName() {
		return structure.getStructureName();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		int width = 0;
		int height = 0;

		// resize to make sure children is visible
		setSize(d);

		Iterator<DiagramStructureGroup> itr = dependent_components.iterator();
		while (itr.hasNext()) {
			Dimension dcd = itr.next().getPreferredSize();
			width += dcd.width;
			height = Math.max(height, dcd.height);
		}

		if (width > 0 || height > 0) {
			// there is something
			width += (dependent_components.size() - 1) * block_size.width;
			height += block_size.height;
		}

		dependent_components_width = width; // cache the width of dependent
		// components
		width = Math.max(d.width, width);
		height += d.height;

		return new Dimension(width, height);
	}

	@Override
	public void setLocation(int x, int y) {
		Dimension preferredSize = getPreferredSize();
		Dimension thisSize = getSize();

		// grid-style positioning
		x = (int) (5 * Math.floor((double) x / 5));
		y = (int) (5 * Math.floor((double) y / 5));

		// update our self managed location
		pseudoLocation = new Point(x, y);

		super.setLocation(x + (preferredSize.width - thisSize.width) / 2, y);

		int dcx = pseudoLocation.x
				+ (preferredSize.width - dependent_components_width) / 2;
		int dcy = pseudoLocation.y + thisSize.height + block_size.height;

		Iterator<DiagramStructureGroup> itr = dependent_components.iterator();
		while (itr.hasNext()) {
			DiagramStructureGroup dc = itr.next();
			Dimension dcs = dc.getPreferredSize();

			dc.setLocation(dcx, dcy);

			dcx += dcs.width + block_size.width;
		}
	}

	public void setLocationRelative(int dx, int dy) {
		setLocation(pseudoLocation.x + dx, pseudoLocation.y + dy);
	}

	@Override
	public Component add(Component comp) {
		if (comp instanceof DiagramStructureGroup) {
			dependent_components.add((DiagramStructureGroup) comp);

			// skip adding comp to this component children
			// we will manage it by ourself
			return this;
		} else {
			try {
				DiagramStructureName label = (DiagramStructureName) comp;
				if (property_first == null
						&& label.getStructure().getStructureName().equals(
								"Property")) {
					property_first = comp;
				}
				if (method_first == null
						&& label.getStructure().getStructureName().equals(
								"Method")) {
					method_first = comp;
				}
			} catch (ClassCastException e) {
				// oops
			}

			comp.addMouseListener(this);

			return super.add(comp);
		}
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (head != null) {
			head.setForeground(fg);
		}
		setBorder(fg);
	}

	/**
	 * A shortcut to make the border for this component
	 * 
	 * @param color
	 *            the border's color
	 */
	private void setBorder(Color color) {
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(color), BorderFactory.createEmptyBorder(
				cfg_border_width, cfg_border_width, cfg_border_width,
				cfg_border_width)));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (property_first != null) {
			int py = property_first.getBounds().y;
			g.drawLine(0, py, getWidth(), py);
		}
		if (method_first != null) {
			int my = method_first.getBounds().y;
			g.drawLine(0, my, getWidth(), my);
		}
	}

	/**
	 * Catches mouse events from children components and notify its own
	 * listeners (and make the event looks like it was triggered by this
	 * component)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// update the source of the event to this component
		e.setSource(this);
		super.processMouseEvent(e);
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
		// TODO Auto-generated method stub

	}
}
