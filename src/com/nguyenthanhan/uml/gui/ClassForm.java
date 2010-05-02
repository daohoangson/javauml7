package com.nguyenthanhan.uml.gui;

import java.awt.Frame;

import com.daohoangson.uml.gui.Diagram;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

public class ClassForm extends StructureForm {
	private static final long serialVersionUID = 8063825239295045L;
	private Diagram diagram;
	
	public ClassForm(Frame owner, Diagram diagram){
		super(owner, "Adding new Class", false, true, false);
		
		this.diagram = diagram;
		setVisible(true);
	}

	@Override
	public void __submit() throws StructureException {
		Class newClass = new Class(txt_name.getText());
		if (visibility.length() > 0) newClass.setModifier(visibility);
		
		diagram.add(newClass);
	}
}
