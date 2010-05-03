package com.daohoangson.uml.parser;

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
