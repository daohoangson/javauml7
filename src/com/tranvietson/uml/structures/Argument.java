package com.tranvietson.uml.structures;

import com.daohoangson.uml.structures.Structure;

/**
 * An argument (for {@link Method methods})
 * @author Tran Viet Son
 * @version 1.0
 * @see Method
 *
 */
public class Argument extends Structure {
	/**
	 * Argument uses type and accepts no modifier
	 */
	protected void config() {
		cfg_use_type = true;
	}
	
	/**
	 * Constructor with name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public Argument(String name,String type) throws StructureException {
		setName(name);
		setType(type);
	}
	
	@Override
	public boolean checkIsAlike(Structure that) {
		return that.getStructureName().equals(getStructureName())
			&& getName().equals(that.getName());
	}
}
