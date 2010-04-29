package com.daohoangson.uml.structures;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureException;
import com.tranvietson.uml.structures.StructureListener;


/**
 * A very basic structure.
 * @author Dao Hoang Son
 * @version 1.2
 *
 */
public abstract class Structure implements StructureListener {
	/**
	 * Name of the structure
	 */
	private String name = null;
	/**
	 * Type of the structure. Some structures do not have a type
	 * @see #config()
	 */
	private String type = null;
	/**
	 * The visibility of the structure.
	 */
	private int visibility = -1;
	/**
	 * The scope of the structure.
	 */
	private boolean is_static = false;
	/**
	 * List of objects which listen to our event.
	 */
	private Vector<StructureListener> listeners;
	/**
	 * The container.
	 */
	private Structure container = null;
	/**
	 * Hold list of all parents of the structure.
	 */
	private List<Structure> parents = new LinkedList<Structure>();
	/**
	 * Hold list of all children of the structure.
	 */
	private List<Structure> children = new LinkedList<Structure>();
	
	/**
	 * Specifies if the structure needs unique name globally
	 * @see #setName(String)
	 */
	protected boolean cfg_unique_globally = false;
	/**
	 * The structure uses a type or not.
	 * Should be changed in the {@link #config()} method
	 */
	protected boolean cfg_use_type = false;
	/**
	 * Specifies which visibility is available for this type of structure
	 */
	protected boolean cfg_use_visibility = false;
	/**
	 * Specifies if the structure accepts static scope
	 */
	protected boolean cfg_use_scope = false;
	/**
	 * Specifies if the structure accepts container.
	 * Each structure can only accept 1 container!
	 * Default value is empty string which means doesn't accept container.
	 * A non-null structure name means accept a container 
	 * with the specified structure name.
	 */
	protected String cfg_container_structures[] = {};
	/**
	 * Specifies if the structure accepts parents.
	 * Each structure can accept many container!
	 * Default value is empty string which means doesn't accept parents.
	 * A non-null structure name means accept parents 
	 * with the specified structure name.
	 */
	protected String cfg_parent_structures[] = {};
	/**
	 * Specifies if the structure accepts children.
	 */
	protected String cfg_child_structures[] = {};
	/**
	 * Specifies if the structure wants to hide visibility
	 */
	protected boolean cfg_hide_visibility = false;
	/**
	 * Specifies if the structure wants to manage children by itself.
	 */
	protected boolean cfg_hide_children = false;
	
	/**
	 * List of global names. Maintaince by superclass in {@link #setName(String)}
	 * @see #cfg_unique_globally
	 */
	static private Hashtable<String,Structure> names = new Hashtable<String,Structure>();
	/**
	 * Global listener. Will be call when new structure is created
	 */
	static public StructureListener global_listener = null;
	
	/**
	 * Specifics structure configuration. Accepts type or not. 
	 * Determine allowed modifiers. Register the structure name.
	 * @see #cfg_unique_globally
	 * @see #cfg_use_type
	 * @see #cfg_use_visibility
	 * @see #cfg_use_scope
	 * @see #cfg_container_structure
	 * @see #cfg_parent_structure
	 * @see #cfg_child_structures
	 */
	abstract protected void config();
	
	/**
	 * Global Constructor
	 */
	public Structure() {
		config();
		
		//new structure is created
		//call the listener if any
		if (Structure.global_listener != null) {
			Structure.global_listener.structureChanged(new StructureEvent(this));
		}
	}
	
	/**
	 * Checks if the name is valid and set it.
	 * @param name
	 * @throws StructureException
	 */
	public boolean setName(String name) throws StructureException {
		String name_check = name.toLowerCase();
		
		//checks for name which needs to be unique in global scope
		if (cfg_unique_globally) {
			if (names.containsKey(name))
				throw new StructureException("The name '" + name + "' has already been declared!");
			names.put(name, this);
		}
		
		if (name_check.matches("^[a-z][a-z0-9_]*$")) {
			//use Regular Expression to check for valid characters
			this.name = name;
			return true;
		}
		
		throw new StructureException("Invalid Name: " + name);
	}
	
	/**
	 * Checks if the type is valid and set it. 
	 * Currently support both normal type (String, Integer) and specified (LinkedList&lt;String&gt;)
	 * @param type
	 * @throws StructureException
	 */
	public boolean setType(String type) throws StructureException {
		if (cfg_use_type == false)
			throw new StructureException("No Type Supported!");
		
		String type_check = type.toLowerCase();
		
		if (type_check.matches("^[a-z][a-z0-9_]*(<[a-z][a-z0-9_]*>)?$")) {
			//use Regular Expression to check for valid characters
			//supports general type
			this.type = type;
			return true;
		}
		
		throw new StructureException("Invalid Type: " + type);
	}
	
