package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

/**
 * @(#)ClassForm.java Create a simple class
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class ClassForm extends StructureForm {
	private static final long serialVersionUID = 8063825239295045L;
	private Diagram diagram;

	/**
	 * ClassForm constructor
	 * 
	 * @param owner
	 *            , diagram
	 */
	public ClassForm(Window owner, Diagram diagram) {
		super(owner, "Adding new Class", new Class());

		this.diagram = diagram;
	}

	@Override
	protected Structure __submit(Structure prototype) throws StructureException {
		Structure newClass = super.__submit(prototype);

		diagram.add(newClass);

		return newClass;
	}
}
