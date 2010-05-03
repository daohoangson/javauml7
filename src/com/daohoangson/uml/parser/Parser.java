package com.daohoangson.uml.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	public int parse(File file) throws ParserException, IOException {
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
				} catch (Exception e) {
					throw new ParserException("Can not parse " + path, e);
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
	public void parse(String source) throws ParserException, StructureException {
		LexicalAnalyzer analyzer = new LexicalAnalyzer(source);

		List<String> modifiers = new LinkedList<String>();
		Structure structure = null;
		boolean adding_parent = false;

		while (analyzer.hasMoreElements()) {
			ParserToken token = analyzer.nextElement();
			if (Parser.debuging) {
				System.err.println(token.token);
			}
			switch (token.type) {
			case ParserToken.is_name:
				if (adding_parent) {
					structure.addParentName(token.token);
				} else {
					if (structure != null) {
						structure.setName(token.token);
					} else {
						throw new ParserException(
								"Undefined structure type for " + token.token);
					}
				}
				break;
			case ParserToken.is_modifier:
				modifiers.add(token.token);
				break;

			case ParserToken.is_left_brace:
				adding_parent = false;
				parsePropertyAndMethod(structure, analyzer);
				// the parsePropertyAndMethod method should proceed a right
				// brace by itself
				// we will finish this structure now
				diagram.add(structure);
				structure = null;
				break;

			case ParserToken.kw_class:
				structure = new Class(randomName(), modifiers
						.toArray(new String[0]));
				modifiers.clear();
				break;
			case ParserToken.kw_interface:
				structure = new Interface(randomName(), modifiers
						.toArray(new String[0]));
				modifiers.clear();
				break;
			case ParserToken.kw_extends:
			case ParserToken.kw_implements:
				adding_parent = true;
				break;

			case ParserToken.kw_package:
			case ParserToken.kw_import:
				analyzer.skipLine();
			}
		}
	}

	/**
	 * Builds a random valid name for classes/interfaces
	 * 
	 * @return the random name
	 */
	private String randomName() {
		return "randomName" + (int) Math.ceil((Math.random() * 100000));
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
			LexicalAnalyzer analyzer) throws StructureException,
			ParserException {
		List<String> modifiers = new LinkedList<String>();
		String name = null;
		String type = null;
		Structure child = null;
		boolean flag_abstract_method = false;
		boolean flag_inside_interface = structure.getStructureName().equals(
				"Interface");

		while (analyzer.hasMoreElements()) {
			ParserToken token = analyzer.nextElement();
			if (Parser.debuging) {
				System.err.println(token.token);
			}
			switch (token.type) {
			case ParserToken.is_name:
				if (type == null) {
					type = token.token;
				} else {
					name = token.token;
				}
				break;
			case ParserToken.is_modifier:
				modifiers.add(token.token);
				if (token.token.equals("abstract")) {
					flag_abstract_method = true;
				}
				break;

			case ParserToken.is_left_parentheses:
				if (name == null && type.equals(structure.getName())) {
					// constructor
					name = "" + type;
					type = null;
				}
				child = new Method(name, type, modifiers.toArray(new String[0]));
				name = null;
				type = null;
				modifiers.clear();
				parseArgument(child, analyzer);
				// the parseArgument method should proceed a right parentheses
				// by itself
				// we will finish this method now
				structure.add(child);
				child = null;
				break;
			case ParserToken.is_left_brace:
				analyzer.skipRightBrace();
				break;
			case ParserToken.is_right_brace:
				return;
			case ParserToken.op_assign:
			case ParserToken.is_colon:
				if (flag_abstract_method || flag_inside_interface) {
					flag_abstract_method = false; // switch back
				} else {
					if (token.type == ParserToken.op_assign || name != null
							&& type != null) {
						child = new Property(name, type, modifiers
								.toArray(new String[0]));
						structure.add(child);
					}
					name = null;
					type = null;
					modifiers.clear();
					child = null;
					switch (token.type) {
					case ParserToken.op_assign:
						analyzer.skipColon();
					}
				}
				break;

			case ParserToken.kw_throws:
				analyzer.skipLeftBrace();
				analyzer.skipRightBrace();
				break;
			}
		}

		// reach here? Should be an error
		throw new ParserException("Incomplete body for " + structure);
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
			throws ParserException, StructureException {
		String name = null;
		String type = null;
		Structure argument = null;

		while (analyzer.hasMoreElements()) {
			ParserToken token = analyzer.nextElement();
			if (Parser.debuging) {
				System.err.println(token.token);
			}
			switch (token.type) {
			case ParserToken.is_name:
				if (type == null) {
					type = token.token;
				} else {
					name = token.token;
				}
				break;
			case ParserToken.is_right_parentheses:
			case ParserToken.is_comma:
				if (name != null && type != null) {
					argument = new Argument(name, type);
					name = null;
					type = null;
					method.add(argument);
					argument = null;
				}
				switch (token.type) {
				case ParserToken.is_right_parentheses:
					return;
				}
				break;
			}
		}

		// reach here?
		// should be an error
		throw new ParserException("Incomplete declaration for " + method);
	}
}

/**
 * A Token being used in parsing source files.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class ParserToken {
	final static int is_empty = -1;
	final static int is_name = 0;
	final static int is_modifier = 1;

	final static int is_left_parentheses = 101;
	final static int is_comma = 102;
	final static int is_right_parentheses = 103;
	final static int is_left_brace = 104;
	final static int is_right_brace = 105;
	final static int is_colon = 106;
	final static int op_assign = 107;

	final static int kw_class = 901;
	final static int kw_interface = 902;
	final static int kw_extends = 903;
	final static int kw_implements = 904;

	final static int kw_throws = 801;

	final static int kw_package = 1001;
	final static int kw_import = 1002;

	int type = 0;
	String token;

	ParserToken(String token) {
		this.token = token;

		if (token.length() == 0) {
			type = ParserToken.is_empty;
		} else if (token.length() == 1) {
			char c = token.charAt(0);
			switch (c) {
			case '(':
				type = ParserToken.is_left_parentheses;
				break;
			case ',':
				type = ParserToken.is_comma;
				break;
			case ')':
				type = ParserToken.is_right_parentheses;
				break;
			case '{':
				type = ParserToken.is_left_brace;
				break;
			case '}':
				type = ParserToken.is_right_brace;
				break;
			case ';':
				type = ParserToken.is_colon;
				break;
			case '=':
				type = ParserToken.op_assign;
				break;
			}
		} else {
			if (token.equals("class")) {
				type = ParserToken.kw_class;
			} else if (token.equals("interface")) {
				type = ParserToken.kw_interface;
			} else if (token.equals("extends")) {
				type = ParserToken.kw_extends;
			} else if (token.equals("implements")) {
				type = ParserToken.kw_implements;
			} else if (token.equals("throws")) {
				type = ParserToken.kw_throws;
			} else if (token.equals("package")) {
				type = ParserToken.kw_package;
			} else if (token.equals("import")) {
				type = ParserToken.kw_import;
			} else if (isModifier(token)) {
				type = ParserToken.is_modifier;
			}
		}
	}

	private boolean isModifier(String token) {
		if (token.equals("public") || token.equals("protected")
				|| token.equals("private") || token.equals("static")
				|| token.equals("abstract") || token.equals("final")
				|| token.equals("native") || token.equals("strictfp")
				|| token.equals("synchronized") || token.equals("transient")
				|| token.equals("volatile")) {
			return true;
		}

		return false;
	}
}
