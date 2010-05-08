import java.io.File;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.UMLGUI;
import com.daohoangson.uml.parser.Parser;
import com.tranvietson.uml.structures.StructureException;

public class TestGUI {

	/**
	 * @param args
	 * @throws StructureException
	 */
	public static void main(String[] args) throws StructureException {
		String preload = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug")) {
				UMLGUI.debuging = true;
			} else if (args[i].equals("--load")) {
				if (i < args.length - 1) {
					preload = args[i + 1];
					i++;
				} else {
					System.err
							.println("Unable to fetch pre-load PATH. Use --help for more information");
				}
			} else if (args[i].equals("--help") || args[i].equals("-h")) {
				System.out.println("Commandline modifiers:");
				System.out
						.println("\t--debug\n\t\tDebug mode. Be careful of massive ouput data!");
				System.out
						.println("\t--load PATH\n\t\tPre-load source from PATH");
				System.out
						.println("\t--help, -h\n\t\tDisplay this help screen");
				System.exit(0);
			}
		}

		UMLGUI gui = new UMLGUI();

		if (preload != null) {
			Diagram d = gui.diagram;
			Parser p = new Parser(d);
			try {
				System.out.println("Trying to pre-load " + preload);
				d.setAutoDrawing(false);
				p.parse(new File(preload));
				d.setAutoDrawing(true);
			} catch (Exception e) {
				System.err.println("Unable to pre-load " + preload);
				e.printStackTrace();
				System.exit(1);
			}
		}

		gui.setVisible(true);
	}

}
