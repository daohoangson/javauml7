package com.daohoangson.uml.parser;

public class AutomataState {
	private List<AutomataStateEdge> edges;
	public int matched;
	public boolean cfg_alert_on_matched;

	public void matched(int length) {
		// TODO Auto-generated method stub
	}

	void add(AutomataStateEdge ase) {
		// TODO Auto-generated method stub
	}

	void addAny(AutomataState next) {
		// TODO Auto-generated method stub
	}

	void add(String connection, AutomataState next) {
		// TODO Auto-generated method stub
	}

	void add(char c, AutomataState next) {
		// TODO Auto-generated method stub
	}

	void add(char[] cs, AutomataState next) {
		// TODO Auto-generated method stub
	}

	void add(char cstart, char cend, AutomataState next) {
		// TODO Auto-generated method stub
	}

	void addDigit(AutomataState next) {
		// TODO Auto-generated method stub
	}

	void addAlphabet(AutomataState next) {
		// TODO Auto-generated method stub
	}

	void addAlphabetDigitDash(AutomataState next) {
		// TODO Auto-generated method stub
	}

	AutomataState next(String string, int offset) {
		// TODO Auto-generated method stub
	}
}