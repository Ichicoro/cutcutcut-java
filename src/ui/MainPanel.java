package ui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import actors.*;
import utils.FileUtils;
import utils.Progress;
import utils.Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Point;
import java.awt.TrayIcon.MessageType;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import javax.swing.JProgressBar;
import javax.swing.ImageIcon;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;

public class MainPanel extends JPanel {
	private JTable table;
	private JButton btnRemoveFile;
	private JButton btnAddFile;
	private JButton btnRun;
	private DefaultTableModel mod;
	private JPanel panel;
	private JButton btnPlus;
	private JButton btnEdit;
	private JProgressBar progressBar;
	
	private ArrayList<Action> actions;
	private boolean addAction(Action a) {
		if (a == null) return false;
		actions.add(a);
		
		Object[] listEntry = {
			a.getFile().getPath(),
			(a instanceof FileSplitter) ? "Split" : "Merge",
			Utils.getActionTypeText(a),
			"Waiting..."
		};
		
		((DefaultTableModel) table.getModel()).addRow(listEntry);
		return true;
	}
	
	private boolean replaceAction(Action a, int index) {
		if (a == null) return false;
		DefaultTableModel dtm = ((DefaultTableModel) table.getModel());
		if (index < 0 || index >= dtm.getRowCount()) 
			return false;
		
		actions.remove(index);
		actions.add(index, a);
		
		return true;
	}

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		DefaultFileMerger dfm;
		try {
			dfm = new DefaultFileMerger(new File("/Users/ichicoro/Desktop/test_split/debian-10.2.0-amd64-netinst.iso.dpart001"));
			dfm.getFiles();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		actions = new ArrayList<Action>();
		
		setOpaque(false);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		btnRun = new JButton("Run");
		springLayout.putConstraint(SpringLayout.SOUTH, btnRun, -5, SpringLayout.SOUTH, this);
		add(btnRun);
		
		JLabel lblQueue = new JLabel("Queue");
		springLayout.putConstraint(SpringLayout.NORTH, lblQueue, 5, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblQueue, 10, SpringLayout.WEST, this);
		add(lblQueue);
		lblQueue.setHorizontalAlignment(SwingConstants.LEFT);
		lblQueue.setFont(lblQueue.getFont().deriveFont(16f).deriveFont(Font.BOLD));
		
		JPanel splitQueuePanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, splitQueuePanel, 5, SpringLayout.SOUTH, lblQueue);
		springLayout.putConstraint(SpringLayout.WEST, splitQueuePanel, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, splitQueuePanel, -5, SpringLayout.NORTH, btnRun);
		springLayout.putConstraint(SpringLayout.EAST, splitQueuePanel, -10, SpringLayout.EAST, this);
		add(splitQueuePanel);
		splitQueuePanel.setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(table);
		splitQueuePanel.add(scrollPane);
		splitQueuePanel.setOpaque(false);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -196, SpringLayout.NORTH, btnRun);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setMaximumSize(new Dimension(32, 32767));
		panel.setPreferredSize(new Dimension(32, 10));
		panel.setMinimumSize(new Dimension(32, 10));
		splitQueuePanel.add(panel, BorderLayout.EAST);
		
		btnPlus = new JButton();
		btnPlus.setMnemonic(KeyEvent.VK_A);
		btnPlus.setBorder(null);
//		btnPlus.setFont(btnPlus.getFont().deriveFont(40f));
		btnPlus.setIcon(new ImageIcon(MainPanel.class.getResource("/resources/plus.png")));
//		btnPlus.setBorderPainted(false);
//		btnPlus.setFocusPainted(false);
//		btnPlus.setContentAreaFilled(false);
		panel.add(btnPlus);
		
		
		btnRemoveFile = new JButton();
		btnRemoveFile.setFont(btnRemoveFile.getFont().deriveFont(32f));
		btnRemoveFile.setIcon(new ImageIcon(MainPanel.class.getResource("/resources/minus.png")));
//		btnRemoveFile.setBorderPainted(false);
		btnRemoveFile.setBorder(null);
