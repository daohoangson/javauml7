package com.daohoangson.uml.parser;

/**
 * An automata for Java name.<br/>
 * RegEx equivalent: <code>[a-zA-Z_][a-zA-Z0-9_]*</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfName extends Automata {

	AutomataOfName() {
		setId(Token.NAME);

		AutomataState sFirst = new AutomataState();
		AutomataState sBody = new AutomataState();

		getStartState().addAlphabet(sFirst);
		getStartState().add('_', sFirst);

		sFirst.addAlphabetDigitDash(sBody);
		sBody.addAlphabetDigitDash(sBody);

		setAcceptStage(sFirst);
		setAcceptStage(sBody);
	}
}
