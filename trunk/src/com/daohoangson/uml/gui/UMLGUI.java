package com.daohoangson.uml.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.structures.Structure;
import com.nguyenthanhan.uml.gui.ArgumentForm;
import com.nguyenthanhan.uml.gui.ClassForm;
import com.nguyenthanhan.uml.gui.InterfaceForm;
import com.nguyenthanhan.uml.gui.ListForm;
import com.nguyenthanhan.uml.gui.MethodForm;
import com.nguyenthanhan.uml.gui.PropertyForm;
import com.tranvietson.uml.codegen.CodeGenerator;

public class UMLGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = -2192671131702680754L;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debuging = false;
	public Diagram diagram = new Diagram();

	public UMLGUI() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		
		JMenuItem mfi;
		
		mfi = new JMenuItem("Load Source File(s)");
		mfi.setActionCommand("load");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_O);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);
		
		mfi = new JMenuItem("Save as Image");
		mfi.setActionCommand("image");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_S);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);
		
		mfi = new JMenuItem("Export Source File(s)");
		mfi.setActionCommand("generate");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_E);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);
		
		mfi = new JMenuItem("Exit");
		mfi.setActionCommand("exit");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_X);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);
		
		JMenu menuNew = new JMenu("New");
		menuNew.setMnemonic(KeyEvent.VK_N);
		menuBar.add(menuNew);
		
		String[] structures = new String[]{
				"Class", "Interface", "Property", "Method", "Argument"
		};
		for (int i = 0; i < structures.length; i++) {
			JMenuItem mi = new JMenuItem(structures[i]);
			mi.setActionCommand("new");
			mi.addActionListener(this);
			mi.setMnemonic((int)structures[i].charAt(0));
			mi.setAccelerator(KeyStroke.getKeyStroke(structures[i].charAt(0), InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
			menuNew.add(mi);
		}
		
		setJMenuBar(menuBar);
		add(diagram.getScrollable());
		pack();
		setTitle("UML");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		Structure.debuging = debuging;
		Diagram.debuging = debuging;
		Parser.debuging = debuging;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		AbstractButton s = (AbstractButton) e.getSource();
		
		if (action.equals("exit")) {
			dispose();
		} else if (action.equals("load")) {
			doLoad(s.getText());
		} else if (action.equals("image")) {
			doImage(s.getText());
		} else if (action.equals("generate")) {
			doGenerate(s.getText());
		} else if (action.equals("new")) {
			//creating new structure
			String type = s.getText();
			if (type.equals("Class")) {
				new ClassForm(this,diagram);
			} else if (type.equals("Interface")) {
				new InterfaceForm(this,diagram);
			} else if (type.equals("Property")) {
				Structure structure = ListForm.select(this, "New Property", diagram.getStructures(), "Class/Interface");
				if (structure != null) 
					new PropertyForm(this, structure);
			} else if (type.equals("Method")) {
				Structure structure = ListForm.select(this, "New Method", diagram.getStructures(), "Class/Interface");
				if (structure != null) 
					new MethodForm(this, structure);
			} else if (type.equals("Argument")) {
				Structure structure1 = ListForm.select(this, "New Argument", diagram.getStructures(), "Class/Interface");
				if (structure1 != null) {
					Structure structure2 = ListForm.select(this, "New Argument", Structure.filterStructureArray(structure1.getChildren(), new String[]{"Method"}), "Method");
					if (structure2 != null) {
						new ArgumentForm(this, structure2);
					}
				}
			}
		}
	}
	
	private void doLoad(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Load Source");
		fc.setApproveButtonToolTipText("Load source file(s) inside selected directory (and its' sub-directories)");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.cfg_draw_on_change = false;
				Parser parser =  new Parser(diagram);
				int parsed = parser.parse(fc.getSelectedFile());
				diagram.cfg_draw_on_change = true;
				
				if (parsed > 0) {
					diagram.draw();
					JOptionPane.showMessageDialog(this
							, String.format("Loaded %d file!", parsed)
							, title
							, JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this
							, "Loaded 0 file!"
							, title
							, JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this
						, e.getMessage()
						, title
						, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void doImage(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				String path = f.getAbsolutePath();
				String ext = path.substring(path.length() - 4).toLowerCase();
				return ext.equals(".jpg") || ext.equals(".png");
			}

			@Override
			public String getDescription() {
				return "Supported Image Formats (.JPG, .PNG)";
			}
			
		});
		
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			diagram.saveImage(new DiagramImageObserver(fc.getSelectedFile().getAbsolutePath()));
		}
	}
	
	private void doGenerate(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Export Here");
		fc.setApproveButtonToolTipText("Export source file(s) into selected directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				CodeGenerator cg = new CodeGenerator(diagram);
				int files = cg.generate(fc.getSelectedFile().getAbsolutePath());
				JOptionPane.showMessageDialog(this
						, String.format("Successfully generated %d file(s)", files)
						, title
						, JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this
						, e.getMessage()
						, title
						, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}


