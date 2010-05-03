import com.daohoangson.uml.gui.UMLGUI;
import com.tranvietson.uml.structures.StructureException;

public class TestGUI {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug")) {
				UMLGUI.debuging = true;
			} else if (args[i].equals("--help") || args[i].equals("-h")) {
				System.out.println("Usage: ");
				System.out.println("Commandline modifiers:");
				System.out
						.println("\t--debug\tDebug mode. Be careful of massive ouput data!");
				System.exit(0);
			}
		}

		UMLGUI gui = new UMLGUI();
		gui.setVisible(true);
	}

}
