package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.StructureException;

public class MethodForm extends StructureForm {
	private static final long serialVersionUID = 6957222110006867837L;
	private Structure container;

	public MethodForm(Window owner, Structure container) {
		super(owner, "Adding new Method for " + container, true, new String[] {
				"public", "protected", "private" }, true);

		this.container = container;
	}

	@Override
	public Structure __submit() throws StructureException {
		Method newMethod;
		if (container.getName().equals(txt_name.getText())) {
			// constructor
			newMethod = new Method(txt_name.getText(), null);
		} else {
			// normal method
			newMethod = new Method(txt_name.getText(), txt_type.getText());
		}
		if (visibility.length() > 0) {
			newMethod.setModifier(visibility);
		}
		if (scope != null) {
			newMethod.setModifier(scope);
		}

		container.add(newMethod);

		return newMethod;
	}
}
