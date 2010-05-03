package com.daohoangson.uml.parser;

class AutomataOfSpace extends Automata {

	public AutomataOfSpace() {
		setId(Token.SPACE);

		AutomataState s0 = getStartState();

		s0.add(' ', s0);
		s0.add('\r', s0);
		s0.add('\n', s0);
		s0.add('\t', s0);

		setAcceptStage(s0);

		// also accept comments
		AutomataState sSlash = new AutomataState();
		AutomataState sSingleBody = new AutomataState();
		AutomataState sSingleEnd = new AutomataState();
		AutomataState sBlockBody = new AutomataState();
		AutomataState sBlockEnd = new AutomataState();

		// single line comment
		s0.add('/', sSlash);
		sSlash.add('/', sSingleBody);
		sSingleBody.add('\r', sSingleEnd);
		sSingleBody.add('\n', sSingleEnd);
		sSingleBody.addAny(sSingleBody);

		// block comment
		// s0.add('/', sSlash);
		sSlash.add('*', sBlockBody);
		sBlockBody.add("*/", sBlockEnd);
		sBlockBody.addAny(sBlockBody);

		setAcceptStage(sSingleEnd);
		setAcceptStage(sBlockEnd);
	}
}
