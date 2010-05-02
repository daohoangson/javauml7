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
	
	public int generate(String path) {
		new File(path).mkdirs();
		Structure[] structures = diagram.getStructures();
		String pathSeparator = File.separator;
		String lineSeparator = System.getProperty("line.separator");
		int files = 0;
		
		for (int i = 0; i < structures.length; i++) {
			Structure structure = structures[i];
			
			try {
				//Create new file
				//Setup the writer
				FileWriter fw = new FileWriter(path + pathSeparator + structure.getName() + ".java");
				BufferedWriter bw = new BufferedWriter(fw);
				
				if (structure.getVisibility().length() > 0)
					bw.write(structure.getVisibility() + " "); //visibility
				
				if (structure.getStructureName().equals("Class")) {
					//class declaration
					bw.write("class " + structure.getName()); //class name
					if (structure.getContainer() != null) 
						bw.write(" extends " + structure.getContainer().getName()); //ancestor
					if (structure.getParentsCount() > 0) {
						Structure[] interfaces = structure.getParents();
						bw.write(" implements ");
						for (int j = 0; j < interfaces.length; j++) {
							if (j > 0) bw.write(", ");
							bw.write(interfaces[j].getName()); //interfaces
						}
					}
				} else {
					//interface declaration
					bw.write("interface " + structure.getName()); //interface name
					if (structure.getContainer() != null) {
						bw.write(" extends " + structure.getContainer().getName()); //ancestor
					}
				}
				
				bw.write(" {" + lineSeparator); //ready for children
				
				Structure[] children = structure.getChildren();
				for (int j = 0; j < children.length; j++) {
					Structure child = children[j];
					
					if (child.getStructureName().equals("Method"))
						bw.write(lineSeparator); //separating methods
					
					bw.write("\t"); //indent
					if (child.getScope().length() > 0)
						bw.write(child.getScope() + " "); //scope
					if (child.getVisibility().length() > 0) 
						bw.write(child.getVisibility() + " "); //visibility
					if (child.getType() != null) 
						bw.write(child.getType() + " "); //type (can be null with constructors)
					bw.write(child.getName());
					
					if (child.getStructureName().equals("Property")) {
						bw.write(";" + lineSeparator);
					} else {
						bw.write("("); //ready for arguments
						
						Structure[] arguments = child.getChildren();
						for (int k = 0; k < arguments.length; k++) {
							if (k > 0) bw.write(", ");
							bw.write(arguments[k].getType() + " " + arguments[k].getName());
						}
						
						bw.write(") {" + lineSeparator);
						if (child.getType() == null) {
							bw.write("\t\t// TODO Auto-generated constructor stub" + lineSeparator);
						} else {
							bw.write("\t\t// TODO Auto-generated method stub" + lineSeparator);
						}
						bw.write("\t}" + lineSeparator);
					}
				}
				
				bw.write("}"); //end of file
				
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
