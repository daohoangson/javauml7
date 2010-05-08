package com.nguyenthanhan.uml.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.daohoangson.uml.structures.Structure;
import com.daohoangson.uml.structures.StructureNameComparator;

public class FindForm extends ConvenientForm implements DocumentListener {
	private static final long serialVersionUID = -3188858518165054652L;
	static private Structure found = null;

	private Structure[] structures;
	private JList list;
	private JTextField txtfld;
	private boolean actioned = false;

	public FindForm(Window owner, String title, Structure[] structures) {
		super(owner, title, ModalityType.APPLICATION_MODAL);

		Arrays.sort(structures, new StructureNameComparator());
		this.structures = structures;

		Box left = Box.createVerticalBox();

		JLabel label = new JLabel("Structure Name: ");
		left.add(Box.createHorizontalBox().add(label));

		txtfld = new JTextField(15);
		txtfld.getDocument().addDocumentListener(this);
		txtfld.addKeyListener(this);
		left.add(txtfld);

		list = new JList(structures);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(list);
		sp.setPreferredSize(new Dimension(200, 75));
		left.add(sp);

		setLayout(new FlowLayout());
		add(left);

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

	@Override
	protected void submit() {
		FindForm.found = (Structure) list.getSelectedValue();
		dispose();
	}

	static public Structure find(Window owner, String title,
			Structure[] structures) {
		FindForm.found = null;

		if (structures.length == 0) {
			JOptionPane.showMessageDialog(owner, String
					.format("There is nothing, yet"), title,
					JOptionPane.ERROR_MESSAGE);
		} else {
			new FindForm(owner, title, structures).setVisible(true);
		}

		return FindForm.found;
	}

	private void updateList(String text) {
		Vector<Structure> foundList = new Vector<Structure>();

		if (text.length() > 0) {
			for (int i = 0; i < structures.length; i++) {
				if (structures[i].getName().toLowerCase().startsWith(
						text.toLowerCase())) {
					foundList.add(structures[i]);
				}
			}

			if (foundList.size() > 0) {
				list.setListData(foundList);
				list.setSelectedIndex(0);
			} else {
				list.removeAll();
			}
		} else {
			list.setListData(structures);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			Document d = e.getDocument();
			updateList(d.getText(0, d.getLength()));
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			Document d = e.getDocument();
			updateList(d.getText(0, d.getLength()));
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		super.keyReleased(e);

		if (!actioned) {
			// TODO: I have no idea why array keys get captured twice. Hmm
			if (e.getSource() == txtfld) {
				int kc = e.getKeyCode();
				int i = list.getSelectedIndex();
				int n = list.getModel().getSize();
				switch (kc) {
				case KeyEvent.VK_DOWN:
					if (i < n - 1) {
						list.setSelectedIndex(i + 1);
						list.ensureIndexIsVisible(i + 1);
					}
					break;
				case KeyEvent.VK_UP:
					if (n > 0 && i > 0) {
						list.setSelectedIndex(i - 1);
						list.ensureIndexIsVisible(i - 1);
					}
					break;
				}
			}
		}
		actioned = !actioned;
	}
}
