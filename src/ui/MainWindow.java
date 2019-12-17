package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import actors.DefaultFileMerger;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;

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
