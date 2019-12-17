package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import actors.*;

import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class SplitActionDialog extends JDialog {
	
	private File f;
	Action resultAction;

	private final JPanel contentPanel = new JPanel();
	private JFormattedTextField standardSplitSizeTextField;
	private JFormattedTextField splitAmountTextField;
	private JTabbedPane tabbedPane;
	
	private JTextField passwordTextField;
	private JFormattedTextField encryptedSplitSizeTextField;
	/**
	 * Create the dialog.
	 */
	
	public SplitActionDialog(File f) {
		this();
		this.f = f;
	}
	
	public SplitActionDialog(Action a) {
		this(a.getFile());
		if (a instanceof FileSplitter) {
			if (a instanceof FileSplitterByPartSize) {
				standardSplitSizeTextField.setText(""+((FileSplitterByPartSize) a).getPartSize());
			} else if (a instanceof FileSplitterByPartCount) {
				tabbedPane.setSelectedIndex(2);
				splitAmountTextField.setText(""+((FileSplitterByPartCount) a).getPartCount());
			} else if (a instanceof FileSplitterWithEncryption) {
				tabbedPane.setSelectedIndex(1);
				encryptedSplitSizeTextField.setText(""+((FileSplitterWithEncryption) a).getPartSize());
				passwordTextField.setText(""+((FileSplitterWithEncryption) a).getKey());
			}
		} else {
			// RIP
		}
	}
	
	//@wbp.parser.constructor 
	public SplitActionDialog() {
		setTitle("Split");
		this.setModalityType(DEFAULT_MODALITY_TYPE); 
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setBorder(null);
			contentPanel.add(tabbedPane);
			{
				JPanel splitSizePanel = new JPanel();
				tabbedPane.addTab("Split by size", null, splitSizePanel, null);
				
				// Create formatter for the textfield
			    NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
			    formatter.setValueClass(Integer.class);
			    formatter.setMinimum(0);
			    formatter.setMaximum(Integer.MAX_VALUE);
			    formatter.setAllowsInvalid(false);
			    // If you want the value to be committed on each keystroke instead of focus lost
			    formatter.setCommitsOnValidEdit(true);
				GridBagLayout gbl_splitSizePanel = new GridBagLayout();
				gbl_splitSizePanel.columnWidths = new int[] {116};
				gbl_splitSizePanel.rowHeights = new int[] {34};
				gbl_splitSizePanel.columnWeights = new double[]{1.0, 0.0};
				gbl_splitSizePanel.rowWeights = new double[]{0.0};
				splitSizePanel.setLayout(gbl_splitSizePanel);
				
				JPanel panel_2 = new JPanel();
				GridBagConstraints gbc_panel_2 = new GridBagConstraints();
				gbc_panel_2.anchor = GridBagConstraints.NORTH;
				gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel_2.gridwidth = 2;
				gbc_panel_2.gridx = 0;
				gbc_panel_2.gridy = 0;
				splitSizePanel.add(panel_2, gbc_panel_2);
				
				JLabel lblSize = new JLabel("Split size");
				panel_2.add(lblSize);
				lblSize.setHorizontalAlignment(SwingConstants.CENTER);
				
				
				standardSplitSizeTextField = new JFormattedTextField(formatter);
				panel_2.add(standardSplitSizeTextField);
				standardSplitSizeTextField.setHorizontalAlignment(SwingConstants.CENTER);
				standardSplitSizeTextField.setText("1000000");
				standardSplitSizeTextField.setColumns(10);
			}
			{
				JPanel encryptedSplitSizePanel = new JPanel();
				tabbedPane.addTab("Encrypt", null, encryptedSplitSizePanel, null);
				GridBagLayout gbl_encryptedSplitSizePanel = new GridBagLayout();
				gbl_encryptedSplitSizePanel.columnWidths = new int[] {318};
				gbl_encryptedSplitSizePanel.rowHeights = new int[] {34, 30};
				gbl_encryptedSplitSizePanel.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_encryptedSplitSizePanel.rowWeights = new double[]{0.0, 0.0};
				encryptedSplitSizePanel.setLayout(gbl_encryptedSplitSizePanel);
				{
					
					NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
				    formatter.setValueClass(Integer.class);
				    formatter.setMinimum(0);
				    formatter.setMaximum(Integer.MAX_VALUE);
				    formatter.setAllowsInvalid(false);
				    formatter.setCommitsOnValidEdit(true);
				}
				
				NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
			    formatter.setValueClass(Integer.class);
			    formatter.setMinimum(0);
			    formatter.setMaximum(Integer.MAX_VALUE);
			    formatter.setAllowsInvalid(false);
			    formatter.setCommitsOnValidEdit(true);
				
				JPanel panel_1 = new JPanel();
				GridBagConstraints gbc_panel_1 = new GridBagConstraints();
				gbc_panel_1.gridwidth = 5;
				gbc_panel_1.fill = GridBagConstraints.BOTH;
				gbc_panel_1.insets = new Insets(0, 0, 5, 0);
				gbc_panel_1.gridx = 0;
				gbc_panel_1.gridy = 0;
				encryptedSplitSizePanel.add(panel_1, gbc_panel_1);
				JLabel lblEncSize = new JLabel("Split size");
				panel_1.add(lblEncSize);
				lblEncSize.setHorizontalAlignment(SwingConstants.CENTER);
				
			    encryptedSplitSizeTextField = new JFormattedTextField(formatter);
			    panel_1.add(encryptedSplitSizeTextField);
			    encryptedSplitSizeTextField.setHorizontalAlignment(SwingConstants.CENTER);
			    encryptedSplitSizeTextField.setText("1000000");
			    encryptedSplitSizeTextField.setColumns(10);
				
				JPanel panel = new JPanel();
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel.anchor = GridBagConstraints.NORTH;
				gbc_panel.gridwidth = 5;
				gbc_panel.insets = new Insets(0, 0, 5, 0);
				gbc_panel.gridx = 0;
				gbc_panel.gridy = 1;
				encryptedSplitSizePanel.add(panel, gbc_panel);
				
				JLabel lblNewLabel = new JLabel("Password");
				panel.add(lblNewLabel);
				
				passwordTextField = new JTextField();
				passwordTextField.setText("password");
				passwordTextField.setColumns(10);
				panel.add(passwordTextField);
				passwordTextField.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				JPanel splitAmountPanel = new JPanel();
				tabbedPane.addTab("Split by amount", null, splitAmountPanel, null);
				GridBagLayout gbl_splitAmountPanel = new GridBagLayout();
				gbl_splitAmountPanel.columnWidths = new int[] {0};
				gbl_splitAmountPanel.rowHeights = new int[] {34};
				gbl_splitAmountPanel.columnWeights = new double[]{1.0};
				gbl_splitAmountPanel.rowWeights = new double[]{0.0};
				splitAmountPanel.setLayout(gbl_splitAmountPanel);
				    
				    
			    NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
			    formatter.setValueClass(Integer.class);
			    formatter.setMinimum(1);
			    formatter.setMaximum(Integer.MAX_VALUE);
			    formatter.setAllowsInvalid(false);
			    formatter.setCommitsOnValidEdit(true);
		        
		        JPanel panel = new JPanel();
		        GridBagConstraints gbc_panel = new GridBagConstraints();
		        gbc_panel.insets = new Insets(0, 0, 0, 5);
		        gbc_panel.fill = GridBagConstraints.BOTH;
		        gbc_panel.gridx = 0;
		        gbc_panel.gridy = 0;
		        splitAmountPanel.add(panel, gbc_panel);
		        JLabel lblAmountOfSplits = new JLabel("Amount of splits");
		        panel.add(lblAmountOfSplits);
		        splitAmountTextField = new JFormattedTextField(formatter);
		        panel.add(splitAmountTextField);
		        splitAmountTextField.setHorizontalAlignment(SwingConstants.CENTER);
		        splitAmountTextField.setText("1");
		        splitAmountTextField.setColumns(10);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							if (tabbedPane.getSelectedIndex() == 0)
								resultAction = new FileSplitterByPartSize(f, Long.parseLong(standardSplitSizeTextField.getText().replaceAll(",", "")));
							else if (tabbedPane.getSelectedIndex() == 1)
								resultAction = new FileSplitterWithEncryption(f, passwordTextField.getText(), Long.parseLong(encryptedSplitSizeTextField.getText().replaceAll(",", "")));
							else if (tabbedPane.getSelectedIndex() == 2)
								resultAction = new FileSplitterByPartCount(f, Long.parseLong(splitAmountTextField.getText().replaceAll(",", "")));
							setVisible(false);
							dispose();
						} catch (Exception e1) {
							// Not gonna happen :)
							e1.printStackTrace();
						}
						
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
			}
		}
	}
	
	Action showDialog() {
		setVisible(true);
		return resultAction;
	}
}
