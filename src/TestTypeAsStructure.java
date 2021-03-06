import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

/**
 * Test for {@link Structure#getTypeAsStructure()}
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TestTypeAsStructure {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		new Class("A");
		Structure B = new Class("B");
		Structure C = new Class("C");
		Structure p1 = new Property("p1", "Vector<B>");

		B.add(C);

		Structure[] types = p1.getTypeAsStructure();
		for (int i = 0; i < types.length; i++) {
			System.out.println("types[" + i + "] = " + types[i]);
		}
		System.out.println("END");
	}

}
