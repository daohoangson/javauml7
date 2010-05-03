package com.daohoangson.uml.parser;

class AutomataOfAnnotation extends Automata {
	public AutomataOfAnnotation() {
		setId(Token.ANNOTATION);

		AutomataState sStart = new AutomataState();
		AutomataState sBody = new AutomataState();
		AutomataState sParamStart = new AutomataState();
		AutomataState sParamBody = new AutomataState();
		AutomataState sParamEnd = new AutomataState();

		getStartState().add('@', sStart);

		sStart.addAlphabet(sBody);
		sBody.addAlphabet(sBody);

		sBody.add('(', sParamStart);
		sParamStart.addAny(sParamBody);
		sParamBody.add(')', sParamEnd);
		sParamBody.addAny(sParamBody);

		setAcceptStage(sBody);
		setAcceptStage(sParamEnd);
	}
}
