package com.daohoangson.uml.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.nguyenthanhan.uml.gui.ClassForm;
import com.nguyenthanhan.uml.gui.InterfaceForm;
import com.nguyenthanhan.uml.gui.MethodForm;
import com.nguyenthanhan.uml.gui.PropertyForm;
import com.nguyenthanhan.uml.gui.StructureListForm;

public class UMLGUI extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = -2192671131702680754L;
	public Diagram diagram = new Diagram();

	public UMLGUI() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuNew = new JMenu("New");
		menuBar.add(menuNew);
		
		String[] structures = new String[]{
				"Class", "Interface", "Property", "Method", "Argument"
		};
		for (int i = 0; i < structures.length; i++) {
			JMenuItem mi = new JMenuItem(structures[i]);
			mi.setActionCommand("new");
			mi.addActionListener(this);
			menuNew.add(mi);
		}
		
		setJMenuBar(menuBar);
		add(diagram.getScrollable());
		pack();
		setTitle("UML");
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		AbstractButton s = (AbstractButton) e.getSource();
		
		if (action.equals("new")) {
			//creating new structure
			String type = s.getText();
			if (type.equals("Class")) {
				new ClassForm(diagram);
			} else if (type.equals("Interface")) {
				new InterfaceForm(diagram);
			} else if (type.equals("Property")) {
				new StructureListForm(diagram,"Property").addWindowListener(this);
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		StructureListForm f = (StructureListForm) e.getSource();
		if (f.getSelected() != null) {
			String action = f.getActionCommand();
			if (action.equals("Property")) {
				new PropertyForm(f.getSelected());
			} else if (action.equals("Method")) {
				new MethodForm(f.getSelected());
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