	/**
	 * Checks if the modifier is allowed and set it.
	 * @param modifier
	 * @throws StructureException
	 */
	public boolean setModifier(String modifier) throws StructureException {
		modifier = modifier.toLowerCase();
		
		//checks for access modifiers
		if (cfg_use_visibility) {
			String visibilities[] = new String[]{
					"public", "protected", "private"
			};
			for (int i = 0, n = visibilities.length; i < n; i++) {
				if (visibilities[i].equals(modifier)) {
					if (visibility == -1) {
						visibility = i;
						return true;
					} else {
						throw new StructureException(this + " can not have multiple access modifiers");
					}
				}
			}
		}
		
		//checks for scope modifier
		if (cfg_use_scope) {
			if ("static".equals(modifier)) {
				if (is_static == false) {
					is_static = true;
				}
				return true;
			}
		}
		
		throw new StructureException("Unsupported modifier: " + modifier);
	}
	
	public String toString() {
		String str = "";
		if (cfg_use_visibility && !cfg_hide_visibility) {
			switch (visibility) {
			case -1: str += "~ "; break;
			case 0: str += "+ "; break;
			case 1: str += "# "; break;
			case 2: str += "- "; break;
			}
		}
		if (cfg_use_scope) {
			// TODO: Need something special here
		}
		if (cfg_use_type) {
			str += getType() + " ";
		}
		str += getName();
		
		return str;
	}
	
	/**
	 * Checks if the structure name must be unique globally
	 * @return true if it is
	 */
	public boolean checkIsUniqueGlobally() {
		return cfg_unique_globally;
	}
	
	/**
	 * Checks if the structure accepts type or not.
	 * Use full to build the interface
	 * @return
	 */
	public boolean checkUseType() {
		return cfg_use_type;
	}
	
	/**
	 * Gets all allowed modifiers for the structure
	 * @return
	 */
	public String[] checkAllowedModifiers() {
		List<String> modifiers = new LinkedList<String>();
		
		if (cfg_use_visibility) {
			modifiers.add("public");
			modifiers.add("protected");
			modifiers.add("private");
		}
		
		if (cfg_use_scope) {
			modifiers.add("static");
		}
		
		return modifiers.toArray(new String[0]);
	}
	
	/**
	 * Checks if the structure accepts children.
	 * @return true if yes
	 */
	public boolean checkHasChildren() {
		return cfg_child_structures.length > 0 && !cfg_hide_children;
	}
	
	/**
	 * Checks if the structure accepts parents.
	 * @return true if yes
	 */
	public boolean checkHasParents() {
		return cfg_container_structures.length > 0 || cfg_parent_structures.length > 0;
	}
	
	/**
	 * Checks if the other structure is similar to the current one.
	 * Subclass should do check for structure type (using {@link #getStructureName()}).
	 * @param that the other structure
	 * @return true if it is
	 */
	public abstract boolean checkIsAlike(Structure that);
	
