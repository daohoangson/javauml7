import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;


public class TestTypeAsStructure {

	/**
	 * @param args
	 * @throws StructureException 
	 */
	public static void main(String[] args) throws StructureException {
		// TODO Auto-generated method stub
		new Class("A");
		Structure B = new Class("B");
		Structure C = new Class("C");
		Structure p1 = new Property("p1","Vector<B>");
		
		B.add(C);
		
		Structure[] types = p1.getTypeAsStructure();
		for (int i = 0; i < types.length; i++) {
			System.out.println("types[" + i + "] = " + types[i]);
		}
		System.out.println("END");
	}

}
