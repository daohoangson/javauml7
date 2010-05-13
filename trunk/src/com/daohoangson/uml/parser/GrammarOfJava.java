package com.daohoangson.uml.parser;

/**
 * A grammar of Java language. This grammar makes use of several different type
 * of automata
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class GrammarOfJava extends Grammar {

	/**
	 * Setup a long list of automata. Includes
	 * <ul>
	 * <li>Modifiers</li>
	 * <li>Keywords</li>
	 * <li>Symbols, operators</li>
	 * </ul>
	 * 
	 * @see AutomataOfAnnotation
	 * @see AutomataOfArrayAccess
	 * @see AutomataOfCharValue
	 * @see AutomataOfName
	 * @see AutomataOfNumberValue
	 * @see AutomataOfPackageName
	 * @see AutomataOfSpace
	 * @see AutomataOfStatic
	 * @see AutomataOfStringValue
	 * @see AutomataOfType
	 */
	@Override
	protected void config() {
		add(new AutomataOfStatic(Token.VISIBILITY, new String[] { "public",
				"protected", "private" }));
		add(new AutomataOfStatic(Token.SCOPE, "static"));
		add(new AutomataOfStatic(Token.ABSTRACT, "abstract"));
		add(new AutomataOfStatic(Token.CLASS, "class"));
		add(new AutomataOfStatic(Token.INTERFACE, "interface"));
		add(new AutomataOfStatic(Token.ENUM, "enum"));
		add(new AutomataOfStatic(Token.EXTENDS, "extends"));
		add(new AutomataOfStatic(Token.IMPLEMENTS, "implements"));
		add(new AutomataOfStatic(Token.THROWS, "throws"));

		add(new AutomataOfStatic(Token.KEYWORD_IGNORED, new String[] { "final",
				"native", "strictfp", "synchronized", "transient", "volatile" }));
		add(new AutomataOfArrayAccess());

		add(new AutomataOfAnnotation());
		add(new AutomataOfStatic(Token.PACKAGE, "package"));
		add(new AutomataOfStatic(Token.IMPORT, "import"));

		add(new AutomataOfStatic(Token.LPAR, '('));
		add(new AutomataOfStatic(Token.RPAR, ')'));
		add(new AutomataOfStatic(Token.LBRACE, '{'));
		add(new AutomataOfStatic(Token.RBRACE, '}'));
		add(new AutomataOfStatic(Token.COMMA, ','));
		add(new AutomataOfStatic(Token.COLON, ';'));
		add(new AutomataOfStatic(Token.ASSIGN, '='));
		add(new AutomataOfStatic(Token.OPERATOR, Token.operators));

		add(new AutomataOfName());
		add(new AutomataOfPackageName());
		add(new AutomataOfType());
		add(new AutomataOfSpace());
		add(new AutomataOfCharValue());
		add(new AutomataOfStringValue());
		add(new AutomataOfNumberValue());
	}
}

/**
 * An automata for Java annotation.<br/>
 * RegEx equivalent: <code>@[a-zA-Z]*(\(.*\))?</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
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

/**
 * An automata for Java array access.<br/>
 * RegEx equivalent:
 * <code>(\[(\([a-zA-Z0-9_]+\))?[a-zA-Z0-9_ \(\)\operator]*\])+</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
class AutomataOfArrayAccess extends Automata {
	public AutomataOfArrayAccess() {
		setId(Token.KEYWORD_IGNORED);

		AutomataState sIndex = new AutomataState();
		AutomataState sClose = new AutomataState();

		getStartState().add('[', sIndex);

		sIndex.addAlphabetDigitDash(sIndex);
		sIndex.add(' ', sIndex);
		sIndex.add('(', sIndex);
		sIndex.add(')', sIndex);
		for (int i = 0; i < Token.operators.length; i++) {
			sIndex.add(Token.operators[i], sIndex);
		}
		sIndex.add(']', sClose);

		sClose.add('[', sIndex);

		setAcceptStage(sClose);
	}
}

/**
 * An automata for Java character value.<br/>
 * RegEx equivalent: <code>'\\?.'</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
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

/**
 * An automata for Java space. Spaces consist of blank space, line feeds, tabs
 * and comments (both single line comment and block comment)
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
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

/**
 * An automata for static strings/characters
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class AutomataOfStatic extends Automata {
	public AutomataOfStatic(int id, String[] strings) {
		setId(id);

		AutomataState sStart = getStartState();
		AutomataState sFinish = new AutomataState();
		for (int i = 0; i < strings.length; i++) {
			sStart.add(strings[i], sFinish);
		}

		setAcceptStage(sFinish);
	}

	public AutomataOfStatic(int id, char[] chars) {
		setId(id);

		AutomataState sStart = getStartState();
		AutomataState sFinish = new AutomataState();
		for (int i = 0; i < chars.length; i++) {
			sStart.add(chars[i], sFinish);
		}

		setAcceptStage(sFinish);
	}

	public AutomataOfStatic(int id, String string) {
		this(id, new String[] { string });
	}

	public AutomataOfStatic(int id, char c) {
		this(id, new char[] { c });
	}
}

/**
 * An automata for Java String value.<br/>
 * RegEx equivalent: <code>"(.|\\.)*"</code>
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see GrammarOfJava
 * 
 */
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
