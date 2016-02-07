package ygba.ui;

import javax.swing.table.AbstractTableModel;

abstract class InternationalTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	String[] columnName;

	public InternationalTableModel() {
		columnName = null;
	}

	public final int getColumnCount() {
		return columnName.length;
	}

	public final String getColumnName(int col) {
		return columnName[col];
	}

	public abstract int getRowCount();

	public abstract Object getValueAt(int row, int col);

}
