package com.daohoangson.uml.parser;

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
