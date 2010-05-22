import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.codegen.CodeGenerator;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

/**
 * Test for Code Generation. New source will be generated into D:\CodeGen
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TestCodeGen {
	public TestCodeGen() throws StructureException {
		final Diagram d = new Diagram();
		JFrame f = new JFrame();
		f.add(d);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		Class A = new Class("A");
		A.add(new Property("p1", "String"));
		A.add(new Property("p2", "int"));
		Method m1 = new Method("m1", "String");
		m1.add(new Argument("a", "int"));
		m1.add(new Argument("b", "int"));
		A.add(m1);

		Class B = new Class("B");
		d.add(A);
		d.add(B);

		Interface iA = new Interface("iA");
		iA.add(A);
		d.add(iA);

		Class A2 = new Class("A2");
		iA.add(A2);
		d.add(A2);

		Class A3 = new Class("A3");
		iA.add(A3);
		d.add(A3);

		Interface iC = new Interface("iC");
		d.add(iC);

		Class C = new Class("C");
		iC.add(C);
		d.add(C);

		Class D = new Class("D");
		D.add(new Property("p", "LinkedList<B>"));
		d.add(D);

		C.add(A3);

		CodeGenerator codegen = new CodeGenerator("D:\\CodeGen");
		Structure[] structures = d.getStructures();
		for (int i = 0; i < structures.length; i++) {
			codegen.generate(structures[i]);
		}
	}

	public static void main(String[] args) throws StructureException {
		new TestCodeGen();
	}
}
