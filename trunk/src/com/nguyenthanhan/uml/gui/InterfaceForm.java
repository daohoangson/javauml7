
package com.nguyenthanhan.uml.gui;

import java.awt.event.MouseEvent;

import com.daohoangson.uml.gui.Diagram;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.StructureException;

public class InterfaceForm extends StructureForm{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -789691956735803020L;
	private Diagram diagram;
	
	public InterfaceForm(Diagram diagram) {
		super(false, true, false);
		this.diagram = diagram;
		// TODO Auto-generated constructor stub
	}
	
	public void mouseClicked(MouseEvent e) {
		try {
			Interface newInterface = new Interface(txt_name.getText());
			if (visibility.length() > 0) newInterface.setModifier(visibility);
			
			diagram.add(newInterface);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
	
}
