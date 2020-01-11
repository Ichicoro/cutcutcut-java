package ui;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import actors.*;
import utils.FileUtils;
import utils.Utils;

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
	private JButton btnRun;
	private DefaultTableModel mod;
	private JPanel panel;
	private JButton btnPlus;
	private JButton btnEdit;
	private JProgressBar progressBar;
	
	private boolean activeButtons = true;
	
	private volatile int amountOfCompletedActions = 0;
	
	private ArrayList<Action> actions;
	private boolean addAction(Action a) {
		if (a == null) return false;
		a.setStatus(Action.Status.WAITING);
		actions.add(a);
		System.out.println(a.getClass());
		String actionType = Utils.getActionTypeText(a);
		System.out.println(actionType);
		
		Object[] listEntry = {
			a.getFile().getPath(),
			(a instanceof FileSplitter) ? "Split" : "Merge",
			actionType,
			"Waiting..."
		};
		
		((DefaultTableModel) table.getModel()).addRow(listEntry);
		table.update(table.getGraphics());
		progressBar.setMaximum(table.getRowCount());
		progressBar.setValue(0);
		progressBar.paint(progressBar.getGraphics());
		return true;
	}
	
	private boolean replaceAction(Action a, int index) {
		if (a == null) return false;
		DefaultTableModel dtm = ((DefaultTableModel) table.getModel());
		if (index < 0 || index >= dtm.getRowCount()) 
			return false;
		
		actions.remove(index);
		actions.add(index, a);
		btnRun.setEnabled(true);
		dtm.setValueAt(Utils.getActionTypeText(a), index, 2);
		dtm.setValueAt("Waiting...", index, 3);
		
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
//		springLayout.putConstraint(SpringLayout.WEST, btnRemoveFile, 0, SpringLayout.EAST, btnAddFile);
		
		
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
				startQueue();
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
			  if (table.getSelectedRow() == -1 || !activeButtons) {
		    	  btnRemoveFile.setEnabled(false);
		    	  btnEdit.setEnabled(false);
			  } else {
		    	  btnRemoveFile.setEnabled(true);
		    	  btnEdit.setEnabled(false);
				  if (!(actions.get(table.getSelectedRow()) instanceof DefaultFileMerger) && actions.get(table.getSelectedRow()).getStatus() != Action.Status.FINISHED)
					  btnEdit.setEnabled(true);
		      }
		  }

		});
		
		table.setModel(mod);
		mod.addColumn("Filename");
		mod.addColumn("Action");
		mod.addColumn("Method");
		mod.addColumn("Status");
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
			switch (fileType) {
			case 'd':
				a = new DefaultFileMerger(selectedFile.getPath());
				break;
			case 'e':
				String key = JOptionPane.showInputDialog("What's the decryption key?");
				a = new EncryptedFileMerger(selectedFile.getPath(), key);
				break;
			case 'c':
			default:
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (a == null) return;
		addAction(a);
	}
	
	private void setButtonsEnabled(final boolean btnState) {
		btnPlus.setEnabled(btnState);
		table.setRowSelectionAllowed(btnState);
		this.paint(this.getGraphics());
		activeButtons = btnState;
	}
	
	private void startQueue() {
		amountOfCompletedActions = 0;
		
		for (int i=0; i<table.getRowCount(); i++) {
			if (actions.get(i).getStatus() == Action.Status.WAITING)
				((DefaultTableModel) table.getModel()).setValueAt("Processing...", i, table.getColumnCount()-1);
		}
		
		table.clearSelection();
		table.update(table.getGraphics());
		progressBar.setValue(0);
		progressBar.paint(progressBar.getGraphics());
		setButtonsEnabled(false);
		
		btnRun.setEnabled(false);
		btnRun.paint(btnRun.getGraphics());
		
		final int actionCount = table.getRowCount();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for (int i=0; i<actionCount; i++) {
			if (actions.get(i).getStatus() != Action.Status.WAITING) continue;
			final int actionIndex = i;
			threads.add(new Thread() {
				@Override
				public void run() {
					Action selectedAction = actions.get(actionIndex);
					
					int actionResult;
					try {
						if (selectedAction instanceof FileSplitter) {
							actionResult = ((FileSplitter) selectedAction).split();
						} else {
							actionResult = ((FileMerger) selectedAction).merge();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						actionResult = Action.Status.ERROR.ordinal();
					}
					
					String finalActionStatus = Utils.capitalizeString(selectedAction.getStatus().name());
					
					if (selectedAction.getStatus() == Action.Status.ERROR) {
						finalActionStatus += ": ";
						if (selectedAction instanceof FileSplitter) {
							finalActionStatus += FileSplitterByPartSize.SplitResult.values()[actionResult];
						} else if (selectedAction instanceof DefaultFileMerger) {
							finalActionStatus += DefaultFileMerger.MergeResult.values()[actionResult];
						} else if (selectedAction instanceof EncryptedFileMerger) {
							finalActionStatus += EncryptedFileMerger.MergeResult.values()[actionResult];
						}
					}
					
					((DefaultTableModel) table.getModel()).setValueAt(finalActionStatus, actionIndex, table.getColumnCount()-1);
					table.update(table.getGraphics());
					
					synchronized(this) {
						amountOfCompletedActions += 1;
					}
					progressBar.setValue(amountOfCompletedActions);
					progressBar.paint(progressBar.getGraphics());
					
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							
						}
					});
				}
			});
		}
		
		progressBar.setMaximum(threads.size());
		progressBar.repaint();
		
		for (Thread t : threads) {
			t.start();
		}
		
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		setButtonsEnabled(true);
	}
}
