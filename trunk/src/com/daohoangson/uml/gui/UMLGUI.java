package com.daohoangson.uml.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import com.daohoangson.uml.parser.Parser;
import com.daohoangson.uml.structures.Structure;
import com.nguyenthanhan.uml.gui.AboutForm;
import com.nguyenthanhan.uml.gui.ArgumentForm;
import com.nguyenthanhan.uml.gui.ClassForm;
import com.nguyenthanhan.uml.gui.FindForm;
import com.nguyenthanhan.uml.gui.InfoForm;
import com.nguyenthanhan.uml.gui.InterfaceForm;
import com.nguyenthanhan.uml.gui.ListForm;
import com.nguyenthanhan.uml.gui.MethodForm;
import com.nguyenthanhan.uml.gui.PropertyForm;
import com.tavanduc.uml.gui.DiagramStructureGroup;
import com.tranvietson.uml.codegen.CodeGenerator;
import com.tranvietson.uml.structures.StructureException;

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
 * <code>UMLGUI.debugging = true;</code>. It's kind of a shortcut to debug
 * globally.
 * 
 * @author Dao Hoang Son
 * @version 1.2
 * 
 * @see UMLGUI#debugging
 */
public class UMLGUI extends JFrame implements ActionListener, ContainerListener {
	private static final long serialVersionUID = -2192671131702680754L;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debugging = false;
	/**
	 * The diagram which holds all the structure and build primary display area
	 */
	public Diagram diagram;
	public Outline outline;
	/**
	 * Hold the last opened window and used to make sure no more than one
	 * additional window is opened at a given time.
	 * 
	 * @see #doInfo(Structure)
	 * @see #doRelated(String, Structure, boolean)
	 */
	private Window lastOpened = null;
	/**
	 * Hold the last checked structure for commands
	 * 
	 * @see #getStructureCommands(Structure, boolean)
	 */
	private Structure lastStructure = null;
	/**
	 * Hold all disabled actions
	 * 
	 * @see #findAndSetActionEnabled(Component[], String, boolean)
	 * @see #getStructureCommands(Structure, boolean)
	 */
	private List<String> disabledActions = new LinkedList<String>();

