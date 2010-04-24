package com.tranvietson.uml;

/**
 * A property (for {@link ClassStructure classes} and {@link InterfaceStructure interfaces})
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class PropertyStructure extends Structure {
	/**
	 * Property uses type.
	 * Allowed modifiers: final public protected private static
	 */
	void config() {
		structure_name = "Property";
		use_type = true;
		allowed_modifiers = new String[]{
				"final"
				,"public", "protected", "private"
				,"static"
		};
	}
	
	/**
	 * Constructor with property name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public PropertyStructure(String name,String type) throws StructureException {
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
	public PropertyStructure(String name,String type,String modifier) throws StructureException {
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
	public PropertyStructure(String name,String type,String modifiers[]) throws StructureException {
		setName(name);
		setType(type);
		for (int i = 0; i < modifiers.length; i++) setModifier(modifiers[i]);
	}
	
	/**
	 * Checks if this property is alike with the other property
	 * @param p the other property
	 * @return true if the 2 are alike
	 */
	public boolean isAlike(PropertyStructure p) {
		return (getName() == p.getName());
	}
}
