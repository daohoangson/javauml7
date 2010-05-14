package com.daohoangson.uml.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The base class for an automata. Provide basic related operations for
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

/**
 * An automata state. Contains edge(s) to next state(s).
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class AutomataState {
	/**
	 * A list containing {@link AutomataStateEdge} objects
	 */
	private List<AutomataStateEdge> edges = new LinkedList<AutomataStateEdge>();
	/**
	 * The length of the last matched string
	 */
	public int matched = 0;
	/**
	 * Determines if the state should send output to System.err on matched.
	 * Useful in debug
	 */
	public boolean cfg_alert_on_matched = false;

	/**
	 * Notifies that the state has been matched.
	 * 
	 * @param length
	 */
	public void matched(int length) {
		matched = length;
		if (cfg_alert_on_matched) {
			System.err.println("GOT ME!");
		}
	}

	/**
	 * Adds an edge to the edges list
	 * 
	 * @param ase
	 */
	void add(AutomataStateEdge ase) {
		edges.add(ase);
	}

	/**
	 * Shortcut to add a ANY edge
	 * 
	 * @param next
	 *            the next state
	 */
	void addAny(AutomataState next) {
		add(new AutomataStateEdge(next));
	}

	/**
	 * Shortcut to add an edge with a string
	 * 
	 * @param connection
	 *            the string connecting the 2 state
	 * @param next
	 *            the next state
	 */
	void add(String connection, AutomataState next) {
		add(new AutomataStateEdge(connection, next));
	}

	/**
	 * Shortcut to add an edge with a character
	 * 
	 * @param c
	 *            the character connection the 2 state
	 * @param next
	 *            the next state
	 */
	void add(char c, AutomataState next) {
		add(new AutomataStateEdge(c, next));
	}

	/**
	 * Shortcut to add a set of edges from an array of characters
	 * 
	 * @param cs
	 *            the array of characters
	 * @param next
	 *            the next state
	 */
	void add(char[] cs, AutomataState next) {
		for (int i = 0; i < cs.length; i++) {
			add(cs[i], next);
		}
	}

	/**
	 * Shortcut to add a set of edges from a range of characters
	 * 
	 * @param cstart
	 *            the starting character of the range
	 * @param cend
	 *            the ending character of the range (included)
	 * @param next
	 *            the next state
	 */
	void add(char cstart, char cend, AutomataState next) {
		if (cend > cstart) {
			// do an additional check, just in case
			// nobody loves infinitive loops
			for (char c = cstart; c <= cend; c++) {
				add(String.valueOf(c), next);
			}
		}
	}

	/**
	 * Shortcut to add 0..9 edges
	 * 
	 * @param next
	 *            the next state
	 */
	void addDigit(AutomataState next) {
		add('0', '9', next);
	}

	/**
	 * Shortcut to add a..z, A..Z edges
	 * 
	 * @param next
	 *            the next state
	 */
	void addAlphabet(AutomataState next) {
		add('a', 'z', next);
		add('A', 'Z', next);
	}

	/**
	 * Shortcut to add a..z, A..Z, 0..9 and _ edges
	 * 
	 * @param next
	 *            the next state
	 */
	void addAlphabetDigitDash(AutomataState next) {
		addAlphabet(next);
		addDigit(next);
		add('_', next);
	}

	/**
	 * Calculates the next match by finding a suitable next state
	 * 
	 * @param string
	 *            the string to matched
	 * @param offset
	 *            the offset to start looking
	 * @return the next state
	 */
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

/**
 * An automata state edge connecting 2 states together.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see AutomataState#add(AutomataStateEdge)
 * 
 */
class AutomataStateEdge {
	/**
	 * The edge type. Possible values:<br/>
	 * <ul>
	 * <li><strong>-1</strong> means connection is a string</li>
	 * <li><strong>0</strong> means will match any character</li>
	 * <li><strong>greater than 0</strong> means will match a specific character
	 * </li>
	 * </ul>
	 */
	int type;
	/**
	 * The connection string if available
	 */
	String connection = null;
	/**
	 * The target state
	 */
	AutomataState next;

	/**
	 * Constructor for an ANY match edge
	 * 
	 * @param next
	 *            the target state
	 */
	AutomataStateEdge(AutomataState next) {
		type = 0;
		this.next = next;
	}

	/**
	 * Constructor for a character match edge
	 * 
	 * @param c
	 *            the character
	 * @param next
	 *            the target state
	 */
	AutomataStateEdge(char c, AutomataState next) {
		type = c;
		this.next = next;
	}

	/**
	 * Constructor for a string match edge
	 * 
	 * @param connection
	 *            the string
	 * @param next
	 *            the target state
	 */
	AutomataStateEdge(String connection, AutomataState next) {
		type = -1;
		this.connection = connection;
		this.next = next;
	}

	/**
	 * Checks if this edge matches the string at the specified offset. Process
	 * based on the {@link #type} value
	 * 
	 * @param string
	 *            the string need matching
	 * @param offset
	 *            the offset to start looking
	 * @return true if matched found
	 */
	boolean matches(String string, int offset) {
		if (type == 0 && offset < string.length()) {
			// the connection is any character
			next.matched(1);
			return true;
		} else if (type > 0) {
			// the connection is a character
			char c = (char) type;
			if (string.charAt(offset) == c) {
				next.matched(1);
				return true;
			} else {
				return false;
			}
		} else {
			// the connection is a string
			int length = connection.length();
			if (string.length() < offset + length) {
				// the string doesn't have enough character left
				return false;
			}
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
