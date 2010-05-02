package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;

class DnDPanel extends JPanel implements DropTargetListener {
	private static final long serialVersionUID = -6330448983826031865L;
	private Structure structure;
	private Component head;
	private Component property_first;
	private Component method_first;
	
	private Color original_color;
	private Color cfg_hover_color = Color.red;
	/**
	 * The width of border for structure's component
	 */
	private int cfg_border_width = 5;

	DnDPanel(Structure structure, Component head) {
		super();
		
		this.structure = structure;
		this.head = head;
		new DropTarget(this,this);
		
		original_color = getForeground();
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setAlignmentX(JComponent.CENTER_ALIGNMENT);
		setBorder(getForeground());
		
		add(head);
	}
	
	public String toString() {
		return "DnDPanel of " + head;
	}
	
	public Component add(Component comp) {
		try {
			DnDLabel label = (DnDLabel) comp;
			if (property_first == null && label.getStructureName().equals("Property")) {
				property_first = comp;
			}
			if (method_first == null && label.getStructureName().equals("Method")) {
				method_first = comp;
			}
		} catch (ClassCastException e) {
			//oops
		}
		
		return super.add(comp);
	}
	
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (head != null) head.setForeground(fg);
		setBorder(fg);
	}

	private void setBorder(Color color) {
		setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(color)
						, BorderFactory.createEmptyBorder(cfg_border_width, cfg_border_width, cfg_border_width, cfg_border_width)
				)
		);
	}
	
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
}
