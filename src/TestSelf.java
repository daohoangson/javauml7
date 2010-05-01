import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureException;
import com.tranvietson.uml.structures.StructureListener;


public class TestSelf implements StructureListener {
	Diagram d;

	@Override
	public void structureChanged(StructureEvent e) {
		d.add((Structure) e.getSource());
	}
	
	public static void main(String[] args) throws StructureException {
		new TestSelf();
	}
}