	/**
	 * Checks if the structure is a child of another structure
	 * @param that the other structure
	 * @return true if it is
	 */
	public boolean checkIsChildOf(Structure that) {
		if (container == that) 
			return true;
		
		synchronized (parents) {
			Iterator<Structure> itr = parents.iterator();
			while (itr.hasNext()) {
				if (itr.next() == that)
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the structure has a child similar to another structure
	 * @param that the other structure
	 * @return true if it does
	 */
	public boolean checkHasChildLike(Structure that) {
		synchronized (children) {
			Iterator<Structure> itr = children.iterator();
			while (itr.hasNext()) {
				if (itr.next().checkIsAlike(that)) 
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the type of the structure. 
	 * Do not get confused with {@link #getName()}
	 * @return
	 */
	public String getStructureName() {
		//extracts the name from real class name
		//the fullname is something like ...uml.structures.X
		//we will get the "X"
		String fullname = this.getClass().getName();
		String[] parts = fullname.split("\\.");
		String structure_name = parts[parts.length - 1];
		return structure_name;
	}
	
	/**
	 * 
	 * @return name of the structure object
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return type of the structure
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Checks to see if the type of the structure contains other structure
	 * @return array of target structures if any
	 */
	public Structure[] getTypeAsStructure() {
		List<Structure> types = new LinkedList<Structure>();
		
		if (type != null) {
			//use Regular Expression to parse type string
			Pattern p = Pattern.compile("^([^<]*)(<(.+)>)?$");
			Matcher m = p.matcher(type);
			if (m.matches()) {
				for (int i = 1; i <= m.groupCount(); i++) {
					String typename = m.group(i);
					if (typename != null) {
						if (names.containsKey(typename)) {
							types.add(names.get(typename));
						}
					}
				}
			}
		}
		
		return types.toArray(new Structure[0]);
	}
	
	/**
	 * Gets a string represent structure's visibility
	 * @return
	 */
	public String getVisibility() {
		switch (visibility) {
		case 0: return "public";
		case 1: return "protected";
		case 2: return "private";
		default: return "";
		}
	}
	
	/**
	 * Gets a string represent structure's scope
	 * @return
	 */
	public String getScope() {
		if (is_static) {
			return "static";
		} else {
			return "";
		}
	}
	
	/**
	 * Return the container of the structure.
	 * May be null
	 * @return
	 */
	public Structure getContainer() {
		return container;
	}
	
	/**
	 * Returns an array containing all parents of the current structure.
	 * It is sorted: class > interface > property > method > argument
	 * @return
	 */
	public Structure[] getParents() {
		Structure[] parents_array = parents.toArray(new Structure[0]);
		Comparator<Structure> byStructureName = new StructureNameComparator();
		Arrays.sort(parents_array,byStructureName);
		return parents_array;
	}
	
	/**
	 * Returns an array containing all children of the current structure.
	 * It is sorted: class > interface > property > method > argument
	 * @return the array
	 */
	public Structure[] getChildren() {
		Structure[] children_array = children.toArray(new Structure[0]);
		Comparator<Structure> byStructureName = new StructureNameComparator();
		Arrays.sort(children_array,byStructureName);
		return children_array;
	}
	
	/**
	 * Gets number of children of this structure.
	 * @return
	 */
	public int getChildrenCount() {
		return children.size();
	}
	
	/**
	 * Adds another structure as a child. 
	 * @param structure
	 * @throws StructureException
	 */
	public boolean add(Structure that) throws StructureException {
		boolean added = false;
		
		//checks if it's safe to add that as a child of this
		if (cfg_child_structures.length > 0) {
			for (int i = 0, n = cfg_child_structures.length; i < n; i++) {
				if (cfg_child_structures[i].equals(that.getStructureName())) {
					if (checkHasChildLike(that)) {
						throw new StructureException(this + " already has a similar " + that.getStructureName() + " to " + that);
					}
					that.addStructureListener(this);
					children.add(that);
					
					added = true;
				}
			}
		}
		
		//checks if it's safe to add this as a container of that
		if (foundStringInArray(getStructureName(), that.cfg_container_structures)) {
			if (that.container == null && !checkIsChildOf(that)) {
				that.container = this;
				
				added = true;
			} else if (added == false) {
				throw new StructureException("Can not add " + this + " as direct parent of " + that + " any more");
			}
		}
		
		//checks if it's safe to add this as a parent of that
		if (foundStringInArray(getStructureName(), that.cfg_parent_structures)) {
			if (!that.checkIsChildOf(this) && !checkIsChildOf(that)) {
				that.parents.add(this);
				
				added = true;
			} else if (added == false) {
				throw new StructureException("Can not add " + this + " as one of parents of " + that + " any more");
			}
		}
		
		if (added) {
			fireChanged();
			return true;
		} else 
			throw new StructureException(this + " can NOT accept " + that + " as a child");
	}
	
	/**
	 * Removes a child.
	 * @param structure
	 * @throws StructureException
	 */
	public boolean remove(Structure that) throws StructureException {
		boolean removed = false;
		
		//checks if it's possible to remove that from this's children
		if (cfg_child_structures.length > 0) {
			if (children.remove(that)) {
				that.removeStructureListener(this);
				
				removed = true;
			}
		}
		
		//checks if it's possible to remove this as that's container
		if (that.cfg_container_structures.length > 0) {
			if (that.container == this) {
				that.container = null;
				
				removed = true;
			}
		}
		
		//checks if it's possible to remove this from that's parents
		if (that.cfg_parent_structures.length > 0) {
			if (that.parents.remove(this)) {
				removed = true;
			}
		}
		
		if (removed) {
			fireChanged();
			return true;
		} else 
			throw new StructureException(this + " doesn't have " + that + " as a child");
	}
	
	/**
	 * Adds an object to this structure listeners list
	 * @param listener the object who listens to this
	 */
	synchronized public void addStructureListener(StructureListener listener) {
		if (listeners == null) listeners = new Vector<StructureListener>();
		if (!listeners.contains(listener)) listeners.add(listener);
	}
	
	synchronized public void removeStructureListener(StructureListener listener) {
		if (listeners != null)
			listeners.remove(listener);
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
	
	private static boolean foundStringInArray(String str, String[] array) {
		for (int i = 0, n = array.length; i < n; i++) 
			if (array[i].equals(str)) 
				return true;
		
		return false;
	}
}

/**
 * Comparator to sort structures. 
 * @author Dao Hoang Son
 *
 * @see Structure#getChildren()
 */
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
