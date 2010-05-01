package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public abstract class StructureForm extends JFrame implements MouseListener, ActionListener {
	private static final long serialVersionUID = 297547056430421671L;
	protected JTextField txt_name;
	protected JTextField txt_type;
	protected String visibility = "";
	protected String scope = null;
	protected JButton btn_create;

	public StructureForm(
			boolean cfg_use_type
			,boolean cfg_use_visibility
			,boolean cfg_use_scope) {
		setLayout(new FlowLayout());
		
		txt_name = new JTextField("<name>",15); add(txt_name);
		
		if (cfg_use_type) {
			txt_type = new JTextField("<type>",15); add(txt_type);
		}
		
		if (cfg_use_visibility) {
			ButtonGroup bg = new ButtonGroup();
			String[] visibilities = new String[]{"public","protected","private"};
			for (int i = 0; i < visibilities.length; i++) {
				JRadioButton rb = new JRadioButton(visibilities[i]);
				rb.setActionCommand("visibility");
				rb.addActionListener(this);
				
				bg.add(rb);
				add(rb);
			}
		}
		
		if (cfg_use_scope) {
			JCheckBox cb = new JCheckBox("static");
			cb.setActionCommand("scope");
			cb.addActionListener(this);
			
			add(cb);
		}
		
		btn_create = new JButton("Create");
		btn_create.addMouseListener(this);
		add(btn_create);
		
		setTitle("AddInfo");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		String modifier = ((AbstractButton)e.getSource()).getText();
		
		if (action.equals("visibility")) {
			visibility = modifier;
		} else if (action.equals("scope")) {
			JCheckBox cb = (JCheckBox)e.getSource();
			if (cb.isSelected()) {
				scope = modifier;
			} else {
				scope = null;
			}
		}
	}
	
	
}
