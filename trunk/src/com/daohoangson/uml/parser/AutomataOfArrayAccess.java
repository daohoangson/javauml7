package com.daohoangson.uml.parser;

/**
 * An automata for Java array access.<br/>
 * RegEx equivalent:
 * <code>(\[(\([a-zA-Z0-9_]+\))?[a-zA-Z0-9_ \(\)\operator]*\])+</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfArrayAccess extends Automata {
	public AutomataOfArrayAccess() {
		setId(Token.KEYWORD_IGNORED);

		AutomataState sIndex = new AutomataState();
		AutomataState sClose = new AutomataState();

		getStartState().add('[', sIndex);

		sIndex.addAlphabetDigitDash(sIndex);
		sIndex.add(' ', sIndex);
		sIndex.add('(', sIndex);
		sIndex.add(')', sIndex);
		for (int i = 0; i < Token.operators.length; i++) {
			sIndex.add(Token.operators[i], sIndex);
		}
		sIndex.add(']', sClose);

		sClose.add('[', sIndex);

		setAcceptStage(sClose);
	}
}
