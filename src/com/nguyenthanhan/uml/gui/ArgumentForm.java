package com.nguyenthanhan.uml.gui;

import java.awt.event.MouseEvent;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.StructureException;

public class ArgumentForm extends StructureForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1887392761961369237L;
	private Structure container;
	
	public ArgumentForm(Structure container)
	{
		super(true, false, false);
		setTitle("addArgument");
		this.container = container;
	}
	public void mouseClicked(MouseEvent e) {
		try {
			Argument newArgument = new Argument(txt_name.getText(), txt_type.getText());
			if (visibility.length() > 0) newArgument.setModifier(visibility);
			if (scope != null) newArgument.setModifier(scope);
			
			container.add(newArgument);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
}
