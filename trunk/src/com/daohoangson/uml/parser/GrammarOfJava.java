package com.daohoangson.uml.parser;

class GrammarOfJava extends Grammar {

	@Override
	protected void config() {
		add(new AutomataOfStatic(Token.VISIBILITY, new String[] { "public",
				"protected", "private" }));
		add(new AutomataOfStatic(Token.SCOPE, "static"));
		add(new AutomataOfStatic(Token.ABSTRACT, "abstract"));
		add(new AutomataOfStatic(Token.CLASS, "class"));
		add(new AutomataOfStatic(Token.INTERFACE, "interface"));
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
