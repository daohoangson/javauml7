import javax.swing.JFrame;
import javax.swing.JTree;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.Outline;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Argument;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

public class TestTree {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		// TODO Auto-generated method stub
		JFrame f = new JFrame("Test Tree");

		Diagram d = new Diagram();

		Structure A = new Class("A", "public");
		A.add(new Property("i", "int"));
		A.add(new Property("j", "int"));
		Structure m1 = new Method("m1", "int");
		m1.add(new Argument("i", "int"));
		m1.add(new Argument("j", "int"));
		A.add(m1);

		Structure B = new Class("B", "public");
		A.add(B);

		JTree t = new Outline(d);

		d.add(A);
		d.add(B);

		f.add(t);
		f.pack();
		f.setVisible(true);
	}
}