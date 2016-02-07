package ygba.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import ygba.YGBA;
import ygba.gfx.GFXScreen;
import ygba.memory.IORegMemory;
import ygba.memory.Memory;

public final class YGBAApplet extends JApplet implements ActionListener, KeyListener, MouseListener {

	private static final long serialVersionUID = 1L;

	private YGBA ygba;

	private Memory memory;
	private IORegMemory iorMem;

	private GFXScreen gfxScreen;

	private boolean isApplet;

	private URL biosURL, romURL;

	private JPanel mainPanel;

	private JPopupMenu popupMenu;

	private JMenu fileMenu;
	private JMenuItem openBIOSMenuItem, openROMMenuItem;
	private JMenuItem resetMenuItem;
	private JCheckBoxMenuItem pauseMenuItem;

	private JMenu toolsMenu;
	private JMenuItem debuggerMenuItem;

	private JMenuItem aboutMenuItem;

	private JFileChooser biosFileChooser, romFileChooser;
	private YGBAFileFilter fileFilter;

	private final static String OpenBIOSCommand = "OPEN_BIOS", OpenROMCommand = "OPEN_ROM", ResetCommand = "RESET", PauseCommand = "PAUSE", LaunchDebuggerCommand = "LAUNCH_DEBUGGER", DisplayAboutInfoCommand = "DISPLAY_ABOUT_INFO";

	private final static int OpenBIOSKey = KeyEvent.VK_F1, OpenROMKey = KeyEvent.VK_F2, ResetKey = KeyEvent.VK_R, PauseKey = KeyEvent.VK_P, LaunchDebuggerKey = KeyEvent.VK_D;

	public YGBAApplet() {
		this(true);
	}

	public YGBAApplet(boolean isApplet) {
		this.isApplet = isApplet;
		biosURL = romURL = null;
	}

