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
 * Drag and Drop Label for {@link Structure}s
 * @author Dao Hoang Son
 * @version 1.0
 *
 */
class DnDLabel extends JLabel implements DragGestureListener, DragSourceListener {
	private static final long serialVersionUID = -6281032125570028093L;
	private Structure structure;
	private DragSource dragSource;
	
	private Color original_color;
	private Color cfg_drag_color = Color.blue;
	
	DnDLabel(Structure structure) {
		super(structure.toString());
		
		this.structure = structure;
		
		original_color = getForeground();

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}
	
	public String toString() {
		return "DnDLabel of " + structure;
	}
	
	String getStructureName() {
		return structure.getStructureName();
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		setForeground(cfg_drag_color);
		dragSource.startDrag(dge
				, null
				, new TransferableStructure(structure)
				, this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (getForeground() == cfg_drag_color)
			setForeground(original_color);
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
