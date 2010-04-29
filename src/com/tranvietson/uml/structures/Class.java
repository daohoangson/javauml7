package com.tranvietson.uml.structures;

import java.util.LinkedList;
import java.util.List;

import com.daohoangson.uml.structures.Structure;


/**
 * A class
 * @author Tran Viet Son
 * @version 1.1
 *
 */
public class Class extends Structure {
	protected List<Interface> interfaces = new LinkedList<Interface>();
	
	/**
	 * Class doesn't use type.
	 */
	protected void config() {
		cfg_unique_globally = true;
		cfg_use_visibility = true;
		cfg_use_scope = true;
		cfg_container_structures = new String[] {
				"Class"
		};
		cfg_parent_structures = new String[] {
				"Interface"
		};
		cfg_child_structures = new String[]{
				"Property"
				,"Method"
		};
		cfg_hide_visibility = true;
	}
	
	/**
	 * Constructor with class name
	 * @param name
	 * @throws StructureException
	 */
	public Class(String name) throws StructureException {
		setName(name);
	}
	
	/**
	 * Constructor with class name and one modifier
	 * @param name
	 * @param modifier
	 * @throws StructureException
	 */
	public Class(String name, String modifier) throws StructureException {
		setName(name);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with class name and an array of modifiers
	 * @param name
	 * @param modifiers
	 * @throws StructureException
	 */
	public Class(String name, String modifiers[]) throws StructureException {
		setName(name);
		for (int i = 0, n = modifiers.length; i < n; i++)
			setModifier(modifiers[i]);
	}

	@Override
	public boolean checkIsAlike(Structure that) {
		return that.getStructureName().equals(getStructureName())
			&& getName().equals(that.getName());
	}
}
