package com.daohoangson.uml.parser;

class Token {
	static int ERROR;
	static int SPACE;
	static int NAME;
	static int TYPE;
	static int KEYWORD;
	static int VISIBILITY;
	static int SCOPE;
	static int ABSTRACT;
	static int CLASS;
	static int INTERFACE;
	static int EXTENDS;
	static int IMPLEMENTS;
	static int THROWS;
	static int KEYWORD_IGNORED;
	static int ANNOTATION;
	static int PACKAGE;
	static int IMPORT;
	static int PACKAGENAME;
	static int LPAR;
	static int RPAR;
	static int LBRACE;
	static int RBRACE;
	static int COMMA;
	static int COLON;
	static int ASSIGN;
	static int OPERATOR;
	static String[] operators;
	static int VALUE_CHAR;
	static int VALUE_STRING;
	static int VALUE_NUMBER;
	int type;
	String content;
	int offset;

	Token(int type, String content, int offset) {
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		// TODO Auto-generated method stub
	}
}