package com.nguyenthanhan.uml.gui;

import java.awt.Window;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.StructureException;

/**
 * @(#)MethodForm.java Create methods for class or interface
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class MethodForm extends StructureForm {
	private static final long serialVersionUID = 6957222110006867837L;
	private Structure container;

	/**
	 * MethodForm constructor
	 * 
	 * @param owner
	 *            , container
	 */
	public MethodForm(Window owner, Structure container) {
		super(owner, "Adding new Method for " + container.getName(),
				new Method());

		this.container = container;
	}

	@Override
	protected Structure __submit(Structure prototype) throws StructureException {
		// rewritten this to support constructor!
		Structure newMethod = null;

		try {
			newMethod = prototype.getClass().newInstance();
		} catch (Exception e) {
			throw new StructureException("Unable to create new "
					+ prototype.getStructureName());
		}

		newMethod.setName(txt_name.getText());
		if (newMethod.getName().equals(container.getName())) {
			// this is a constructor
			newMethod.setType(null);
		} else {
			// this is just a normal method
			newMethod.setType(txt_type.getText());
		}
		if (sel_visibility != null) {
			newMethod.setModifier(sel_visibility);
		}
		if (sel_scope != null) {
			newMethod.setModifier(sel_scope);
		}
		if (sel_abstract != null) {
			newMethod.setModifier(sel_abstract);
		}

		container.add(newMethod);

		return newMethod;
	}
}
