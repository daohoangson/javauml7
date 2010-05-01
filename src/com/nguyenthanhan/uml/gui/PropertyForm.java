package com.nguyenthanhan.uml.gui;

import java.awt.event.MouseEvent;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

public class PropertyForm extends StructureForm {
	private static final long serialVersionUID = -1297394168720166300L;
	private Structure container;

	public PropertyForm(Structure container) {
		super(true, true, true);
		
		this.container = container;
		
		setTitle("Adding new Property for " + container);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			Property newProperty = new Property(txt_name.getText(),txt_type.getText());
			if (visibility.length() > 0) newProperty.setModifier(visibility);
			if (scope != null) newProperty.setModifier(scope);
			
			container.add(newProperty);
			setVisible(false);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
}
