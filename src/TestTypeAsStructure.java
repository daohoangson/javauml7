import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureException;
import com.tranvietson.uml.structures.Property;
import com.daohoangson.uml.structures.Class;


public class TestTypeAsStructure {

	/**
	 * @param args
	 * @throws StructureException 
	 */
	public static void main(String[] args) throws StructureException {
		// TODO Auto-generated method stub
		Structure A = new Class("A");
		Structure test = new Property("a","LinkedList<A>");
		Structure[] types = test.getTypeAsStructure();
		for (int i = 0; i < types.length; i++) {
			System.err.println(types[i]);
		}
		System.err.println("FML");
	}

}
