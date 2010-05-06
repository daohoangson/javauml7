import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JApplet;
import javax.swing.JButton;

import com.daohoangson.uml.gui.UMLGUI;

public class TestApplet extends JApplet {
	private static final long serialVersionUID = -6135902941375606145L;

	@Override
	public void init() {
		setSize(300, 300);

		JButton btn = new JButton("Click me!");
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new UMLGUI().setVisible(true);
			}
		});
		add(btn);

		// add(new JLabel("Click the \"Launch\" button!"));
	}
}
