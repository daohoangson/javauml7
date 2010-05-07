package com.tranvietson.uml.structures;

import java.util.Arrays;

import com.daohoangson.uml.structures.Structure;

/**
 * A method (for {@link Class classes} and {@link Interface interfaces})
 * 
 * @author Tran Viet Son
 * @version 1.0
 * 
 */
public class Method extends Structure {
	/**
	 * Method uses type
	 */
	@Override
	protected void config() {
		cfg_use_type = true;
		cfg_use_visibility = true;
		cfg_use_scope = true;
		cfg_container_structures = new String[] { "Class", "Interface" };
		cfg_child_structures = new String[] { "Argument" };
		cfg_hide_children = true;
	}

	/**
	 * Constructor with method name and type
	 * 
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public Method(String name, String type) throws StructureException {
		setName(name);
		setType(type);
	}

	/**
	 * Constructor with method name, type and a modifier
	 * 
	 * @param name
	 * @param type
	 * @param modifier
	 * @throws StructureException
	 */
	public Method(String name, String type, String modifier)
			throws StructureException {
		setName(name);
		setType(type);
		setModifier(modifier);
	}

	/**
	 * Constructor with method name, type and an array of modifiers
	 * 
	 * @param name
	 * @param type
	 * @param modifiers
	 * @throws StructureException
	 */
	public Method(String name, String type, String[] modifiers)
			throws StructureException {
		setName(name);
		setType(type);
		for (int i = 0, n = modifiers.length; i < n; i++) {
			setModifier(modifiers[i]);
		}
	}

	@Override
	public boolean setType(String type) throws StructureException {
		if (type == null) {
			cfg_use_type = false;
			return true;
		}

		return super.setType(type);
	}

	/**
	 * Returns the method string with its' arguments
	 */
	@Override
	public String toString() {
		String arguments_str = "";

		Structure arguments[] = getChildren();
		for (int i = 0, n = arguments.length; i < n; i++) {
			if (arguments_str.length() > 0) {
				arguments_str += ", ";
			}
			arguments_str += arguments[i];
		}

		return super.toString("", "(" + arguments_str + ")");
	}

	@Override
	public boolean checkIsAlike(Structure that) {
		if (Structure.debuging) {
			System.err.println("Comparing: " + this + " vs. " + that);
		}

		if (that.getStructureName().equals(getStructureName())
				&& getName().equals(that.getName())
				&& getChildrenCount() == that.getChildrenCount()) {
			Structure this_arguments[] = getChildren();
			Structure that_arguments[] = that.getChildren();
			int n = getChildrenCount();
			String this_types[] = new String[n];
			String that_types[] = new String[n];

			for (int i = 0; i < n; i++) {
				this_types[i] = this_arguments[i].getType();
				that_types[i] = that_arguments[i].getType();
			}

			Arrays.sort(this_types);
			Arrays.sort(that_types);

			for (int i = 0; i < n; i++) {
				if (!this_types[i].equals(that_types[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}
}
