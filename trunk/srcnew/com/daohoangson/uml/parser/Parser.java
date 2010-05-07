package com.daohoangson.uml.parser;

public class Parser {
	private Diagram diagram;
	private Grammar grammar;
	static public boolean debuging;

	public Parser(Diagram diagram) {
		// TODO Auto-generated constructor stub
	}

	static private String readFileAsString(File file) {
		// TODO Auto-generated method stub
	}

	public int parse(File file) {
		// TODO Auto-generated method stub
	}

	public void parse(String source) {
		// TODO Auto-generated method stub
	}

	private Token requires(Enumeration<Token> analyzer, int[] types) {
		// TODO Auto-generated method stub
	}

	private void skipTo(Enumeration<Token> analyzer, int type) {
		// TODO Auto-generated method stub
	}

	private void skipRightBrace(Enumeration<Token> analyzer, int opened) {
		// TODO Auto-generated method stub
	}

	private void parsePropertyAndMethod(Structure structure, Enumeration<Token> analyzer) {
		// TODO Auto-generated method stub
	}

	private void parseArgument(Structure method, Enumeration<Token> analyzer) {
		// TODO Auto-generated method stub
	}
}