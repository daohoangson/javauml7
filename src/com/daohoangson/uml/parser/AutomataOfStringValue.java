package com.daohoangson.uml.parser;

/**
 * An automata for Java String value.<br/>
 * RegEx equivalent: <code>"(.|\\.)*"</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfStringValue extends Automata {

	public AutomataOfStringValue() {
		setId(Token.VALUE_STRING);

		AutomataState sBody = new AutomataState();
		AutomataState sEscape = new AutomataState();
		AutomataState sEnd = new AutomataState();

		getStartState().add('"', sBody);

		sBody.add('"', sEnd);
		sBody.add('\\', sEscape);
		sBody.addAny(sBody);
		sEscape.addAny(sBody);

		setAcceptStage(sEnd);
	}
}
