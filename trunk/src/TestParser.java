import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.parser.ParserException;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;


public class TestParser {
	Diagram d;
	Parser parser;
	static boolean debuging = false;
	
	public TestParser(String[] args) throws StructureException, ParserException, IOException {
		d = new Diagram();
		JFrame f = new JFrame();
		f.add(d.getScrollable());		
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		parser =  new Parser(d);
		
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
			
			parse(new File(args[0]));
			
//			for (int i = 0; i < args.length; i++) {
//				if (args[i].equals("--image") && i < args.length - 1) {
//					if (d.saveImage(args[i+1])) {
//						System.out.println("Saved image: " + args[i+1]);
//					}
//					break;
//				}
//			}
		} else {
			System.err.println("Usage: TestParser SOURCEPATH [--debug] [--image IMAGEPATH]");
			System.err.println("Exiting now...");
			System.exit(1);
		}
	}
	
	private void parse(File dir) throws StructureException, ParserException, IOException {
		if (dir.isDirectory()) {
			if (TestParser.debuging) System.err.println("Paring " + dir.getAbsolutePath());
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				parse(files[i]);
			}
		} else if (dir.isFile()) {
			String path = dir.getAbsolutePath();
			if (path.substring(path.length() - 5).equalsIgnoreCase(".java"))
				parser.parse(readFileAsString(dir));
		}
	}
	
	private String readFileAsString(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream f = new BufferedInputStream(new FileInputStream(file));
		f.read(buffer);
		return new String(buffer);
	}
	
	public static void main(String[] args) throws StructureException, ParserException, IOException {
		//new TestParser(args);
		new TestParser(new String[]{"src"});
	}
}
