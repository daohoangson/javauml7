package com.nguyenthanhan.uml.gui;

import java.awt.event.MouseEvent;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.StructureException;

public class MethodForm extends StructureForm{
	private static final long serialVersionUID = 6957222110006867837L;
	private Structure container;
	
	public MethodForm(Structure container) {
		super(true, true, true);
		
		this.container = container;
		
		setTitle("Adding new Method for " + container);
	}
	
	public void mouseClicked(MouseEvent e) {
		try {
			Method newMethod = new Method(txt_name.getText(), txt_type.getText());
			if (visibility.length() > 0) newMethod.setModifier(visibility);
			if (scope != null) newMethod.setModifier(scope);
			
			container.add(newMethod);
			setVisible(false);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
}
