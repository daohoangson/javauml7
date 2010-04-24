package com.tranvietson.uml;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * A very basic structure.
 * @author Tran Viet Son
 * @author Dao Hoang Son
 * @version 1.0
 *
 */
public abstract class Structure implements StructureListener {
	/**
	 * Name of the structure
	 */
	private String name;
	/**
	 * Specifies of the structure needs unique name globally
	 * @see #setName(String)
	 */
	protected boolean unique_globally = false;
	/**
	 * Type of the structure. Some structures do not have a type
	 * @see #config()
	 */
	private String type;
	/**
	 * Modifiers used with the structure. Stored as a flag array.
	 * Use with {@link #allowed_modifiers} to determine which modifier is used
	 */
	private boolean used_modifiers[];
	private Vector<StructureListener> listeners;
	
	/**
	 * Name of the structure itself. It's different from {@link #name}.
	 * Should be changed in the {@link #config()} method
	 */
	protected String structure_name = "Structure";
	/**
	 * The structure uses a type or not.
	 * Should be changed in the {@link #config()} method
	 */
	protected boolean use_type = false;
	/**
	 * Modifiers are allowed to use with the structure.
	 * Should be changed in the {@link #config()} method
	 */
	protected String allowed_modifiers[] = new String[0];
	
	private List<Structure> parents = new LinkedList<Structure>();
	protected boolean has_parents = false;
	private List<Structure> children = new LinkedList<Structure>();
	protected boolean has_children = false;
	static private List<String> names = new LinkedList<String>();
	
	/**
	 * Specifics structure configuration. Accepts type or not. 
	 * Determine allowed modifiers. Register the structure name.
	 * @see #structure_name
	 * @see #use_type
	 * @see #allowed_modifiers
	 */
	abstract void config();
	
	/**
	 * Global Constructor
	 */
	public Structure() {
		name = null;
		type = null;
		
		config();
		used_modifiers = new boolean[allowed_modifiers.length];
	}
	
	/**
	 * Checks if the name is valid and set it
	 * @param name
	 * @throws StructureException
	 */
	public void setName(String name) throws StructureException {
		String name_check = name.toLowerCase();
		
		//checks for name which needs to be unique in global scope
		if (unique_globally) {
			Iterator<String> itr = names.iterator();
			while (itr.hasNext())
				if (itr.next().equals(name))
					throw new StructureException("The name '" + name + "' has already been declared!");
			names.add(name);
		}
		
		if (name_check.matches("^[a-z][a-z0-9_]*$")) {
			//use Regular Expression to check for valid characters
			this.name = name;
			return;
		}
		
		throw new StructureException("Invalid Name: " + name);
	}
	
	/**
	 * Checks if the type is valid and set it. 
	 * Currently support both normal type (String, Integer) and specified (LinkedList&lt;String&gt;)
	 * @param type
	 * @throws StructureException
	 */
	public void setType(String type) throws StructureException {
		if (use_type == false) throw new StructureException("No Type Supported!");
		
		String type_check = type.toLowerCase();
		
		if (type_check.matches("^[a-z][a-z0-9_]*(<[a-z][a-z0-9_]*>)?$")) {
			//use Regular Expression to check for valid characters
			this.type = type;
			return;
		}
		
		throw new StructureException("Invalid Type: " + type);
	}
	
	/**
	 * Checks if the modifier is allowed and set it.
	 * @param modifier
	 * @throws StructureException
	 */
	public void setModifier(String modifier) throws StructureException {
		modifier = modifier.toLowerCase();
		String modifiers_list = "";
		
		for (int i = 0; i < allowed_modifiers.length; i++) {
			if (allowed_modifiers[i].equals(modifier)) {
				used_modifiers[i] = true;
				return;
			}
			
			modifiers_list += " " + allowed_modifiers[i];
		}

		throw new StructureException("Not allowed modifier: " + modifier + ". Only accept:" + modifiers_list);
	}
	
