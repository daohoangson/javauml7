package com.daohoangson.uml.parser;

class AutomataStateEdge {
	int type;
	String connection;
	AutomataState next;

	AutomataStateEdge(AutomataState next) {
		// TODO Auto-generated constructor stub
	}

	AutomataStateEdge(char c, AutomataState next) {
		// TODO Auto-generated constructor stub
	}

	AutomataStateEdge(String connection, AutomataState next) {
		// TODO Auto-generated constructor stub
	}

	boolean matches(String string, int offset) {
		// TODO Auto-generated method stub
	}
}