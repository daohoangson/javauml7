package com.tranvietson.uml.structures;

/**
 * A Structure-based exception
 * @author Tran Viet Son
 * @version 1.0
 *
 */
public class StructureException extends Exception {
	private static final long serialVersionUID = 5010518603900193754L;

	/**
	 * Assigns the exception message
	 * @param string
	 */
	public StructureException(String string) {
		super(string);
	}
}
