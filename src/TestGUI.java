import com.daohoangson.uml.gui.UMLGUI;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.StructureException;

public class TestGUI {

	/**
	 * @param args
	 * @throws StructureException 
	 */
	public static void main(String[] args) throws StructureException {
		UMLGUI gui = new UMLGUI();
		gui.diagram.add(new Class("A"));
		gui.diagram.add(new Class("B"));
		gui.diagram.add(new Class("C"));
	}

}
