import javax.swing.JFrame;

import com.daohoangson.uml.Diagram;
import com.tranvietson.uml.ClassStructure;
import com.tranvietson.uml.InterfaceStructure;
import com.tranvietson.uml.Structure;
import com.tranvietson.uml.StructureException;


public class TestFast {
	public static void main(String[] args) throws StructureException {
		final Diagram d = new Diagram();
		JFrame f = new JFrame();
		f.add(d.getDisplay());
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		Structure A = new ClassStructure("A");
		Structure B = new ClassStructure("B");
		d.add(A);
		d.add(B);
		
		Structure iA = new InterfaceStructure("iA");
		A.assign(iA);
		d.add(iA);
		
		Structure A2 =  new ClassStructure("A2");
		A2.assign(iA);
		d.add(A2);
		
		Structure A3 =  new ClassStructure("A3");
		A3.assign(iA);
		d.add(A3);
		
		Structure iC = new InterfaceStructure("iC");
		d.add(iC);
		
		Structure C = new ClassStructure("C");
		C.assign(iC);
		d.add(C);
	}
}
