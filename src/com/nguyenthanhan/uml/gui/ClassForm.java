package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

public class ClassForm extends StructureForm {
	private static final long serialVersionUID = 8063825239295045L;
	private Diagram diagram;

	public ClassForm(Window owner, Diagram diagram) {
		super(owner, "Adding new Class", false, new String[] { "public" },
				false);

		this.diagram = diagram;
	}

	@Override
	public Structure __submit() throws StructureException {
		Class newClass = new Class(txt_name.getText());
		if (visibility.length() > 0) {
			newClass.setModifier(visibility);
		}

		diagram.add(newClass);

		return newClass;
	}
}
