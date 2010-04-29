package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JLabel;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;

/**
 * Drag and Drop Label for {@link Structure}s
 * @author Dao Hoang Son
 * @version 1.0
 *
 */
public class DnDLabel extends JLabel implements DropTargetListener,
		DragGestureListener {
	private static final long serialVersionUID = -6281032125570028093L;
	private Structure structure;
	private DragSource dragSource;
	
	private Color originalForeground;
	private Color dragForeground = Color.blue;
	private Color hoverForeground = Color.red;
	
	public DnDLabel(Structure structure, Diagram diagram) {
		super(structure.toString());
		
		this.structure = structure;
		
		if (structure.checkHasChildren()) {
			new DropTarget(this,this);
			originalForeground = getForeground();
		}
		

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		if (getForeground() != dragForeground)
			setForeground(hoverForeground);
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		if (getForeground() == hoverForeground)
			setForeground(originalForeground);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Check it out if we need to do something here...
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (getForeground() == hoverForeground)
			setForeground(originalForeground);
		
		try {
			Structure structure = (Structure)dtde.getTransferable().getTransferData(TransferableStructure.df);
			
			if (structure.getContainer() != this.structure) {
				if (structure.getContainer() != null
						&& structure.getContainer().getStructureName().equals(this.structure.getStructureName())) {
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
			//skip all other exceptions
		}
		
		dtde.rejectDrop();
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Check it out if we need to do something here...

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		setForeground(dragForeground);
		dragSource.startDrag(dge
				, DragSource.DefaultMoveDrop
				, new TransferableStructure(structure)
				, null);
	}
}
