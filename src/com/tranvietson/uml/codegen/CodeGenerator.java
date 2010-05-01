package com.tranvietson.uml.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

public class CodeGenerator {
	private Diagram diagram;
	
	public CodeGenerator(Diagram diagram) {
		this.diagram = diagram;
	}

	public void generate() {
		String newDir1 = "UML\\Classes";
		String newDir2 = "UML\\Interfaces";
		new File(newDir1).mkdirs();
		new File(newDir2).mkdirs();
		Structure[] structures = diagram.structures.toArray(new Structure[0]);
		System.err.println("Size = " + structures.length);
		for(int i =0; i < structures.length; i++){
			Structure structure = structures[i];
			System.err.println("Generating " + structure);
				
				//Write Classes
				if (structure.getStructureName().equals("Class")){
					try
					{
						FileWriter fw = new FileWriter("UML\\Classes\\" + diagram.structures.get(i).getName() + ".java", true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(diagram.structures.get(i).getVisibility() + " class " + diagram.structures.get(i).getName());
						
						if (diagram.structures.get(i).getContainer() != null && diagram.structures.get(i).getParents() != null){
						bw.write(" extends " + diagram.structures.get(i).getContainer().getName());
							Structure[] parent = diagram.structures.get(i).getParents();
							for (int index = 0; index < parent.length; index++){
								bw.write(" implements " + parent[index]+ " ");
							}
							bw.write("{ \n");
						}
						
						if (diagram.structures.get(i).getContainer() != null && diagram.structures.get(i).getParents() == null){
						bw.write(" extends " + diagram.structures.get(i).getContainer().getName() + "{ \n");	
						}
						
						if (diagram.structures.get(i).getContainer() == null && diagram.structures.get(i).getParents() != null){
						Structure[] parent = diagram.structures.get(i).getParents();
							for (int index = 0; index < parent.length; index++){
								bw.write(" implements " + parent[index]+ " ");
							}
							bw.write("{ \n");
						}
						
						if (diagram.structures.get(i).getContainer() == null && diagram.structures.get(i).getParents() == null){
						bw.write(" { \n");
						}
						
						//Write constructor
						bw.write("\n//Constructor: \n");
						bw.write("\tpublic " + diagram.structures.get(i).getName() + "(){}" + "\n \n");
						
						for (int j = 0; j < diagram.structures.get(i).getChildrenCount(); j++){
							Structure[] a = diagram.structures.get(i).getChildren();
							
							//Write Properties of class
							if (a[j].getStructureName().equalsIgnoreCase("Property")){ 
								bw.write("//Properties:\n");
								bw.write(a[j].getScope() + " " + a[j].getVisibility() + " " + a[j].getType() + " " + a[j].getName() + ";");
								bw.write("\n");
							}
							//Write Methods class
							else {
								bw.write("//Methods:\n");
								bw.write(a[j].getVisibility() + " " + a[j].getType() + " " + a[j].getName() + "(");
								Structure[] b = a[j].getChildren();
								bw.write(b[0].getType() + " " + b[0].getName());
								
								for(int k = 1; k < a[j].getChildrenCount(); k++){
									bw.write(", " + b[k].getType() + " " + b[k].getName());
								}
								bw.write("){} \n \n");	
							}
							
							}

						bw.write("} \n \n");
						bw.close();
						fw.close();
					}
					catch (FileNotFoundException ex1)
					{
					System.out.println("new_class.java not found !");
					}
					catch (IOException ex2)
					{
					System.out.println("can not write into test_class.java !");
					}
				} 
				
				//Write Interfaces
				else if (structure.getStructureName().equals("Interface")){
					try
					{
						FileWriter fw = new FileWriter("UML\\Interfaces\\" + diagram.structures.get(i).getName() + ".java", true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(diagram.structures.get(i).getVisibility() + " inteface " + diagram.structures.get(i).getName());
						
						if (diagram.structures.get(i).getParents() != null){
								Structure[] parent = diagram.structures.get(i).getParents();
								for (int index = 0; index < parent.length; index++){
									bw.write(" implements " + parent[index]+ " ");
								}
								bw.write("{ \n");
							}
							
						if (diagram.structures.get(i).getParents() == null){
								bw.write("{ \n");	
							}
						for (int j = 0; j < diagram.structures.get(i).getChildrenCount(); j++){
							Structure[] a = diagram.structures.get(i).getChildren();
							
							//Write Properties of interface
							if (a[j].getStructureName().equalsIgnoreCase("Property")){
								bw.write(a[j].getScope() + " " + a[j].getVisibility() + " " + a[j].getType() + " " + a[j].getName() + ";");
								bw.write("\n \n");
							}
							
							//Write Methods of interface
							else {
								bw.write(a[j].getVisibility() + " " + a[j].getType() + " " + a[j].getName() + "(");
								Structure[] b = a[j].getChildren();
								bw.write(b[0].getType() + " " + b[0].getName());
								
								for(int k = 1; k < a[j].getChildrenCount(); k++){
									bw.write(", " + b[k].getType() + " " + b[k].getName());
								}
								bw.write("){} \n \n");	
							}
							
							}
						
						bw.write("} \n \n");
						bw.close();
						fw.close();
					}
					catch (FileNotFoundException ex1)
					{
					System.out.println("File's not found !");
					}
					catch (IOException ex2)
					{
					System.out.println("Can not write into file !");
					}
				}
				else return;
		}
	}
}