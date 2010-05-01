package com.nguyenthanhan.uml.gui;

import java.awt.event.MouseEvent;

import com.daohoangson.uml.gui.Diagram;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

public class ClassForm extends StructureForm {
	private static final long serialVersionUID = 8063825239295045L;
	private Diagram diagram;
	
	public ClassForm(Diagram diagram){
		super(false,true,false);
		
		this.diagram = diagram;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			Class newClass = new Class(txt_name.getText());
			if (visibility.length() > 0) newClass.setModifier(visibility);
			
			diagram.add(newClass);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
}
