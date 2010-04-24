package com.tranvietson.uml;

import java.util.LinkedList;
import java.util.List;

/**
 * A method (for {@link ClassStructure classes} and {@link InterfaceStructure interfaces})
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class MethodStructure extends Structure {
	private List<ArgumentStructure> arguments = new LinkedList<ArgumentStructure>();
	
	/**
	 * Method uses type
	 * Allowed modifiers: abstract final public protected private static
	 */
	void config() {
		structure_name = "Method";
		use_type = true;
		allowed_modifiers = new String[]{
				"abstract", "final"
				,"public", "protected", "private"
				,"static"
		};
	}
	
	/**
	 * Constructor with method name and type
	 * @param name
	 * @param type
	 * @throws StructureException
	 */
	public MethodStructure(String name,String type) throws StructureException {
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
	public MethodStructure(String name,String type,String modifier) throws StructureException {
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
	public MethodStructure(String name,String type,String modifiers[]) throws StructureException {
		setName(name);
		setType(type);
		for (int i = 0; i < modifiers.length; i++) setModifier(modifiers[i]);
	}
	
	/**
	 * Adds an argument and listens to it
	 * @param a
	 * @throws StructureException if an alike argument has already been added
	 */
	public void add(ArgumentStructure a) throws StructureException {
		for (int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i).isAlike(a)) {
				throw new StructureException(this + " has already had the argument \"" + arguments.get(i) + "\"");
			}
		}
		arguments.add(a);
		super.add(a);
	}
	
	/**
	 * Returns the method string with its' arguments
	 */
	public String toString() {
		String str = "";
		
		for(int i = 0; i < arguments.size(); i++) {
			if (str.length() > 0) str += ", ";
			str += arguments.get(i);
		}
		
		str = getType() + " " + getName() + "(" + str + ")";
		
		return str;
	}
	
	/**
	 * 
	 * @return method arguments
	 */
	public ArgumentStructure[] getArguments() {
		return arguments.toArray(new ArgumentStructure[0]);
	}

	/**
	 * Checks if this method is alike with the other method.
	 * @param m the other method
	 * @return true if the 2 are alike
	 */
	public boolean isAlike(MethodStructure m) {
		if (this.name == m.name && this.arguments.size() == m.arguments.size()) {
			for (int i = 0; i < this.arguments.size(); i++) {
				if (this.arguments.get(i).isAlike(m.arguments.get(i)) == false) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
