package com.daohoangson.uml.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Grammar {
	private List<Automata> automatas;

	protected abstract void config();

	public Grammar() {
		automatas = new LinkedList<Automata>();
		config();
	}

	public void add(Automata a) {
		automatas.add(a);
	}

	public GrammarMatch next(String source, int offset) {
		Automata found_automata = null;
		String found = "";

		Iterator<Automata> itr = automatas.iterator();
		while (itr.hasNext()) {
			Automata automata = itr.next();
			String str = automata.next(source, offset);
			if (str.length() > found.length()) {
				found_automata = automata;
				found = str;
			}
		}

		return new GrammarMatch(found_automata, found);
	}
}

class GrammarMatch {
	Automata automata;
	String found;

	GrammarMatch(Automata automata, String found) {
		this.automata = automata;
		this.found = found;
	}
}
