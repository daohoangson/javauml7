package com.daohoangson.uml.parser;

import java.text.ParseException;
import java.util.Enumeration;

/**
 * The lexical analyzer which parse java syntax into the correct {@link Token}
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class LexicalAnalyzer implements Enumeration<Token> {
	private String source;
	private Grammar grammar;
	private int offset = 0;
	private Token lastToken = null;
	/**
	 * Determines if we are in debug mode
	 */
	static public boolean debuging = false;

	/**
	 * Constructor. Accepts a String prepared to be parsed
	 * 
	 * @param source
	 *            the source content
	 */
	public LexicalAnalyzer(String source, Grammar grammar) {
		this.source = source;
		this.grammar = grammar;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public boolean hasMoreElements() {
		return offset < source.length();
	}

	@Override
	public Token nextElement() {
		return nextElement(new int[0]);
	}

	/**
	 * Returns the next token. Automatically skips comment (both comment line
	 * and comment block). Automatically skips directive. Automatically skips
	 * strings
	 * 
	 * @param types
	 *            type filtering. Only automata that matches these types will
	 *            get processed
	 * @return
	 */
	public Token nextElement(int[] types) {
		GrammarMatch next = grammar.next(source, offset, types);

		int length = next.found.length();
		if (length == 0) {
			lastToken = new Token(-1, "EOF", 0);
			offset = source.length();
		} else {
			lastToken = new Token(next.automata.getId(), next.found, offset);
			offset += length;
		}

		switch (lastToken.type) {
		case Token.KEYWORD_IGNORED:
		case Token.SPACE:
		case Token.ANNOTATION:
			if (LexicalAnalyzer.debuging) {
				System.err.println("Auto Skipped: "
						+ lastToken.content.length() + " character(s)");
			}
			return nextElement(types);
		default:
			if (LexicalAnalyzer.debuging) {
				System.err.println(lastToken);
			}
			return lastToken;
		}
	}

	public void pushback() throws ParseException {
		if (LexicalAnalyzer.debuging) {
			System.err.println("pushback(): " + lastToken);
		}

		if (lastToken != null) {
			offset -= lastToken.content.length();
			lastToken = null;
		} else {
			throw new ParseException("Can NOT pushback() anymore!", offset);
		}
	}
}

class Token {
	final static int ERROR = -1;
	final static int SPACE = 0;
	final static int NAME = 1;
	final static int TYPE = 2;

	final static int KEYWORD = 100;
	final static int VISIBILITY = 101;
	final static int SCOPE = 102;
	final static int ABSTRACT = 103;

	final static int CLASS = 110;
	final static int INTERFACE = 111;
	final static int ENUM = 112;
	final static int EXTENDS = 113;
	final static int IMPLEMENTS = 114;
	final static int THROWS = 115;

	final static int KEYWORD_IGNORED = 500;
	final static int ANNOTATION = 501;
	final static int PACKAGE = 502;
	final static int IMPORT = 503;
	final static int PACKAGENAME = 504;

	final static int LPAR = 1001;
	final static int RPAR = 1002;
	final static int LBRACE = 1003;
	final static int RBRACE = 1004;
	final static int COMMA = 1005;
	final static int COLON = 1006;
	final static int ASSIGN = 1007;
	final static int OPERATOR = 1100;
	final static String[] operators = { "+", "-", "*", "/", "%", "==", "!",
			"!=", ">", "<", ">=", "<=", "&&", "||", ".", "?", ":", "[]" };

	final static int VALUE_CHAR = 2001;
	final static int VALUE_STRING = 2002;
	final static int VALUE_NUMBER = 2003;

	int type;
	String content;
	int offset;

	Token(int type, String content, int offset) {
		this.type = type;
		this.content = content;
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "'" + content + "' (" + type + ")";
	}
}
