package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.daohoangson.uml.gui.Diagram;
import com.daohoangson.uml.structures.Structure;

public class StructureListForm extends JFrame implements ActionListener {
	private static final long serialVersionUID = 5442144455550474270L;
	private String actionCommand;
	private Structure selected = null;

	public StructureListForm(Diagram diagram, String actionCommand) {
		this.actionCommand = actionCommand;
		
		getContentPane().setLayout(new FlowLayout());
		
		JComboBox cbb = new JComboBox(diagram.getStructures());
		if (cbb.getItemCount() > 0) {
			selected = (Structure) cbb.getItemAt(0);
			cbb.setSelectedIndex(0);
		}
		cbb.addActionListener(this);
		add(cbb);
		
		JButton btn = new JButton("Create " + actionCommand);
		btn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		add(btn);
		
		pack();
		setLocationRelativeTo(null);
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
}
