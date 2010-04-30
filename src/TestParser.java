import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.parser.ParserException;
import com.tranvietson.uml.structures.StructureException;


public class TestParser {
	Diagram d;
	Parser parser;
	
	public TestParser(String[] args) throws StructureException, ParserException, IOException {
		d = new Diagram();
		JFrame f = new JFrame();
		f.add(d.getScrollable());		
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		parser =  new Parser(d);
		
		if (args.length > 0) {
			parse(new File(args[0]));
			
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--image") && i < args.length - 1) {
					d.saveImage(args[i+1]);
					break;
				}
			}
		} else {
			parse(new File("src"));
		}
	}
	
	private void parse(File dir) throws StructureException, ParserException, IOException {
		if (dir.isDirectory()) {
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
		new TestParser(args);
	}
}
