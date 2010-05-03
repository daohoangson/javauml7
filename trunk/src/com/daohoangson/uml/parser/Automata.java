package com.daohoangson.uml.parser;

import java.util.LinkedList;
import java.util.List;

public abstract class Automata {
	private int id = -1;
	private AutomataState start = new AutomataState();
	private List<AutomataState> accepted_states = new LinkedList<AutomataState>();

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public AutomataState getStartState() {
		return start;
	}

	public void setAcceptStage(AutomataState state) {
		if (!accepted_states.contains(state)) {
			accepted_states.add(state);
		}
	}

	/**
	 * Looking for the next portion of string that matches this automata.
	 * 
	 * @param source
	 *            the string to proceed
	 * @param offset
	 *            the starting offset to proceed
	 * @return found string
	 */
	public String next(String source, int offset) {
		int length = 0;
		AutomataState current = start;

		for (int i = offset; i < source.length();) {
			AutomataState next = current.next(source, i);
			if (next != null) {
				length += next.matched;
				i += next.matched;
				current = next;
			} else {
				break;
			}
		}

		if (length > 0 && !accepted_states.contains(current)) {
			// the current state is not in accepted states list
			// we will remove the last character from the found string
			length--;
		}

		return source.substring(offset, offset + length);
	}
}
