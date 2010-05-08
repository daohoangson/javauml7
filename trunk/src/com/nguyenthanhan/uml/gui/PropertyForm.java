package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

public class PropertyForm extends StructureForm {
	private static final long serialVersionUID = -1297394168720166300L;
	private Structure container;

	public PropertyForm(Window owner, Structure container) {
		super(owner, "Adding new Property for " + container, true, true, true);

		this.container = container;
		setVisible(true);
	}

	@Override
	public void __submit() throws StructureException {
		Property newProperty = new Property(txt_name.getText(), txt_type
				.getText());
		if (visibility.length() > 0) {
			newProperty.setModifier(visibility);
		}
		if (scope != null) {
			newProperty.setModifier(scope);
		}

		container.add(newProperty);
	}
}
