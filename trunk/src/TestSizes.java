import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.UMLGUI;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

/**
 * Test for diagram sizes (with lots of structures)
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TestSizes {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		UMLGUI gui = new UMLGUI();
		Diagram diagram = gui.diagram;

		// horizontal test
		// for (char c = 'A'; c <= 'Z'; c++) {
		// diagram.add(new Class(String.valueOf(c)));
		// }

		// vertical test
		Structure previous = null;
		for (char c = 'A'; c <= 'Z'; c++) {
			Structure s = new Class(String.valueOf(c));
			if (previous != null) {
				previous.add(s);
			}
			previous = s;

			diagram.add(s);
		}
		// diagram.add(new Interface("iA"));

		gui.setVisible(true);
	}

}
