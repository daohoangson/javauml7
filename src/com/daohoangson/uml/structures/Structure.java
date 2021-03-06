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
 * A very basic structure. It has all the required methods but no direct
 * instance can be created
 * 
 * @author Dao Hoang Son
 * @version 1.3
 * 
 */
public abstract class Structure implements StructureListener {
	/**
	 * Status of the structure
	 * 
	 * @see #getActive()
	 */
	private boolean active = true;
	/**
	 * Name of the structure
	 * 
	 * @see #getName()
	 */
	private String name = null;
	/**
	 * Type of the structure. Some structures do not have a type
	 * 
	 * @see #config()
	 * @see #getType()
	 * @see #getTypeAsStructure()
	 */
	private String type = null;
	/**
	 * The visibility of the structure. Default value is "default". Obviously,
	 * every structure has default visibility... in default
	 * 
	 * @see #getVisibility()
	 */
	private String visibility = "default";
	/**
	 * The scope of the structure.
	 * 
	 * @see #getScope()
	 */
	private boolean is_static = false;
	/**
	 * The abstract-ness of the structure
	 * 
	 * @see #getAbstract()
	 */
	private boolean is_abstract = false;
	/**
	 * The container.
	 * 
	 * @see #add(Structure)
	 * @see #remove(Structure)
	 * @see #getContainer()
	 */
	private Structure container = null;
	/**
	 * Hold list of all parents of the structure.
	 * 
	 * @see #add(Structure)
	 * @see #remove(Structure)
	 * @see #getParents()
	 * @see #getParentsCount()
	 */
	private List<Structure> parents = new LinkedList<Structure>();
	/**
	 * Hold list of all children of the structure.
	 * 
	 * @see #add(Structure)
	 * @see #remove(Structure)
	 * @see #getChildren()
	 * @see #getChildrenCount()
	 */
	private List<Structure> children = new LinkedList<Structure>();
	/**
	 * Additional information
	 * 
	 * @see #setInfo(String, String)
	 * @see #getInfo(String)
	 */
	private Hashtable<String, String> info = new Hashtable<String, String>();

	/**
	 * Specifies if the structure needs unique name globally
	 * 
	 * @see #checkIsUniqueGlobally()
	 */
	protected boolean cfg_unique_globally = false;
	/**
	 * The structure uses a type or not.
	 * 
	 * @see #checkUseType()
	 */
	protected boolean cfg_use_type = false;
	/**
	 * Specifies which visibility is available for this type of structure
	 * 
	 * @see #checkUseVisibility()
	 */
	protected boolean cfg_use_visibility = false;
	/**
	 * Specifies if the structure accepts static scope
	 * 
	 * @see #checkUseScope()
	 */
	protected boolean cfg_use_scope = false;
	/**
	 * Specifies if the structure accepts abstract modifier
	 * 
	 * @see #checkUseAbstract()
	 */
	protected boolean cfg_use_abstract = false;
	/**
	 * Specifies if the structure accepts container. Each structure can only
	 * have 1 container! Default value is an empty array which means doesn't
	 * accept container. A non-zero array of structure names means accept a
	 * container with one of the specified structure names.
	 */
	protected String[] cfg_container_structures = {};
	/**
	 * Specifies if the structure accepts parents. Each structure can have many
	 * parents! Default value is an empty array which means doesn't accept
	 * parents. A non-zero array of structure name means accept parents with one
	 * of the specified structure names.
	 */
	protected String[] cfg_parent_structures = {};
	/**
	 * Specifies if the structure accepts children. Each structure can have many
	 * children! Default value is an empty array which mean doesn't accept
	 * children. A non-zero array of structure name means accept parents with
	 * one of the specified structure names.
	 */
	protected String[] cfg_child_structures = {};
	/**
	 * Specifies if the structure wants to hide visibility from
	 * {@link #toString()}
	 */
	protected boolean cfg_hide_visibility = false;
	/**
	 * Specifies if the structure wants to manage children by itself.
	 */
	protected boolean cfg_hide_children = false;
	/**
	 * The Regular Expression used match against name string. Subclass can set
	 * to null value to skip checking for match. Name must not begin with
	 * number. Name can only contain alphabetic characters and numbers.
	 * 
	 * @see #setName(String)
	 */
	protected String cfg_regex_name = "[a-zA-Z_][a-zA-Z0-9_]*";
	/**
	 * The Regular Expression used to match against type string. Subclass can
	 * set to null value to skip checking for match. Supports generalize types.
	 * Supports arrays. Primary type: group 1. <br />
	 * Generalized: group 3.
	 * 
	 * @see #setType(String)
	 */
	protected String cfg_regex_type = "(" + cfg_regex_name
			+ ")(<( *.*( *, *.*)*)>)?(\\[\\])*";

