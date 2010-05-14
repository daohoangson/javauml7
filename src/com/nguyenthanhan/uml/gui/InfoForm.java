package com.nguyenthanhan.uml.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.daohoangson.uml.structures.Structure;
import com.tranvietson.uml.structures.StructureException;

public class InfoForm extends ConvenientForm implements ActionListener {
	private static final long serialVersionUID = 4904668752901107018L;
	private Structure structure;
	private Structure container = null;
	private Structure[] parents = null;
	private Structure[] children = null;
	private Box actionsBox;

	public InfoForm(Window owner, Structure structure) {
		super(owner, structure.getName(), ModalityType.MODELESS);

		this.structure = structure;

		Box primaryBox = Box.createHorizontalBox();

		Box infoBox = Box.createVerticalBox();

		infoBox.add(new JLabel("Type: " + structure.getStructureName()));
		infoBox.add(new JLabel("Name: " + structure.getName()));
		if (structure.getContainer() != null || structure.getParentsCount() > 0) {
			infoBox.add(new JLabel("Parents"));

			container = structure.getContainer();
			if (container != null) {
				JCheckBox container_cb = new JCheckBox(container.toString(),
						null, true);
				container_cb.setActionCommand("container");
				container_cb.addActionListener(this);
				infoBox.add(container_cb);
			}

			parents = structure.getParents();
			for (int i = 0; i < parents.length; i++) {
				JCheckBox parent_cb = new JCheckBox(parents[i].toString(),
						null, true);
				parent_cb.setActionCommand("parent." + i);
				parent_cb.addActionListener(this);
				infoBox.add(parent_cb);
			}
		}
		if (structure.getChildrenCount() > 0) {
			infoBox.add(new JLabel("Children"));

			children = structure.getChildren();
			for (int i = 0; i < children.length; i++) {
				JCheckBox child_cb = new JCheckBox(children[i].toString(),
						null, true);
				child_cb.setActionCommand("child." + i);
				child_cb.addActionListener(this);
				infoBox.add(child_cb);
			}
		}

		actionsBox = Box.createVerticalBox();

		primaryBox.add(infoBox);
		primaryBox.add(actionsBox);

		add(primaryBox);

		pack();
		setLocationRelativeTo(null);
	}

	public Structure getStructure() {
		return structure;
	}

	public Box getBox() {
		return actionsBox;
	}

	@Override
	protected void submit() {
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox cb = (JCheckBox) e.getSource();
		String action = cb.getActionCommand();
		Structure parent, child;

		if (action.equals("container")) {
			parent = container;
			child = structure;
		} else {
			String istr = action.substring(action.indexOf('.') + 1);
			int i = Integer.valueOf(istr);

			if (action.contains("parent")) {
				parent = parents[i];
				child = structure;
			} else {
				parent = structure;
				child = children[i];
			}
		}

		if (parent != null && child != null) {
			try {
				if (cb.isSelected()) {
					parent.add(child);
				} else {
					parent.remove(child);
				}
			} catch (StructureException se) {
				JOptionPane.showMessageDialog(getOwner(), se.getMessage(),
						getTitle(), JOptionPane.ERROR_MESSAGE);
				dispose();
			}
		}
	}

}
