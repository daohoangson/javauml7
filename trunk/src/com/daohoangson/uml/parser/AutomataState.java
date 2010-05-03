package com.daohoangson.uml.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AutomataState {
	private List<AutomataStateEdge> edges = new LinkedList<AutomataStateEdge>();
	public int matched = 0;
	public boolean alert_on_matched = false;
	static public boolean debuging = true; // use with yourown risk

	public void matched(int length) {
		matched = length;
		if (alert_on_matched) {
			System.err.println("GOT ME!");
		}
	}

	void add(AutomataStateEdge ase) {
		edges.add(ase);
	}

	void addAny(AutomataState next) {
		add(new AutomataStateEdge(next));
	}

	void add(String connection, AutomataState next) {
		add(new AutomataStateEdge(connection, next));
	}

	void add(char c, AutomataState next) {
		add(String.valueOf(c), next);
	}

	void add(char[] cs, AutomataState next) {
		for (int i = 0; i < cs.length; i++) {
			add(cs[i], next);
		}
	}

	void add(char cstart, char cend, AutomataState next) {
		if (cend > cstart) {
			// do an additional check, just in case
			// nobody loves infinitive loops
			for (char c = cstart; c <= cend; c++) {
				add(String.valueOf(c), next);
			}
		}
	}

	void addDigit(AutomataState next) {
		add('0', '9', next);
	}

	void addAlphabet(AutomataState next) {
		add('a', 'z', next);
		add('A', 'Z', next);
	}

	void addAlphabetDigitDash(AutomataState next) {
		addAlphabet(next);
		addDigit(next);
		add('_', next);
	}

	AutomataState next(String string, int offset) {
		Iterator<AutomataStateEdge> itr = edges.iterator();
		while (itr.hasNext()) {
			AutomataStateEdge edge = itr.next();
			if (edge.matches(string, offset)) {
				return edge.next;
			}
		}
		return null;
	}
}

class AutomataStateEdge {
	final static int ANY = 1;
	int type = 0;
	String connection = "";
	AutomataState next;

	AutomataStateEdge(String connection, AutomataState next) {
		this.connection = connection;
		this.next = next;
	}

	AutomataStateEdge(AutomataState next) {
		type = AutomataStateEdge.ANY;
		this.next = next;
	}

	boolean matches(String string, int offset) {
		if (type == AutomataStateEdge.ANY && offset < string.length()) {
			next.matched(1);
			return true;
		} else {
			int length = connection.length();
			String substr = string.substring(offset, offset + length);
			if (connection.equals(substr)) {
				next.matched(length);
				return true;
			} else {
				return false;
			}
		}
	}
}