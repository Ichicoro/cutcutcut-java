package Main;

import java.awt.EventQueue;

import javax.swing.UIManager;

import UI.MainWindow;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("Cutcutcut v1.0");
		if (args.length == 0) {
			System.out.println("No args passed. Launching as GUI app.");
			try {
				// com.sun.java.swing.plaf.motif.MotifLookAndFeel
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				MainWindow frame = new MainWindow();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Else split the input file
		}
	}

}