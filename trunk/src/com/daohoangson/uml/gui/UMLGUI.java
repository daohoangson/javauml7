package com.daohoangson.uml.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import com.nguyenthanhan.uml.gui.InfoForm;
import com.nguyenthanhan.uml.gui.InterfaceForm;
import com.nguyenthanhan.uml.gui.ListForm;
import com.nguyenthanhan.uml.gui.MethodForm;
import com.nguyenthanhan.uml.gui.PropertyForm;
import com.tranvietson.uml.codegen.CodeGenerator;

/**
 * The primary Graphical User Interface which includes all other components. It
 * even has a menu bar. It supports shortcut keys also. Using it is as simple as
 * using a normal <code>JFrame</code> (the only difference is this
 * <code>JFrame</code> is shipped with everything you may need!
 * 
 * <pre>
 * UMLGUI gui = new UMLGUI();
 * gui.setVisible(true);
 * </pre>
 * 
 * You may enable the debug mode of ALL other component by triggering
 * <code>UMLGUI.debuging = true;</code>. It's kind of a shortcut to debug
 * globally.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see UMLGUI#debuging
 */
public class UMLGUI extends JFrame implements ActionListener, ContainerListener {
	private static final long serialVersionUID = -2192671131702680754L;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debuging = false;
	/**
	 * The diagram which holds all the structure and build primary display area
	 */
	public Diagram diagram;

