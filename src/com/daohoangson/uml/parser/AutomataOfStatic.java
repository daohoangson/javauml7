package com.daohoangson.uml.parser;

/**
 * An automata for static strings/characters
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class AutomataOfStatic extends Automata {
	public AutomataOfStatic(int id, String[] strings) {
		setId(id);

		AutomataState sStart = getStartState();
		AutomataState sFinish = new AutomataState();
		for (int i = 0; i < strings.length; i++) {
			sStart.add(strings[i], sFinish);
		}

		setAcceptStage(sFinish);
	}

	public AutomataOfStatic(int id, char[] chars) {
		setId(id);

		AutomataState sStart = getStartState();
		AutomataState sFinish = new AutomataState();
		for (int i = 0; i < chars.length; i++) {
			sStart.add(chars[i], sFinish);
		}

		setAcceptStage(sFinish);
	}

	public AutomataOfStatic(int id, String string) {
		this(id, new String[] { string });
	}

	public AutomataOfStatic(int id, char c) {
		this(id, new char[] { c });
	}
}
