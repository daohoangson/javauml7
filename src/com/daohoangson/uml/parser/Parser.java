package com.daohoangson.uml.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

/**
 * A Jave source parser. It makes use of {@link LexicalAnalyzer} and put
 * structure found into a diagram. Parse everything from Class/Interface to
 * Argument. The content of methods is ignored.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class Parser {
	/**
	 * The target diagram which will get updated
	 */
	private Diagram diagram;
	private Grammar grammar;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debuging = false;

	/**
	 * Constructor. Accepts a diagram and set it own object in order to update
	 * later when something is parsed
	 * 
	 * @param diagram
	 *            the target diagram
	 */
	public Parser(Diagram diagram) {
		this.diagram = diagram;
		grammar = new GrammarOfJava();

		LexicalAnalyzer.debuging = Parser.debuging;
	}

	/**
	 * Reads file into a string. Simple enough
	 * 
	 * @param file
	 *            the source file
	 * @return the file content as a string
	 * @throws IOException
	 */
	static private String readFileAsString(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = new BufferedInputStream(new FileInputStream(
				file));
		f.read(buffer);
		return new String(buffer);
	}

	/**
	 * Parses file and directory (recursively). It does nothing except looking
	 * for .java source files to pass them to {@link #parse(String)} method
	 * 
	 * @param file
	 *            the file or directory
	 * @return number of file parsed
	 * @throws StructureException
	 * @throws ParserException
	 * @throws IOException
	 */
	public int parse(File file) throws ParseException, IOException {
		int parsed = 0;

		if (Parser.debuging) {
			System.err.println("Parsing " + file.getAbsolutePath());
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				parsed += parse(files[i]);
			}
		} else if (file.isFile()) {
			String path = file.getAbsolutePath();
			if (path.substring(path.length() - 5).equalsIgnoreCase(".java")) {
				parsed++;
				try {
					parse(Parser.readFileAsString(file));
				} catch (StructureException e) {
					throw new ParseException("Structure Hierarchy: "
							+ e.getMessage(), -1);
				}
			}
		}

		return parsed;
	}

	/**
	 * Parses source file content. Looks for classes and interfaces and adds
	 * them into the diagram.
	 * 
	 * @param source
	 *            the source content which needs parsing
	 * @throws StructureException
	 * @throws ParserException
	 * @throws StructureException
	 */
	public void parse(String source) throws ParseException, StructureException {
		LexicalAnalyzer analyzer = new LexicalAnalyzer(source, grammar);

		String packagename = "";
		Structure structure = null;
		int pending_type = -1;
		List<String> pending_modifiers = new LinkedList<String>();
		boolean flag_adding_parent = false;

		while (analyzer.hasMoreElements()) {
			Token token = analyzer.nextElement();

			switch (token.type) {
			case Token.CLASS:
			case Token.INTERFACE:
			case Token.ENUM:
				if (pending_type == -1) {
					pending_type = token.type;
				} else {
					throw new ParseException("Unexpected " + token.content,
							token.offset);
				}
				break;
			case Token.VISIBILITY:
			case Token.SCOPE:
				pending_modifiers.add(token.content);
				break;
			case Token.ABSTRACT:
				// ignore
				break;
			case Token.TYPE:
				if (flag_adding_parent) {
					structure.addParentName(token.content);
				} else {
					throw new ParseException(
							"Unexpected type " + token.content, token.offset);
				}
			case Token.NAME:
				if (flag_adding_parent) {
					structure.addParentName(token.content);
				} else {
					String name = token.content;
					String[] modifiers = pending_modifiers
							.toArray(new String[0]);
					switch (pending_type) {
					case Token.CLASS:
						structure = new Class(name, modifiers);
						break;
					case Token.INTERFACE:
						structure = new Interface(name, modifiers);
						break;
					case Token.ENUM:
						// skip enum
						skipTo(analyzer, Token.LBRACE);
						skipRightBrace(analyzer, 1);
						break;
					default:
						throw new ParseException("Unexpected name found: "
								+ name, token.offset);
					}
					pending_type = -1;
					pending_modifiers.clear();
				}
				break;
			case Token.EXTENDS:
			case Token.IMPLEMENTS:
				flag_adding_parent = true;
				break;

			case Token.LBRACE:
				flag_adding_parent = false;
				parsePropertyAndMethod(structure, analyzer);
				break;
			case Token.RBRACE:
				structure.setInfo("package", packagename);
				diagram.add(structure);
				structure = null;
				break;

			case Token.COMMA:
				if (flag_adding_parent) {
					// ignore
				} else {
					throw new ParseException("Unexpected ,", token.offset);
				}
				break;

			case Token.PACKAGE:
				packagename = requires(analyzer,
						new int[] { Token.PACKAGENAME }).content;
				requires(analyzer, new int[] { Token.COLON });
				break;
			case Token.IMPORT:
				requires(analyzer, new int[] { Token.PACKAGENAME });
				requires(analyzer, new int[] { Token.COLON });
				break;

			case -1:
				if (pending_type == -1 && pending_modifiers.size() == 0
						&& structure == null) {
					// all good
					return;
				} else if (Parser.debuging) {
					System.err.println(pending_type);
					System.err.println(pending_modifiers);
					System.err.println(structure);
				}

			default:
				throw new ParseException("Unexpected " + token.content,
						token.offset);
			}
		}
	}

	private Token requires(LexicalAnalyzer analyzer, int[] types)
			throws ParseException {
		if (!analyzer.hasMoreElements()) {
			throw new ParseException("Unexpected end of file", -1);
		}
		Token token;
		do {
			token = analyzer.nextElement(types);
		} while (token != null && token.type == Token.SPACE);

		if (token == null) {
			throw new ParseException("Unexpected end of file", -1);
		}

		for (int i = 0; i < types.length; i++) {
			if (types[i] == token.type) {
				return token;
			}
		}

		// TODO: Actually, this exception will never be raised because of type
		// filtering
		String types_string = "";
		for (int i = 0; i < types.length; i++) {
			if (types_string.length() > 0) {
				types_string += ", ";
			}
			types_string += types[i];
		}
		throw new ParseException("Unexpected " + token.content + ". Expecting "
				+ types_string, token.offset);
	}

	private void skipTo(LexicalAnalyzer analyzer, int type) {
		while (analyzer.hasMoreElements()) {
			Token token = analyzer.nextElement();
			if (token.type == type) {
				return;
			}
		}
	}

	private void skipRightBrace(LexicalAnalyzer analyzer, int opened) {
		if (Parser.debuging) {
			System.err.println("SkipRightBrace is in action!");
		}
		while (analyzer.hasMoreElements() && opened > 0) {
			Token token = analyzer.nextElement();
			switch (token.type) {
			case Token.LBRACE:
				opened++;
				if (Parser.debuging) {
					System.err.println("SkipRightBrace: opened = " + opened);
				}
				break;
			case Token.RBRACE:
				opened--;
				if (Parser.debuging) {
					System.err.println("SkipRightBrace: opened = " + opened);
				}
				break;
			}
		}
	}

	/**
	 * Parses the source file (continue from before). Looks for properties and
	 * methods. Add them to the container structure
	 * 
	 * @param structure
	 *            the container structure
	 * @param analyzer
	 *            the analyzer
	 * @throws StructureException
	 * @throws ParserException
	 */
	private void parsePropertyAndMethod(Structure structure,
			LexicalAnalyzer analyzer) throws StructureException, ParseException {
		List<String> pending_modifiers = new LinkedList<String>();
		String pending_name = null;
		String pending_type = null;
		Structure child = null;
		boolean flag_abstract_method = false;
		boolean flag_inside_interface = structure.getStructureName().equals(
				"Interface");

		if (Parser.debuging) {
			System.err.println("parsePropertyAndMethod for " + structure);
		}

		while (analyzer.hasMoreElements()) {
			Token token = analyzer.nextElement();

			switch (token.type) {
			case Token.VISIBILITY:
			case Token.SCOPE:
				pending_modifiers.add(token.content);
				break;
			case Token.ABSTRACT:
				flag_abstract_method = true;
				break;
			case Token.TYPE:
			case Token.NAME:
				if (pending_type == null) {
					pending_type = token.content;
					Token name = requires(analyzer, new int[] { Token.NAME,
							Token.LPAR });
					if (name.type == Token.NAME) {
						pending_name = name.content;
					} else {
						analyzer.pushback();
					}
				} else if (pending_type != null && token.type == Token.NAME) {
					pending_name = token.content;
				} else {
					throw new ParseException("Unexpected " + token.content,
							token.offset);
				}
				break;
			case Token.OPERATOR:
				if (token.content.equals("[]") && pending_type != null) {
					pending_type += "[]";
				} else {
					throw new ParseException("Unexpected operator "
							+ token.content, token.offset);
				}
				break;

			case Token.LPAR:
				if (pending_name == null && pending_type != null
						&& pending_type.equals(structure.getName())) {
					// must be a constructor
					pending_name = "" + pending_type;
					pending_type = null;
				}
				if (pending_name == null) {
					throw new ParseException("Unexpected '('. Expecting name",
							token.offset);
				}
				child = new Method(pending_name, pending_type,
						pending_modifiers.toArray(new String[0]));
				pending_name = null;
				pending_type = null;
				pending_modifiers.clear();
				parseArgument(child, analyzer);
				break;
			case Token.RPAR:
				structure.add(child);
				child = null;
				break;
			case Token.LBRACE:
				skipRightBrace(analyzer, 1);
				break;
			case Token.RBRACE:
				analyzer.pushback();
				return;
			case Token.COMMA:
				if (pending_name == null || pending_type == null) {
					throw new ParseException(
							"Unexpected ','. Expecting type with name",
							token.offset);
				}
				structure.add(new Property(pending_name, pending_type,
						pending_modifiers.toArray(new String[0])));
				pending_name = null;
				break;
			case Token.COLON:
				if (flag_abstract_method || flag_inside_interface) {
					flag_abstract_method = false; // switch back the flag
				} else {
					if (pending_name == null || pending_type == null) {
						throw new ParseException(
								"Unexpected ';'. Expecting type with name",
								token.offset);
					}
					structure.add(new Property(pending_name, pending_type,
							pending_modifiers.toArray(new String[0])));
					pending_name = null;
					pending_type = null;
					pending_modifiers.clear();
				}
				break;
			case Token.ASSIGN:
				if (pending_name == null || pending_type == null) {
					throw new ParseException(
							"Unexpected '='. Expecting type with name",
							token.offset);
				}
				structure.add(new Property(pending_name, pending_type,
						pending_modifiers.toArray(new String[0])));
				pending_name = null;
				pending_type = null;
				pending_modifiers.clear();
				skipTo(analyzer, Token.COLON);
				break;

			case Token.THROWS:
				skipTo(analyzer, Token.LBRACE);
				analyzer.pushback();
				break;

			default:
				throw new ParseException("Unexpected " + token.content,
						token.offset);
			}
		}

		// reach here? Should be an error
		throw new ParseException("Incomplete body for " + structure, 0);
	}

	/**
	 * Parses the source file (continue from before). Looks for method's
	 * arguments. Add them to the method
	 * 
	 * @param method
	 *            the target method
	 * @param analyzer
	 *            the analyzer
	 * @throws ParserException
	 * @throws StructureException
	 */
	private void parseArgument(Structure method, LexicalAnalyzer analyzer)
			throws ParseException, StructureException {
		String pending_type = null;
		String pending_name = null;
		boolean flag_awaiting = false;

		if (Parser.debuging) {
			System.err.println("parseArgument for " + method);
		}

		while (analyzer.hasMoreElements()) {
			Token token = analyzer.nextElement();

			switch (token.type) {
			case Token.TYPE:
			case Token.NAME:
				if (pending_type == null) {
					pending_type = token.content;
					Token name = requires(analyzer, new int[] { Token.NAME });
					pending_name = name.content;
				} else {
					throw new ParseException("Unexpected " + token.content,
							token.offset);
				}
				break;
			case Token.OPERATOR:
				if (token.content.equals("[]") && pending_type != null) {
					pending_type += "[]";
				} else {
					throw new ParseException("Unexpected operator "
							+ token.content, token.offset);
				}
				break;

			case Token.COMMA:
			case Token.RPAR:
				if (pending_type != null && pending_name != null) {
					method.add(new Argument(pending_name, pending_type));
					pending_name = null;
					pending_type = null;
					flag_awaiting = false;
				} else if (flag_awaiting) {
					throw new ParseException(
							"Unexpected '('. Expecting argument declaration",
							token.offset);
				}
				switch (token.type) {
				case Token.COMMA:
					flag_awaiting = true;
					break;
				case Token.RPAR:
					analyzer.pushback();
					return;
				}
				break;

			default:
				throw new ParseException("Unexpected " + token.content,
						token.offset);
			}
		}

		// reach here?
		// should be an error
		throw new ParseException("Incomplete declaration for " + method, 0);
	}
}