package com.tranvietson.uml.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.daohoangson.uml.gui.Diagram;


public class test {
	private Diagram diagram;
	
	public test(Diagram diagram){
		this.diagram = diagram;
	}
	public void generate() {
		for(int i =0; i < diagram.structures.size(); i++){
			if (diagram.structures.get(i) != null){
				if (diagram.structures.get(i).equals("Class")){
					try
					{
					FileWriter fw = new FileWriter("new_class.java", true);
					BufferedWriter bw = new BufferedWriter(fw);

					bw.write(diagram.structures.get(i).getVisibility());
					bw.write(" class ");
					bw.write(diagram.structures.get(i).getName() + "{");
					bw.write("\n");
					//.write(diagram.structures.get(i).get);
					//bw.newLine();

					bw.close();
					fw.close();
					}
					catch (FileNotFoundException ex1)
					{
					System.out.println("new_class.java not found !");
					}
					catch (IOException ex2)
					{
					System.out.println("can not write to new_class.java !");
					}
					}
					} 
		}
	}
}