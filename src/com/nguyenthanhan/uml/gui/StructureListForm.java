package com.nguyenthanhan.uml.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

public class StructureListForm extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 5442144455550474270L;
	private String actionCommand;
	private Structure selected = null;

	public StructureListForm(Diagram diagram, String actionCommand) {
		this.actionCommand = actionCommand;
		
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		
		JComboBox cbb = new JComboBox(diagram.getStructures());
		cbb.addActionListener(this);
		add(cbb);
		
		JButton btn = new JButton("Create " + actionCommand);
		btn.addMouseListener(this);
		//add(btn);
		
		pack();
		setVisible(true);
	}
	
	public String getActionCommand() {
		return actionCommand;
	}
	
	public Structure getSelected() {
		return selected;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cbb = (JComboBox) e.getSource();
		selected = (Structure) cbb.getSelectedItem();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		setVisible(false);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
