import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.daohoangson.uml.Diagram;
import com.tranvietson.uml.ClassStructure;
import com.tranvietson.uml.InterfaceStructure;
import com.tranvietson.uml.MethodStructure;
import com.tranvietson.uml.StructureException;


public class Test {
	public static void main(String[] args) throws StructureException {
		final Diagram d = new Diagram();
		JFrame f = new JFrame();
		f.add(d.getDisplay());
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		JFrame f2 = new JFrame();
		f2.setLayout(new FlowLayout());
		JButton b1 = new JButton("Test 1");
		JButton b2 = new JButton("Test 2");
		JButton b3 = new JButton("Test 3");
		f2.add(b1);
		f2.add(b2);
		f2.add(b3);
		f2.pack();
		f2.setVisible(true);
		
		final ClassStructure cb = new ClassStructure("Big");
		d.add(cb);
		
		b1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					d.add(new InterfaceStructure("interface"));
				} catch (StructureException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		b2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					cb.add(new MethodStructure("Hihi","Blah"));
				} catch (StructureException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		b3.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					InterfaceStructure i = new InterfaceStructure("Huh");
					d.add(i);
					cb.assign(i);
				} catch (StructureException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
}