	/**
	 * List of global names. Maintained by superclass in
	 * {@link #setName(String)}
	 * 
	 * @see #cfg_unique_globally
	 */
	static private Hashtable<String, Structure> names = new Hashtable<String, Structure>();
	/**
	 * Hold list of all adding structures' names on queue
	 * 
	 * @see #addParentName(String)
	 * @see #addParentNameProceed()
	 */
	static private List<StructureAdding> adding_queue = new LinkedList<StructureAdding>();
	/**
	 * List of objects which listen to our event.
	 * 
	 * @see #addStructureListener(StructureListener)
	 * @see #removeStructureListener(StructureListener)
	 */
	private Vector<StructureListener> listeners;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debugging = false;

	/**
	 * Specifics structure configuration. Accepts type or not. Determine allowed
	 * modifiers. Register the structure name. Subclasses must override this
	 * method
	 * 
	 * @see #cfg_unique_globally
	 * @see #cfg_use_type
	 * @see #cfg_use_visibility
	 * @see #cfg_use_scope
	 * @see #cfg_container_structures
	 * @see #cfg_parent_structures
	 * @see #cfg_child_structures
	 * @see #cfg_hide_visibility
	 * @see #cfg_hide_children
	 * @see #cfg_regex_name
	 * @see #cfg_regex_type
	 */
	abstract protected void config();

	/**
	 * Global Constructor
	 */
	public Structure() {
		config();
	}

	/**
	 * Checks if the name is valid and set it.
	 * 
	 * @param name
	 * @throws StructureException
	 */
	public boolean setName(String name) throws StructureException {
		if (name != null) {
			// checks for name which needs to be unique in global scope
			if (cfg_unique_globally) {
				if (Structure.names.containsKey(name)) {
					throw new StructureException("The name '" + name
							+ "' has already been declared!");
				}
				if (Structure.names.contains(this)) {
					Structure.names.remove(this.name); // remove old-name<->this
					// pair
				}
				Structure.names.put(name, this);
			}

			if (cfg_regex_name == null
					|| name.matches("^" + cfg_regex_name + "$")) {
				// use Regular Expression to check for valid characters
				this.name = name;
				addParentNameProceed();
				if (Structure.debugging) {
					System.err
							.println(getStructureName() + ".setName: " + name);
				}

				fireChanged(StructureEvent.NAME);
				return true;
			}
		}

		throw new StructureException("Invalid Name: " + name);
	}

	/**
	 * Checks if the type is valid and set it. Currently support both normal
	 * type (String, Integer) and generalized (LinkedList&lt;String&gt;)
	 * 
	 * @param type
	 * @throws StructureException
	 */
	public boolean setType(String type) throws StructureException {
		if (cfg_use_type == false) {
			throw new StructureException("No Type Supported!");
		}

		if (type != null) {
			if (validateType(type, false)) {
				// use Regular Expression to check for valid characters
				this.type = type;
				if (Structure.debugging) {
					System.err
							.println(getStructureName() + ".setType: " + type);
				}

				fireChanged(StructureEvent.TYPE);
				return true;
			}
		}

		throw new StructureException("Invalid Type: " + type);
	}

