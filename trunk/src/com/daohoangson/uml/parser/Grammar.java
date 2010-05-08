package com.daohoangson.uml.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The base class for a grammar. Provides basic methods of a grammar
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
public abstract class Grammar {
	/**
	 * A list of automata in the grammar
	 */
	private List<Automata> automatas;

	/**
	 * Specifies grammar configuration. Subclasses must override this method
	 */
	protected abstract void config();

	public Grammar() {
		automatas = new LinkedList<Automata>();
		config();
	}

	/**
	 * Adds a new automata into the grammar
	 * 
	 * @param a
	 *            the new automata
	 */
	public void add(Automata a) {
		automatas.add(a);
	}

	/**
	 * Finds the next matched {@link GrammarMatch} with grammar's automata
	 * 
	 * @param source
	 *            the string to find matches
	 * @param offset
	 *            the starting offset
	 * @param types
	 *            an array of types limitation. Use zero-length array to accept
	 *            all automata type. If an array is specified, only automata
	 *            with that type is processed, others will be ignored
	 * @return the matched <code>GrammarMatch</code>
	 */
	public GrammarMatch next(String source, int offset, int[] types) {
		Automata found_automata = null;
		String found = "";

		Iterator<Automata> itr = automatas.iterator();
		while (itr.hasNext()) {
			Automata automata = itr.next();

			if (types.length > 0) {
				// there is a types filtering
				boolean type_matched = false;
				int id = automata.getId();
				for (int i = 0; i < types.length; i++) {
					if (types[i] == id || id == 0) {
						type_matched = true;
						break;
					}
				}
				if (!type_matched) {
					// skip this automata
					continue;
				}
			}

			String str = automata.next(source, offset);
			if (str.length() > found.length()) {
				found_automata = automata;
				found = str;
			}
		}

		return new GrammarMatch(found_automata, found);
	}
}

/**
 * A grammar match object. Containing the source automata and the found string
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class GrammarMatch {
	Automata automata;
	String found;

	GrammarMatch(Automata automata, String found) {
		this.automata = automata;
		this.found = found;
	}
}
