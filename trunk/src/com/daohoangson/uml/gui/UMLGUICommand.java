package com.daohoangson.uml.gui;

import javax.swing.Icon;

/**
 * A action command
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class UMLGUICommand {
	/**
	 * Title of the action
	 */
	public String title;
	/**
	 * Action command
	 */
	public String action;
	/**
	 * Icon for user interface
	 */
	public Icon icon;

	/**
	 * Constructor
	 * 
	 * @param title
	 *            the title
	 * @param action
	 *            the action command
	 * @param icon_name
	 *            the icon name
	 */
	public UMLGUICommand(String title, String action, String icon_name) {
		this.title = title;
		this.action = action;
		icon = UMLGUI.getIcon(icon_name);
	}

	/**
	 * Constructor. This will get icon automatically based on the action command
	 * 
	 * @param title
	 *            the title
	 * @param action
	 *            the action command
	 */
	public UMLGUICommand(String title, String action) {
		this(title, action, action.replace('.', '_'));
	}
}
