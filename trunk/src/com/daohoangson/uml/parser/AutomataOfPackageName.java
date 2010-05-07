package com.daohoangson.uml.parser;

/**
 * Automata for Java package name. Quite the same as {@link AutomataOfName} but
 * with dots.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfPackageName extends Automata {

	AutomataOfPackageName() {
		setId(Token.PACKAGENAME);

		AutomataState sStart = new AutomataState();
		AutomataState sDot = new AutomataState();
		AutomataState sBody = new AutomataState();
		AutomataState sStar = new AutomataState();

		getStartState().addAlphabet(sStart);
		getStartState().add('_', sStart);

		sStart.addAlphabetDigitDash(sBody);
		sBody.addAlphabetDigitDash(sBody);

		sStart.add('.', sDot);
		sBody.add('.', sDot);

		sDot.addAlphabet(sStart);
		sDot.add('_', sStart);
		sDot.add('*', sStar);

		setAcceptStage(sStart);
		setAcceptStage(sBody);
		setAcceptStage(sStar);
	}
}
