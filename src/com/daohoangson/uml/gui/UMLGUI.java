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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import javax.swing.filechooser.FileFilter;

import com.daohoangson.uml.parser.LexicalAnalyzer;
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
import com.nguyenthanhan.uml.gui.StructureForm;
import com.tavanduc.uml.gui.DiagramStructureGroup;
import com.tranvietson.uml.codegen.CodeGenerator;
import com.tranvietson.uml.structures.StructureEvent;
import com.tranvietson.uml.structures.StructureException;
import com.tranvietson.uml.structures.StructureListener;

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
public class UMLGUI extends JFrame implements ActionListener,
		ContainerListener, PropertyChangeListener {
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
	private JSplitPane splitPane;
	private int splitPaneLeftWidth = 200;
	private JMenuBar menuBar;
	private JToolBar toolBar;
	private JButton toolBar_zoomIn;
	private JButton toolBar_zoomOut;
	private boolean flag_readonly = false;
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
	 * <li><u>V</u>iew (view)
	 * <ul>
	 * <li><u>T</u>ool-bar (view.toolbar)</li>
	 * <li><u>O</u>utline (view.outline)</li>
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
	 * <li>New structures: Class, Interface, Property, Method, Argument</li>
	 * </ul>
	 * Items can be disable by using {@link #setActionEnabled(String, boolean)}
	 * with the correct action command (specified in parenthesis above).
	 * 
	 * Of course, the diagram is displayed at the center with outline on the
	 * left
	 * 
	 * @param readonly
	 *            specifies if this frame is readonly or not
	 */
	public UMLGUI(boolean readonly) {
		flag_readonly = readonly;

		// start building main menu
		menuBar = new JMenuBar();

		// menu File
		JMenu menuFile = new JMenu("File");
		menuFile.setActionCommand("file");
		menuFile.setMnemonic(KeyEvent.VK_F);
		UMLGUI.setIcon(menuFile, "file", null);
		menuBar.add(menuFile);

		JMenuItem mfi;

		// File > Clear
		mfi = new JMenuItem("Clear");
		mfi.setActionCommand("clear");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_R);
		UMLGUI.setIcon(mfi, "clear", null);
		menuFile.add(mfi);

		// File > Re-Draw
		mfi = new JMenuItem("Re-Draw");
		mfi.setActionCommand("draw");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_W);
		UMLGUI.setIcon(mfi, "draw", null);
		menuFile.add(mfi);

		// File > Quick Find
		mfi = new JMenuItem("Quick Find");
		mfi.setActionCommand("find");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_F);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "find", null);
		menuFile.add(mfi);

		// File > Show Related Structures
		mfi = new JMenuItem("Show Related Structures");
		mfi.setActionCommand("related");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_L);
		UMLGUI.setIcon(mfi, "related", null);
		menuFile.add(mfi);

		menuFile.addSeparator();

		// File > Load Source File(s)
		mfi = new JMenuItem("Load Source File(s)");
		mfi.setActionCommand("load");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_O);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "load", null);
		menuFile.add(mfi);

		// File > Save as Image
		mfi = new JMenuItem("Save as Image");
		mfi.setActionCommand("image");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_S);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "image", null);
		menuFile.add(mfi);

		// File > Clipping
		mfi = new JMenuItem("Clipping");
		mfi.setActionCommand("clipping");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_C);
		UMLGUI.setIcon(mfi, "clipping", null);
		menuFile.add(mfi);

		// File > Export Source File(s)
		mfi = new JMenuItem("Export Source File(s)");
		mfi.setActionCommand("export");
		mfi.addActionListener(this);
		mfi.setMnemonic(KeyEvent.VK_E);
		mfi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_DOWN_MASK));
		UMLGUI.setIcon(mfi, "export", null);
		menuFile.add(mfi);

		menuFile.addSeparator();

		// File > Close
		// this is "Exit" originally but sometimes it doesn't make sense to
		// exit in a sub window so I changed it to "Close"
		mfi = new JMenuItem("Close");
		mfi.setActionCommand("exit");
		mfi.addActionListener(this);
		UMLGUI.setIcon(mfi, "exit", null);
		menuFile.add(mfi);

		// menu New
		JMenu menuNew = new JMenu("New");
		menuNew.setActionCommand("new");
		menuNew.setMnemonic(KeyEvent.VK_N);
		UMLGUI.setIcon(menuNew, "new", null);
		menuBar.add(menuNew);

		// New > X
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

		// menu View
		JMenu menuView = new JMenu("View");
		menuNew.setActionCommand("view");
		menuNew.setMnemonic(KeyEvent.VK_V);
		UMLGUI.setIcon(menuView, "view", null);
		menuBar.add(menuView);

		JMenuItem mvi;

		// View > Tool-bar
		mvi = new JCheckBoxMenuItem("Tool-bar");
		mvi.setActionCommand("view.toolbar");
		mvi.addActionListener(this);
		mvi.setMnemonic(KeyEvent.VK_T);
		mvi.setSelected(true);
		menuView.add(mvi);

		// View > Outline
		mvi = new JCheckBoxMenuItem("Outline");
		mvi.setActionCommand("view.outline");
		mvi.addActionListener(this);
		mvi.setMnemonic(KeyEvent.VK_O);
		mvi.setSelected(true);
		menuView.add(mvi);

		// menu Help
		JMenu menuHelp = new JMenu("Help");
		menuHelp.setActionCommand("help");
		menuHelp.setMnemonic(KeyEvent.VK_H);
		UMLGUI.setIcon(menuHelp, "help", null);
		menuBar.add(menuHelp);

		JMenuItem mhi;

		// Help > About
		mhi = new JMenuItem("About");
		mhi.setActionCommand("about");
		mhi.addActionListener(this);
		mhi.setMnemonic(KeyEvent.VK_A);
		UMLGUI.setIcon(mhi, "about", null);
		menuHelp.add(mhi);
		// finished with the main menu

		// start building the tool bar
		toolBar = new JToolBar();

		toolBar_zoomIn = new JButton();
		toolBar_zoomIn.setActionCommand("zoom.in");
		toolBar_zoomIn.addActionListener(this);
		UMLGUI.setIcon(toolBar_zoomIn, "zoom_in", null);
		toolBar.add(toolBar_zoomIn);

		toolBar_zoomOut = new JButton();
		toolBar_zoomOut.setActionCommand("zoom.out");
		toolBar_zoomOut.addActionListener(this);
		UMLGUI.setIcon(toolBar_zoomOut, "zoom_out", null);
		toolBar.add(toolBar_zoomOut);

		// use the above declared array
		for (int i = 0; i < structures.length; i++) {
			toolBar.add(new JToolBar.Separator());
			JLabel label = new JLabel();
			label.setBorder(BorderFactory.createLineBorder(label
					.getForeground()));
			Icon icon = UMLGUI.getIcon("one_"
					+ structures[i].substring(0, 1).toLowerCase());
			if (icon != null) {
				label.setIcon(icon);
			}
			new UMLGUIDrager(label, "new." + structures[i].toLowerCase());
			toolBar.add(label);
		}

		// recycle bin
		toolBar.add(new JToolBar.Separator());
		toolBar.add(new UMLGUIRecycleBin(UMLGUI.getIcon("bin_empty"), UMLGUI
				.getIcon("bin_full"), this));

		toolBar.setOrientation(SwingConstants.HORIZONTAL);
		toolBar.setFloatable(false);
		// finished with the tool bar

		// start adding components to our frame
		// it's funny that this scope it much shorter than the above part
		setLayout(new BorderLayout());

		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.PAGE_START);

		// build the diagram
		diagram = new Diagram();
		diagram.addContainerListener(this);
		new UMLGUIMouseMaster(this, diagram, false);
		new UMLGUIDroper(diagram, this);
		diagram.addPropertyChangeListener(this);
		// build the outline
		outline = new Outline(diagram);
		new UMLGUIMouseMaster(this, outline, false);
		new UMLGUIDrager(outline);
		new UMLGUIDroper(outline, this);
		// build the split pane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outline
				.getScrollable(), diagram.getScrollable());
		splitPane.setDividerLocation(splitPaneLeftWidth);
		splitPane.setDividerSize(5);
		add(splitPane, BorderLayout.CENTER);
		// finished adding main components

		// get ready to display now...
		setSize(700, 400);
		setTitle("UML");

		try {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (SecurityException se) {
			// just ignore
			// there are cases when we can't do this simple close operation
			// ...in an Applet for example?
		}
	}

	public UMLGUI() {
		this(false);
	}

	protected boolean isAllowedAction(String action, boolean dialog) {
		boolean allowed = true;

		if (flag_readonly
				&& (action.contains("new") || action.contains("edit")
						|| action.contains("remove") || action
						.contains("clear"))) {
			// prevent all actions beginning with "new"
			allowed = false;
		}

		if (allowed) {
			return true;
		} else {
			if (dialog) {
				showError(getTitle(),
						"Sorry. This action is currently not available");
			}
			return false;
		}
	}

	/**
	 * Main handler for all actions.<br/>
	 * Supported actions (listed by action command):<br/>
	 * <ul>
	 * <li>clear: Confirm and clear the entire diagram</li>
	 * <li>draw: Call the {@link Diagram#draw()} method to re-draw immediately</li>
	 * <li>find: Display the quick find dialog using {@link #doFind(Structure)}</li>
	 * <li>related: Display related structures by calling
	 * {@link #doRelated(Structure, boolean)} after a find form</li>
	 * <li>load: Do the load procedure by calling {@link #doLoad(File[])}</li>
	 * <li>image: Do the save image procedure by calling {@link #doImage(File)}</li>
	 * <li>clipping: Do the clipping procedure by calling
	 * {@link #doClipping(File)}</li>
	 * <li>export: Do the export source procedure by calling
	 * {@link #doExport(Structure)}</li>
	 * <li>exit: Simply dispose the JFrame</li>
	 * <li>view.fullscreen: Go into fullscreen mode</li>
	 * <li>about: Display author information</li>
	 * <li>Tool bar actions
	 * <ul>
	 * <li>zoom.in/zoom.out: Using {@link Diagram#setSizeFactor(float)} to
	 * change the zooming level</li>
	 * </ul>
	 * </li>
	 * <li>info.x: Redirect to {@link #doStructureCommand(String, String)} with
	 * the real action command only (omit the "info." part) and the last
	 * selected structure</li>
	 * </ul>
	 * This method even catches all Security Exception if they are thrown out.
	 * This behavior is expected in limited policy environments (in an Applet?)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (UMLGUI.debugging) {
			System.err.println("actionPerformed: " + action);
		}

		// check for disabled actions
		if (!isAllowedAction(action, true)) {
			return;
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
			doFind(null);
		} else if (action.equals("related")) {
			// display related structures selecting form
			doRelated(null, true);
		} else if (action.equals("load")) {
			// start loading
			try {
				doLoad(null);
			} catch (SecurityException se) {
				showPolicyError(getTitle());
			}
		} else if (action.equals("image")) {
			// start saving image
			try {
				doImage(null);
			} catch (SecurityException se) {
				showPolicyError(getTitle());
			}
		} else if (action.equals("clipping")) {
			// start clipping
			try {
				doClipping(null);
			} catch (SecurityException se) {
				showPolicyError(getTitle());
			}
		} else if (action.equals("export")) {
			// start exporting
			try {
				doExport(null);
			} catch (SecurityException se) {
				showPolicyError(getTitle());
			}
		} else if (action.equals("exit")) {
			// close the form
			dispose();
		} else if (action.startsWith("new")) {
			// creating new structure
			String type = action.substring("new.".length());
			if (type.equals("class")) {
				// new class
				new ClassForm(this, diagram).setVisible(true);
			} else if (type.equals("interface")) {
				// new interface
				new InterfaceForm(this, diagram).setVisible(true);
			} else if (type.equals("property")) {
				// new property
				// must select a class/interface first
				Structure structure = ListForm.select(this, "New Property",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new PropertyForm(this, structure).setVisible(true);
				}
			} else if (type.equals("method")) {
				// new method
				// must select a class/interface first
				Structure structure = ListForm.select(this, "New Method",
						diagram.getStructures(), "Class/Interface");
				if (structure != null) {
					new MethodForm(this, structure).setVisible(true);
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
						new ArgumentForm(this, structure2).setVisible(true);
					}
				}
			}
		} else if (action.startsWith("view")) {
			JCheckBoxMenuItem mi = null;
			if (action.equals("view.toolbar")) {
				mi = (JCheckBoxMenuItem) e.getSource();
				toolBar.setVisible(mi.isSelected());
			} else if (action.equals("view.outline")) {
				mi = (JCheckBoxMenuItem) e.getSource();
				if (mi.isSelected()) {
					splitPane.setLeftComponent(outline);
					splitPane.setDividerLocation(splitPaneLeftWidth);
				} else {
					splitPane.setLeftComponent(null);
				}
			}
		} else if (action.equals("about")) {
			// display about form
			new AboutForm(this).setVisible(true);
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
			doStructureCommand(action.substring("info.".length()),
					lastStructure);
		}
	}

	/**
	 * Displays the quick find form and make sure the selected structure is
	 * visible using {@link Diagram#ensureStructureIsVisible(Structure)} and
	 * {@link Outline#ensureStructureIsVisible(Structure)}
	 * 
	 * @param title
	 *            the title for the window
	 * @param structure
	 *            a null value will trigger the quick form. Otherwise the action
	 *            will be execute automatically
	 */
	public void doFind(Structure structure) {
		String title = "Quick Find";
		if (structure == null) {
			structure = FindForm.find(this, title, diagram.getStructures());
		}
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
	 * @param structure
	 *            the root structure. Pass null to use a {@link FindForm}
	 * @param ask_for_mode
	 *            a boolean value specifies to ask for mode or not. If
	 *            <code>false</code> is passed, the mode is automatically set to
	 *            <code>true</code>
	 */
	public void doRelated(Structure structure, boolean ask_for_mode) {
		if (lastOpened != null && lastOpened.isVisible()) {
			lastOpened.requestFocus();
			return;
		}

		String title = "Related Structures";

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

		UMLGUI target_gui = new UMLGUI(true);
		lastOpened = target_gui;
		Diagram d = target_gui.diagram;

		d.setAutoDrawing(false);
		findRelated(structure, d, flag_display_multiplicity_related);
		d.setAutoDrawing(true);

		d.ensureStructureIsVisible(structure);

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
	protected void findRelated(Structure structure, Diagram target_diagram,
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
	 * @param path
	 *            an array of <code>File</code> to skip the chooser. Pass a null
	 *            array to follow the normal flow
	 * 
	 * @see Diagram#setAutoDrawing(boolean)
	 */
	public void doLoad(File[] paths) {
		String title = "Load Source File(s)";
		if (paths == null) {
			JFileChooser fc = new JFileChooser(".");
			fc.setDialogTitle(title);
			fc.setApproveButtonText("Load Source");
			fc
					.setApproveButtonToolTipText("Load source file(s) inside selected directory (and its' sub-directories)");
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setFileFilter(new JavaSourceFilter());
			fc.setMultiSelectionEnabled(true);

			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				paths = fc.getSelectedFiles();
			}
		}

		if (paths != null) {
			try {
				diagram.setAutoDrawing(false);
				Parser parser = new Parser(diagram);
				int parsed = 0;
				for (int i = 0; i < paths.length; i++) {
					// we support multiple files
					try {
						parsed += parser.parse(paths[i]);
					} catch (ParseException e) {
						if (UMLGUI.debugging) {
							e.printStackTrace();
						}
						showError(title, e.getMessage() + "\nOffset: "
								+ e.getErrorOffset());
					}
				}

				if (parsed > 0) {
					diagram.setAutoDrawing(true);
					showInfo(title, String.format("Loaded %d file(s)!", parsed));
				} else {
					showError(title, "Loaded 0 file!");
				}
			} catch (Exception e) {
				if (UMLGUI.debugging) {
					e.printStackTrace();
				}
				showError(title, e.getMessage());
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
	 * @param path
	 *            a <code>File</code> to skip the chooser. Pass a null
	 *            <code>File</code> to follow the normal flow
	 * 
	 * @see Diagram#saveImage(java.awt.image.ImageObserver)
	 * @see DiagramImageObserver
	 */
	public void doImage(File path) {
		String title = "Save as Image";

		if (path == null) {
			JFileChooser fc = new JFileChooser(".");
			fc.setDialogTitle(title);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new DiagramImageFilter());
			fc.setSelectedFile(new File("Diagram_" + UMLGUI.currentDateTime()));
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				path = fc.getSelectedFile();
			}
		}

		if (path != null) {
			diagram.saveImage(new DiagramImageObserver(path.getAbsolutePath()));
		}
	}

	/**
	 * Processes the clipping request.<br/>
	 * Simply makes use of the method of the diagram.
	 * 
	 * @param path
	 *            a <code>File</code> to skip the chooser. Pass a null
	 *            <code>File</code> to follow the normal flow
	 * 
	 * @see Diagram#startClipping(java.awt.image.ImageObserver)
	 * @see DiagramImageObserver
	 */
	public void doClipping(File path) {
		String title = "Clipping";

		if (path == null) {
			JFileChooser fc = new JFileChooser(".");
			fc.setDialogTitle(title);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new DiagramImageFilter());
			fc
					.setSelectedFile(new File("Clipping_"
							+ UMLGUI.currentDateTime()));
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				path = fc.getSelectedFile();
			}
		}

		if (path != null) {
			showInfo(title,
					"OK, select the area you want to take screen shot now...");
			diagram.startClipping(new DiagramImageObserver(path
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
	 * @param rootpath
	 *            a <code>File</code> to skip the chooser. Pass a null
	 *            <code>File</code> to follow the normal flow
	 */
	public void doExport(File rootpath) {
		String title = "Export Source File(s)";

		if (rootpath == null) {
			JFileChooser fc = new JFileChooser(".");
			fc.setDialogTitle(title);
			fc.setApproveButtonText("Export Here");
			fc
					.setApproveButtonToolTipText("Export source file(s) into selected directory");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				rootpath = fc.getSelectedFile();
			}
		}

		if (rootpath != null) {
			try {
				CodeGenerator cg = new CodeGenerator(rootpath.getAbsolutePath());
				Structure[] structures = diagram.getStructures();
				int files = 0;
				for (int i = 0; i < structures.length; i++) {
					if (cg.generate(structures[i])) {
						files++;
					}
				}

				showInfo(title, String.format(
						"Successfully exported %d file(s)", files));
			} catch (Exception e) {
				showError(title, e.getMessage());
			}
		}
	}

	/**
	 * Displays the {@link InfoForm} for the requested structure
	 * 
	 * @param structure
	 *            the requested structure. Null structure will be ignored
	 */
	public void doInfo(Structure structure) {
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
		outline.ensureStructureIsVisible(structure);

		lastOpened.setVisible(true);
	}

	protected void doPopup(MouseEvent e, Structure structure) {
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

	protected UMLGUICommand[] getStructureCommands(Structure structure,
			boolean info) {
		List<UMLGUICommand> list = new LinkedList<UMLGUICommand>();

		if (structure != null) {
			String structureName = structure.getStructureName();

			if (info) {
				list.add(new UMLGUICommand("Information: "
						+ structure.getName(), "info.show"));
			}

			if (structureName.equals("Class")
					|| structureName.equals("Interface")) {
				list.add(new UMLGUICommand("Related Structure", "info.related",
						"related"));
			}

			if (structureName.equals("Class")
					|| structureName.equals("Interface")) {
				list.add(new UMLGUICommand("New Class", "info.new.class"));
				if (structureName.equals("Interface")) {
					list.add(new UMLGUICommand("New Interface",
							"info.new.interface"));
				}
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
			} else if (structureName.equals("Method")) {
				list
						.add(new UMLGUICommand("New Argument",
								"info.new.argument"));
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

	/**
	 * Handles actions with a structure. Those actions are:
	 * <ul>
	 * <li>show: Display information about a structure using
	 * {@link #doInfo(Structure)}</li>
	 * <li>related: Display related structures using
	 * {@link #doRelated(Structure, boolean)}
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
	 * <li>remove: Call structure's {@link Structure#dispose()} to remove it
	 * completely</li>
	 * </ul>
	 * Some of the actions can work without a valid structure. Otherwise an
	 * error message will come up
	 * 
	 * @param action
	 *            the action command
	 * @param structure
	 *            the associated structure
	 */
	protected void doStructureCommand(String action, final Structure structure) {
		if (UMLGUI.debugging) {
			System.err.println("doStructureCommand: " + action + " with "
					+ structure);
		}

		if (!isAllowedAction(action, true)) {
			return;
		}

		if (action.equals("show")) {
			// display the information form
			if (lastOpened != null) {
				// force dispose the last form
				lastOpened.dispose();
			}
			if (structure != null) {
				doInfo(structure);
			} else {
				if (UMLGUI.debugging) {
					System.err.println("show action with null structure!");
				}
			}
		} else if (action.equals("related")) {
			// display the related structure form
			if (lastOpened != null) {
				// force dispose the last form
				lastOpened.dispose();
			}
			if (structure != null) {
				doRelated(structure, false);
			} else {
				if (UMLGUI.debugging) {
					System.err.println("related action with null structure!");
				}
			}
		} else if (action.startsWith("new")) {
			// create new structure
			String type = action.substring("new.".length());
			if (type.equals("class") || type.equals("interface")) {
				// new.class or new.interface
				StructureForm form;
				if (type.equals("class")) {
					form = new ClassForm(this, diagram);
				} else {
					form = new InterfaceForm(this, diagram);
				}
				// add the new class/interface to the associated
				// structure if there is
				if (structure != null) {
					form.addStructureListener(new StructureListener() {
						@Override
						public void structureChanged(StructureEvent e) {
							Structure changed = (Structure) e.getSource();
							try {
								structure.add(changed);
							} catch (StructureException se) {
								changed.dispose();
								showActionError(se.getMessage());
							}
						}
					});
				}
				form.setVisible(true);
			} else if (type.equals("property")) {
				// new.property
				Structure parent = null;
				if (structure == null) {
					parent = ListForm.select(this, "New Property", diagram
							.getStructures(), "Class/Interface");
				} else {
					parent = structure;
				}
				// display the creating form if we have enough data
				if (parent != null && parent.checkCanHaveChildren("Property")) {
					new PropertyForm(this, parent).setVisible(true);
				}
			} else if (type.equals("method")) {
				// new.method
				Structure parent = null;
				if (structure == null) {
					parent = ListForm.select(this, "New Method", diagram
							.getStructures(), "Class/Interface");
				} else {
					parent = structure;
				}
				// display the creating form if we have enough data
				if (parent != null && parent.checkCanHaveChildren("Method")) {
					new MethodForm(this, parent).setVisible(true);
				}
			} else if (type.equals("argument")) {
				// new argument (for this structure)
				Structure structure1 = null;
				Structure method = null;
				if (structure != null
						&& structure.checkCanHaveChildren("Method")) {
					structure1 = structure;
				}
				if (structure != null
						&& structure.checkCanHaveChildren("Argument")) {
					method = structure;
				}
				if (method == null) {
					if (structure1 != null) {
						// we may need to select method from the list
						method = ListForm.select(this, "New Argument",
								Structure.filterStructureArray(structure1
										.getChildren(),
										new String[] { "Method" }), "Method");
					}
				}
				if (method != null) {
					new ArgumentForm(this, method).setVisible(true);
				}
			}
		} else if (action.equals("remove")) {
			// remove the structure completely
			if (structure != null) {
				if (JOptionPane.showConfirmDialog(this,
						"Are you sure you want to remove "
								+ structure.getName()
								+ "?\nThis action can not be undone!",
						getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					structure.dispose();
				}
			} else {
				showActionError("Please specify a valid Structure!");
			}
			if (lastOpened != null) {
				lastOpened.dispose();
			}
		}
	}

	/**
	 * Display a policy error
	 * 
	 * @param title
	 *            the title for the message box
	 */
	protected void showPolicyError(String title) {
		showError(
				title,
				"Sorry, your system doesn't allow us to perform this action."
						+ "\nPlease try another version of the application to use all of the (amazing) features");
	}

	protected void showActionError(String message) {
		showError("Action Error", "Action can not be completed\n" + message);
	}

	protected void showError(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	protected void showInfo(String title, String message) {
		JOptionPane.showMessageDialog(this, message, title,
				JOptionPane.INFORMATION_MESSAGE);
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
			new UMLGUIMouseMaster(this, c, true);
		}

		if (c instanceof DiagramStructureGroup) {
			// make it drop-able
			new UMLGUIDroper(c, this);
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == diagram) {
			// the source is our diagram
			if (evt.getPropertyName().equals("size_factor")) {
				// listen to size_factor only. We should use the other version
				// of addPropertyChangeListener (which specifies a property
				// name). But I want a general solution. So here we are
				toolBar_zoomIn.setEnabled(diagram.checkCanZoomIn());
				toolBar_zoomOut.setEnabled(diagram.checkCanZoomOut());
			}
		}
	}

	/**
	 * Get the current date time in a easy to read format
	 * 
	 * @return the string of the current time
	 */
	static private String currentDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		return sdf.format(Calendar.getInstance().getTime());
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

	public static void setDebugging(boolean flag) {
		UMLGUI.debugging = flag;
		if (flag == true) {
			Structure.debugging = UMLGUI.debugging;
			Diagram.debugging = UMLGUI.debugging;
			Outline.debugging = UMLGUI.debugging;
			Parser.debugging = UMLGUI.debugging;
			LexicalAnalyzer.debugging = UMLGUI.debugging;
		}
	}
}

class UMLGUIMouseMaster implements MouseListener {
	private UMLGUI gui;
	private boolean double_for_info;
	/**
	 * The corresponding structure. This can be null sometimes
	 */
	private Structure structure;

	public UMLGUIMouseMaster(UMLGUI gui, Component component,
			boolean double_for_info) {
		this.gui = gui;
		this.double_for_info = double_for_info;

		if (component instanceof StructureBased) {
			structure = ((StructureBased) component).getStructure();
		} else {
			structure = null;
		}

		component.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && double_for_info) {
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
				structure = ((Outline) e.getSource()).getStructureForLocation(e
						.getPoint());
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
	 * The action to perform. Use with non-StructureBased component
	 */
	private String action;
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
		this(component, null);
	}

	public UMLGUIDrager(Component component, String action) {
		this.component = component;
		this.action = action;

		if (component instanceof StructureBased) {
			// this can be created any time with any component
			// but it only works with StructureBased components
			original_color = component.getForeground();

			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(component,
					DnDConstants.ACTION_COPY_OR_MOVE, this);
		} else {
			// this can also be created with an abstract button
			// with some action command
			original_color = component.getForeground();
			cfg_drag_color = original_color; // do not change color

			dragSource = new DragSource();
			dragSource.createDefaultDragGestureRecognizer(component,
					DnDConstants.ACTION_MOVE, this); // move only
		}
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		component.setForeground(cfg_drag_color);
		Structure structure = null;

		if (component instanceof StructureBased) {
			structure = ((StructureBased) component).getStructure();
		} else if (component instanceof Outline) {
			structure = ((Outline) component).getStructureForLocation(dge
					.getDragOrigin());
		}

		if (structure != null) {
			// send a structure
			dragSource.startDrag(dge, null,
					new TransferableStructure(structure), this);
		} else if (action != null) {
			// send an action command
			dragSource.startDrag(dge, null, new TransferableStructureCommand(
					action), this);
		}
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		if (component.getForeground() == cfg_drag_color) {
			component.setForeground(original_color);
		}

		if (UMLGUI.debugging) {
			System.err.println("DnD result: " + dsde.getDropSuccess());
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
	 * The component to get dropping enabled
	 */
	private Component component;
	/**
	 * The corresponding structure. This can be null sometimes
	 */
	private Structure structure;
	/**
	 * The primary active graphical user interface
	 */
	private UMLGUI gui;
	/**
	 * The original (foreground) color of the panel
	 */
	private Color original_color;
	/**
	 * The color when the panel has something dragging by
	 */
	private Color cfg_hover_color = Color.red;

	public UMLGUIDroper(Component component, UMLGUI gui) {
		this.component = component;
		this.gui = gui;
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
		boolean done = false;
		component.setForeground(original_color);

		Structure structure = this.structure;
		if (structure == null) {
			// try to specify a structure
			if (component instanceof Outline) {
				structure = ((Outline) component).getStructureForLocation(dtde
						.getLocation());
			}
		}

		try {
			if (dtde.isDataFlavorSupported(TransferableStructure.df)
					&& structure != null) {
				// a structure is being transfered
				// we should have an associated structure to make use the
				// transfered one
				Structure transfered = (Structure) dtde.getTransferable()
						.getTransferData(TransferableStructure.df);

				if (transfered.getContainer() != structure) {
					// only move/copy from a structure to another
					int action = dtde.getDropAction();

					switch (action) {
					case DnDConstants.ACTION_COPY:
						// copying structure
						Structure copied = transfered.copy();
						structure.add(copied);

						done = true;
						break;
					case DnDConstants.ACTION_MOVE:
						// moving structure
						if (transfered.getContainer() != null) {
							Structure container = transfered.getContainer();
							if (container.getStructureName().equals(
									structure.getStructureName())) {
								// only remove container with the same structure
								// name
								transfered.getContainer().remove(transfered);
							}
						}

						structure.add(transfered);

						done = true;
						break;
					}
				}
			} else if (dtde
					.isDataFlavorSupported(TransferableStructureCommand.df)
					&& dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
				// a structure command is being transfered
				// with this kind of transfer, we only accept moving
				String action = (String) dtde.getTransferable()
						.getTransferData(TransferableStructureCommand.df);

				if (structure == null) {
					// there is no structure in the drop zone
					// trigger the normal handler of the graphical user
					// interface object
					gui.actionPerformed(new ActionEvent(component, dtde
							.hashCode(), action));

					// we don't know the result of the above method
					// so it's best to notify this is a successful DnD
					done = true;
				} else if (!structure.getStructureName().equals("Class")
						&& !structure.getStructureName().equals("Interface")) {
					// doesn't support dropping to other structures than
					// class/interface, simply trigger a structure exception
					throw new StructureException("Unsupported action for "
							+ structure);
				} else {
					gui.doStructureCommand(action, structure);
				}

				done = true;
			}
		} catch (UnsupportedFlavorException e) {
			// ignore
			if (UMLGUI.debugging) {
				System.err.println("Ewwww. This is not well tasted!");
			}
		} catch (IOException e) {
			// ignore
		} catch (StructureException e) {
			gui.showActionError(e.getMessage());
		}

		if (done) {
			dtde.acceptDrop(dtde.getDropAction());
			dtde.dropComplete(true);
		} else {
			dtde.rejectDrop();
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
}

/**
 * A special drop zone: Recycle Bin. If a structure is dropped into this, it
 * will be disposed using {@link Structure#dispose()}
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class UMLGUIRecycleBin extends JLabel implements DropTargetListener {
	private static final long serialVersionUID = 7985251056148836727L;
	private Icon emptyIcon, fullIcon;
	private UMLGUI gui;

	public UMLGUIRecycleBin(Icon emptyIcon, Icon fullIcon, UMLGUI gui) {
		super();

		this.emptyIcon = emptyIcon;
		this.fullIcon = fullIcon;
		this.gui = gui;

		new DropTarget(this, this);

		if (emptyIcon != null) {
			setIcon(emptyIcon);
		} else {
			setText("Recycle Bin");
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		if (fullIcon != null) {
			setIcon(fullIcon);
		}
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		if (emptyIcon != null) {
			setIcon(emptyIcon);
		}
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		boolean done = false;

		if (emptyIcon != null) {
			setIcon(emptyIcon);
		}

		// check for allowed actions
		try {
			if (dtde.isDataFlavorSupported(TransferableStructure.df)
					&& dtde.getDropAction() == DnDConstants.ACTION_MOVE) {
				// recycle bin only accept structure transfer and moving
				// what it should do with a copying request????
				// rejecting, I guess!
				Structure transfered = (Structure) dtde.getTransferable()
						.getTransferData(TransferableStructure.df);

				// SIMPLEST JOB: dispose the structure
				gui.doStructureCommand("remove", transfered);
				if (transfered.getActive() == false) {
					// check to make sure the structure is gone
					done = true;
				}
			}
		} catch (UnsupportedFlavorException e) {
			// ignore
			if (UMLGUI.debugging) {
				System.err.println("Ewwww. This is not well tasted!");
			}
		} catch (IOException e) {
			// ignore
		}

		if (done) {
			dtde.acceptDrop(dtde.getDropAction());
			dtde.dropComplete(true);
		} else {
			dtde.rejectDrop();
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}
}

/**
 * A file filter which accepts .java files only
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class JavaSourceFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String path = f.getAbsolutePath();
		String ext = path.substring(path.length() - 5).toLowerCase();
		return ext.equals(".java");
	}

	@Override
	public String getDescription() {
		return "Java Source Files (.JAVA)";
	}

}