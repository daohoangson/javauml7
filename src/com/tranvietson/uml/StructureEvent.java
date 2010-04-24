package com.tranvietson.uml;

import java.util.EventObject;

/**
 * A Structure-based event
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class StructureEvent extends EventObject {
	private static final long serialVersionUID = 2546540183231663995L;

	/**
	 * Assigns the source {@link Structure} of the event.
	 * @param source
	 */
	public StructureEvent(Structure source) {
		super(source);
	}
	
}
