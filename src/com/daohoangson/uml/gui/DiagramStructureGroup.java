package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;

/**
 * Grouping for several {@linkplain Structure structure}s
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class DiagramStructureGroup extends JPanel implements DropTargetListener {
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
	 * The original (foreground) color of the panel
	 */
	private Color original_color;
	/**
	 * The color when the panel has something dragging by
	 */
	private Color cfg_hover_color = Color.red;
	/**
	 * The width of border for structure's component
	 */
	private int cfg_border_width = 5;
	private Dimension block_size;

	DiagramStructureGroup(Structure structure, Component head,
			Dimension block_size) {
		super();

		this.structure = structure;
		this.head = head;
		this.block_size = block_size;
		new DropTarget(this, this);

		original_color = getForeground();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		setBorder(getForeground());

		add(head);
	}

	@Override
	public String toString() {
		return "DnDPanel of " + head;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();

		d.width = (int) (Math.ceil((double) d.width / block_size.width) * block_size.width);
		d.height = (int) (Math.ceil((double) d.height / block_size.height) * block_size.height);

		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	Component getHead() {
		return head;
	}

	@Override
	public Component add(Component comp) {
		try {
			DiagramStructureName label = (DiagramStructureName) comp;
			if (property_first == null
					&& label.getStructureName().equals("Property")) {
				property_first = comp;
			}
			if (method_first == null
					&& label.getStructureName().equals("Method")) {
				method_first = comp;
			}
		} catch (ClassCastException e) {
			// oops
		}

		return super.add(comp);
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (head != null) {
			head.setForeground(fg);
		}
		setBorder(fg);
	}

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

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		setForeground(cfg_hover_color);
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		setForeground(original_color);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		setForeground(original_color);

		try {
			Structure structure = (Structure) dtde.getTransferable()
					.getTransferData(TransferableStructure.df);

			if (structure.getContainer() != this.structure) {
				if (structure.getContainer() != null
						&& structure.getContainer().getStructureName().equals(
								this.structure.getStructureName())) {
					structure.getContainer().remove(structure);
				}

				this.structure.add(structure);

				dtde.acceptDrop(DnDConstants.ACTION_MOVE);
				return;
			}
		} catch (UnsupportedFlavorException e) {
			System.err.println("Ewwww. This is not well tasted!");
		} catch (StructureException e) {
			System.err.println(e);
		} catch (Exception e) {
			// skip all other exceptions
		}

		dtde.rejectDrop();
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
}