package ygba.ui;

import ygba.cpu.ARM7TDMI;
import ygba.util.Hex;

final class FlagViewerTableModel extends InternationalTableModel {

	private static final long serialVersionUID = 1L;

	private ARM7TDMI cpu;

	public FlagViewerTableModel(ARM7TDMI cpu) {
		this.cpu = cpu;

		columnName = new String[2];
		columnName[0] = "Flag";
		columnName[1] = "Value";
	}

	public int getRowCount() {
		return 8;
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			switch (row) {
			case 0:
				return "M";
			case 1:
				return "T";
			case 2:
				return "F";
			case 3:
				return "I";
			case 4:
				return "V";
			case 5:
				return "C";
			case 6:
				return "N";
			case 7:
				return "Z";
			default:
				return "";
			}

		case 1:
			switch (row) {
			case 0:
				return cpu.getModeName() + " (" + Hex.toHexString(cpu.getMode()) + ")";
			case 1:
				return cpu.getTFlag();
			case 2:
				return cpu.getFFlag();
			case 3:
				return cpu.getIFlag();
			case 4:
				return cpu.getVFlag();
			case 5:
				return cpu.getCFlag();
			case 6:
				return cpu.getNFlag();
			case 7:
				return cpu.getZFlag();
			default:
				return "";
			}

		default:
			return "";
		}
	}

}