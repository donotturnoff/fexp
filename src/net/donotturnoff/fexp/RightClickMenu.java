package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.event.*;

public class RightClickMenu extends JPopupMenu implements ActionListener {
	private final JMenuItem copyOption, cutOption, pasteOption, renameOption, newFileOption, newDirOption, deleteOption, rubbishBinOption;
	private final FileExplorer fexp;
	
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == copyOption) {
			fexp.copy();
		} else if (source == cutOption) {
			fexp.cut();
		} else if (source == pasteOption) {
			fexp.paste();
		} else if (source == renameOption) {
			fexp.rename();
		} else if (source == newFileOption) {
			fexp.newFile();
		} else if (source == newDirOption) {
			fexp.newDir();
		} else if (source == deleteOption) {
			fexp.delete();
		} else if (source == rubbishBinOption) {
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
