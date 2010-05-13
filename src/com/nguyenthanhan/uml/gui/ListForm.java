package com.nguyenthanhan.uml.gui;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureNameComparator;

public class ListForm extends ConvenientForm {
	private static final long serialVersionUID = -1172733846493025909L;
	private static Structure selected = null;

	private JComboBox cbb;

	private ListForm(Window owner, String title, Structure[] list,
			String listStructureName) {
		super(owner, title, ModalityType.APPLICATION_MODAL);

		setLayout(new FlowLayout());

		JLabel lb = new JLabel(listStructureName);
		add(lb);

		Arrays.sort(list, new StructureNameComparator());
		cbb = new JComboBox(list);
		if (cbb.getItemCount() > 0) {
			cbb.setSelectedIndex(0);
		}
		add(cbb);

		JButton btn = new JButton(title);
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				submit();
			}
		});
		add(btn);

		pack();
		setLocationRelativeTo(null);
	}

	static public Structure select(Window owner, String title,
			Structure[] list, String listStructureName) {
		ListForm.selected = null;

		if (list.length == 0) {
			JOptionPane.showMessageDialog(owner, String.format(
					"There is no %s", listStructureName), title,
					JOptionPane.ERROR_MESSAGE);
		} else {
			new ListForm(owner, title, list, listStructureName)
					.setVisible(true);
		}

		return ListForm.selected;
	}

	@Override
	protected void submit() {
		ListForm.selected = (Structure) cbb.getSelectedItem();
		dispose();
	}
}
