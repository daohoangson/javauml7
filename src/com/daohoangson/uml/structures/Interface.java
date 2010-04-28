package com.daohoangson.uml.structures;


/**
 * An interface
 * @author Dao Hoang Son
 * @version 1.1
 *
 */
public class Interface extends Structure {
	/**
	 * Interface doesn't use type.
	 */
	protected void config() {
		cfg_unique_globally = true;
		cfg_use_visibility = true;
		cfg_use_scope = true;
		cfg_container_structures = new String[] {
				"Interface"
		};
		cfg_child_structures = new String[]{
				"Property"
				,"Method"
		};
		cfg_hide_visibility = true;
	}

	/**
	 * Constructor with interface name
	 * @param name
	 * @throws StructureException
	 */
	public Interface(String name) throws StructureException {
		setName(name);
	}
	
	/**
	 * Constructor with interface name and a modifier
	 * @param name
	 * @param modifier
	 * @throws StructureException
	 */
	public Interface(String name, String modifier) throws StructureException {
		setName(name);
		setModifier(modifier);
	}
	
	/**
	 * Constructor with interface name and an array of modifers
	 * @param name
	 * @param modifiers
	 * @throws StructureException
	 */
	public Interface(String name, String modifiers[]) throws StructureException {
		setName(name);
		for (int i = 0, n = modifiers.length; i < n; i++)
			setModifier(modifiers[i]);
	}

	@Override
	public boolean checkIsAlike(Structure that) {
		return that.getStructureName().equals(getStructureName())
			&& getName().equals(that.getName());
	}
}
