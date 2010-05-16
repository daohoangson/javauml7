package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;
/**
 * @(#)PropertyForm.java
 *      Create properties for class
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class PropertyForm extends StructureForm {
	private static final long serialVersionUID = -1297394168720166300L;
	private Structure container;
	/**
	 * PropertyForm constructor
	 * @param owner, container
	 */
	public PropertyForm(Window owner, Structure container) {
		super(owner, "Adding new Property for " + container, true,
				new String[] { "public", "protected", "private" }, true);

		this.container = container;
	}

	@Override
	public Structure __submit() throws StructureException {
		Property newProperty = new Property(txt_name.getText(), txt_type
				.getText());
		if (visibility.length() > 0) {
			newProperty.setModifier(visibility);
		}
		if (scope != null) {
			newProperty.setModifier(scope);
		}

		container.add(newProperty);

		return newProperty;
	}
}
