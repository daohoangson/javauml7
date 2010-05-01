package com.nguyenthanhan.uml.gui;

import com.daohoangson.uml.gui.Diagram;
import com.tranvietson.uml.structures.Interface;
import com.tranvietson.uml.structures.StructureException;

public class InterfaceForm extends StructureForm{
	private static final long serialVersionUID = -789691956735803020L;
	private Diagram diagram;
	
	public InterfaceForm(Diagram diagram) {
		super(false, true, false);
		
		this.diagram = diagram;
		
		setTitle("Adding new Interface");
	}
	
	@Override
	public void __submit() {
		try {
			Interface newInterface = new Interface(txt_name.getText());
			if (visibility.length() > 0) newInterface.setModifier(visibility);
			
			diagram.add(newInterface);
		} catch (StructureException e1) {
			e1.printStackTrace();
		}
	}
	
}
