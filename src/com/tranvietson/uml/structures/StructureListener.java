package com.tranvietson.uml.structures;

import com.daohoangson.uml.structures.Structure;


/**
 * A listener to {@link Structure} objects
 * @author Tran Viet Son
 * @version 1.0
 */
public interface StructureListener {
	/**
	 * Runs when the target {@link Structure} is changed.
	 * @param e
	 */
	public void structureChanged(StructureEvent e);
}
