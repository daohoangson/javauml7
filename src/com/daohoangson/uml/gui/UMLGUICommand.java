package com.daohoangson.uml.gui;

import javax.swing.Icon;

public class UMLGUICommand {
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
