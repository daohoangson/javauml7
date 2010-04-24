package com.tranvietson.uml;

import java.util.LinkedList;
import java.util.List;

/**
 * A class
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class ClassStructure extends InterfaceStructure {
	private ClassStructure parent = null;
	private List<InterfaceStructure> interfaces = new LinkedList<InterfaceStructure>();
	
	/**
	 * Class doesn't use type.
	 * Allowed modifiers: abstract final public protected private
	 */
	void config() {
		structure_name = "Class";
		use_type = false;
		allowed_modifiers = new String[]{
				"abstract", "final"
				,"public", "protected", "private"
		};
		unique_globally = true;
		has_parents = true;
		has_children = true;
	}
	
	public ClassStructure() { }
	
	/**
	 * Constructor with class name
	 * @param name
	 * @throws StructureException
	 */
	public ClassStructure(String name) throws StructureException {
		setName(name);
	}
	
	/**
	 * Constructor with class name and one modifier
	 * @param name
	 * @param modifier
	 * @throws StructureException
	 */
	public ClassStructure(String name, String modifier) throws StructureException {
		setName(name);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with class name and an array of modifiers
	 * @param name
	 * @param modifiers
	 * @throws StructureException
	 */
	public ClassStructure(String name, String modifiers[]) throws StructureException {
		setName(name);
		for (int i = 0; i < modifiers.length; i++) setModifier(modifiers[i]);
	}
	
	/**
	 * Assigns parent class and listens to it
	 * @param parent must be another class
	 * @throws StructureException 
	 */
	public void assign(ClassStructure parent) throws StructureException {
		if (parent == this) throw new StructureException(getName() + " can not extend itself");
		this.parent = parent;
		super.assign(parent);
	}
	
	/**
	 * Assigns an interface and listens to it
	 * @param i the interface
	 */
	public void assign(InterfaceStructure i) {
		if (!interfaces.contains(i)) {
			interfaces.add(i);
			super.assign(i);
		}
	}
	
	/**
	 * 
	 * @return parent class
	 */
	public ClassStructure getParent() {
		return parent;
	}
	
	/**
	 * 
	 * @return implemented interfaces
	 */
	public InterfaceStructure[] getInterfaces() {
		return interfaces.toArray(new InterfaceStructure[0]);
	}
}
