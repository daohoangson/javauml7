package com.daohoangson.uml.parser;

/**
 * A grammar of Java language. This grammar makes use of several different type
 * of automata
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class GrammarOfJava extends Grammar {

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
