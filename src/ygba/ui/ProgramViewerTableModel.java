package ygba.ui;

import ygba.cpu.ARM7TDMI;
import ygba.memory.Memory;
import ygba.util.Hex;

final class ProgramViewerTableModel extends InternationalTableModel {

	private static final long serialVersionUID = 1L;

	private ARM7TDMI cpu;
	private Memory memory;

	private int memoryBank;

	public ProgramViewerTableModel(ARM7TDMI cpu, Memory memory) {
		this.cpu = cpu;
		this.memory = memory;

		columnName = new String[3];
		columnName[0] = "Offset";
		columnName[1] = "Opcode";
		columnName[2] = "Instruction";

		memoryBank = 0x00;
	}

	protected int getMemoryBank() {
		return memoryBank;
	}

	protected void setMemoryBank(int bankNumber) {
		memoryBank = bankNumber;
		fireTableDataChanged();
	}

	public int getRowCount() {
		return memory.getSize(memoryBank) >>> (cpu.getTFlag() ? 1 : 2);
	}

	public Object getValueAt(int row, int col) {
		int offset = (memoryBank << 24);
		if (cpu.getTFlag()) {
			offset += (row << 1);
			switch (col) {
			case 0:
				return Hex.toAddrString(offset, Hex.Word);
			case 1:
				return Hex.toHexString(memory.getHalfWord(offset), Hex.HalfWord);
			case 2:
				return cpu.disassembleTHUMB(offset);
			default:
				return "";
			}
		} else {
			offset += (row << 2);
			switch (col) {
			case 0:
				return Hex.toAddrString(offset, Hex.Word);
			case 1:
				return Hex.toHexString(memory.getWord(offset), Hex.Word);
			case 2:
				return cpu.disassembleARM(offset);
			default:
				return "";
			}
		}
	}

}
