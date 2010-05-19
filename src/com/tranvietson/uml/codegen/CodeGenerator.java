package com.tranvietson.uml.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.daohoangson.uml.structures.Structure;

public class CodeGenerator {
	/**
	 * The root path for generated source file(s)
	 */
	private String rootpath;
	/**
	 * Specifies if we should overwrite existed files
	 */
	private boolean overwrite;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debugging = false;

	/**
	 * Constructor with a root path
	 * 
	 * @param rootpath
	 *            the root path to use for source file(s)
	 */
	public CodeGenerator(String rootpath) {
		this(rootpath, false);
	}

	/**
	 * Constructor with a root path and the overwrite flag.
	 * 
	 * @param rootpath
	 *            the root path to use for source file(s)
	 * @param overwrite
	 *            set to true to overwrite without asking
	 */
	public CodeGenerator(String rootpath, boolean overwrite) {
		this.rootpath = rootpath;
		this.overwrite = overwrite;
	}

	/**
	 * Generates source file the a structure
	 * 
	 * @param structure
	 *            the structure that needs generating source file
	 * @return true if the process is complete without any errors
	 */
	public boolean generate(Structure structure) {
		// get the path separator (system dependent)
		String pathSeparator = File.separator;
		// get the line separator (system dependent)
		String lineSeparator = System.getProperty("line.separator");
		// get structure's children
		Structure[] children = structure.getChildren();

		try {
			// prepare the directory tree
			String path = rootpath;
			String packagename = structure.getInfo("package");
			if (packagename.length() > 0) {
				String[] packages = packagename.split("\\.");
				for (int j = 0; j < packages.length; j++) {
					path += pathSeparator + packages[j];
				}
			}
			new File(path).mkdirs();

			File source = new File(path + pathSeparator + structure.getName()
					+ ".java");
			if (source.exists() && !overwrite) {
				// the file existed and we do not have overwrite flag
				// stop here
				return false;
			}

			// setup the writer
			FileWriter fw = new FileWriter(source);
			BufferedWriter bw = new BufferedWriter(fw);

			// output the package
			if (packagename.length() > 0) {
				bw.write("package " + packagename + ";" + lineSeparator
						+ lineSeparator);
			}

			// auto import
			// TODO: figure it out if we need this or not
			// List<String> imports = new LinkedList<String>();
			// for (int j = 0; j < children.length; j++) {
			// Structure[] types = children[j].getTypeAsStructure();
			// for (int k = 0; k < types.length; k++) {
			// String importpackagename = types[k].getInfo("package");
			// if (importpackagename.length() > 0
			// && !imports.contains(importpackagename)) {
			// imports.add(importpackagename + "."
			// + types[k].getName());
			// }
			// }
			// }
			// if (imports.size() > 0) {
			// Iterator<String> itr = imports.iterator();
			// while (itr.hasNext()) {
			// bw.write("import " + itr.next() + ";" + lineSeparator);
			// }
			// bw.write(lineSeparator);
			// }

			// write visibility if it exists
			if (structure.getVisibility().length() > 0) {
				bw.write(structure.getVisibility() + " "); // visibility
			}

			// write abstract if it exists
			if (structure.checkUseAbstract()
					&& structure.getAbstract().length() > 0) {
				bw.write(structure.getAbstract() + " "); // abstract
			}

			// if it's a class
			if (structure.getStructureName().equals("Class")) {
				// class declaration
				bw.write("class " + structure.getName()); // Write class name

				// check if the class extends another class
				if (structure.getContainer() != null) {
					// write the relationship
					bw.write(" extends " + structure.getContainer().getName());
				}

				// check if the class implements interfaces
				if (structure.getParentsCount() > 0) {
					Structure[] interfaces = structure.getParents();
					// write the relationship(s)
					bw.write(" implements ");
					for (int j = 0; j < interfaces.length; j++) {
						if (j > 0) {
							// need "," between interfaces
							bw.write(", ");
						}
						// write the interface's name
						bw.write(interfaces[j].getName());
					}
				}
			} else {
				// this should be an interface
				// interface declaration
				bw.write("interface " + structure.getName());

				// check if the interface extends another interface
				if (structure.getContainer() != null) {
					bw.write(" extends " + structure.getContainer().getName());
				}
			}

			// ready for children
			bw.write(" {" + lineSeparator);

			for (int j = 0; j < children.length; j++) {
				// loop through all the children
				Structure child = children[j];

				// check if child is a Method
				if (child.getStructureName().equals("Method")) {
					bw.write(lineSeparator); // separating methods
				}

				bw.write("\t"); // indent

				// check if the child has a scope
				if (child.getScope().length() > 0) {
					bw.write(child.getScope() + " ");
				}
				// check if it has visibility
				if (child.getVisibility().length() > 0) {
					bw.write(child.getVisibility() + " ");
				}
				// check if it is abstract
				if (child.getAbstract().length() > 0) {
					bw.write(child.getAbstract() + " ");
				}
				// check if it has type
				if (child.getType() != null) {
					bw.write(child.getType() + " ");
				}
				bw.write(child.getName()); // write name

				// check if child is a Property
				if (child.getStructureName().equals("Property")) {
					bw.write(";" + lineSeparator); // end of property
				} else {
					// it should be a method now
					bw.write("("); // ready for arguments

					Structure[] arguments = child.getChildren();
					for (int k = 0; k < arguments.length; k++) {
						if (k > 0) {
							// need "," between arguments
							bw.write(", ");
						}

						// write arguments of the method
						bw.write(arguments[k].getType() + " "
								+ arguments[k].getName());
					}

					bw.write(") {" + lineSeparator); // ready for content

					// constructor is a null-type method
					if (child.getType() == null) {
						bw.write("\t\t// TODO Auto-generated constructor stub"
								+ lineSeparator);
					} else {
						// just a normal method
						bw.write("\t\t// TODO Auto-generated method stub"
								+ lineSeparator);
					}
					bw.write("\t}" + lineSeparator); // end of method
				}
			}

			bw.write("}"); // end of file

			bw.close();
			fw.close();
			return true; // finished everything beautifully
		} catch (IOException e) {
			// ignore any raised exceptions
			if (CodeGenerator.debugging) {
				e.printStackTrace();
			}
		}

		return false; // oops
	}
}
