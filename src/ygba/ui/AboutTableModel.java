package ygba.ui;

final class AboutTableModel extends InternationalTableModel {

	private static final long serialVersionUID = 1L;

	public AboutTableModel() {
		columnName = new String[2];
		columnName[0] = "Parameter";
		columnName[1] = "Description";
	}

	public int getRowCount() {
		return 6;
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			switch (row) {
			case 0:
				return "Name";
			case 1:
				return "Release Version";
			case 2:
				return "Release Date";
			case 3:
				return "Author";
			case 4:
				return "Contact";
			case 5:
				return "URL";
			}
			break;
		case 1:
			switch (row) {
			case 0:
				return "YahGBA";
			case 1:
				return "0.9.1";
			case 2:
				return "2011-10-08";
			case 3:
				return "Khaled Lakehal";
			case 4:
				return "khaled.lakehal@gmail.com";
			case 5:
				return "http://khaledev.github.io";
			}
			break;
		}
		return "";
	}

}