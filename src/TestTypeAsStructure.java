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
		Structure test = new Property("a","LinkedList<A>");
		Structure[] types = test.getTypeAsStructure();
		for (int i = 0; i < types.length; i++) {
			System.err.println(types[i]);
		}
		System.err.println("FML");
	}

}
