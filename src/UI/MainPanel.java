package UI;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JToolBar;

public class MainPanel extends JPanel {
	private JTable table;

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		table = new JTable();
		springLayout.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, table, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, table, 448, SpringLayout.WEST, this);
		add(table);
		
		JButton btnSplit = new JButton("Split");
		springLayout.putConstraint(SpringLayout.SOUTH, table, 0, SpringLayout.NORTH, btnSplit);
		springLayout.putConstraint(SpringLayout.SOUTH, btnSplit, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnSplit, 0, SpringLayout.EAST, this);
		add(btnSplit);
		
		JButton btnAddFile = new JButton("Add file");
		springLayout.putConstraint(SpringLayout.WEST, btnAddFile, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnAddFile, 0, SpringLayout.SOUTH, this);
		add(btnAddFile);
		
		JButton btnRemoveFile = new JButton("Remove file");
		springLayout.putConstraint(SpringLayout.NORTH, btnRemoveFile, 0, SpringLayout.NORTH, btnSplit);
		springLayout.putConstraint(SpringLayout.WEST, btnRemoveFile, 64, SpringLayout.WEST, this);
		add(btnRemoveFile);
		
	}
}
