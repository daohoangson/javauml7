package com.daohoangson.uml.parser;

/**
 * An automata for Java type string. Supports generalized type and array.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfType extends Automata {

	AutomataOfType() {
		setId(Token.TYPE);

		AutomataState sFirst = new AutomataState();
		AutomataState sBody = new AutomataState();
		AutomataState sGeneralized = new AutomataState();
		AutomataState sGeneralizedFirst = new AutomataState();
		AutomataState sGeneralizedBody = new AutomataState();
		AutomataState sGeneralizedEnd = new AutomataState();
		AutomataState sArray = new AutomataState();
		AutomataState sArrayEnd = new AutomataState();

		getStartState().addAlphabet(sFirst);
		getStartState().add('_', sFirst);

		sFirst.addAlphabetDigitDash(sBody);
		sBody.addAlphabetDigitDash(sBody);

		sFirst.add('<', sGeneralized);
		sBody.add('<', sGeneralized);

		sGeneralized.addAlphabet(sGeneralizedFirst);
		sGeneralized.add('_', sGeneralizedFirst);
		sGeneralized.add(' ', sGeneralized);

		sGeneralizedFirst.addAlphabetDigitDash(sGeneralizedBody);
		sGeneralizedBody.addAlphabetDigitDash(sGeneralizedBody);

		sGeneralizedFirst.add(',', sGeneralized);
		sGeneralizedFirst.add(' ', sGeneralized);
		sGeneralizedBody.add(',', sGeneralized);
		sGeneralizedBody.add(' ', sGeneralized);

		sGeneralizedFirst.add('>', sGeneralizedEnd);
		sGeneralizedBody.add('>', sGeneralizedEnd);

		sFirst.add('[', sArray);
		sBody.add('[', sArray);
		sGeneralizedEnd.add('[', sArray);
		sArrayEnd.add('[', sArray);
		sArray.add(']', sArrayEnd);

		setAcceptStage(sFirst);
		setAcceptStage(sBody);
		setAcceptStage(sGeneralizedEnd);
		setAcceptStage(sArrayEnd);
	}

}
