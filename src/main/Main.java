package main;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bulenkov.darcula.DarculaLookAndFeelInfo;

import ui.MainWindow;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("Cutcutcut v1.0");
		if (args.length == 0) {
			System.out.println("No args passed. Launching as GUI app.");
			SwingUtilities.invokeLater ( new Runnable () {
	            @Override
	            public void run () {
	            	try {
	            		if (true || !System.getProperty("os.name").equals("Mac"))
	            			UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
					} catch (Exception e) {
						e.printStackTrace();
					}
	            	if (System.getProperty("os.name").equals("Mac"))
	            		setUIFont(new javax.swing.plaf.FontUIResource("SF Pro Text",Font.PLAIN,12));
	            	MainWindow frame = new MainWindow();
					frame.setVisible(true);
	            }
	        });
		} else {
			// Else split the input file
		}
	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put (key, f);
	      }
    } 

}