	/**
	 * Constructor. Sets up everything up. Including a menu bar with the
	 * structure as follow
	 * <ul>
	 * <li><u>F</u>ile (file)
	 * <ul>
	 * <li>Clea<u>r</u> (clear)</li>
	 * <li>Re-Dra<u>w</u> (draw)</li>
	 * <li>Quick <u>F</u>ind (find)</li>
	 * <li>Show Re<u>l</u>ated Structures (related)</li>
	 * <li>L<u>o</u>ad Source File(s) (load)</li>
	 * <li><u>S</u>ave as Image (image)</li>
	 * <li><u>C</u>lipping (clipping)</li>
	 * <li><u>E</u>xport Source File(s) (export)</li>
	 * <li>Close (exit)</li>
	 * </ul>
	 * </li>
	 * <li><u>N</u>ew (new)
	 * <ul>
	 * <li><u>C</u>lass</li>
	 * <li><u>I</u>nterface</li>
	 * <li><u>P</u>roperty</li>
	 * <li><u>M</u>ethod</li>
	 * <li><u>A</u>rgument</li>
	 * </ul>
	 * </li>
	 * <li><u>H</u>elp (help)
	 * <ul>
	 * <li><u>A</u>bout (about)</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * It also has a tool bar (no drag-able)
	 * <ul>
	 * <li>Zoom In (zoom.in)</li>
	 * <li>Zoom Out (zoom.out)</li>
	 * </ul>
	 * Items can be disable by using {@link #setActionEnabled(String, boolean)}
	 * with the correct action command (specified in parenthesis above).
	 * 
	 * Of course, the diagram is displayed at the center with outline on the
	 * left
	 */
	public UMLGUI() {
		// start building main menu
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menuFile.setActionCommand("file");
		menuFile.setMnemonic(KeyEvent.VK_F);
		UMLGUI.setIcon(menuFile, "file", null);
		menuBar.add(menuFile);

		JMenuItem mfi;

		mfi = new JMenuItem("Clear");
		mfi.setActionCommand("clear");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_R);
		UMLGUI.setIcon(mfi, "clear", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Re-Draw");
		mfi.setActionCommand("draw");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_W);
		UMLGUI.setIcon(mfi, "draw", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Quick Find");
		mfi.setActionCommand("find");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_F);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "find", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Show Related Structures");
		mfi.setActionCommand("related");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_L);
		UMLGUI.setIcon(mfi, "related", null);
		menuFile.add(mfi);

		menuFile.addSeparator();

		mfi = new JMenuItem("Load Source File(s)");
		mfi.setActionCommand("load");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_O);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "load", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Save as Image");
		mfi.setActionCommand("image");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_S);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "image", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Clipping");
		mfi.setActionCommand("clipping");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_C);
		UMLGUI.setIcon(mfi, "clipping", null);
		menuFile.add(mfi);

		mfi = new JMenuItem("Export Source File(s)");
		mfi.setActionCommand("export");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_E);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "export", null);
		menuFile.add(mfi);

		menuFile.addSeparator();

		// this is "Exit" originally but sometimes it doesn't make sense to
		// exit in a sub window so I changed it to "Close"
		mfi = new JMenuItem("Close");
		mfi.setActionCommand("exit");
		mfi.addActionListener(this);
		UMLGUI.setIcon(mfi, "exit", null);
		menuFile.add(mfi);

		JMenu menuNew = new JMenu("New");
		menuNew.setActionCommand("new");
		menuNew.setMnemonic(KeyEvent.VK_N);
		UMLGUI.setIcon(menuNew, "new", null);
		menuBar.add(menuNew);

		String[] structures = new String[] { "Class", "Interface", "Property",
				"Method", "Argument" };
		for (int i = 0; i < structures.length; i++) {
			JMenuItem mi = new JMenuItem(structures[i]);
			mi.setActionCommand("new." + structures[i].toLowerCase());
			mi.addActionListener(this);
			mi.setMnemonic((int) structures[i].charAt(0));
			mi.setAccelerator(KeyStroke.getKeyStroke(structures[i].charAt(0),
					InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
			UMLGUI.setIcon(mi, "new_" + structures[i].toLowerCase(), null);
			menuNew.add(mi);
		}

		JMenu menuHelp = new JMenu("Help");
		menuHelp.setActionCommand("help");
		menuHelp.setMnemonic(KeyEvent.VK_H);
		UMLGUI.setIcon(menuHelp, "help", null);
		menuBar.add(menuHelp);

		JMenuItem mhi;

		mhi = new JMenuItem("About");
		mhi.setActionCommand("about");
		mhi.addActionListener(this);
		mhi.setMnemonic(KeyEvent.VK_A);
		UMLGUI.setIcon(mhi, "about", null);
		menuHelp.add(mhi);
		// finished with the main menu

		// start building the tool bar
		JToolBar toolBar = new JToolBar();

		JButton tbb;

		tbb = new JButton();
		tbb.setActionCommand("zoom.in");
		tbb.addActionListener(this);
		UMLGUI.setIcon(tbb, "zoom_in", null);
		toolBar.add(tbb);

		tbb = new JButton();
		tbb.setActionCommand("zoom.out");
		tbb.addActionListener(this);
		UMLGUI.setIcon(tbb, "zoom_out", null);
		toolBar.add(tbb);

		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		// finished with the tool bar

		// start adding components to our frame
		// it's funny that this scope it much shorter than the above part
		setLayout(new BorderLayout());

		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.LINE_END);

		diagram = new Diagram();
		diagram.addContainerListener(this);
		new UMLGUIMouseMaster(this, diagram);
		outline = new Outline(diagram);
		new UMLGUIMouseMaster(this, outline);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outline
				.getScrollable(), diagram.getScrollable());
		sp.setDividerLocation(200);
		add(sp, BorderLayout.CENTER);
		// finished adding main components

		// get ready to display now...
		setSize(700, 400);
		setTitle("UML");

		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (SecurityException se) {
			// just ignore
			// there are cases when we can't do this simple close operation
			// in an Applet for example?
		}

		// take care of debugging
		if (UMLGUI.debugging) {
			Structure.debugging = UMLGUI.debugging;
			Diagram.debugging = UMLGUI.debugging;
			Parser.debugging = UMLGUI.debugging;
		}
	}

	/**
	 * Set the enabled property for menu/menuitem by action command
	 * 
	 * @param action
	 *            the action command
	 * @param enabled
	 *            the new value for the menu/menuitem
	 * @return true if the menu/menuitem can be found and set
	 * 
	 * @see #findAndSetActionEnabled(Component[], String, boolean)
	 */
	public boolean setActionEnabled(String action, boolean enabled) {
		return findAndSetActionEnabled(getJMenuBar().getComponents(), action,
				enabled);
	}

	/**
	 * Finds in a list of components, looks for specified action command, set
	 * the enabled property to the requested value. This method recurses with
	 * children of components until it can find the requested action command
	 * 
	 * @param components
	 *            an array of components
	 * @param action
	 *            the needed action command
	 * @param enabled
	 *            the new value for the component
	 * @return true if the requested action command component can be found
	 */
	private boolean findAndSetActionEnabled(Component[] components,
			String action, boolean enabled) {

		if (enabled && disabledActions.contains(action)) {
			disabledActions.remove(action);
		}
		if (!enabled && !disabledActions.contains(action)) {
			disabledActions.add(action);
		}

		// check for the disabled actions when perform the action
		// instead of disable the menu item
		// actually, disabling menus make it more professional
		// but it's hard to disable all of them,
		// especially when we have SO MANY popup menus
		// TODO: find a nicer way to do this
		// for (int i = 0; i < components.length; i++) {
		// if (components[i] instanceof AbstractButton) {
		// AbstractButton button = (AbstractButton) components[i];
		// if (button.getActionCommand().equals(action)) {
		// button.setEnabled(enabled);
		//
		// return true;
		// } else if (button instanceof JMenu) {
		// JMenu menu = (JMenu) button;
		// if (findAndSetActionEnabled(menu.getMenuComponents(),
		// action, enabled)) {
		// return true;
		// }
		// }
		// }
		// }
		//
		// return false;
		return true;
	}

	/**
	 * Gets the icon resource and sets it for the component. It's kind of a
	 * shortcut method to quickly do things
	 * 
	 * @param component
	 *            the component that needs icons
	 * @param icon_name
	 *            the name of the icon
	 * @param pressedIcon_name
	 *            the name of the pressed icon
	 * 
	 * @see #getIcon(String)
	 */
	private static void setIcon(AbstractButton component, String icon_name,
			String pressedIcon_name) {
		Icon icon = UMLGUI.getIcon(icon_name);
		if (icon != null) {
			component.setIcon(icon);
		}

		Icon pressedIcon = UMLGUI.getIcon(pressedIcon_name);
		if (pressedIcon != null) {
			component.setPressedIcon(pressedIcon);
		}
	}

	/**
	 * Get the icon resource using {@link Class#getResource(String)}. The name
	 * for the resource is built with the pattern
	 * icon/<strong>[name]</strong>.gif
	 * 
	 * @param name
	 *            the icon name
	 * @return the <code>Icon</code> object if found or null otherwise
	 */
	public static Icon getIcon(String name) {
		if (name == null) {
			return null;
		}
		name = "icon/" + name + ".gif";
		URL iconURL = UMLGUI.class.getResource(name);
		if (iconURL != null) {
			return new ImageIcon(iconURL);
		} else {
			if (UMLGUI.debugging) {
				System.err.println("Icon not found: " + name);
			}
			return null;
		}
	}

	/**
	 * Main handler for all actions.<br/>
	 * Supported actions (listed by action command):<br/>
	 * <ul>
	 * <li>clear: Confirm and clear the entire diagram</li>
	 * <li>draw: Call the {@link Diagram#draw()} method to re-draw immediately</li>
	 * <li>find: Display the quick find dialog using {@link #doFind(String)}</li>
	 * <li>related: Display related structures by calling
	 * {@link #doRelated(String, Structure, boolean)}</li>
	 * <li>load: Do the load procedure by calling {@link #doLoad(String)}</li>
	 * <li>image: Do the save image procedure by calling
	 * {@link #doImage(String)}</li>
	 * <li>clipping: Do the clipping procedure by calling
	 * {@link #doClipping(String)}</li>
	 * <li>export: Do the export source procedure by calling
	 * {@link #doExport(String)}</li>
	 * <li>exit: Simply dispose the JFrame</li>
	 * <li>new: Create new structure
	 * <ul>
	 * <li>Class: use {@link ClassForm}</li>
	 * <li>Interface: use {@link InterfaceForm}</li>
	 * <li>Property: use
	 * {@link ListForm#select(Window, String, Structure[], String)} allowing
	 * user to choose a Class or a Interface before using {@link PropertyForm}</li>
	 * <li>Method: use
	 * {@link ListForm#select(Window, String, Structure[], String)} allowing
	 * user to choose a Class or a Interface before using {@link MethodForm}</li>
	 * <li>Argument: user
	 * {@link ListForm#select(Window, String, Structure[], String)} 2 times
	 * before using {@link ArgumentForm}
	 * </ul>
	 * </li>
	 * <li>about: Display author information</li>
	 * <li>Tool bar actions
	 * <ul>
	 * <li>zoom.in/zoom.out: Using {@link Diagram#setSizeFactor(float)} to
	 * change the zooming level</li>
	 * </ul>
	 * </li>
	 * <li>info.x: Redirect to {@link #doStructureCommand(String, String)}</li>
	 * </ul>
	 * This method even catches all Security Exception if they are thrown out.
	 * This behavior is expected in limited policy environments (in an Applet?)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		AbstractButton s = (AbstractButton) e.getSource();

		// check for disabled actions
		Iterator<String> itr = disabledActions.iterator();
		while (itr.hasNext()) {
			if (action.startsWith(itr.next())) {
				// stop perform action here
				JOptionPane.showMessageDialog(this,
						"Sorry. This action is currently not available", s
								.getText(), JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (action.equals("clear")) {
			// confirm and clear the entire diagram
			if (JOptionPane
					.showConfirmDialog(
							this,
							"Are you sure you want to clear the Diagram?\nThis action can not be undone!",
							"Clear", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				diagram.clear();
			}
		} else if (action.equals("draw")) {
			// re-draw the diagram
			diagram.draw();
		} else if (action.equals("find")) {
			// display quick find form
			doFind(s.getText());
		} else if (action.equals("related")) {
			// display related structures selecting form
			doRelated(s.getText(), null, true);
		} else if (action.equals("load")) {
			// start loading
			try {
				doLoad(s.getText());
			} catch (SecurityException se) {
				showPolicyError(s.getText());
			}
		} else if (action.equals("image")) {
			// start saving image
			try {
				doImage(s.getText());
			} catch (SecurityException se) {
				showPolicyError(s.getText());
			}
		} else if (action.equals("clipping")) {
			// start clipping
			try {
				doClipping(s.getText());
			} catch (SecurityException se) {
				showPolicyError(s.getText());
			}
		} else if (action.equals("export")) {
			// start exporting
			try {
				doExport(s.getText());
			} catch (SecurityException se) {
				showPolicyError(s.getText());
			}
		} else if (action.equals("exit")) {
			// close the form
			dispose();
		} else if (action.startsWith("new")) {
			// creating new structure
			String type = action.substring("new.".length());
			if (type.equals("class")) {
				// new class
				new ClassForm(this, diagram);
			} else if (type.equals("interface")) {
				// new interface
				new InterfaceForm(this, diagram);
			} else if (type.equals("property")) {
				// new property
				// must select a class/interface first
				Structure structure = ListForm.select(this, "New Property",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new PropertyForm(this, structure);
				}
			} else if (type.equals("method")) {
				// new method
				// must select a class/interface first
				Structure structure = ListForm.select(this, "New Method",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new MethodForm(this, structure);
				}
			} else if (type.equals("argument")) {
				// new argument
				// must select a method from a class/structure first
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
		} else if (action.equals("about")) {
			// display about form
			new AboutForm(this);
		} else if (action.startsWith("zoom.")) {
			float dsf = 0.25f;
			if (action.equals("zoom.in")) {
				dsf *= 1;
			} else {
				dsf *= -1;
			}
			diagram.setSizeFactor(diagram.getSizeFactor() + dsf);
			// float size_factor = diagram.getSizeFactor();
		} else if (action.startsWith("info.")) {
			// redirect action handler
			doStructureCommand(s.getText(), action);
		}
	}

	/**
	 * Displays the quick find form
	 * 
	 * @param title
	 *            the title for the window
	 */
	void doFind(String title) {
		Structure structure = FindForm.find(this, title, diagram
				.getStructures());
		if (structure != null) {
			diagram.ensureStructureIsVisible(structure);
			outline.ensureStructureIsVisible(structure);
		}
	}

	/**
	 * Displays the related structures of a structure. It will ask for the
	 * structure if null is passed. It also display a dialog to ask if the users
	 * want to show multiplicity related structure or not.
	 * 
	 * @param title
	 *            the title for the window
	 * @param structure
	 *            the root structure. Pass null to use a {@link FindForm}
	 * @param ask_for_mode
	 *            a boolean value specifies to ask for mode or not. If
	 *            <code>false</code> is passed, the mode is automatically set to
	 *            <code>true</code>
	 */
	void doRelated(String title, Structure structure, boolean ask_for_mode) {
		if (lastOpened != null && lastOpened.isVisible()) {
			lastOpened.requestFocus();
			return;
		}

		if (structure == null) {
			// try to find what user wants
			structure = FindForm.find(this, title, diagram.getStructures());
		}

		if (structure == null) {
			// nothing is selected, simply do nothing
			return;
		}

		boolean flag_display_multiplicity_related;
		if (ask_for_mode) {
			if (JOptionPane.showOptionDialog(this,
					"Do you want to display Multiplicity Related structures?",
					title, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
				flag_display_multiplicity_related = true;
			} else {
				flag_display_multiplicity_related = false;
			}
		} else {
			flag_display_multiplicity_related = true;
		}

		UMLGUI target_gui = new UMLGUI();
		lastOpened = target_gui;
		Diagram d = target_gui.diagram;

		d.setAutoDrawing(false);
		findRelated(structure, d, flag_display_multiplicity_related);
		d.setAutoDrawing(true);

		d.ensureStructureIsVisible(structure);

		// disable some functionalities in the new window
		target_gui.setActionEnabled("clear", false);
		target_gui.setActionEnabled("load", false);
		target_gui.setActionEnabled("new", false);

		target_gui.setTitle(title + " - " + structure.getName());
		target_gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		target_gui.setLocationRelativeTo(null);
		target_gui.setVisible(true);
	}

	/**
	 * Finds related structures for the requested structure. Recurse itself also
	 * 
	 * @param structure
	 *            the structure to find relations
	 * @param target_diagram
	 *            the diagram to add found structures to
	 * @param flag_display_multiplicity_related
	 *            should look for multiplicity relations or not
	 */
	private void findRelated(Structure structure, Diagram target_diagram,
			boolean flag_display_multiplicity_related) {
		if (!target_diagram.add(structure)) {
			// the diagram refused the adding request
			// so we should stop recursing here
			return;
		}

		Structure[] structures = diagram.getStructures();
		for (int i = 0; i < structures.length; i++) {
			Structure other_structure = structures[i];
			if (other_structure.checkIsChildOf(structure)
					|| structure.checkIsChildOf(other_structure)) {
				findRelated(other_structure, target_diagram, false);
			} else {
				if (flag_display_multiplicity_related) {
					Structure[] children = other_structure.getChildren();
					for (int j = 0; j < children.length; j++) {
						Structure[] types = children[j].getTypeAsStructure();
						for (int k = 0; k < types.length; k++) {
							if (types[k] == structure) {
								findRelated(other_structure, target_diagram,
										false);
							}
						}
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
	 * @see Diagram#setAutoDrawing(boolean)
	 */
	void doLoad(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Load Source");
		fc
				.setApproveButtonToolTipText("Load source file(s) inside selected directory (and its' sub-directories)");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				diagram.setAutoDrawing(false);
				Parser parser = new Parser(diagram);
				int parsed = parser.parse(fc.getSelectedFile());

				if (parsed > 0) {
					diagram.setAutoDrawing(true);
					JOptionPane.showMessageDialog(this, String.format(
							"Loaded %d file(s)!", parsed), title,
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Loaded 0 file!",
							title, JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				if (UMLGUI.debugging) {
					e.printStackTrace();
				}
				JOptionPane.showMessageDialog(this, e.getMessage(), title,
						JOptionPane.ERROR_MESSAGE);
			}

			// just to make sure because sometimes the try catch statement will
			// fail and we need the diagram to be drawn
			diagram.setAutoDrawing(true);
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
	void doImage(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setFileFilter(new DiagramImageFilter());

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			diagram.saveImage(new DiagramImageObserver(fc.getSelectedFile()
					.getAbsolutePath()));
		}
	}

	/**
	 * Processes the clipping request.<br/>
	 * Simply makes use of the method of the diagram.
	 * 
	 * @param title
	 *            the title of the action
	 * 
	 * @see Diagram#startClipping(java.awt.image.ImageObserver)
	 * @see DiagramImageObserver
	 */
	void doClipping(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setFileFilter(new DiagramImageFilter());

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(this,
					"OK, select the area you want to take screen shot now...",
					title, JOptionPane.INFORMATION_MESSAGE);
			diagram.startClipping(new DiagramImageObserver(fc.getSelectedFile()
					.getAbsolutePath()));
		}
	}

	/**
	 * Processes the source generating functionality.<br/>
	 * At first, display a directory chooser allowing select the destination
	 * directory for generating. And then, obviously, create a
	 * {@linkplain CodeGenerator code generator} to export to the specified
	 * directory
	 * 
	 * @param title
	 *            the title of the action
	 */
	void doExport(String title) {
		JFileChooser fc = new JFileChooser(".");
		fc.setDialogTitle(title);
		fc.setApproveButtonText("Export Here");
		fc
				.setApproveButtonToolTipText("Export source file(s) into selected directory");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				CodeGenerator cg = new CodeGenerator(fc.getSelectedFile()
						.getAbsolutePath());
				Structure[] structures = diagram.getStructures();
				int files = 0;
				for (int i = 0; i < structures.length; i++) {
					if (cg.generate(structures[i])) {
						files++;
					}
				}
				JOptionPane.showMessageDialog(this, String.format(
						"Successfully exported %d file(s)", files), title,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), title,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Displays the {@link InfoForm} for the requested structure
	 * 
	 * @param structure
	 *            the requested structure. Null structure will be ignored
	 */
	void doInfo(Structure structure) {
		if (structure == null) {
			if (UMLGUI.debugging) {
				System.err.println("doInfo() got null structure!");
			}
			return;
		}

		if (lastOpened != null && lastOpened.isVisible()) {
			// we don't allow multiple information form to be
			// displayed
			lastOpened.requestFocus();
			return;
		}

		lastOpened = new InfoForm(this, structure);
		UMLGUICommand[] commands = getStructureCommands(structure, false);

		if (commands.length > 0) {
			// we have some commands to do
			JButton btn;
			Box box = ((InfoForm) lastOpened).getBox();

			for (int i = 0; i < commands.length; i++) {
				UMLGUICommand c = commands[i];
				if (i > 0) {
					box.add(Box.createVerticalStrut(10));
				}

				btn = new JButton(c.title);
				btn.setAlignmentX((float) 0.5);
				if (c.icon != null) {
					btn.setIcon(c.icon);
				}
				btn.setActionCommand(c.action);
				btn.addActionListener(this);
				box.add(btn);
			}

			box.validate();
			lastOpened.pack();
			lastOpened.setLocationRelativeTo(null);
			lastOpened.validate();
		}

		diagram.setFocus(structure);
		lastOpened.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// remove the focus of diagram
				// TODO: Too complicated. Any other ways?
				diagram.setFocus(null);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// remove the focus of diagram
				// TODO: This is also too complicated :(
				diagram.setFocus(null);
			}
		});
	}

	void doPopup(MouseEvent e, Structure structure) {
		if (!e.isPopupTrigger()) {
			return;
		}

		Object obj = e.getSource();

		if (obj instanceof Component) {
			UMLGUICommand[] commands = getStructureCommands(structure, true);

			if (commands != null && commands.length > 0) {
				JPopupMenu popup = new JPopupMenu();
				JMenuItem mi;

				for (int i = 0; i < commands.length; i++) {
					UMLGUICommand c = commands[i];

					mi = new JMenuItem(c.title);
					mi.setActionCommand(c.action);
					if (c.icon != null) {
						mi.setIcon(c.icon);
					}
					mi.addActionListener(this);
					popup.add(mi);
				}

				popup.show((Component) obj, e.getX(), e.getY());
			}
		}
	}

	private UMLGUICommand[] getStructureCommands(Structure structure,
			boolean info) {
		List<UMLGUICommand> list = new LinkedList<UMLGUICommand>();

		if (structure != null) {
			if (info) {
				list.add(new UMLGUICommand("Information", "info.show"));
			}
			list.add(new UMLGUICommand("Related Structure", "info.related",
					"related"));

			String structureName = structure.getStructureName();
			if (structureName.equals("Class")
					|| structureName.equals("Interface")) {
				list
						.add(new UMLGUICommand("New Property",
								"info.new.property"));
				list.add(new UMLGUICommand("New Method", "info.new.method"));

				Structure[] methods = Structure.filterStructureArray(structure
						.getChildren(), new String[] { "Method" });
				if (methods.length > 0) {
					list.add(new UMLGUICommand("New Argument",
							"info.new.argument"));
				}
			}

			list.add(new UMLGUICommand("Remove this", "info.remove"));
		} else {
			if (info) {
				list.add(new UMLGUICommand("About", "about"));
			}
			list.add(new UMLGUICommand("New Class", "new.class"));
			list.add(new UMLGUICommand("New Interface", "new.interface"));
		}

		lastStructure = structure;
		return list.toArray(new UMLGUICommand[0]);
	}

	private void doStructureCommand(String title, String action) {
		String real_action = action.substring("info.".length());
		Structure structure = lastStructure;

		if (real_action.equals("show")) {
			// display the information form
			if (lastOpened != null) {
				lastOpened.dispose();
			}
			doInfo(structure);
		} else if (real_action.equals("related")) {
			// display the related structure form
			if (lastOpened != null) {
				lastOpened.dispose();
			}
			doRelated(title, structure, false);
		} else if (real_action.startsWith("new")) {
			// create new structure
			String type = real_action.substring("new.".length());
			if (type.equals("property")) {
				// new property (for this structure)
				new PropertyForm(this, structure);
			} else if (type.equals("method")) {
				// new method (for this structure)
				new MethodForm(this, structure);
			} else if (type.equals("argument")) {
				// new argument (for this structure)
				// must select a method first
				Structure method = ListForm.select(this, "New Argument",
						Structure.filterStructureArray(structure.getChildren(),
								new String[] { "Method" }), "Method");
				if (method != null) {
					new ArgumentForm(this, method);
				}
			}
		} else if (real_action.equals("remove")) {
			// remove the structure completely
			try {
				structure.dispose();
				if (lastOpened != null) {
					lastOpened.dispose();
				}
			} catch (StructureException exception) {
				JOptionPane.showMessageDialog(this, "Unable to remove "
						+ structure + "\n" + exception.getMessage(),
						"Structure Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Display a policy error
	 * 
	 * @param title
	 *            the title for the message box
	 */
	private void showPolicyError(String title) {
		JOptionPane
				.showMessageDialog(
						this,
						"Sorry, your system doesn't allow us to perform this action.\nPlease try another version of the application to use all of the (amazing) features",
						title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Sets up hooks to display {@link InfoForm} when user double clicks the
	 * component and show a popup menu if user right clicks. It also process
	 * children components of the original component to make sure everything
	 * will work well
	 * 
	 * @param c
	 *            the component
	 */
	private void setupDiagramComponentActions(Component c) {
		if (c instanceof Container) {
			Container container = (Container) c;

			Component[] children = container.getComponents();
			for (int i = 0; i < children.length; i++) {
				setupDiagramComponentActions(children[i]);
			}

			// watch future changes too
			container.addContainerListener(this);
		}

		if (c instanceof StructureBased) {
			// make it double-click-able and right-click-able
			new UMLGUIMouseMaster(this, c);
		}

		if (c instanceof DiagramStructureGroup) {
			// make it drop-able
			new UMLGUIDroper(c);
		} else if (c instanceof StructureBased) {
			// make it drag-able
			new UMLGUIDrager(c);
		}
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		if (e.getSource() == diagram) {
			setupDiagramComponentActions(e.getChild());
		}
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		// do nothing intentionally
	}
}

class UMLGUICommand {
	public String title;
	public String action;
	public Icon icon;

	public UMLGUICommand(String title, String action, String icon_name) {
		this.title = title;
		this.action = action;
		icon = UMLGUI.getIcon(icon_name);
	}

	public UMLGUICommand(String title, String action) {
		this(title, action, action.replace('.', '_'));
	}
}

class UMLGUIMouseMaster implements MouseListener {
	private UMLGUI gui;
	private Structure structure;

	public UMLGUIMouseMaster(UMLGUI gui, Component component) {
		this.gui = gui;

		if (component instanceof StructureBased) {
			structure = ((StructureBased) component).getStructure();
		} else {
			structure = null;
		}

		component.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			gui.doInfo(structure);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO: Sometime charming here?
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO: Sometime charming here?
	}

	@Override
	public void mousePressed(MouseEvent e) {
		doPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		doPopup(e);
	}

	private void doPopup(MouseEvent e) {
		Structure structure = this.structure;
		if (structure == null) {
			// make sure no structure can be associated with the menu
			if (e.getSource() instanceof Outline) {
				Outline outline = (Outline) e.getSource();
				TreePath path = outline.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					Object node = path.getLastPathComponent();
					if (node instanceof TreeNodeStructure) {
						structure = ((TreeNodeStructure) node).getStructure();
					}
				}
			}
		}

		gui.doPopup(e, structure);
	}
}

class UMLGUIDrager implements DragGestureListener, DragSourceListener {
	/**
	 * The component to get dragging enabled
	 */
	private Component component;
	/**
	 * The primary dragging source to support drag functionality
	 */
	private DragSource dragSource;
	/**
	 * The original (foreground) color of the label
	 */
	private Color original_color;
	/**
	 * The color when the label is being dragged
	 */
	private Color cfg_drag_color = Color.blue;

	public UMLGUIDrager(Component component) {
		this.component = component;

		if (component instanceof StructureBased) {
			// this can be created any time with any component
			// but it only works with StructureBased components
			original_color = component.getForeground();

			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(component,
					DnDConstants.ACTION_COPY_OR_MOVE, this);
		}
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		component.setForeground(cfg_drag_color);
		dragSource.startDrag(dge, null, new TransferableStructure(
				((StructureBased) component).getStructure()), this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (component.getForeground() == cfg_drag_color) {
			component.setForeground(original_color);
		}
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub

	}
}

class UMLGUIDroper implements DropTargetListener {
	/**
	 * The component to get dragging enabled
	 */
	private Component component;
	/**
	 * The corresponding structure. This can be null sometimes
	 */
	private Structure structure;
	/**
	 * The original (foreground) color of the panel
	 */
	private Color original_color;
	/**
	 * The color when the panel has something dragging by
	 */
	private Color cfg_hover_color = Color.red;

	public UMLGUIDroper(Component component) {
		this.component = component;
		original_color = component.getForeground();

		if (component instanceof StructureBased) {
			structure = ((StructureBased) component).getStructure();
		} else {
			structure = null;
		}

		new DropTarget(component, this);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		component.setForeground(cfg_hover_color);
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		component.setForeground(original_color);
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	/**
	 * Accepts the drop action. Removes the structure from its container and add
	 * to the corresponding structure of this component
	 * 
	 * @param dtde
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		component.setForeground(original_color);

		try {
			Structure transfered = (Structure) dtde.getTransferable()
					.getTransferData(TransferableStructure.df);

			if (transfered.getContainer() != structure) {
				int action = dtde.getDropAction();

				switch (action) {
				case DnDConstants.ACTION_COPY:
					// copying structure
					Structure copied = transfered.copy();
					structure.add(copied);

					dtde.acceptDrop(action);
					return;
				case DnDConstants.ACTION_MOVE:
					// moving structure
					if (transfered.getContainer() != null) {
						transfered.getContainer().remove(transfered);
					}

					structure.add(transfered);

					dtde.acceptDrop(action);
					return;
				}
			}
		} catch (UnsupportedFlavorException e) {
			System.err.println("Ewwww. This is not well tasted!");
		} catch (IOException e) {
			// ignore
		} catch (StructureException e) {
			JOptionPane.showMessageDialog(component,
					"Action can not be completed\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		dtde.rejectDrop();
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
}