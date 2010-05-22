import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.UMLGUI;
import com.daohoangson.uml.parser.Parser;

/**
 * Not really a test. This is the main application starting point. Accept
 * several command line directives. Use --help OR -h OR /? to display the help
 * screen
 * 
 * @author Dao Hoang Son
 * @version 2.0
 * 
 */
public class TestGUI {

	/**
	 * Creates the {@link UMLGUI} and parses command line directives. Supported
	 * directives are
	 * <ul>
	 * <li>--debug</li>
	 * <li>--load</li>
	 * <li>--title</li>
	 * <li>--maximize</li>
	 * <li>--no</li>
	 * <li>--help, -h, /?</li>
	 * </ul>
	 * 
	 * @param args
	 *            an array of command line arguments (passed in by the JVM)
	 */
	public static void main(String[] args) {
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
			} else if (arg.equals("--maximize")) {
				// go maximized
				gui.setExtendedState(Frame.MAXIMIZED_BOTH);
			} else if (arg.equals("--no")) {
				String name = args[i + 1];
				JMenuItem mi = null;

				if (name.equals("toolbar")) {
					mi = gui.findMenuItem("view.toolbar");
				} else if (name.equals("outline")) {
					mi = gui.findMenuItem("view.outline");
				}

				if (mi != null && mi instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) mi;
					cbmi.setSelected(false);
					gui.actionPerformed(new ActionEvent(cbmi, (int) System
							.currentTimeMillis(), cbmi.getActionCommand()));
				}

				// skip the next
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
						.println("\t--maximize\n\t\tMaximize the frame initially");
				System.out
						.println("\t--no toolbar|outline\n\t\tDisable the specified functionality");
				System.out
						.println("\t--help, -h, /?\n\t\tDisplay this help screen");
				System.exit(0);
			}
		}

		gui.setVisible(true);
	}

}
