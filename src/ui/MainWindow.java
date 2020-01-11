package ui;

import java.awt.Dimension;
import javax.swing.JFrame;

public class MainWindow extends JFrame {

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("Cutcutcut");
		setContentPane(new MainPanel());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setMinimumSize(new Dimension(512, 430));
	}
}
