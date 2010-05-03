package com.daohoangson.uml.parser;

class AutomataOfPackageName extends Automata {

	AutomataOfPackageName() {
		setId(Token.PACKAGENAME);

		AutomataState sStart = new AutomataState();
		AutomataState sDot = new AutomataState();
		AutomataState sBody = new AutomataState();

		getStartState().addAlphabet(sStart);
		getStartState().add('_', sStart);

		sStart.addAlphabetDigitDash(sBody);
		sBody.addAlphabetDigitDash(sBody);

		sStart.add('.', sDot);
		sBody.add('.', sDot);

		sDot.addAlphabet(sStart);
		sDot.add('_', sStart);

		setAcceptStage(sStart);
		setAcceptStage(sBody);
	}
}
