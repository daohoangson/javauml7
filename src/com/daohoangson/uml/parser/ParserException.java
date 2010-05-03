package com.daohoangson.uml.parser;

/**
 * Exception while parsing source files
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class ParserException extends Exception {
	private static final long serialVersionUID = 8710335823557987842L;

	public ParserException(String message) {
		super(message);
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
