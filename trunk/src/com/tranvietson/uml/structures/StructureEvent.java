package com.tranvietson.uml.structures;

import java.util.EventObject;

import com.daohoangson.uml.structures.Structure;

/**
 * A Structure-based event
 * 
 * @author Tran Viet Son
 * @version 1.0
 * 
 */
public class StructureEvent extends EventObject {
	private static final long serialVersionUID = 2546540183231663995L;
	private int type;
	public final static int UNDEFINED = 0;
	public final static int NAME = 1;
	public final static int TYPE = 2;
	public final static int MODIFIER = 3;
	public final static int CHILDREN = 4;
	public final static int ACTIVE = 5;

	/**
	 * Assigns the source {@link Structure} of the event.
	 * 
	 * @param source
	 */
	public StructureEvent(Structure source, int type) {
		super(source);
		this.type = type;
	}

	public int getType() {
		return type;
	}

}
