package com.tranvietson.uml;

import java.util.LinkedList;
import java.util.List;

/**
 * An interface
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class InterfaceStructure extends Structure {
	private InterfaceStructure parent = null;
	private List<PropertyStructure> properties = new LinkedList<PropertyStructure>();
	private List<MethodStructure> methods = new LinkedList<MethodStructure>();
	
	/**
	 * Interface doesn't use type.
	 * Allowed modifiers: abstract public protected private
	 */
	void config() {
		structure_name = "Interface";
		use_type = false;
		allowed_modifiers = new String[]{
				"abstract"
				,"public", "protected", "private"
		};
		unique_globally = true;
	}
	
	public InterfaceStructure() { }

	/**
	 * Constructor with interface name
	 * @param name
	 * @throws StructureException
	 */
	public InterfaceStructure(String name) throws StructureException {
		setName(name);
	}
	
	/**
	 * Constructor with interface name and a modifier
	 * @param name
	 * @param modifier
	 * @throws StructureException
	 */
	public InterfaceStructure(String name, String modifier) throws StructureException {
		setName(name);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with interface name and an array of modifers
	 * @param name
	 * @param modifiers
	 * @throws StructureException
	 */
	public InterfaceStructure(String name, String modifiers[]) throws StructureException {
		setName(name);
		for (int i = 0; i < modifiers.length; i++) setModifier(modifiers[i]);
	}
	
	/**
	 * Assigns parent interface and listens to it
	 * @param parent must be another interface
	 * @throws StructureException 
	 */
	public void assign(ClassStructure parent) throws StructureException {
		if (parent == this) throw new StructureException(getName() + " can not extend itself");
		this.parent = parent;
		super.assign(parent);
	}
	
	/**
	 * Adds a property and listens to it
	 * @param p the property
	 * @throws StructureException if an alike property has already been added 
	 */
	public void add(PropertyStructure p) throws StructureException {
		for (int i = 0; i < properties.size(); i++) {
			if (properties.get(i).isAlike(p)) {
				throw new StructureException(this + " has already had the property \"" + properties.get(i) + "\"");
			}
		}
		properties.add(p);
		super.add(p);
	}
	
	/**
	 * Adds a method and listens to it
	 * @param m the method
	 * @throws StructureException if an alike method has already been added
	 */
	public void add(MethodStructure m) throws StructureException {
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).isAlike(m)) {
				throw new StructureException(this + " has already had the method \"" + methods.get(i) + "\"");
			}
		}
		methods.add(m);
		super.add(m);
	}
	
	/**
	 * 
	 * @return parent interface
	 */
	public InterfaceStructure getParent() {
		return parent;
	}
	
	/**
	 * 
	 * @return interface properties
	 */
	public PropertyStructure[] getProperties() {
		return properties.toArray(new PropertyStructure[0]);
	}
	
	/**
	 * 
	 * @return interface methods
	 */
	public MethodStructure[] getMethods() {
		return methods.toArray(new MethodStructure[0]);
	}
}
