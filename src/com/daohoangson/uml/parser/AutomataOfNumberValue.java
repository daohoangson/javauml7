package com.daohoangson.uml.parser;

/**
 * An automata for number value. Supports sign, exponent, etc
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfNumberValue extends Automata {
	public AutomataOfNumberValue() {
		setId(Token.VALUE_NUMBER);

		AutomataState sSign = new AutomataState();
		AutomataState sNumber = new AutomataState();
		AutomataState sDot = new AutomataState();
		AutomataState sDecimal = new AutomataState();
		AutomataState sE = new AutomataState();
		AutomataState sESign = new AutomataState();
		AutomataState sENumber = new AutomataState();

		getStartState().add('-', sSign);
		getStartState().add('+', sSign);

		getStartState().addDigit(sNumber);
		sSign.addDigit(sNumber);
		sNumber.addDigit(sNumber);

		sNumber.add('.', sDot);

		sDot.addDigit(sDecimal);
		sDecimal.addDigit(sDecimal);

		sNumber.add('E', sE);
		sNumber.add('e', sE);
		sDecimal.add('E', sE);
		sDecimal.add('e', sE);

		sE.add('-', sESign);
		sE.add('+', sESign);

		sE.addDigit(sENumber);
		sESign.addDigit(sENumber);
		sENumber.addDigit(sENumber);

		setAcceptStage(sNumber);
		setAcceptStage(sDecimal);
		setAcceptStage(sENumber);
	}
}
