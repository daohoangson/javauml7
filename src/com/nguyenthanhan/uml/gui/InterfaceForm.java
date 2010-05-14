package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.StructureException;

public class InterfaceForm extends StructureForm {
	private static final long serialVersionUID = -789691956735803020L;
	private Diagram diagram;

	public InterfaceForm(Window owner, Diagram diagram) {
		super(owner, "Adding new Interface", false, true, false);

		this.diagram = diagram;
	}

	@Override
	public Structure __submit() throws StructureException {
		Interface newInterface = new Interface(txt_name.getText());
		if (visibility.length() > 0) {
			newInterface.setModifier(visibility);
		}

		diagram.add(newInterface);

		return newInterface;
	}

}
