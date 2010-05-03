package com.daohoangson.uml.parser;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * The lexical analyzer which parse java syntax into the correct
 * {@link ParserToken}
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class LexicalAnalyzer implements Enumeration<ParserToken> {
	/**
	 * The primary <code>StringTokenizer</code> used through out the methods
	 */
	private StringTokenizer st;
	/**
	 * The default delimiters
	 */
	private String delim = " (){}<>,;\r\n\"'\\";
	/**
	 * Parsing flag: specifies that we should handle angle statement
	 * automatically
	 */
	public boolean flag_handle_angle = true;
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
	public LexicalAnalyzer(String source) {
		st = new StringTokenizer(source, delim, true);
	}

	@Override
	public boolean hasMoreElements() {
		return st.hasMoreElements();
	}

	/**
	 * Returns the next token. Automatically skips comment (both comment line
	 * and comment block). Automatically skips directive. Automatically skips
	 * strings
	 */
	@Override
	public ParserToken nextElement() {
		String token = "";

		while (token.length() == 0 && st.hasMoreElements()) {
			token = st.nextToken(delim).trim();

			if (token.length() > 0) {
				if (token.charAt(0) == '@') {
					skipLine();
					token = "";
					continue;
				}

				if (token.charAt(0) == '\'') {
					token += getChar();
					continue;
				}

				if (token.charAt(0) == '"') {
					token += getString();
					continue;
				}

				if (token.length() >= 2) {
					String first2 = token.substring(0, 2);
					if (first2.equals("/*")) {
						// skip comment block
						skipCommentBlockClosure();
						token = "";
						continue;
					} else if (first2.equals("//")) {
						// skip comment line
						skipLine();
						token = "";
						continue;
					}
				}

				if (token.indexOf('<') != -1) {
					if (flag_handle_angle) {
						token += getAngle();
						continue;
					} else {
						if (LexicalAnalyzer.debuging) {
							System.err
									.println("Prevented from handling angle statement");
						}
					}
				}
			}
		}

		return new ParserToken(token);
	}

	/**
	 * Skips to the next comment block closing symbol
	 */
	private void skipCommentBlockClosure() {
		if (LexicalAnalyzer.debuging) {
			System.err.println("LexicalAnalyzer: skipping comment block");
		}
		while (st.hasMoreElements()) {
			String token = st.nextToken(delim);
			if (token.length() >= 2) {
				if (token.substring(token.length() - 2).equals("*/")) {
					return;
				}
			}
		}
	}

	/**
	 * Skips to the next left brace symbol
	 */
	public void skipLeftBrace() {
		if (LexicalAnalyzer.debuging) {
			System.err.println("LexicalAnalyzer: skipping left brace");
		}

		int type = -1;

		do {
			type = nextElement().type;
		} while (type != ParserToken.is_left_brace);
	}

	/**
	 * Skip to the next right brace symbol. This method assumes we are inside a
	 * left brace already!
	 * 
	 * @throws ParserException
	 */
	public void skipRightBrace() throws ParserException {
		if (LexicalAnalyzer.debuging) {
			System.err.println("LexicalAnalyzer: skipping right brace... ");
		}

		boolean flag = flag_handle_angle;
		flag_handle_angle = false;
		int opened_count = 1;

		while (opened_count > 0) {
			ParserToken token = nextElement();
			if (LexicalAnalyzer.debuging) {
				System.err.println("Skipped: " + token.token + " ("
						+ opened_count + ")");
			}
			int type = token.type;
			switch (type) {
			case ParserToken.is_left_brace:
				opened_count++;
				break;
			case ParserToken.is_right_brace:
				opened_count--;
				break;
			}
		}

		if (LexicalAnalyzer.debuging) {
			System.err.println("Final opened_count = " + opened_count);
		}

		flag_handle_angle = flag; // change back the flag

		if (opened_count > 0) {
			// there must be something wrong
			throw new ParserException("Can not find the correct right brace!");
		}
	}

	/**
	 * Skips the current line
	 */
	public void skipLine() {
		String token = st.nextToken("\r\n");
		if (LexicalAnalyzer.debuging) {
			System.err.println("skipLine: " + token);
		}
	}

	/**
	 * Skips the next colon symbol (;)
	 */
	public void skipColon() {
		while (true) {
			ParserToken token = nextElement();
			if (LexicalAnalyzer.debuging) {
				System.err.println("skipColon: " + token.token);
			}
			if (token.type == ParserToken.is_colon) {
				return;
			}
		}
	}

	/**
	 * Gets the character starting from current position. This method assumes we
	 * are in a char already! It can handle escape characters without any
	 * problem
	 * 
	 * @return the found character
	 */
	public String getChar() {
		String chr = "";
		boolean escaping = false;

		while (chr.length() == 0 || escaping) {
			String token = st.nextToken("\\'");
			chr += token;

			if (escaping) {
				escaping = false;
			} else if (token.charAt(0) == '\\') {
				escaping = true;
			}
		}

		chr += st.nextToken("'");

		return chr;
	}

	/**
	 * Gets the entire string starting from current position. This method
	 * assumes we are in a string already! It can handle escape characters
	 * without any problem
	 * 
	 * @return the found string
	 */
	public String getString() {
		String str = "";
		boolean escaping = false;

		while (true) {
			String token = st.nextToken("\\\"");
			str += token;
			if (LexicalAnalyzer.debuging) {
				System.err.println("String building: " + str + "(" + escaping
						+ ")");
			}

			if (token.length() == 1) {
				char c = token.charAt(0);
				if (c == '"' && !escaping) {
					return str;
				}

				if (escaping) {
					escaping = false;
				} else if (c == '\\') {
					escaping = true;
				}
			} else {
				if (escaping) {
					escaping = false;
				}
			}
		}
	}

	/**
	 * Gets the entire statement inside <> starting from current position. This
	 * method assumes we are behind a open angle (<) already!
	 * 
	 * @return the found statement
	 */
	public String getAngle() {
		String str = "";
		String token = "";

		do {
			token = st.nextToken(">");
			str += token;
		} while (!token.equals(">"));

		return str;
	}
}
