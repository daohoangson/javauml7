package com.daohoangson.uml.parser;

import java.util.LinkedList;
import java.util.List;

/**
 * The case class for an automata. Provide basic related operations for
 * automata. This is an abstract class so sub classes are needed all the time
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public abstract class Automata {
	/**
	 * ID of the automata. Default value is -1. New value should be a
	 * non-negative value
	 * 
	 * @see #setId(int)
	 * @see #getId()
	 */
	private int id = -1;
	/**
	 * The starting state. Every automata has this start state. Refer to
	 * automata concept/theory to understand
	 */
	private AutomataState start = new AutomataState();
	/**
	 * A list of accepted states. Not ALL states can be accepted so this need to
	 * be added one by one using {@link #setAcceptStage(AutomataState)}
	 */
	private List<AutomataState> accepted_states = new LinkedList<AutomataState>();

	/**
	 * Sets the automata id to help distinguish automatas later
	 * 
	 * @param id
	 *            the automata id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the automata id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the starting state
	 * 
	 * @return the starting state
	 */
	public AutomataState getStartState() {
		return start;
	}

	/**
	 * Add an accepted state
	 * 
	 * @param state
	 *            the state need adding
	 */
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
