package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JLabel;

import com.daohoangson.uml.structures.Structure;

/**
 * Primary component for {@linkplain Structure structure}s
 * 
 * @author Dao Hoang Son
 * @version 1.2
 * 
 */
class DiagramStructureName extends JLabel implements DragGestureListener,
		DragSourceListener {
	private static final long serialVersionUID = -6281032125570028093L;
	/**
	 * The corresponding structure of the label
	 */
	private Structure structure;
	/**
	 * The primary dragging source to support drag functionality
	 */
	private DragSource dragSource;
	/**
	 * The original (foreground) color of the label
	 */
	private Color original_color;
	/**
	 * The color when the label is being dragged
	 */
	private Color cfg_drag_color = Color.blue;

	/**
	 * Constructor. Accepts a structure. Create the label using
	 * <code>JLabel</code> constructor
	 * 
	 * @param structure
	 */
	DiagramStructureName(Structure structure) {
		super(structure.toString());

		this.structure = structure;

		original_color = getForeground();

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	@Override
	public String toString() {
		return "DnDLabel of " + structure;
	}

	/**
	 * Gets the structure in associate with this label
	 * 
	 * @return the structure
	 */
	Structure getStructure() {
		return structure;
	}

	/**
	 * Gets the structure name of the structure
	 * 
	 * @return
	 */
	String getStructureName() {
		return structure.getStructureName();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		setForeground(cfg_drag_color);
		dragSource.startDrag(dge, null, new TransferableStructure(structure),
				this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (getForeground() == cfg_drag_color) {
			setForeground(original_color);
		}
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}
}
