package UI;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;

public class MainPanel extends JPanel {
	private JTable table;
	private JButton btnRemoveFile;
	private JButton btnAddFile;
	private JButton btnSplit;
	private DefaultTableModel mod;
	private JPanel panel;
	private JButton btnPlus;

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		setOpaque(false);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		btnSplit = new JButton("Split");
		springLayout.putConstraint(SpringLayout.SOUTH, btnSplit, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnSplit, 0, SpringLayout.EAST, this);
		add(btnSplit);
		
		JLabel lblQueue = new JLabel("Queue");
		springLayout.putConstraint(SpringLayout.NORTH, lblQueue, 5, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblQueue, 10, SpringLayout.WEST, this);
		add(lblQueue);
		lblQueue.setHorizontalAlignment(SwingConstants.LEFT);
		lblQueue.setFont(new Font("SF Pro Text", Font.BOLD, 16));
		
		JPanel splitQueuePanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, splitQueuePanel, 5, SpringLayout.SOUTH, lblQueue);
		springLayout.putConstraint(SpringLayout.WEST, splitQueuePanel, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, splitQueuePanel, -5, SpringLayout.NORTH, btnSplit);
		springLayout.putConstraint(SpringLayout.EAST, splitQueuePanel, -10, SpringLayout.EAST, this);
		add(splitQueuePanel);
		splitQueuePanel.setLayout(new BorderLayout(0, 0));
		
		
		btnAddFile = new JButton("Add file");
		springLayout.putConstraint(SpringLayout.WEST, btnAddFile, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnAddFile, 0, SpringLayout.SOUTH, this);
		add(btnAddFile);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(table);
		splitQueuePanel.add(scrollPane);
		splitQueuePanel.setOpaque(false);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -196, SpringLayout.NORTH, btnSplit);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);
		table.setBackground(Color.WHITE);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(44, 10));
		panel.setMinimumSize(new Dimension(44, 10));
		splitQueuePanel.add(panel, BorderLayout.EAST);
		
		btnPlus = new JButton("Add");
		panel.add(btnPlus);
		
		btnRemoveFile = new JButton("Del");
		panel.add(btnRemoveFile);
		btnRemoveFile.setEnabled(false);
		springLayout.putConstraint(SpringLayout.NORTH, btnRemoveFile, 0, SpringLayout.NORTH, btnSplit);
		springLayout.putConstraint(SpringLayout.WEST, btnRemoveFile, 0, SpringLayout.EAST, btnAddFile);
		
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
		btnPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File selectedFile = pickFile();
				System.out.println(selectedFile.getPath());
				mod.addRow(new Object[] { selectedFile.getPath(), "By size", "Waiting..."});
			}
		});
	}
	
	private void setupTable() {
		table.setFocusable(false);
		table.getTableHeader().setReorderingAllowed(false); 
		mod=new DefaultTableModel() {
			@Override 
		    public boolean isCellEditable(int row, int column)
		    {
		        return (column == 2);
		    }
			
			@Override
			public void removeRow(int row) {
				super.removeRow(row);
				if (table.getRowCount() == 0) {
					// Disable the Split button, duh
					btnSplit.setEnabled(false);
				}
				// TODO: CALL DATA REMOVAL HERE
			}
		};
		
		ListSelectionModel cellSelectionModel = table.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
		  public void valueChanged(ListSelectionEvent e) {
			  if (table.getSelectedRow() == -1) {
		    	  btnRemoveFile.setEnabled(false);
		    	  // TODO: FIX THIS SHIT													<------------------- FIX ME
		      } else {
		    	  btnRemoveFile.setEnabled(true);
//		    	  settingsPanel.add(new JLabel((String) table.getValueAt(table.getSelectedRow(), 0)));
		      }
		  }

		});
		
		table.setModel(mod);
		mod.addColumn("Filename");
		mod.addColumn("Action");
		mod.addColumn("Method");
		mod.addColumn("Status");
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(generateComboBox()));
		mod.addRow(new Object [] {"manjaro.iso", "Split","By Size", "Waiting..."});
		mod.addRow(new Object [] {"andre_gay_se_leggi.txt", "Split", "By Size", "Waiting..."});
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
	
	public File pickFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
}
