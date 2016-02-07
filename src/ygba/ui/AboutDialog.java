package ygba.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

final class AboutDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTable aboutTable;
	private AboutTableModel aboutTableModel;

	private JButton closeButton;
	private JPanel closePanel;

	private final static String CloseCommand = "CLOSE";

	public AboutDialog() {
		aboutTableModel = new AboutTableModel();
		aboutTable = new JTable(aboutTableModel);
		aboutTable.setEnabled(false);
		aboutTable.setRowHeight(25);
		TableColumnModel aboutColumnModel = aboutTable.getTableHeader().getColumnModel();
		aboutColumnModel.getColumn(0).setPreferredWidth(150);
		aboutColumnModel.getColumn(1).setPreferredWidth(250);

		closeButton = new JButton("Close");
		closeButton.setActionCommand(CloseCommand);
		closeButton.addActionListener(this);
		closePanel = new JPanel();
		closePanel.add(closeButton);

		Container container = getContentPane();
		container.add(aboutTable, BorderLayout.PAGE_START);
		container.add(closePanel, BorderLayout.PAGE_END);

		setTitle("About");
		pack();
		setResizable(false);
		setModal(true);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();

		if (actionCommand.equals(CloseCommand))
			dispose();
	}

}