package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.StructureException;

public class ArgumentForm extends StructureForm {
	private static final long serialVersionUID = 1887392761961369237L;
	private Structure container;

	public ArgumentForm(Window owner, Structure container) {
		super(owner, "Adding Argument for " + container, true, null, false);

		this.container = container;
	}

	@Override
	public Structure __submit() throws StructureException {
		Argument newArgument = new Argument(txt_name.getText(), txt_type
				.getText());
		if (visibility.length() > 0) {
			newArgument.setModifier(visibility);
		}
		if (scope != null) {
			newArgument.setModifier(scope);
		}

		container.add(newArgument);

		return newArgument;
	}
}
