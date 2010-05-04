package com.daohoangson.uml.parser;

/**
 * An automata for Java character value.<br/>
 * RegEx equivalent: <code>'\\?.'</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfCharValue extends Automata {

	public AutomataOfCharValue() {
		setId(Token.VALUE_CHAR);

		AutomataState sStart = new AutomataState();
		AutomataState sBody = new AutomataState();
		AutomataState sEscape = new AutomataState();
		AutomataState sEnd = new AutomataState();

		getStartState().add('\'', sStart);

		sStart.add('\\', sEscape);
		sStart.addAny(sBody);
		sEscape.addAny(sBody);
		sBody.add('\'', sEnd);

		setAcceptStage(sEnd);
	}
}
