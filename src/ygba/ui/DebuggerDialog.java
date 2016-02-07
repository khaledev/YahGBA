package ygba.ui;

import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableColumnModel;

import ygba.YGBA;
import ygba.cpu.ARM7TDMI;
import ygba.memory.Memory;

final class DebuggerDialog extends JDialog implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private ARM7TDMI cpu;
	private Memory memory;

	private JComboBox memoryBankSelector;

	private JScrollPane programViewerScrollPane;
	private JTable programViewerTable;
	private ProgramViewerTableModel programViewerTableModel;
	private JViewport programViewerViewport;

	private JScrollPane registerViewerScrollPane;
	private JTable registerViewerTable;
	private RegisterViewerTableModel registerViewerTableModel;

	private JScrollPane flagViewerScrollPane;
	private JTable flagViewerTable;
	private FlagViewerTableModel flagViewerTableModel;

	private JButton refreshButton, stepButton, closeButton;
	private JPanel buttonsPanel;

	private Container container;

	private final static int RefreshKey = KeyEvent.VK_F5, StepKey = KeyEvent.VK_F7, CloseKey = KeyEvent.VK_ESCAPE;

	private final static String SwitchMemoryBankCommand = "SWITCH_MEM_BANK", RefreshCommand = "REFRESH", StepCommand = "STEP", CloseCommand = "CLOSE";

	public DebuggerDialog(YGBA ygba) {
		cpu = ygba.getCPU();
		memory = ygba.getMemory();

		memoryBankSelector = new JComboBox();
		for (int i = 0; i < 0x10; i++) {
			memoryBankSelector.addItem(memory.getDescription(i));
		}
		memoryBankSelector.setActionCommand(SwitchMemoryBankCommand);
		memoryBankSelector.addActionListener(this);
		memoryBankSelector.addKeyListener(this);

		programViewerTableModel = new ProgramViewerTableModel(cpu, memory);
		programViewerTable = new JTable(programViewerTableModel);
		programViewerTable.addKeyListener(this);
		programViewerTable.setRowHeight(20);
		TableColumnModel programViewerColumnModel = programViewerTable.getTableHeader().getColumnModel();
		programViewerColumnModel.getColumn(0).setPreferredWidth(100);
		programViewerColumnModel.getColumn(1).setPreferredWidth(100);
		programViewerColumnModel.getColumn(2).setPreferredWidth(250);
		programViewerScrollPane = new JScrollPane(programViewerTable);
		programViewerViewport = programViewerScrollPane.getViewport();

		registerViewerTableModel = new RegisterViewerTableModel(cpu);
		registerViewerTable = new JTable(registerViewerTableModel);
		registerViewerTable.addKeyListener(this);
		registerViewerTable.setFont(new Font(null, Font.PLAIN, 10));
		registerViewerTable.setRowHeight(15);
		TableColumnModel registerViewerColumnModel = registerViewerTable.getTableHeader().getColumnModel();
		registerViewerColumnModel.getColumn(0).setPreferredWidth(75);
		registerViewerColumnModel.getColumn(1).setPreferredWidth(105);
		registerViewerScrollPane = new JScrollPane(registerViewerTable);

		flagViewerTableModel = new FlagViewerTableModel(cpu);
		flagViewerTable = new JTable(flagViewerTableModel);
		flagViewerTable.setFont(new Font(null, Font.PLAIN, 10));
		flagViewerTable.addKeyListener(this);
		flagViewerTable.setRowHeight(15);
		TableColumnModel flagViewerColumnModel = flagViewerTable.getTableHeader().getColumnModel();
		flagViewerColumnModel.getColumn(0).setPreferredWidth(75);
		flagViewerColumnModel.getColumn(1).setPreferredWidth(105);
		flagViewerScrollPane = new JScrollPane(flagViewerTable);

		refreshButton = new JButton();
		refreshButton.setText("Refresh" + " " + KeyEvent.getKeyText(RefreshKey));
		refreshButton.setActionCommand(RefreshCommand);
		refreshButton.addActionListener(this);
		refreshButton.addKeyListener(this);
		stepButton = new JButton();
		stepButton.setText("Step" + " " + KeyEvent.getKeyText(StepKey));
		stepButton.setActionCommand(StepCommand);
		stepButton.addActionListener(this);
		stepButton.addKeyListener(this);
		closeButton = new JButton();
		closeButton.setText("Close" + " " + KeyEvent.getKeyText(CloseKey));
		closeButton.setActionCommand(CloseCommand);
		closeButton.addActionListener(this);
		closeButton.addKeyListener(this);

		buttonsPanel = new JPanel();
		buttonsPanel.add(refreshButton);
		buttonsPanel.add(stepButton);
		buttonsPanel.add(closeButton);

		container = getContentPane();
		container.setLayout(null);

		memoryBankSelector.setBounds(5, 5, 450, 25);
		container.add(memoryBankSelector);
		programViewerScrollPane.setBounds(5, 40, 450, 360);
		container.add(programViewerScrollPane);
		buttonsPanel.setBounds(5, 405, 450, 40);
		container.add(buttonsPanel);
		registerViewerScrollPane.setBounds(460, 5, 180, 290);
		container.add(registerViewerScrollPane);
		flagViewerScrollPane.setBounds(460, 300, 180, 140);
		container.add(flagViewerScrollPane);

		refresh();

		setTitle("Debugger");
		setSize(650, 500);
		setResizable(false);
		setModal(true);
		setVisible(true);
	}

	private void go(int offset) {
		int memoryBank = (offset >>> 24) & 0x0F;

		memoryBankSelector.setSelectedIndex(memoryBank);

		int y = memory.getInternalOffset(memoryBank, offset) >>> (cpu.getTFlag() ? 1 : 2);

		if (!programViewerViewport.getViewRect().contains(0, y * 20, 1, 20)) {
			programViewerViewport.setViewPosition(new Point(0, y * 20));
		}

		programViewerTable.grabFocus();
		programViewerTable.setRowSelectionInterval(y, y);

		registerViewerTableModel.fireTableDataChanged();
		flagViewerTableModel.fireTableDataChanged();
	}

	private void refresh() {
		go(cpu.getCurrentPC());
	}

	private void step() {
		cpu.run(ARM7TDMI.CyclesPerInstruction);
		refresh();
	}

	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();

		if (actionCommand.equals(SwitchMemoryBankCommand)) {
			int oldBank = programViewerTableModel.getMemoryBank();
			int newBank = memoryBankSelector.getSelectedIndex();
			if (newBank != oldBank) {
				programViewerTableModel.setMemoryBank(newBank);
				programViewerViewport.validate();
				programViewerViewport.setViewPosition(new Point(0, 0));
			}
		} else if (actionCommand.equals(RefreshCommand))
			refresh();
		else if (actionCommand.equals(StepCommand))
			step();
		else if (actionCommand.equals(CloseCommand))
			dispose();
	}

	public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case RefreshKey:
			refresh();
			break;
		case StepKey:
			step();
			break;
		case CloseKey:
			dispose();
			break;
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}

}
