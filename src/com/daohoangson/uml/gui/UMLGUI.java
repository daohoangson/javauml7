package com.daohoangson.uml.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.structures.Structure;
import com.nguyenthanhan.uml.gui.ClassForm;
import com.nguyenthanhan.uml.gui.InterfaceForm;
import com.nguyenthanhan.uml.gui.MethodForm;
import com.nguyenthanhan.uml.gui.PropertyForm;
import com.nguyenthanhan.uml.gui.StructureListForm;

public class UMLGUI extends JFrame implements ActionListener, WindowListener {
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
			doLoad();
		} else if (action.equals("image")) {
			doImage();
		} else if (action.equals("new")) {
			//creating new structure
			String type = s.getText();
			if (type.equals("Class")) {
				new ClassForm(diagram);
			} else if (type.equals("Interface")) {
				new InterfaceForm(diagram);
			} else if (type.equals("Property")) {
				new StructureListForm(diagram,"Property").addWindowListener(this);
			} else if (type.equals("Method")) {
				new StructureListForm(diagram,"Method").addWindowListener(this);
			}
		}
	}
	
	private void doLoad() {
		JFileChooser fc = new JFileChooser(".");
		fc.setApproveButtonText("Load Source");
		fc.setApproveButtonToolTipText("Load source file(s) inside selected directory (and its' sub-directories)");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.cfg_draw_on_change = false;
				Parser parser =  new Parser(diagram);
				parser.parse(fc.getSelectedFile());
				diagram.cfg_draw_on_change = true;
				diagram.draw();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void doImage() {
		JFileChooser fc = new JFileChooser(".");
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
			try {
				String imagepath = fc.getSelectedFile().getAbsolutePath();
				if (imagepath.indexOf('.') == -1) imagepath += ".png";
				boolean result = diagram.saveImage(imagepath);
				if (UMLGUI.debuging) {
					System.err.println("Saving Image Result: " + result);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		StructureListForm f = (StructureListForm) e.getSource();
		if (f.getSelected() != null) {
			String action = f.getActionCommand();
			if (action.equals("Property")) {
				new PropertyForm(f.getSelected());
			} else if (action.equals("Method")) {
				new MethodForm(f.getSelected());
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
