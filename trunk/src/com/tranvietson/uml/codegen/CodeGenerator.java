package com.tranvietson.uml.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

public class CodeGenerator {
	private Diagram diagram;

	public CodeGenerator(Diagram diagram) {
		this.diagram = diagram;
	}

	public int generate(String rootpath) {
		Structure[] structures = diagram.getStructures();
		String pathSeparator = File.separator;
		String lineSeparator = System.getProperty("line.separator");
		int files = 0;

		for (int i = 0; i < structures.length; i++) {
			Structure structure = structures[i];
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

				// setup the writer
				FileWriter fw = new FileWriter(path + pathSeparator
						+ structure.getName() + ".java");
				BufferedWriter bw = new BufferedWriter(fw);

				// output the package
				if (packagename.length() > 0) {
					bw.write("package " + packagename + ";" + lineSeparator
							+ lineSeparator);
				}

				// auto import
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

				if (structure.getVisibility().length() > 0) {
					bw.write(structure.getVisibility() + " "); // visibility
				}

				if (structure.getStructureName().equals("Class")) {
					// class declaration
					bw.write("class " + structure.getName()); // class name
					if (structure.getContainer() != null) {
						bw.write(" extends "
								+ structure.getContainer().getName()); // ancestor
					}
					if (structure.getParentsCount() > 0) {
						Structure[] interfaces = structure.getParents();
						bw.write(" implements ");
						for (int j = 0; j < interfaces.length; j++) {
							if (j > 0) {
								bw.write(", ");
							}
							bw.write(interfaces[j].getName()); // interfaces
						}
					}
				} else {
					// interface declaration
					bw.write("interface " + structure.getName()); // interface
					// name
					if (structure.getContainer() != null) {
						bw.write(" extends "
								+ structure.getContainer().getName()); // ancestor
					}
				}

				bw.write(" {" + lineSeparator); // ready for children

				for (int j = 0; j < children.length; j++) {
					Structure child = children[j];

					if (child.getStructureName().equals("Method")) {
						bw.write(lineSeparator); // separating methods
					}

					bw.write("\t"); // indent
					if (child.getScope().length() > 0) {
						bw.write(child.getScope() + " "); // scope
					}
					if (child.getVisibility().length() > 0) {
						bw.write(child.getVisibility() + " "); // visibility
					}
					if (child.getType() != null) {
						bw.write(child.getType() + " "); // type (can be null
						// with
						// constructors)
					}
					bw.write(child.getName());

					if (child.getStructureName().equals("Property")) {
						bw.write(";" + lineSeparator);
					} else {
						bw.write("("); // ready for arguments

						Structure[] arguments = child.getChildren();
						for (int k = 0; k < arguments.length; k++) {
							if (k > 0) {
								bw.write(", ");
							}
							bw.write(arguments[k].getType() + " "
									+ arguments[k].getName());
						}

						bw.write(") {" + lineSeparator);
						if (child.getType() == null) {
							bw
									.write("\t\t// TODO Auto-generated constructor stub"
											+ lineSeparator);
						} else {
							bw.write("\t\t// TODO Auto-generated method stub"
									+ lineSeparator);
						}
						bw.write("\t}" + lineSeparator);
					}
				}

				bw.write("}"); // end of file

				bw.close();
				fw.close();
				files++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return files;
	}
}