//		btnRemoveFile.setFocusPainted(false);
//		btnRemoveFile.setContentAreaFilled(false);
		btnRemoveFile.setMnemonic(KeyEvent.VK_BACK_SPACE);
		panel.add(btnRemoveFile);
		btnRemoveFile.setEnabled(false);
		springLayout.putConstraint(SpringLayout.NORTH, btnRemoveFile, 0, SpringLayout.NORTH, btnRun);
		springLayout.putConstraint(SpringLayout.WEST, btnRemoveFile, 0, SpringLayout.EAST, btnAddFile);
		
		
		btnEdit = new JButton();
		btnEdit.setEnabled(false);
		btnEdit.setMnemonic(KeyEvent.VK_M);
		btnEdit.setBorder(null);
		btnEdit.setIcon(new ImageIcon(MainPanel.class.getResource("/resources/pencil.png")));
		panel.add(btnEdit);
		
		
		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.NORTH, btnRun);
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, btnRun);
		springLayout.putConstraint(SpringLayout.WEST, btnRun, 6, SpringLayout.EAST, progressBar);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -64, SpringLayout.EAST, this);
		add(progressBar);
		
		setupTable();
		setupListeners();
	}
	
	private void setupListeners() {
		btnRemoveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() == -1) return;
				((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
			}
		});
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem splitMenuItem = new JMenuItem("Split...");
	    popupMenu.add(splitMenuItem);
	    splitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pickSplittableFile();
				btnRun.setEnabled(true);
			}
		});
	    JMenuItem mergeMenuItem = new JMenuItem("Merge...");
	    popupMenu.add(mergeMenuItem);
	    mergeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pickMergeableFile();
				btnRun.setEnabled(true);
			}
		});
	    
		btnPlus.setComponentPopupMenu(popupMenu);
		btnPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        Component b=(Component)e.getSource();
		        Point p=b.getLocationOnScreen();
		        popupMenu.setLocation(p.x,p.y+b.getHeight());
		        popupMenu.show(b,0,b.getHeight());
				
			}
		});
		
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Action sel = actions.get(table.getSelectedRow());
				Action a = null;
				// String key = JOptionPane.showInputDialog("What's the decryption key?");
				// a = new EncryptedFileMerger(selectedFile.getPath(), key);
				if (sel instanceof FileSplitter) {
					SplitActionDialog sad = new SplitActionDialog(sel);
					a = sad.showDialog();
					System.out.println(a);
					if (a == null)
						return;
					else
						replaceAction(a, table.getSelectedRow());
				} else {
					if (sel instanceof EncryptedFileMerger) {
						String key = JOptionPane.showInputDialog("What's the decryption key?", ((EncryptedFileMerger)sel).getKey());
						try {
							a = new EncryptedFileMerger(sel.getFile(), key);
						} catch (InvalidKeyException | FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (a == null)
					return;
				else
					replaceAction(a, table.getSelectedRow());
			}
		});
		
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() == -1) return;
				Action selectedAction = actions.get(table.getSelectedRow());
				
				try {
					if (selectedAction instanceof FileSplitter)
						((FileSplitter) selectedAction).split();
					else
						if (EncryptedFileMerger.MergeResult.values()[((FileMerger) selectedAction).merge()] == EncryptedFileMerger.MergeResult.DECRYPTION_ERROR)
							JOptionPane.showMessageDialog(null, "Wrong password", "Error!", JOptionPane.ERROR_MESSAGE, null);
						//System.out.println(EncryptedFileMerger.MergeResult.values()[((FileMerger) selectedAction).merge()]);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	private void setupTable() {
		table.setFocusable(false);
		table.getTableHeader().setReorderingAllowed(false); 
		mod=new DefaultTableModel() {
			@Override 
		    public boolean isCellEditable(int row, int column) { return false; }
			
			@Override
			public void removeRow(int row) {
				super.removeRow(row);
				actions.remove(row);
				if (table.getRowCount() == 0)
					btnRun.setEnabled(false);
				else
					table.addRowSelectionInterval(table.getRowCount()-1, table.getRowCount()-1);
			}
		};
		
		ListSelectionModel cellSelectionModel = table.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
		  public void valueChanged(ListSelectionEvent e) {
			  if (table.getSelectedRow() == -1) {
		    	  btnRemoveFile.setEnabled(false);
		    	  btnEdit.setEnabled(false);
		    	  // TODO: FIX THIS SHIT													<------------------- FIX ME
			  } else {
		    	  btnRemoveFile.setEnabled(true);
		    	  btnEdit.setEnabled(false);
				  if (!(actions.get(table.getSelectedRow()) instanceof DefaultFileMerger))
					  btnEdit.setEnabled(true);
//		    	  settingsPanel.add(new JLabel((String) table.getValueAt(table.getSelectedRow(), 0)));
		      }
		  }

		});
		
		table.setModel(mod);
		mod.addColumn("Filename");
		mod.addColumn("Action");
		mod.addColumn("Method");
		mod.addColumn("Status");
		//table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(generateComboBox()));
	}
	
	private JComboBox generateComboBox() {
		JComboBox jc = new JComboBox();
		jc.addItem(makeObj("By Size"));
		jc.addItem(makeObj("Encrypted"));
		jc.addItem(makeObj("Compressed"));
		jc.addItem(makeObj("By Amount"));
		jc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e);
				System.out.println(jc.getSelectedIndex());
			}
		});
		System.out.println(jc.getSelectedIndex());
		return jc;
	}
	
	private Object makeObj(final String item)  {
        return new Object() { public String toString() { return item; } };
    }
	
	public File pickFile() { return pickFile("Pick a file..."); }
	public File pickFile(String dialogTitle) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		    return selectedFile;
		} else {
			return null;
		}
	}
	
	private void pickSplittableFile() {
		File selectedFile = pickFile("Select to-be-split file...");
		if (selectedFile == null || !selectedFile.exists() || !selectedFile.canRead()) {
			JOptionPane.showMessageDialog(this, "Invalid file selected", "Error!", JOptionPane.ERROR_MESSAGE, null);
			return;
		}
		SplitActionDialog sad = new SplitActionDialog(selectedFile);
		Action a = sad.showDialog();
		if (a == null) return;
		addAction(a);
	}
	
	private void pickMergeableFile() {
		File selectedFile = pickFile("Select to-be-merged file...");
		if (selectedFile == null || !selectedFile.exists() || !selectedFile.canRead() || !FileUtils.verifyMergeFilename(selectedFile.getName())) {
			System.out.println(selectedFile.getName());
			JOptionPane.showMessageDialog(this, "Invalid file selected. File has to end with .dpart001, .epart001 or .cpart001", "Error!", JOptionPane.ERROR_MESSAGE, null);
			return;
		}
		char fileType = FileUtils.getMergeFileType(selectedFile.getName());
		if (fileType == '0') {
			return;
		}
		Action a = null;
		try {
			switch (FileUtils.getMergeFileType(selectedFile.getName())) {
			case 'd':
				a = new DefaultFileMerger(selectedFile.getPath());
			case 'e':
				String key = JOptionPane.showInputDialog("What's the decryption key?");
				a = new EncryptedFileMerger(selectedFile.getPath(), key);
			case 'c':
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (a == null) return;
		addAction(a);
	}
}
