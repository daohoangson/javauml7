package com.nguyenthanhan.uml.gui;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
/**
 * @(#)AboutForm.java
 * 		Introduction team members 
 * 		& information of source code
 * @author Nguyen Thanh An
 * @version 1.0
 */
public class AboutForm extends ConvenientForm implements HyperlinkListener {
	private static final long serialVersionUID = 7722581420480179614L;

	public AboutForm(Window owner) {
		super(owner, "About", ModalityType.APPLICATION_MODAL);

		// String uri = "http://code.google.com/p/javauml7/";
		String uri = "http://www.google.com/search?q=it's+not+that+easy,+man!";
		String html = "<html><h1>UML Assignment Application</h1>"
				+ "<em>Built by Team 7, K53CC, UET, VNU</em><br/>"
				+ "Team Members:<br/><ol>"
				+ "<li>Nguyen Thanh An</li>"
				+ "<li>Nguyen Binh Dieu</li>"
				+ "<li>Ta Van Duc</li>"
				+ "<li>Dao Hoang Son</li>"
				+ "<li>Tran Viet Son</li>"
				+ "</ol>"
				+ "More information (including source code) is available <a href=\""
				+ uri + "\">here</a>" + "</html>";

		Box b = Box.createVerticalBox();

		JEditorPane ep = new JEditorPane("text/html", html);
		ep.setEditable(false);
		ep.addHyperlinkListener(this);
		ep.setBackground(getBackground());
		b.add(ep);

		// TODO: Create the button "OK" is center aligned
		JButton btn = new JButton("OK");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submit();
			}
		});
		btn.setAlignmentX(0.5f);
		b.add(btn);

		int w = 10;
		b.setBorder(BorderFactory.createEmptyBorder(w, w, w, w));
		add(b);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	protected void submit() {
		dispose();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() != EventType.ACTIVATED) {
			return;
		}

		URI uri;
		try {
			uri = e.getURL().toURI();
		} catch (URISyntaxException e1) {
			return;
		}

		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Desktop.Action.BROWSE)) {
				try {
					d.browse(uri);
					return;
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		JOptionPane
				.showMessageDialog(
						getOwner(),
						"Sorry, your systen doesn't support opening URI\nPlease visit the address manually: "
								+ uri, "Lack of Browsing Support",
						JOptionPane.ERROR_MESSAGE);
	}

}
