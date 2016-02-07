package ygba.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

final class YGBAFileFilter extends FileFilter {

	public boolean accept(File file) {
		String fileName = file.getName();
		fileName = fileName.toLowerCase();

		return (file.isDirectory() || fileName.endsWith(".agb") || fileName.endsWith(".bin") || fileName.endsWith(".gba") || fileName.endsWith(".zip"));
	}

	public String getDescription() {
		return "GameBoy Advance ROM file";
	}

}
