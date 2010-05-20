package com.nguyenthanhan.uml.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;

/**
 * Parent for all other forms
 * 
 * @author Nguyen Thanh An
 * @version 1.0
 */
public abstract class ConvenientForm extends JDialog implements
		ContainerListener, KeyListener {
	private static final long serialVersionUID = -1726732388115683951L;

	public ConvenientForm(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setResizable(false);

		addListener(this);
	}

	protected abstract void submit();

	private void addListener(Component c) {
		c.addKeyListener(this);
		if (c instanceof Container) {
			Container container = (Container) c;
			container.addContainerListener(this);
			Component[] children = container.getComponents();
			for (int i = 0; i < children.length; i++) {
				addListener(children[i]);
			}
		}
	}

	private void removeListener(Component c) {
		c.removeKeyListener(this);
		if (c instanceof Container) {
			Container container = (Container) c;
			container.removeContainerListener(this);
			Component[] children = container.getComponents();
			for (int i = 0; i < children.length; i++) {
				removeListener(children[i]);
			}
		}
	}

	@Override
	public void componentAdded(ContainerEvent e) {
		addListener(e.getChild());
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		removeListener(e.getChild());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int kc = e.getKeyCode();
		if (kc == KeyEvent.VK_ESCAPE) {
			dispose();
		} else if (kc == KeyEvent.VK_ENTER) {
			submit();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