	public void init() {
		ygba = new YGBA();

		memory = ygba.getMemory();
		iorMem = memory.getIORegMemory();

		gfxScreen = new GFXScreen(ygba.getGraphics());

		openBIOSMenuItem = new JMenuItem("Open BIOS");
		openBIOSMenuItem.setAccelerator(KeyStroke.getKeyStroke(OpenBIOSKey, 0));
		openBIOSMenuItem.setActionCommand(OpenBIOSCommand);
		openBIOSMenuItem.addActionListener(this);
		openROMMenuItem = new JMenuItem("Open ROM");
		openROMMenuItem.setAccelerator(KeyStroke.getKeyStroke(OpenROMKey, 0));
		openROMMenuItem.setActionCommand(OpenROMCommand);
		openROMMenuItem.addActionListener(this);
		resetMenuItem = new JMenuItem("Reset");
		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(ResetKey, KeyEvent.CTRL_DOWN_MASK));
		resetMenuItem.setActionCommand(ResetCommand);
		resetMenuItem.addActionListener(this);
		pauseMenuItem = new JCheckBoxMenuItem("Pause");
		pauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(PauseKey, KeyEvent.CTRL_DOWN_MASK));
		pauseMenuItem.setActionCommand(PauseCommand);
		pauseMenuItem.addActionListener(this);
		pauseMenuItem.setSelected(false);
		fileMenu = new JMenu("File");
		fileMenu.add(openBIOSMenuItem);
		fileMenu.add(openROMMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(resetMenuItem);
		fileMenu.add(pauseMenuItem);

		debuggerMenuItem = new JMenuItem("Debugger");
		debuggerMenuItem.setAccelerator(KeyStroke.getKeyStroke(LaunchDebuggerKey, KeyEvent.ALT_DOWN_MASK));
		debuggerMenuItem.setActionCommand(LaunchDebuggerCommand);
		debuggerMenuItem.addActionListener(this);
		toolsMenu = new JMenu("Tools");
		toolsMenu.add(debuggerMenuItem);

		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setActionCommand(DisplayAboutInfoCommand);
		aboutMenuItem.addActionListener(this);

		setupPopupMenu(false);

		popupMenu = new JPopupMenu();
		popupMenu.add(fileMenu);
		popupMenu.add(toolsMenu);
		popupMenu.add(aboutMenuItem);

		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		mainPanel.setBackground(Color.BLACK);
		mainPanel.setFocusable(true);
		mainPanel.requestFocus();
		mainPanel.add(gfxScreen);
		mainPanel.addKeyListener(this);
		mainPanel.addMouseListener(this);

		getContentPane().add(mainPanel);

		if (isApplet) {
			openBIOSMenuItem.setEnabled(false);
			openROMMenuItem.setEnabled(false);

			try {
				biosURL = new URL(getParameter("bios"));
				romURL = new URL(getParameter("rom"));
			} catch (MalformedURLException e) {
			}
		} else {
			fileFilter = new YGBAFileFilter();

			biosFileChooser = new JFileChooser();
			biosFileChooser.setFileFilter(fileFilter);
			biosFileChooser.setDialogTitle("Open BIOS");

			romFileChooser = new JFileChooser();
			romFileChooser.setFileFilter(fileFilter);
			romFileChooser.setDialogTitle("Open ROM");
		}

		/*
		 * // Autoload BIOS file on startup try { biosURL = new URL("file:/C:/BIOS.gba"); memory.loadBIOS(biosURL); } catch (MalformedURLException e) {}
		 */
	}

	public void start() {
		if (biosURL != null)
			memory.loadBIOS(biosURL);
		if (romURL != null)
			memory.loadROM(romURL);
		if (ygba.isReady()) {
			ygba.reset();
			ygba.run();
			setupPopupMenu(true);
		}
	}

	public void stop() {
		ygba.stop();
	}

	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();

		boolean isPaused = pauseMenuItem.isSelected();

		if (actionCommand.equals(OpenBIOSCommand)) {

			int option = biosFileChooser.showOpenDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				try {
					biosURL = biosFileChooser.getSelectedFile().toURI().toURL();
				} catch (MalformedURLException e) {
				}
				ygba.stop();
				memory.unloadBIOS();
				memory.loadBIOS(biosURL);
				if (ygba.isReady()) {
					ygba.reset();
					if (!isPaused)
						ygba.run();
					setupPopupMenu(true);
				} else {
					gfxScreen.clear();
					setupPopupMenu(false);
				}
			}

		} else if (actionCommand.equals(OpenROMCommand)) {

			int option = romFileChooser.showOpenDialog(null);
			if (option == JFileChooser.APPROVE_OPTION) {
				try {
					romURL = romFileChooser.getSelectedFile().toURI().toURL();
				} catch (MalformedURLException e) {
				}
				ygba.stop();
				memory.unloadROM();
				memory.loadROM(romURL);
				if (ygba.isReady()) {
					ygba.reset();
					if (!isPaused)
						ygba.run();
					setupPopupMenu(true);
				} else {
					gfxScreen.clear();
					setupPopupMenu(false);
				}
			}

		} else if (actionCommand.equals(ResetCommand)) {

			ygba.stop();
			ygba.reset();
			if (!isPaused)
				ygba.run();

		} else if (actionCommand.equals(PauseCommand)) {

			if (isPaused)
				ygba.stop();
			else
				ygba.run();

		} else if (actionCommand.equals(LaunchDebuggerCommand)) {

			ygba.stop();
			new DebuggerDialog(ygba);
			if (!isPaused)
				ygba.run();

		} else if (actionCommand.equals(DisplayAboutInfoCommand)) {

			ygba.stop();
			new AboutDialog();
			if (ygba.isReady() & !isPaused)
				ygba.run();

		}
	}

	public void keyPressed(KeyEvent ke) {
		int keyCode = ke.getKeyCode();
		boolean isControlDown = ke.isControlDown();
		boolean isAltDown = ke.isAltDown();

		switch (keyCode) {
		case OpenBIOSKey:
			openBIOSMenuItem.doClick();
			break;
		case OpenROMKey:
			openROMMenuItem.doClick();
			break;
		case ResetKey:
			if (isControlDown)
				resetMenuItem.doClick();
			break;
		case PauseKey:
			if (isControlDown)
				pauseMenuItem.doClick();
			break;
		case LaunchDebuggerKey:
			if (isAltDown)
				debuggerMenuItem.doClick();
			break;
		}

		iorMem.keyPressed(ke.getKeyCode());
	}

	public void keyReleased(KeyEvent ke) {
		iorMem.keyReleased(ke.getKeyCode());
	}

	public void keyTyped(KeyEvent ke) {
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if (me.isPopupTrigger())
			popupMenu.show(gfxScreen, me.getX(), me.getY());
	}

	public void mouseReleased(MouseEvent me) {
		if (me.isPopupTrigger())
			popupMenu.show(gfxScreen, me.getX(), me.getY());
	}

	private void setupPopupMenu(boolean isRunning) {
		resetMenuItem.setEnabled(isRunning);
		debuggerMenuItem.setEnabled(isRunning);
	}

}
