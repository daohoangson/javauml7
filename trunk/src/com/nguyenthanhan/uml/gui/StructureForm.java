package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
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

/**
 * Parent for all other structure form
 * 
 * @author Nguyen Thanh An
 * @version 1.0
 */
public abstract class StructureForm extends ConvenientForm implements
		ActionListener {
	private static final long serialVersionUID = 297547056430421671L;
	private Structure prototype;
	protected JTextField txt_name;
	protected JTextField txt_type;
	protected String sel_visibility = null;
	protected String sel_scope = null;
	protected String sel_abstract = null;
	protected JButton btn_create;
	protected JCheckBox cb_multiple;
	/**
	 * List of objects which listen to our event.
	 * 
	 * @see #addStructureListener(StructureListener)
	 * @see #removeStructureListener(StructureListener)
	 */
	private Vector<StructureListener> listeners;

	/**
	 * StructureForm constructor
	 * 
	 * @param owner
	 *            , title, cfg_use_type, cfg_use_visibility, cfg_use_scope
	 * 
	 */
	public StructureForm(Window owner, String title, Structure prototype) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		setLayout(new FlowLayout());

		txt_name = new JTextField(15);
		JLabel lb_name = new JLabel("Name");
		lb_name.setLabelFor(txt_name);
		lb_name.setDisplayedMnemonic(KeyEvent.VK_N);
		add(lb_name);
		add(txt_name);

		if (prototype.checkUseType()) {
			txt_type = new JTextField(15);
			JLabel lb_type = new JLabel("Type");
			lb_type.setLabelFor(txt_type);
			lb_type.setDisplayedMnemonic(KeyEvent.VK_T);
			add(lb_type);
			add(txt_type);
		}

		if (prototype.checkUseVisibility()) {
			ButtonGroup bg = new ButtonGroup();
			String[] cfg_visibilities = prototype.checkAllowedVisibilities();
			char[] vowels = { 'a', 'e', 'i', 'o', 'u' };
			int[] vowels_code = { KeyEvent.VK_A, KeyEvent.VK_E, KeyEvent.VK_I,
					KeyEvent.VK_O, KeyEvent.VK_U };
			for (int i = 0; i < cfg_visibilities.length; i++) {
				JRadioButton rb = new JRadioButton(cfg_visibilities[i]);
				int mnemonic = -1;
				for (int j = 0; j < cfg_visibilities[i].length(); j++) {
					int index = Arrays.binarySearch(vowels, cfg_visibilities[i]
							.charAt(j));
					if (index > 0 && vowels_code[index] > 0) {
						mnemonic = vowels_code[index];
						vowels_code[index] = -1; // do not use this anymore
						break;
					}
				}
				if (mnemonic > -1) {
					rb.setMnemonic(mnemonic);
				}
				rb.setActionCommand("visibility");
				rb.addActionListener(this);

				bg.add(rb);
				add(rb);

				// automatically select the first one
				if (i == 0) {
					rb.setSelected(true);
				}
			}
		}

		if (prototype.checkUseScope()) {
			JCheckBox cb = new JCheckBox("static");
			cb.setMnemonic(KeyEvent.VK_S);
			cb.setActionCommand("scope");
			cb.addActionListener(this);

			add(cb);
		}

		if (prototype.checkUseAbstract()) {
			JCheckBox cb = new JCheckBox("abstract");
			cb.setMnemonic(KeyEvent.VK_T);
			cb.setActionCommand("abstract");
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

		this.prototype = prototype;
	}

	private void reset() {
		txt_name.setText("");
		if (txt_type != null) {
			txt_type.setText("");
		}

		txt_name.requestFocusInWindow();
	}

	protected Structure __submit(Structure prototype) throws StructureException {
		Structure structure = null;

		try {
			structure = prototype.getClass().newInstance();
		} catch (Exception e) {
			throw new StructureException("Unable to create new "
					+ prototype.getStructureName());
		}

		structure.setName(txt_name.getText());
		if (prototype.checkUseType()) {
			structure.setType(txt_type.getText());
		}
		if (sel_visibility != null) {
			structure.setModifier(sel_visibility);
		}
		if (sel_scope != null) {
			structure.setModifier(sel_scope);
		}
		if (sel_abstract != null) {
			structure.setModifier(sel_abstract);
		}

		return structure;
	}

	@Override
	protected void submit() {
		try {
			Structure structure = __submit(prototype);

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
			sel_visibility = modifier;
		} else if (action.equals("scope")) {
			JCheckBox cb = (JCheckBox) e.getSource();
			if (cb.isSelected()) {
				sel_scope = cb.getText();
			} else {
				sel_scope = null;
			}
		} else if (action.equals("abstract")) {
			JCheckBox cb = (JCheckBox) e.getSource();
			if (cb.isSelected()) {
				sel_abstract = cb.getText();
			} else {
				sel_abstract = null;
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
