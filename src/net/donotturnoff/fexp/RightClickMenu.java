package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.lang.Runtime;
import java.lang.Process;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.awt.Image;
import javax.imageio.*;
import java.awt.image.*;
import javax.imageio.stream.ImageOutputStream;

import java.awt.geom.AffineTransform;

public class RightClickMenu extends JPopupMenu implements ActionListener {
	JMenuItem copyOption, cutOption, pasteOption, renameOption, newFileOption, newDirOption, deleteOption, rubbishBinOption;
	FileExplorer fexp;
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == copyOption) {
			fexp.copy();
		} else if (event.getSource() == cutOption) {
			fexp.cut();
		} else if (event.getSource() == pasteOption) {
			fexp.paste();
		} else if (event.getSource() == renameOption) {
			fexp.rename();
		} else if (event.getSource() == newFileOption) {
			fexp.newFile();
		} else if (event.getSource() == newDirOption) {
			fexp.newDir();
		} else if (event.getSource() == deleteOption) {
			fexp.delete();
		} else if (event.getSource() == rubbishBinOption) {
			fexp.rubbish();
		}
	}
	
	public RightClickMenu(FileExplorer explorer, String options) {
		fexp = explorer;
	
		copyOption = new JMenuItem("Copy");
		cutOption = new JMenuItem("Cut");
		pasteOption = new JMenuItem("Paste");
		renameOption = new JMenuItem("Rename");
		newFileOption = new JMenuItem("New file");
		newDirOption = new JMenuItem("New directory");
		deleteOption = new JMenuItem("Delete");
		rubbishBinOption = new JMenuItem("Move to rubbish bin");
		
		copyOption.addActionListener(this);
		cutOption.addActionListener(this);
		pasteOption.addActionListener(this);
		renameOption.addActionListener(this);
		newFileOption.addActionListener(this);
		newDirOption.addActionListener(this);
		deleteOption.addActionListener(this);
		rubbishBinOption.addActionListener(this);
		
		for (int i = 0; i < options.length(); i++) {
			switch (options.charAt(i)) {
				case 'c': add(copyOption); break;
				case 'x': add(cutOption); break;
				case 'v': add(pasteOption); break;
				case 'r': add(renameOption); break;
				case 'n': add(newFileOption); break;
				case 'm': add(newDirOption); break;
				case 'd': add(deleteOption); break;
				case 't': add(rubbishBinOption); break;
				case ' ': add(new JSeparator()); break;
			}
		}
	}
}
