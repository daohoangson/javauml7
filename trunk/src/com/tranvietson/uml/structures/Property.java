package com.tranvietson.uml.structures;

import com.daohoangson.uml.structures.Class;
import com.daohoangson.uml.structures.Interface;
import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureException;

/**
 * A property (for {@link Class classes} and {@link Interface interfaces})
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class Property extends Structure {
	/**
	 * Property uses type.
	 */
	protected void config() {
		cfg_use_type = true;
		cfg_use_visibility = true;
		cfg_use_scope = true;
		cfg_container_structures = new String[] {
				"Class"
				,"Interface"
		};
	}
	
	/**
	 * Constructor with property name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public Property(String name,String type) throws StructureException {
		setName(name);
		setType(type);
	}
	
	/**
	 * Constructor with property name, type and a modifier
	 * @param name
	 * @param type
	 * @param modifier
	 * @throws StructureException
	 */
	public Property(String name,String type,String modifier) throws StructureException {
		setName(name);
		setType(type);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with property name, type and an array of modifiers
	 * @param name
	 * @param type
	 * @param modifiers
	 * @throws StructureException
	 */
	public Property(String name,String type,String modifiers[]) throws StructureException {
		setName(name);
		setType(type);
		for (int i = 0, n = modifiers.length; i < n; i++)
			setModifier(modifiers[i]);
	}
	
	@Override
	public boolean checkIsAlike(Structure that) {
		return that.getStructureName().equals(getStructureName())
			&& getName().equals(that.getName());
	}
}