	public String toString() {
		String str;
		if (use_type) {
			str = getType();
		} else {
			str = structure_name;
		}
		str += " " + getName();
		return str;
	}
	
	public String getStructureName() {
		return structure_name;
	}
	
	public boolean useType() {
		return use_type;
	}
	
	public String[] getAllowedModifiers() {
		return allowed_modifiers;
	}
	
	public boolean hasChildren() {
		return has_children;
	}
	
	public boolean hasParents() {
		return has_parents;
	}
	
	/**
	 * 
	 * @return name of the structure object
	 */
	public String getName() {
		if (name != null) {
			return name;
		} else {
			return "<noname>";
		}
	}
	
	/**
	 * 
	 * @return type of the structure
	 */
	public String getType() {
		if (use_type && type != null) {
			return type;
		} else {
			return "<notype>";
		}
	}
	
	/**
	 * 
	 * @return a string with all set modifiers
	 */
	public String getModifiers() {
		String modifiers_list = "";
		
		for (int i = 0; i < allowed_modifiers.length; i++) {
			if (used_modifiers[i]) {
				if (modifiers_list.length() > 0) modifiers_list += " ";
				modifiers_list += allowed_modifiers[i];
			}
		}
		
		if (modifiers_list.length() > 0) {
			return modifiers_list;
		} else {
			return "<none>";
		}
	}
	
	/**
	 * Adds an object to this structure listeners list
	 * @param listener the object who listens to this
	 */
	synchronized public void addStructureListener(StructureListener listener) {
		if (listeners == null) listeners = new Vector<StructureListener>();
		if (!listeners.contains(listener)) listeners.addElement(listener);
	}
	
	/**
	 * Triggers the changed event
	 */
	@SuppressWarnings("unchecked")
	protected void fireChanged() {
		if (listeners != null && listeners.size() > 0) {
			StructureEvent e = new StructureEvent(this);
			
			Vector<StructureListener> targets;
			synchronized (this) {
				targets = (Vector<StructureListener>)listeners.clone();
			}
			
			for (int i = 0; i < targets.size(); i++) {
				StructureListener listener = targets.elementAt(i);
				listener.structureChanged(e);
			}
		}
	}
	
	/**
	 * Fires the changed event of this structure itself!
	 */
	public void structureChanged(StructureEvent e) {
		fireChanged();
	}
	
	/**
	 * Adds a child. Add listener
	 * @param structure
	 */
	public void add(Structure structure) {
		if (has_children) {
			children.add(structure);
			structure.addStructureListener(this);
			fireChanged();
		}
	}
	
	/**
	 * Adds a parent. Add listener
	 * @param structure
	 */
	public void assign(Structure structure) {
		if (has_parents) {
			parents.add(structure);
			structure.addStructureListener(this);
			fireChanged();
		}
	}
	
	public Structure[] getChildren() {
		Structure[] children_array = children.toArray(new Structure[0]);
		Comparator<Structure> byStructureName = new StructureNameComparator();
		Arrays.sort(children_array,byStructureName);
		return children_array;
	}
	
	/**
	 * Return list of parents
	 * @return
	 */
	public Structure[] getParents() {
		return parents.toArray(new Structure[0]);
	}
}

class StructureNameComparator implements Comparator<Structure> {

	@Override
	public int compare(Structure s1, Structure s2) {
		int i1 = getPriority(s1);
		int i2 = getPriority(s2);
		
		if (i1 == i2) {
			return 0;
		} else if (i1 > i2) {
			return -1;
		} else {
			return 1;
		}
	}
	
	int getPriority(Structure s) {
		String name = s.getStructureName();
		if (name.equals("Interface")) {
			return 10;
		} else if (name.equals("Class")) {
			return 9;
		} else if (name.equals("Property")) {
			return 7;
		} else if (name.equals("Method")) {
			return 5;
		} else if (name.equals("Argument")) {
			return 3;
		}
		
		return 1;
	}
	
}