	/**
	 * Validates type string.
	 * 
	 * @param type
	 *            the string needed validating
	 * @param split
	 *            tells the method to allow splitting type or not (use full in
	 *            generalized string)
	 * @return true if the string is valid
	 */
	private boolean validateType(String type, boolean split) {
		if (cfg_regex_type == null) {
			return true;
		}

		if (Structure.debugging) {
			System.err.println(getStructureName() + ".validateType: " + type
					+ " (" + split + ")");
		}

		String[] types;
		if (split) {
			types = type.split(",");
		} else {
			types = new String[] { type };
		}

		Pattern p = Pattern.compile("^" + cfg_regex_type + "$");
		for (int i = 0; i < types.length; i++) {
			Matcher m = p.matcher(types[i].trim());
			if (m.matches()) {
				// type is matched
				// check for generalized
				String generalized = m.group(3);
				if (generalized != null) {
					if (!validateType(generalized, true)) {
						return false;
					}
				}
			} else {
				// not matched!
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the modifier is allowed and set it.
	 * 
	 * @param modifier
	 * @throws StructureException
	 */
	public boolean setModifier(String modifier) throws StructureException {
		modifier = modifier.toLowerCase();

		// a friendly check to prevent stupid error
		if (modifier.length() == 0) {
			return false;
		}

		// checks for access modifiers
		if (cfg_use_visibility) {
			String visibilities[] = checkAllowedVisibilities();
			for (int i = 0, n = visibilities.length; i < n; i++) {
				if (visibilities[i].equals(modifier)) {
					visibility = new String(modifier);
					if (Structure.debugging) {
						System.err.println(getStructureName()
								+ ".setModifier (visibility): " + modifier);
					}
					return true;
				}
			}
		}

		// checks for scope modifier
		if (cfg_use_scope) {
			if ("static".equals(modifier)) {
				if (is_abstract) {
					// can not static and abstract
					throw new StructureException("Can not apply static scope "
							+ "to abstract structure");
				}
				if (is_static == false) {
					is_static = true;
					fireChanged(StructureEvent.MODIFIER);
				}
				if (Structure.debugging) {
					System.err.println(getStructureName()
							+ ".setModifier (scope): is_static = " + is_static);
				}
				return true;
			}
		}

		// checks for abstract modifier
		if (cfg_use_abstract) {
			if ("abstract".equals(modifier)) {
				if (is_abstract) {
					// can not static and abstract
					throw new StructureException(
							"Can not make static structure "
									+ "an abstract one");
				}
				if (is_abstract == false) {
					is_abstract = true;
					fireChanged(StructureEvent.MODIFIER);
				}
				if (Structure.debugging) {
					System.err.println(getStructureName()
							+ ".setModifier (abstract): is_abstract = "
							+ is_abstract);
				}
				return true;
			}
		}

		throw new StructureException(this + "doesn't support modifier "
				+ modifier);
	}

	/**
	 * Sets an additional piece of information
	 * 
	 * @param key
	 * @param value
	 */
	public void setInfo(String key, String value) {
		info.put(key, value);
	}

	@Override
	public String toString() {
		return toString("", "", cfg_hide_visibility);
	}

	/**
	 * Builds the string representing this structure
	 * 
	 * @param prefix
	 *            the prefix for the string
	 * @param suffix
	 *            the suffix for the string
	 * @param cfg_hide_visibility
	 *            specifies if it should contain visibility in the resulted
	 *            string
	 * @return built string
	 */
	public String toString(String prefix, String suffix,
			boolean cfg_hide_visibility) {
		String str = "";
		if (cfg_use_visibility && !cfg_hide_visibility) {
			if (visibility.equals("default")) {
				str += "~ ";
			} else if (visibility.equals("public")) {
				str += "+ ";
			} else if (visibility.equals("protected")) {
				str += "# ";
			} else if (visibility.equals("private")) {
				str += "- ";
			}
		}
		str += getName();

		str = prefix + str + suffix;

		if (type != null && !type.equals("void")) {
			str += ": " + getType();
		}

		if (is_static) {
			// display static structure with under-line
			str = "<u>" + str + "</u>";
		}

		if (is_abstract) {
			// display abstract structure in italic
			str = "<i>" + str + "</i>";
		}

		if (is_static || is_abstract) {
			// because we used HTML above
			str = "<html>" + str + "</html>";
		}

		return str;
	}

	@Override
	public int hashCode() {
		return (getStructureName() + "." + getName()).hashCode();
	}

	/**
	 * Checks if the structure name must be unique globally
	 * 
	 * @return true if it is
	 */
	public boolean checkIsUniqueGlobally() {
		return cfg_unique_globally;
	}

	/**
	 * Checks if the structure accepts type or not. Useful to build the
	 * interface
	 * 
	 * @return true if it is
	 */
	public boolean checkUseType() {
		return cfg_use_type;
	}

	/**
	 * Checks if the structure accepts visibility or not. Useful to build the
	 * interface
	 * 
	 * @return true if it is
	 */
	public boolean checkUseVisibility() {
		return cfg_use_visibility;
	}

	/**
	 * Checks for allowed visibility modifiers
	 * 
	 * @return an array of allowed ones
	 */
	public String[] checkAllowedVisibilities() {
		return new String[] { "default", "public", "protected", "private" };
	}

	/**
	 * Checks if the structure accepts scope or not. Useful to build the
	 * interface
	 * 
	 * @return true if it is
	 */
	public boolean checkUseAbstract() {
		return cfg_use_abstract;
	}

	/**
	 * Checks if the structure accepts abstract or not. Use full to build the
	 * interface
	 * 
	 * @return true if it is
	 */
	public boolean checkUseScope() {
		return cfg_use_scope;
	}

	/**
	 * Checks if the structure accepts children.
	 * 
	 * @return true if yes
	 */
	public boolean checkCanHaveChildren() {
		return cfg_child_structures.length > 0 && !cfg_hide_children;
	}

	/**
	 * Checks if the structure accepts children with the specified structure
	 * name
	 * 
	 * @param structureName
	 *            the structure name need checking
	 * @return true if this structure does accept
	 */
	public boolean checkCanHaveChildren(String structureName) {
		return Structure
				.foundStringInArray(structureName, cfg_child_structures);
	}

	/**
	 * Checks if the structure accepts parents.
	 * 
	 * @return true if yes
	 */
	public boolean checkCanHaveParents() {
		return cfg_container_structures.length > 0
				|| cfg_parent_structures.length > 0;
	}

	/**
	 * Checks if the structure accepts parents with the specified structure name
	 * 
	 * @param structureName
	 *            the structure name need checking
	 * @return true if the structure does accept
	 */
	public boolean checkCanHaveParents(String structureName) {
		return Structure.foundStringInArray(structureName,
				cfg_parent_structures)
				|| Structure.foundStringInArray(structureName,
						cfg_container_structures);
	}

	/**
	 * Checks if the other structure is similar to the current one. Subclass
	 * should do check for structure type (using {@link #getStructureName()}).
	 * 
	 * @param that
	 *            the other structure
	 * @return true if it is
	 */
	public abstract boolean checkIsAlike(Structure that);

	/**
	 * Checks if the structure is a child of another structure
	 * 
	 * @param that
	 *            the other structure
	 * @return true if it is
	 */
	public boolean checkIsChildOf(Structure that) {
		if (container == that) {
			return true;
		}

		synchronized (parents) {
			Iterator<Structure> itr = parents.iterator();
			while (itr.hasNext()) {
				if (itr.next() == that) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the structure has a child similar to another structure
	 * 
	 * @param that
	 *            the other structure
	 * @return true if it does
	 */
	public boolean checkHasChildLike(Structure that) {
		synchronized (children) {
			Iterator<Structure> itr = children.iterator();
			while (itr.hasNext()) {
				if (itr.next().checkIsAlike(that)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the structure name of the structure. Do not get confused with
	 * {@link #getName()} which gets the name (or id) of the structure
	 * 
	 * @return the structure name of the structure
	 */
	public String getStructureName() {
		// extracts the name from real class name
		// the full name is something like ...uml.structures.X
		// we will get the "X" only
		String fullname = this.getClass().getName();
		int lastDot = fullname.lastIndexOf('.');
		return fullname.substring(lastDot + 1);
	}

	/**
	 * Checks for the active status of the structure.
	 * 
	 * @return true if the structure is active
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Gets the name (or id) of the structure
	 * 
	 * @return name of the structure
	 */
	public String getName() {
		return name;
	}

	/**
	 * Finds the structure with a given name.
	 * 
	 * @param name
	 *            the required structure name
	 * @return the found structure or null if not found
	 */
	public static Structure lookUp(String name) {
		return Structure.names.get(name);
	}

	/**
	 * Gets the type of the structure
	 * 
	 * @return type of the structure
	 */
	public String getType() {
		return type;
	}

	/**
	 * Shortcuts to {@link #getTypeAsStructure(String, boolean)} with the
	 * structure type
	 * 
	 * @return array of target structures if any
	 */
	public Structure[] getTypeAsStructure() {
		return getTypeAsStructure(type, false);
	}

	/**
	 * Checks to see if the type of the structure contains other structure
	 * 
	 * @param type
	 *            the type string that need exporting
	 * @param split
	 *            tells the method to allow splitting type or not (use full in
	 *            generalized string). You may want to take a look at
	 *            {@link #validateType(String, boolean)}
	 * @return an array of structure mentioned/used in type string
	 */
	private Structure[] getTypeAsStructure(String type, boolean split) {
		List<Structure> types = new LinkedList<Structure>();

		if (type != null) {
			String[] types_array;
			if (split) {
				types_array = type.split(",");
			} else {
				types_array = new String[] { type };
			}

			// use Regular Expression to parse type string
			Pattern p = Pattern.compile("^" + cfg_regex_type + "$");
			for (int i = 0; i < types_array.length; i++) {
				Matcher m = p.matcher(types_array[i].trim());
				if (m.matches()) {
					String typename = m.group(1);
					Structure structure_found = Structure.lookUp(typename);
					if (structure_found != null) {
						types.add(structure_found);
					}

					String generalized = m.group(3);
					if (generalized != null) {
						Structure[] generalized_types = getTypeAsStructure(
								generalized, true);
						if (Structure.debugging) {
							System.err.println("Generalized string found: "
									+ generalized);
						}
						for (int j = 0; j < generalized_types.length; j++) {
							types.add(generalized_types[j]);
							if (Structure.debugging) {
								System.err.println("Generalized type found: "
										+ generalized_types[j]);
							}
						}
					}
				}
			}
		}

		return types.toArray(new Structure[0]);
	}

	/**
	 * Gets a string represent structure's visibility. Please note that this
	 * method automatically omit default visibility
	 * 
	 * @return the visibility (public|protected|private) or a zero-length string
	 *         (no null value)
	 */
	public String getVisibility() {
		if (visibility.equals("default")) {
			// omit default
			return "";
		} else {
			// return the visibility
			return new String(visibility);
		}
	}

	/**
	 * Gets a string represent structure's scope
	 * 
	 * @return the scope or a zero-length string (no null value)
	 */
	public String getScope() {
		if (is_static) {
			return "static";
		} else {
			return "";
		}
	}

	/**
	 * Gets a string represent structure's abstract-ness
	 * 
	 * @return the scope or a zero-length string (no null value)
	 */
	public String getAbstract() {
		if (is_abstract) {
			return "abstract";
		} else {
			return "";
		}
	}

	/**
	 * Gets an additional information
	 * 
	 * @param key
	 * @return the information or at least, a zero-length string (no null value)
	 */
	public String getInfo(String key) {
		String value = info.get(key);
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	/**
	 * Gets the container of the structure. May be null
	 * 
	 * @return the only container of the structure
	 */
	public Structure getContainer() {
		return container;
	}

	/**
	 * Gets all parents of the current structure.
	 * 
	 * @return an array of structures. It is sorted: class > interface >
	 *         property > method > argument
	 */
	public Structure[] getParents() {
		Structure[] parents_array = parents.toArray(new Structure[0]);
		Comparator<Structure> byStructureName = new StructureStructureNameComparator();
		Arrays.sort(parents_array, byStructureName);
		return parents_array;
	}

	/**
	 * Gets number of parents of this structure.
	 * 
	 * @return number of parents
	 * 
	 * @see #getParents()
	 */
	public int getParentsCount() {
		return parents.size();
	}

	/**
	 * Gets all children of the current structure
	 * 
	 * @return an array of structures. It is sorted: class > interface >
	 *         property > method > argument
	 */
	public Structure[] getChildren() {
		Structure[] children_array = children.toArray(new Structure[0]);
		Comparator<Structure> byStructureName = new StructureStructureNameComparator();
		Arrays.sort(children_array, byStructureName);
		return children_array;
	}

	/**
	 * Gets number of children of this structure.
	 * 
	 * @return number of children
	 * 
	 * @see #getChildren()
	 */
	public int getChildrenCount() {
		return children.size();
	}

	/**
	 * Adds another structure as a child. Parents always listen for changes from
	 * children
	 * 
	 * @param that
	 *            the child
	 * @throws StructureException
	 */
	public boolean add(Structure that) throws StructureException {
		boolean added = false;

		if (this == that) {
			throw new StructureException("You can't do \"it\" yourself, man!");
		}

		// checks if it's safe to add that as a child of this
		if (cfg_child_structures.length > 0) {
			if (Structure.foundStringInArray(that.getStructureName(),
					cfg_child_structures)) {
				if (checkHasChildLike(that)) {
					throw new StructureException(this
							+ " already has a similar "
							+ that.getStructureName() + " to " + that);
				}
				that.addStructureListener(this);
				synchronized (children) {
					children.add(that);
				}

				added = true;
			}
		}

		// checks if it's safe to add this as a container of that
		if (Structure.foundStringInArray(getStructureName(),
				that.cfg_container_structures)) {
			if (that.container == null && !checkIsChildOf(that)) {
				that.container = this;

				added = true;
			} else if (added == false) {
				throw new StructureException("Can not add " + this
						+ " as direct parent of " + that + " any more");
			}
		}

		// checks if it's safe to add this as a parent of that
		if (Structure.foundStringInArray(getStructureName(),
				that.cfg_parent_structures)) {
			if (!that.checkIsChildOf(this) && !checkIsChildOf(that)) {
				synchronized (that.parents) {
					that.parents.add(this);
				}

				added = true;
			} else if (added == false) {
				throw new StructureException("Can not add " + this
						+ " as one of parents of " + that + " any more");
			}
		}

		if (added) {
			fireChanged(StructureEvent.CHILDREN);
			return true;
		} else {
			throw new StructureException(this + " can NOT accept " + that
					+ " as a child");
		}
	}

	/**
	 * Puts a structure name into queue to add as parent later. Useful if the
	 * parent structure isn't existed at the time of adding. Remember to put
	 * unique globally names only or it will never be added!
	 * 
	 * @param structure_name
	 *            the parent structure's name
	 * @throws StructureException
	 */
	public void addParentName(String structure_name) throws StructureException {
		Structure that = Structure.lookUp(structure_name);
		if (that != null) {
			that.add(this);
		} else {
			Structure.adding_queue
					.add(new StructureAdding(this, structure_name));
		}
	}

	/**
	 * Checks and add all pending adding requests which associate with current
	 * structure's name
	 * 
	 * @throws StructureException
	 */
	private void addParentNameProceed() throws StructureException {
		if (Structure.adding_queue.size() > 0) {
			Iterator<StructureAdding> itr = Structure.adding_queue.iterator();
			while (itr.hasNext()) {
				StructureAdding adding = itr.next();
				if (adding.parent_structure_name.equals(getName())) {
					add(adding.structure);
					itr.remove();
				}
			}
		}
	}

	/**
	 * Removes a child. Also stop listening to that child
	 * 
	 * @param that
	 *            the child
	 * @throws StructureException
	 */
	public boolean remove(Structure that) throws StructureException {
		boolean removed = false;

		// checks if it's possible to remove that from this's children
		if (cfg_child_structures.length > 0) {
			synchronized (children) {
				if (children.remove(that)) {
					that.removeStructureListener(this);

					removed = true;
				}
			}
		}

		// checks if it's possible to remove this as that's container
		if (that.cfg_container_structures.length > 0) {
			if (that.container == this) {
				that.container = null;

				removed = true;
			}
		}

		// checks if it's possible to remove this from that's parents
		if (that.cfg_parent_structures.length > 0) {
			synchronized (that.parents) {
				if (that.parents.remove(this)) {
					removed = true;
				}
			}
		}

		if (removed) {
			fireChanged(StructureEvent.CHILDREN);
			return true;
		} else {
			throw new StructureException(this + " doesn't have " + that
					+ " as a child");
		}
	}

	/**
	 * Disposes this structure. Removes any container/parents. Dispose any
	 * children. Removes from {@link #names} also. Simply speaking, completely
	 * remove this structure, just like it has never existed.
	 */
	public void dispose() {
		try {
			if (container != null) {
				container.remove(this);
			}

			// TODO: Need a more proper way and figure it out why synchronized
			// doesn't work
			Iterator<Structure> itr_parents = new LinkedList<Structure>(parents)
					.iterator();
			while (itr_parents.hasNext()) {
				itr_parents.next().remove(this);
			}

			Iterator<Structure> itr_children = new LinkedList<Structure>(
					children).iterator();
			while (itr_children.hasNext()) {
				itr_children.next().dispose();
			}
		} catch (StructureException e) {
			// ignore
		}

		if (Structure.names.containsValue(this)) {
			Structure.names.remove(getName());
		}

		// mark as inactive
		active = false;

		fireChanged(StructureEvent.ACTIVE);
	}

	/**
	 * Create a new structure which is an exact copy of current structure.
	 * Includes all children. We should have used {@link #clone()} (or
	 * overriden) it but it doesn't support throwing exceptions, so...
	 * 
	 * @return the copied structure
	 * @throws StructureException
	 *             if there is error in copying. Common errors are:
	 *             <ul>
	 *             <li>Trying to copy globally unique structures</li>
	 *             <li>Trying to copy constructors</li>
	 *             </ul>
	 */
	public Structure copy() throws StructureException {
		try {
			Structure copied = this.getClass().newInstance();
			if (cfg_unique_globally) {
				throw new StructureException("Sorry, you can't copy "
						+ getStructureName() + " like " + this);
			}
			// everything has name
			copied.setName(getName());
			if (copied.checkUseType()) {
				if (!checkUseType()) {
					// constructor?
					throw new StructureException("Copying " + this
							+ " is prohibited!");
				}
				copied.setType(getType());
			}
			if (copied.checkUseVisibility()) {
				copied.setModifier(getVisibility());
			}
			if (copied.checkUseScope()) {
				copied.setModifier(getScope());
			}
			if (copied.checkCanHaveChildren()) {
				Structure[] children = getChildren();
				for (int i = 0; i < children.length; i++) {
					Structure child_copied = children[i].copy();
					copied.add(child_copied);
				}
			}

			return copied;
		} catch (InstantiationException e) {
			// ignore
			if (Structure.debugging) {
				e.printStackTrace();
			}
		} catch (IllegalAccessException e) {
			if (Structure.debugging) {
				e.printStackTrace();
			}
		}

		throw new StructureException("Sorry, you can't copy " + this);
	}

	/**
	 * Adds an object to this structure listeners list
	 * 
	 * @param listener
	 *            the object who needs to listen to this
	 */
	synchronized public void addStructureListener(StructureListener listener) {
		if (listeners == null) {
			listeners = new Vector<StructureListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes an object from this structure listeners list
	 * 
	 * @param listener
	 *            the object who is listening to this
	 */
	synchronized public void removeStructureListener(StructureListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Triggers the changed event
	 * 
	 * @param type
	 *            the tyep of the event. Possible value is defined in
	 *            {@link StructureEvent}: <code>UNDEFINED</code>,
	 *            <code>NAME</code>, <code>TYPE</code>, <code>MODIFIER</code>,
	 *            <code>CHILDREN</code>,<code>ACTIVE</code>
	 */
	@SuppressWarnings("unchecked")
	protected void fireChanged(int type) {
		if (listeners != null && listeners.size() > 0) {
			StructureEvent e = new StructureEvent(this, type);

			Vector<StructureListener> targets;
			synchronized (this) {
				targets = (Vector<StructureListener>) listeners.clone();
			}

			for (int i = 0; i < targets.size(); i++) {
				StructureListener listener = targets.elementAt(i);
				listener.structureChanged(e);
			}
		}
	}

	/**
	 * Fires the changed event of this structure itself with a type of children
	 * changes
	 */
	public void structureChanged(StructureEvent e) {
		fireChanged(StructureEvent.CHILDREN);
	}

	/**
	 * Checks if there is a String in an array. Uses equals() method. This is a
	 * helper method
	 * 
	 * @param str
	 *            the requested string
	 * @param array
	 *            the array to check against
	 * @return true if the string is found
	 */
	private static boolean foundStringInArray(String str, String[] array) {
		for (int i = 0, n = array.length; i < n; i++) {
			if (array[i].equals(str)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets structures with some specified structure names
	 * 
	 * @param structures
	 *            the array of structures needs filtering
	 * @param structureNames
	 *            the array of structure name
	 * @return an array of structures with structure names matched
	 */
	public static Structure[] filterStructureArray(Structure[] structures,
			String[] structureNames) {
		List<Structure> result = new LinkedList<Structure>();
		for (int i = 0; i < structures.length; i++) {
			if (Structure.foundStringInArray(structures[i].getStructureName(),
					structureNames)) {
				result.add(structures[i]);
			}
		}
		return result.toArray(new Structure[0]);
	}
}

/**
 * A adding parent request object. Hold a reference of the to-be-child structure
 * and the name of the to-be-parent structure. Used in
 * {@link Structure#addParentName(String)}
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class StructureAdding {
	Structure structure;
	String parent_structure_name;

	StructureAdding(Structure structure, String parent_structure_name) {
		this.structure = structure;
		this.parent_structure_name = parent_structure_name;
	}
}