	/**
	 * Constructor. Sets up everything up. Including a menu bar and the diagram.
	 * 
	 */
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
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);

		mfi = new JMenuItem("Save as Image");
		mfi.setActionCommand("image");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_S);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);

		mfi = new JMenuItem("Export Source File(s)");
		mfi.setActionCommand("generate");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_E);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);

		mfi = new JMenuItem("Exit");
		mfi.setActionCommand("exit");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_X);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_DOWN_MASK));
		menuFile.add(mfi);

		JMenu menuNew = new JMenu("New");
		menuNew.setMnemonic(KeyEvent.VK_N);
		menuBar.add(menuNew);

		String[] structures = new String[] { "Class", "Interface", "Property",
				"Method", "Argument" };
		for (int i = 0; i < structures.length; i++) {
			JMenuItem mi = new JMenuItem(structures[i]);
			mi.setActionCommand("new");
			mi.addActionListener(this);
			mi.setMnemonic((int) structures[i].charAt(0));
			mi.setAccelerator(KeyStroke.getKeyStroke(structures[i].charAt(0),
					InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
			menuNew.add(mi);
		}

		setJMenuBar(menuBar);

		diagram = new Diagram();
		diagram.addContainerListener(this);
		add(diagram.getScrollable());

		pack();
		setTitle("UML");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Structure.debuging = UMLGUI.debuging;
		Diagram.debuging = UMLGUI.debuging;
		Parser.debuging = UMLGUI.debuging;
	}

	/**
	 * Main handler for all actions.<br/>
	 * Supported actions:<br/>
	 * <ul>
	 * <li>exit: Simply dispose the JFrame</li>
	 * <li>load: Do the load procedure by calling {@link #doLoad(String)}</li>
	 * <li>image: Do the save image procedure by calling
	 * {@link #doImage(String)}</li>
	 * <li>generate: Do the generate source procedure by calling
	 * {@link #doGenerate(String)}</li>
	 * <li>new: Create new structure
	 * <ul>
	 * <li>Class: use {@link ClassForm}</li>
	 * <li>Interface: use {@link InterfaceForm}</li>
	 * <li>Property: use
	 * {@link ListForm#select(java.awt.Frame, String, Structure[], String)}
	 * allowing user to choose a Class or a Interface before using
	 * {@link PropertyForm}</li>
	 * <li>Method: use
	 * {@link ListForm#select(java.awt.Frame, String, Structure[], String)}
	 * allowing user to choose a Class or a Interface before using
	 * {@link MethodForm}</li>
	 * <li>Argument: user
	 * {@link ListForm#select(java.awt.Frame, String, Structure[], String)} 2
	 * times before using {@link ArgumentForm}
	 * </ul>
	 * </li>
	 * </ul>
	 */
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
			// creating new structure
			String type = s.getText();
			if (type.equals("Class")) {
				new ClassForm(this, diagram);
			} else if (type.equals("Interface")) {
				new InterfaceForm(this, diagram);
			} else if (type.equals("Property")) {
				Structure structure = ListForm.select(this, "New Property",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new PropertyForm(this, structure);
				}
			} else if (type.equals("Method")) {
				Structure structure = ListForm.select(this, "New Method",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new MethodForm(this, structure);
				}
			} else if (type.equals("Argument")) {
				Structure structure1 = ListForm.select(this, "New Argument",
						diagram.getStructures(), "Class/Interface");
				if (structure1 != null) {
					Structure structure2 = ListForm.select(this,
							"New Argument", Structure.filterStructureArray(
									structure1.getChildren(),
									new String[] { "Method" }), "Method");
					if (structure2 != null) {
						new ArgumentForm(this, structure2);
					}
				}
			}
		}
	}

	/**
	 * Processes the Load request.<br/>
	 * At first, display a directory chooser to select the source directory. And
	 * then temporary disable drawing on change functionality of diagram (for
	 * performance reason). Create new {@linkplain Parser parser} to parse the
	 * directory recursively. Finally, trigger the diagram to update the display
	 * 
	 * @param title
	 *            the title of the action
	 * 
	 * @see Diagram#cfg_draw_on_change
	 */
	private void doLoad(String title) {
		boolean drawn = false;
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Load Source");
		fc
				.setApproveButtonToolTipText("Load source file(s) inside selected directory (and its' sub-directories)");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.cfg_draw_on_change = false;
				Parser parser = new Parser(diagram);
				int parsed = parser.parse(fc.getSelectedFile());

				if (parsed > 0) {
					diagram.draw();
					drawn = true;
					JOptionPane.showMessageDialog(this, String.format(
							"Loaded %d file(s)!", parsed), title,
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Loaded 0 file!",
							title, JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				if (UMLGUI.debuging) {
					e.printStackTrace();
				}
				JOptionPane.showMessageDialog(this, e.getMessage(), title,
						JOptionPane.ERROR_MESSAGE);
			}

			if (!drawn) {
				// we need this because sometime the parser
				// will through an exception and prevent
				// the original draw request inside the try scope
				diagram.draw();
			}
			diagram.cfg_draw_on_change = true;
		}
	}

	/**
	 * Processes the save image request.<br/>
	 * At first, use a file chooser to allow selecting a file to save image to.
	 * Use the saving functionality from the diagram to export image.
	 * 
	 * @param title
	 *            the title of the action
	 * 
	 * @see Diagram#saveImage(java.awt.image.ImageObserver)
	 * @see DiagramImageObserver
	 */
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
			diagram.saveImage(new DiagramImageObserver(fc.getSelectedFile()
					.getAbsolutePath()));
		}
	}

	/**
	 * Processes the source generating functionality.<br/>
	 * At first, display a directory chooser allowing select the destination
	 * directory for generating. And then, obviously, create a
	 * {@linkplain CodeGenerator code generator} to generate to the specified
	 * directory
	 * 
	 * @param title
	 *            the title of the action
	 */
	private void doGenerate(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Export Here");
		fc
				.setApproveButtonToolTipText("Export source file(s) into selected directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				CodeGenerator cg = new CodeGenerator(diagram);
				int files = cg.generate(fc.getSelectedFile().getAbsolutePath());
				JOptionPane.showMessageDialog(this, String.format(
						"Successfully generated %d file(s)", files), title,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), title,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void setupInfoForms(Component c) {
		if (c instanceof Container) {
			Container container = (Container) c;

			Component[] children = container.getComponents();
			for (int i = 0; i < children.length; i++) {
				setupInfoForms(children[i]);
			}
		}

		if (c instanceof DnDPanel) {
			DnDPanel panel = (DnDPanel) c;
			DnDLabel label = (DnDLabel) panel.getHead();
			final UMLGUI owner = this;
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					DnDLabel label = (DnDLabel) e.getSource();
					new InfoForm(owner, label.getStructure());
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		setupInfoForms(e.getChild());
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		// TODO Auto-generated method stub

	}
}
