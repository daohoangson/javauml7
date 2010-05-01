package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public abstract class StructureForm extends JFrame implements MouseListener, ActionListener {
	private static final long serialVersionUID = 297547056430421671L;
	protected JTextField txt_name;
	protected JTextField txt_type;
	protected String visibility = "";
	protected String scope = null;
	protected JButton btn_create;
	protected JCheckBox cb_multiple;

	public StructureForm(
			boolean cfg_use_type
			,boolean cfg_use_visibility
			,boolean cfg_use_scope) {
		setLayout(new FlowLayout());
		
		txt_name = new JTextField(15);
		txt_name.addActionListener(this);
		JLabel lb_name = new JLabel("Name");
		lb_name.setLabelFor(txt_name);
		lb_name.setDisplayedMnemonic(KeyEvent.VK_N);
		add(lb_name);
		add(txt_name);
		
		if (cfg_use_type) {
			txt_type = new JTextField(15);
			JLabel lb_type = new JLabel("Type");
			lb_type.setLabelFor(txt_type);
			lb_type.setDisplayedMnemonic(KeyEvent.VK_T);
			add(lb_type);
			add(txt_type);
		}
		
		if (cfg_use_visibility) {
			ButtonGroup bg = new ButtonGroup();
			String[] visibilities = {"public","protected","private"};
			int[] mnemonics = {KeyEvent.VK_U, KeyEvent.VK_O, KeyEvent.VK_I};
			for (int i = 0; i < visibilities.length; i++) {
				JRadioButton rb = new JRadioButton(visibilities[i]);
				rb.setMnemonic((int)mnemonics[i]);
				rb.setActionCommand("visibility");
				rb.addActionListener(this);
				
				bg.add(rb);
				add(rb);
			}
		}
		
		if (cfg_use_scope) {
			JCheckBox cb = new JCheckBox("static");
			cb.setMnemonic(KeyEvent.VK_S);
			cb.setActionCommand("scope");
			cb.addActionListener(this);
			
			add(cb);
		}
		
		cb_multiple = new JCheckBox("Add more?");
		cb_multiple.setMnemonic(KeyEvent.VK_M);
		add(cb_multiple);
		
		btn_create = new JButton("Create");
		btn_create.addMouseListener(this);
		add(btn_create);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void reset() {
		txt_name.setText("");
		if (txt_type != null) txt_type.setText("");
		
		txt_name.requestFocusInWindow();
	}
	
	abstract protected void __submit();
	
	private void submit() {
		__submit();
		
		if (cb_multiple.isSelected()) {
			reset();
		} else {
			dispose();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		submit();
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
		
		if (action.equals("visibility")) {
			String modifier = ((AbstractButton)e.getSource()).getText();
			visibility = modifier;
		} else if (action.equals("scope")) {
			JCheckBox cb = (JCheckBox)e.getSource();
			if (cb.isSelected()) {
				scope = cb.getText();
			} else {
				scope = null;
			}
		} else if (e.getSource() == txt_name) {
			submit();
		}
	}
	
	
}
