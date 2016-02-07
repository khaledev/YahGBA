package ygba.ui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public final class YGBAFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public YGBAFrame() {
		super("YahGBA");

		setLocation(0, 0);
		setResizable(false);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		YGBAApplet ygbaApplet = new YGBAApplet(false);
		ygbaApplet.init();

		YGBAFrame ygbaFrame = new YGBAFrame();
		ygbaFrame.add(ygbaApplet);
		ygbaFrame.pack();
		ygbaFrame.setVisible(true);
	}

}