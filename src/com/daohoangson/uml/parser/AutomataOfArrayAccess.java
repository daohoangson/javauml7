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

		AutomataState sOpen = new AutomataState();
		AutomataState sIndex = new AutomataState();
		AutomataState sIndexCastStart = new AutomataState();
		AutomataState sIndexCastBody = new AutomataState();
		AutomataState sIndexCastEnd = new AutomataState();
		AutomataState sClose = new AutomataState();

		getStartState().add('[', sOpen);

		sOpen.addAlphabetDigitDash(sIndex);
		sOpen.add('(', sIndexCastStart);
		sIndexCastStart.addAlphabetDigitDash(sIndexCastBody);
		sIndexCastBody.addAlphabetDigitDash(sIndexCastBody);
		sIndexCastBody.add(')', sIndexCastEnd);
		sIndexCastEnd.addAny(sIndex);

		sIndex.addAlphabetDigitDash(sIndex);
		sIndex.add(' ', sIndex);
		sIndex.add('(', sIndex);
		sIndex.add(')', sIndex);
		for (int i = 0; i < Token.operators.length; i++) {
			sIndex.add(Token.operators[i], sIndex);
		}
		sIndex.add(']', sClose);

		sClose.add('[', sOpen);

		setAcceptStage(sClose);
	}
}
