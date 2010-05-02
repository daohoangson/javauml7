package com.nguyenthanhan.uml.gui;

import java.awt.Frame;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.StructureException;

public class MethodForm extends StructureForm{
	private static final long serialVersionUID = 6957222110006867837L;
	private Structure container;
	
	public MethodForm(Frame owner, Structure container) {
		super(owner, "Adding new Method for " + container, true, true, true);
		
		this.container = container;
		setVisible(true);
	}
	
	public void __submit() throws StructureException {
		Method newMethod = new Method(txt_name.getText(), txt_type.getText());
		if (visibility.length() > 0) newMethod.setModifier(visibility);
		if (scope != null) newMethod.setModifier(scope);
		
		container.add(newMethod);
	}
}
