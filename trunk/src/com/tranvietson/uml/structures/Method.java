package com.tranvietson.uml.structures;

import com.daohoangson.uml.structures.Class;
import com.daohoangson.uml.structures.Interface;
import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureException;

/**
 * A method (for {@link Class classes} and {@link Interface interfaces})
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class Method extends Structure {
	/**
	 * Method uses type
	 */
	protected void config() {
		cfg_use_type = true;
		cfg_use_visibility = true;
		cfg_use_scope = true;
		cfg_container_structures = new String[] {
				"Class"
				,"Interface"
		};
		cfg_child_structures = new String[]{
				"Argument"
		};
		cfg_hide_children = true;
	}
	
	/**
	 * Constructor with method name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public Method(String name,String type) throws StructureException {
		setName(name);
		setType(type);
	}
	
	/**
	 * Constructor with method name, type and a modifier
	 * @param name
	 * @param type
	 * @param modifier
	 * @throws StructureException
	 */
	public Method(String name,String type,String modifier) throws StructureException {
		setName(name);
		setType(type);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with method name, type and an array of modifiers
	 * @param name
	 * @param type
	 * @param modifiers
	 * @throws StructureException
	 */
	public Method(String name,String type,String modifiers[]) throws StructureException {
		setName(name);
		setType(type);
		for (int i = 0, n = modifiers.length; i < n; i++)
			setModifier(modifiers[i]);
	}
	
	/**
	 * Returns the method string with its' arguments
	 */
	public String toString() {
		String str = super.toString();
		String arguments_str = "";
		
		Structure arguments[] = getChildren();
		for(int i = 0, n = arguments.length; i < n; i++) {
			if (arguments_str.length() > 0) arguments_str += ", ";
			arguments_str += arguments[i];
		}
		
		str += "(" + arguments_str + ")";
		
		return str;
	}

	@Override
	public boolean checkIsAlike(Structure that) {
		if (that.getStructureName().equals(getStructureName()) 
				&& getName().equals(that.getName())
				&& getChildrenCount() == that.getChildrenCount()) {
			Structure this_arguments[] = getChildren();
			for (int i = 0, n = this_arguments.length; i < n; i++) {
				if (that.checkHasChildLike(this_arguments[i]))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
}
