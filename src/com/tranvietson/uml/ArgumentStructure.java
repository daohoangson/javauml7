package com.tranvietson.uml;

/**
 * An argument (for {@link MethodStructure methods})
 * @author Tran Viet Son
 * @version 1.0
 * @see MethodStructure
 *
 */
public class ArgumentStructure extends Structure {
	/**
	 * Argument uses type and accepts no modifier
	 */
	void config() {
		structure_name = "Argument";
		use_type = true;
		allowed_modifiers = new String[0];
	}
	
	/**
	 * Constructor with name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public ArgumentStructure(String name,String type) throws StructureException {
		setName(name);
		setType(type);
	}

	/**
	 * Checks if this argument is alike with another argument
	 * @param a the other argument
	 * @return true if the 2 are alike
	 */
	public boolean isAlike(ArgumentStructure a) {
		return (getName() == a.getName());
	}
}
