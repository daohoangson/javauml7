package com.daohoangson.uml.parser;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class LexicalAnalyzer implements Enumeration<ParserToken> {
	private StringTokenizer st;
	private String delim = " (){};\r\n\"\\";
	private int flag_in_parentheses = 0;
	static public boolean debuging = false;
	
	public LexicalAnalyzer(String source) {
		st = new StringTokenizer(source,delim,true);
	}

	@Override
	public boolean hasMoreElements() {
		return st.hasMoreElements();
	}

	@Override
	public ParserToken nextElement() {
		String token = "";
		
		while (token.length() == 0 && st.hasMoreElements()) {
			String delim_to_use = delim;
			if (flag_in_parentheses > 0) {
				delim_to_use += ",";
			}
			token = st.nextToken(delim_to_use).trim();
			
			if (token.length() > 0) {
				if (token.charAt(0) == '@') {
					skipLine();
					token = "";
					continue;
				}
				
				if (token.charAt(0) == '"') {
					token += getString();
				}
				
				if (token.length() >= 2) {
					String first2 = token.substring(0, 2); 
					if (first2.equals("/*")) {
						//skip comment block
						skipCommentBlockClosure();
						token = "";
						continue;
					} else if (first2.equals("//")) {
						//skip comment line
						skipLine();
						token = "";
						continue;
					}
				}
				
				if (token.equals("(")) {
					flag_in_parentheses++;
				} else if (token.equals(")")) {
					flag_in_parentheses--;
				}
			}
		}

		return new ParserToken(token);
	}
	
	private void skipCommentBlockClosure() {
		if (LexicalAnalyzer.debuging)
			System.err.println("LexicalAnalyzer: skipping comment block");
		while (st.hasMoreElements()) {
			String token = st.nextToken(delim);
			if (token.length() >= 2) {
				if (token.substring(token.length() - 2).equals("*/"))
					return;
			}
		}
	}
	
	public void skipLeftBrace() {
		if (LexicalAnalyzer.debuging)
			System.err.println("LexicalAnalyzer: skipping left brace");
		
		int type = -1;
		
		do {
			type = new ParserToken(st.nextToken(delim)).type;
		} while (type != ParserToken.is_left_brace);
	}
	
	public void skipRightBrace() throws ParserException {
		if (LexicalAnalyzer.debuging)
			System.err.println("LexicalAnalyzer: skipping right brace... ");
		
		int opened_count = 1;
		
		while (opened_count > 0 && hasMoreElements()) {
			ParserToken token = nextElement();
			if (LexicalAnalyzer.debuging)
				System.err.println("Skipped: " + token.token + " (" + opened_count + ")");
			int type = token.type;
			switch (type) {
			case ParserToken.is_left_brace: opened_count++; break;
			case ParserToken.is_right_brace: opened_count--; break;
			}
		}
		
		if (LexicalAnalyzer.debuging)
			System.err.println("Final opened_count = " + opened_count);
		
		if (opened_count > 0) {
			//there must be something wrong
			throw new ParserException("Can not find the correct right brace!");
		}
	}
	
	public void skipLine() {
		String token = st.nextToken("\r\n");
		if (debuging) System.err.println("skipLine: " + token);
	}
	
	public String getString() {
		String str = "";
		boolean escaping = false;
		
		while (st.hasMoreTokens()) {
			String token = st.nextToken(delim);
			str += token;
			
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
			}
		}
		
		return str;
	}
}
