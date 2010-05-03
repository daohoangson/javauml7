package com.daohoangson.uml.parser;

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
