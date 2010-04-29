import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.Class;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.Method;
import com.tranvietson.uml.structures.Property;
import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureException;
import com.tranvietson.uml.structures.StructureListener;


public class TestSelf implements StructureListener {
	Diagram d;
	
	public TestSelf() throws StructureException {
		d = new Diagram();
		JFrame f = new JFrame();
		f.add(d);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		Structure.global_listener = this;
		
		Structure sStructure = new Class("Structure","public");
		sStructure.add(new Property("children","List<Structure>"));
		sStructure.add(new Method("add","boolean"));
		Structure sClass = new Class("Class","public"); sStructure.add(sClass);
		Structure sInterface = new Class("Interface","public"); sStructure.add(sInterface);
		Structure sMethod = new Class("Method","public"); sStructure.add(sMethod);
		Structure sProperty = new Class("Property","public"); sStructure.add(sProperty);
		
		Structure sRelationship = new Interface("Relationship","public");
		Structure sGeneralRelationship = new Class("GeneralRelationship","public"); sRelationship.add(sGeneralRelationship);
		Structure sGeneralizationRelationship = new Class("GeneralizationRelationship","public"); sGeneralRelationship.add(sGeneralizationRelationship);
		Structure sMultiplicityRelationship = new Class("MultiplicityRelationship","public"); sGeneralRelationship.add(sMultiplicityRelationship);
		
		Structure sDiagram = new Class("Diagram","public");
		sDiagram.add(new Property("structures","List<Structure>"));
	}

	@Override
	public void structureChanged(StructureEvent e) {
		d.add((Structure) e.getSource());
	}
	
	public static void main(String[] args) throws StructureException {
		new TestSelf();
	}
}