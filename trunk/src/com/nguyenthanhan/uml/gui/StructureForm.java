package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureException;
import com.tranvietson.uml.structures.StructureListener;

public abstract class StructureForm extends ConvenientForm implements
		ActionListener {
	private static final long serialVersionUID = 297547056430421671L;
	protected JTextField txt_name;
	protected JTextField txt_type;
	protected String visibility = "";
	protected String scope = null;
	protected JButton btn_create;
	protected JCheckBox cb_multiple;
	/**
	 * List of objects which listen to our event.
	 * 
	 * @see #addStructureListener(StructureListener)
	 * @see #removeStructureListener(StructureListener)
	 */
	private Vector<StructureListener> listeners;

	public StructureForm(Window owner, String title, boolean cfg_use_type,
			boolean cfg_use_visibility, boolean cfg_use_scope) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setLayout(new FlowLayout());

		txt_name = new JTextField(15);
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
			String[] visibilities = { "public", "protected", "private" };
			int[] mnemonics = { KeyEvent.VK_U, KeyEvent.VK_O, KeyEvent.VK_I };
			for (int i = 0; i < visibilities.length; i++) {
				JRadioButton rb = new JRadioButton(visibilities[i]);
				rb.setMnemonic(mnemonics[i]);
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
		btn_create.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				submit();
			}
		});
		add(btn_create);

		pack();
		setLocationRelativeTo(null);
	}

	private void reset() {
		txt_name.setText("");
		if (txt_type != null) {
			txt_type.setText("");
		}

		txt_name.requestFocusInWindow();
	}

	abstract protected Structure __submit() throws StructureException;

	@Override
	protected void submit() {
		try {
			Structure structure = __submit();

			if (structure != null) {
				fireChanged(structure);
			}

			if (cb_multiple.isSelected()) {
				reset();
			} else {
				dispose();
			}
		} catch (StructureException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), getTitle(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("visibility")) {
			String modifier = ((AbstractButton) e.getSource()).getText();
			visibility = modifier;
		} else if (action.equals("scope")) {
			JCheckBox cb = (JCheckBox) e.getSource();
			if (cb.isSelected()) {
				scope = cb.getText();
			} else {
				scope = null;
			}
		}
	}

	/**
	 * Adds an object to this structure listeners list
	 * 
	 * @param listener
	 *            the object who needs to listen to this
	 */
	synchronized public void addStructureListener(StructureListener listener) {
		if (listeners == null) {
			listeners = new Vector<StructureListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes an object from this structure listeners list
	 * 
	 * @param listener
	 *            the object who is listening to this
	 */
	synchronized public void removeStructureListener(StructureListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * Triggers the changed event
	 * 
	 * @param structure
	 *            the structure involved in the event
	 */
	@SuppressWarnings("unchecked")
	protected void fireChanged(Structure structure) {
		if (listeners != null && listeners.size() > 0) {
			StructureEvent e = new StructureEvent(structure,
					StructureEvent.ACTIVE);

			Vector<StructureListener> targets;
			synchronized (this) {
				targets = (Vector<StructureListener>) listeners.clone();
			}

			for (int i = 0; i < targets.size(); i++) {
				StructureListener listener = targets.elementAt(i);
				listener.structureChanged(e);
			}
		}
	}

}
