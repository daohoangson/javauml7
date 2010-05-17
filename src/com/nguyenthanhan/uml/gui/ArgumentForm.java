package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.StructureException;

/**
 * @(#)ArgumentForm.java Create arguments for method
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class ArgumentForm extends StructureForm {
	private static final long serialVersionUID = 1887392761961369237L;
	private Structure container;

	/**
	 * ArgumentForm constructor
	 * 
	 * @param owner
	 *            , container
	 */
	public ArgumentForm(Window owner, Structure container) {
		super(owner, "Adding Argument for " + container.getName(),
				new Argument());

		this.container = container;
	}

	@Override
	protected Structure __submit(Structure prototype) throws StructureException {
		Structure newArgument = super.__submit(prototype);

		container.add(newArgument);

		return newArgument;
	}
}
