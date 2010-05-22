import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.UMLGUI;
import com.daohoangson.uml.structures.Structure;
import com.tavanduc.uml.gui.Relationship;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureException;

/**
 * Test for relationship displaying
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TestRelationship {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		Diagram.debugging = true;
		Relationship.debugging = true;

		UMLGUI gui = new UMLGUI();
		Diagram d = gui.diagram;

		d.add(new Class("A"));
		d.add(new Class("B"));
		d.add(new Class("C"));
		d.add(new Class("D"));
		d.add(new Class("E"));
		d.add(new Class("F"));
		d.add(new Class("G"));

		d.add(new Interface("iA"));
		d.add(new Interface("iB"));

		Structure.lookUp("A").add(Structure.lookUp("B"));
		Structure.lookUp("A").add(Structure.lookUp("C"));
		Structure.lookUp("A").add(Structure.lookUp("F"));
		Structure.lookUp("A").add(Structure.lookUp("G"));
		Structure.lookUp("D").add(new Property("aA", "A[]"));
		Structure.lookUp("D").add(new Property("aB", "B[]"));
		Structure.lookUp("D").add(new Property("aC", "C[]"));
		Structure.lookUp("D").add(new Property("aE", "E[]"));
		Structure.lookUp("iA").add(Structure.lookUp("E"));
		Structure.lookUp("iB").add(Structure.lookUp("E"));

		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}
