package ygba.ui;

import ygba.cpu.ARM7TDMI;
import ygba.util.Hex;

final class RegisterViewerTableModel extends InternationalTableModel {

	private static final long serialVersionUID = 1L;

	private ARM7TDMI cpu;

	public RegisterViewerTableModel(ARM7TDMI cpu) {
		this.cpu = cpu;

		columnName = new String[2];
		columnName[0] = "Register";
		columnName[1] = "Value";
	}

	public int getRowCount() {
		return 18;
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return cpu.getRegisterName(row).toUpperCase();

		case 1:
			int reg;
			switch (row) {
			case 16:
				reg = cpu.getCPSR();
				break;
			case 17:
				if (ARM7TDMI.SPSR == ARM7TDMI.SPSR_null)
					return "";
				reg = cpu.getSPSR();
				break;
			default:
				reg = cpu.getRegister(row);
				break;
			}
			return Hex.toHexString(reg, Hex.Word);

		default:
			return "";
		}
	}

}
