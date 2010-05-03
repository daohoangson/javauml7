package com.daohoangson.uml.parser;

class GrammarOfJava extends Grammar {

	@Override
	protected void config() {
		add(new AutomataOfStatic(Token.VISIBILITY, "public"));
		add(new AutomataOfStatic(Token.VISIBILITY, "protected"));
		add(new AutomataOfStatic(Token.VISIBILITY, "private"));
		add(new AutomataOfStatic(Token.SCOPE, "static"));
		add(new AutomataOfStatic(Token.ABSTRACT, "abstract"));
		add(new AutomataOfStatic(Token.CLASS, "class"));
		add(new AutomataOfStatic(Token.INTERFACE, "interface"));
		add(new AutomataOfStatic(Token.EXTENDS, "extends"));
		add(new AutomataOfStatic(Token.IMPLEMENTS, "implements"));
		add(new AutomataOfStatic(Token.THROWS, "throws"));

		String[] keywords_ignored = { "final", "native", "strictfp",
				"synchronized", "transient", "volatile" };
		for (int i = 0; i < keywords_ignored.length; i++) {
			add(new AutomataOfStatic(Token.KEYWORD_IGNORED, keywords_ignored[i]));
		}
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

		for (int i = 0; i < Token.operators.length; i++) {
			add(new AutomataOfStatic(Token.OPERATOR, Token.operators[i]));
		}

		add(new AutomataOfName());
		add(new AutomataOfPackageName());
		add(new AutomataOfType());
		add(new AutomataOfSpace());
		add(new AutomataOfCharValue());
		add(new AutomataOfStringValue());
		add(new AutomataOfNumberValue());
	}
}
