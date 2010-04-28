package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JLabel;

import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureException;
import com.daohoangson.uml.structures.TransferableStructure;


public class DnDLabel extends JLabel implements DropTargetListener,
		DragGestureListener, DragSourceListener {
	private static final long serialVersionUID = -6281032125570028093L;
	private Structure structure;
	@SuppressWarnings("unused")
	private Diagram diagram;
	@SuppressWarnings("unused")
	private DropTarget dt;
	private DragSource dragSource;
	
	private Color originalForeground;
	private Color dragForeground = Color.blue;
	private Color hoverForeground = Color.red;
	
	public DnDLabel(Structure structure, Diagram diagram) {
		super(structure.toString());
		
		this.structure = structure;
		this.diagram = diagram;
		
		if (structure.checkHasChildren()) {
			dt = new DropTarget(this,this);
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		setForeground(dragForeground);
		dragSource.startDrag(dge
				, DragSource.DefaultMoveDrop
				, new TransferableStructure(structure)
				, this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		setForeground(originalForeground);
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
