package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.StructureException;

/**
 * Create a simple interface
 * 
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class InterfaceForm extends StructureForm {
	private static final long serialVersionUID = -789691956735803020L;
	private Diagram diagram;

	/**
	 * InterfaceForm constructor
	 * 
	 * @param owner
	 *            , diagram
	 */
	public InterfaceForm(Window owner, Diagram diagram) {
		super(owner, "Adding new Interface", new Interface());

		this.diagram = diagram;
	}

	@Override
	protected Structure __submit(Structure prototype) throws StructureException {
		Structure newInterface = super.__submit(prototype);

		diagram.add(newInterface);

		return newInterface;
	}

}
