package main;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bulenkov.darcula.DarculaLookAndFeelInfo;

import actors.*;
import ui.MainWindow;
import utils.FileUtils;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("--- Cutcutcut v1.0 ---");
		if (args.length == 0) {
			System.out.println("No args passed. Launching as GUI app.");
			SwingUtilities.invokeLater ( new Runnable () {
	            @Override
	            public void run () {
	            	UIManager.getFont("Label.font"); // This fixes the Darcula theme on Linux -- WTF??
	            	System.out.println(System.getProperty("os.name"));
	            	if (System.getProperty("os.name").contains("Mac"))
	            		setUIFont(new javax.swing.plaf.FontUIResource("SF Pro Text",Font.PLAIN,12));
	            	try {
//	            		if (true || !System.getProperty("os.name").contains("Mac"))
	            		UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
					} catch (Exception e) {
						e.printStackTrace();
					}
	            	MainWindow frame = new MainWindow();
					frame.setVisible(true);
	            }
	        });
		} else {
			// LAUNCH THE CLI
			List<String> argList = Arrays.asList(args);
			
			if (argList.contains("-h") || argList.contains("--help")) {
				System.out.println("java -jar Cutcutcut.jar [--split [--size <f> [-e <key>]] OR [--amount <i>]] OR [--merge [-e <key>]] <filename>");
				System.exit(0);
			}
			
			String filePath = argList.get(argList.size()-1);
			File selectedFile = new File(filePath).getAbsoluteFile();
			if (!selectedFile.isFile()) {
				printErrorAndExit("Invalid file specified. Exiting!");
			} else {
				System.out.println("Selected file: " + filePath);
//				selectedFile = selectedFile;
			}
			
			if (argList.contains("--split")) {
				// Locate the type of split
				int switchPos;
				
				if ((switchPos = argList.indexOf("--size")) != -1) {
					// We're splitting by size! What's the part size tho?
					int partSize = 0;
					try {
						partSize = Integer.parseInt(argList.get(switchPos+1));
						if (partSize <= 0) {
							throw new NumberFormatException();
						}
					} catch (NumberFormatException e) {
						printErrorAndExit("Invalid split size. Exiting!");
					}
					
					// Are we also encrypting?
					String encKey = null;
					int eSwitchPosition = argList.indexOf("-e");
					if (eSwitchPosition != -1) {
						if (eSwitchPosition+2 != argList.size())
							encKey = argList.get(eSwitchPosition+1);
						else
							printErrorAndExit("Invalid key. Exiting!");
					}
					
					FileSplitter splitter = null;
					try {
						splitter = (encKey == null) ? new FileSplitterByPartSize(selectedFile, partSize)
													: new FileSplitterWithEncryption(selectedFile, encKey, partSize);
					} catch (InvalidKeyException e) {
						printErrorAndExit("Invalid key. Exiting!");
					} catch (FileNotFoundException e) {
						printErrorAndExit("File not found. Exiting!");
					}
					
					int splitResult = splitter.split();
					if (splitResult != FileSplitterWithEncryption.SplitResult.OK.ordinal()) {
						printErrorAndExit("Error: " + FileSplitterWithEncryption.SplitResult.values()[splitResult] + ". Exiting!");
					} else {
						System.out.println("Finished!");
						System.exit(0);
					}
				} else if ((switchPos = argList.indexOf("--amount")) != -1) {
					// We're splitting by amount! What's the amount of parts tho?
					int partAmount = 0;
					try {
						partAmount = Integer.parseInt(argList.get(switchPos+1));
						if (partAmount <= 0) {
							throw new NumberFormatException();
						}
					} catch (NumberFormatException e) {
						printErrorAndExit("Invalid split amount. Exiting!");
					}
					
					FileSplitter splitter = null;
					try {
						splitter = new FileSplitterByPartCount(selectedFile, partAmount);
					} catch (FileNotFoundException e) {
						printErrorAndExit("File not found. Exiting!");
					}
					
					int splitResult = splitter.split();
					if (splitResult != FileSplitterWithEncryption.SplitResult.OK.ordinal()) {
						printErrorAndExit("Error: " + FileSplitterWithEncryption.SplitResult.values()[splitResult] + ". Exiting!");
					} else {
						System.out.println("Finished!");
						System.exit(0);
					}
				}
				
				printErrorAndExit("Split method not specified (--size OR --amount). Exiting!");
			} else if (argList.contains("--merge")) {
				// Check the type of file
				char fileType = FileUtils.getMergeFileType(filePath);
				if (fileType == '0') {
					printErrorAndExit("This file can't be merged. Please select a file with the .dpart001 or .epart001 extension. Exiting!");
				}
				
				String encKey = null;
				if (fileType == 'e') {
					int eSwitchPosition = argList.indexOf("-e");
					if (eSwitchPosition == -1 || eSwitchPosition+2==argList.size()) {
						printErrorAndExit("You need to provide an -e <key>. Exiting!");
					}
					encKey = argList.get(eSwitchPosition+1);
				}
				// We can merge! :D
				FileMerger merger = null;
				try {
					merger = (encKey == null) ? new DefaultFileMerger(selectedFile) : new EncryptedFileMerger(selectedFile, encKey);
				} catch (InvalidKeyException e) {
					printErrorAndExit("Invalid key. Exiting!");
				} catch (FileNotFoundException e) {
					printErrorAndExit("File not found. Exiting!");
				}
				
				int mergeResult = merger.merge();
				if (mergeResult != EncryptedFileMerger.MergeResult.OK.ordinal()) {
					printErrorAndExit("Error: " + EncryptedFileMerger.MergeResult.values()[mergeResult] + ". Exiting!");
				} else {
					System.out.println("Finished!");
					System.exit(0);
				}
			} else {
				printErrorAndExit("No mode specified (--split/--merge). Exiting!");
			}
		}
	}
	
	
	// Pretty self explanatory
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put (key, f);
	      }
    } 
	
	public static void printErrorAndExit(String error) {
		System.err.println((char)27 + "[31;5m" + error);
		System.exit(1);
	}

}