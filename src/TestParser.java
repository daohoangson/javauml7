import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.gui.Relationship;
import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;

public class TestParser {
	Diagram d;
	Parser parser;
	static boolean debuging = false;

	public TestParser(String[] args) throws StructureException, ParseException,
			IOException {
		d = new Diagram();
		JFrame f = new JFrame();
		f.add(d.getScrollable());
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		// Parser.debuging = true;
		// Structure.debuging = true;
		Relationship.debuging = true;
		parser = new Parser(d);

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--debug")) {
					Structure.debuging = true;
					Diagram.debuging = true;
					TestParser.debuging = true;
					System.out.println("Enabled DEBUG modes");
					break;
				}
			}

			parser.parse(new File(args[0]));
		} else {
			System.err
					.println("Usage: TestParser SOURCEPATH [--debug] [--image IMAGEPATH]");
			System.err.println("Exiting now...");
			System.exit(1);
		}
	}

	public static void main(String[] args) throws StructureException,
			ParseException, IOException {
		// new TestParser(args);
		new TestParser(new String[] { "src/com/daohoangson/uml/parser" });
	}
}
