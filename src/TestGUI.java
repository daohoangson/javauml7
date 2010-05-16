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
		UMLGUI gui = new UMLGUI();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equals("--debug")) {
				// debugging
				System.out.println("Debug mode is enabled");
				UMLGUI.setDebugging(true);
			} else if (arg.equals("--load")) {
				// pre-load
				if (i < args.length - 1) {
					String preload = args[i + 1];

					Diagram d = gui.diagram;
					Parser p = new Parser(d);
					try {
						System.out.println("Trying to pre-load " + preload);
						d.setAutoDrawing(false);
						p.parse(new File(preload));
						d.setAutoDrawing(true);
						System.out.println("OK!");
					} catch (Exception e) {
						System.err.println("Unable to pre-load " + preload);
						e.printStackTrace();
						System.exit(1);
					}

					// skip the pre-load path
					i++;
				} else {
					System.err
							.println("Unable to fetch pre-load PATH. Use --help for more information");
				}
			} else if (arg.equals("--title")) {
				// custom title
				String title = args[i + 1];
				gui.setTitle(title);
				System.out.println("Custom title is set: " + title);

				// skip the title
				i++;
			} else if (arg.equals("--help") || arg.equals("-h")
					|| arg.equals("/?")) {
				System.out.println("Commandline modifiers:");
				System.out
						.println("\t--debug\n\t\tDebug mode. Be careful of massive ouput data!");
				System.out
						.println("\t--load PATH\n\t\tPre-load source from PATH");
				System.out
						.println("\t--title TITLE\n\t\tSet a custom title for the main window");
				System.out
						.println("\t--help, -h, /?\n\t\tDisplay this help screen");
				System.exit(0);
			}
		}

		gui.setVisible(true);
	}

}
