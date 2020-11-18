package net.donotturnoff.fexp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Clipboard {
	private final FileExplorer fexp;
	private final Set<Icon> clipboard;
	private String mode, source;
	
	public Clipboard(FileExplorer explorer) {
		fexp = explorer;
		clipboard = new HashSet<>();
	}
	
	public void add(Set<Icon> icons) {
		clipboard.addAll(icons);
	}
	
	public void findChildren(Icon icon) {
		String[] files = icon.getFile().list();
		for (String file : files) {
			Icon childIcon = new Icon(fexp, file, icon.getPath() + icon.getFilename() + "/");
			if (childIcon.getFile().isDirectory()) {
				findChildren(childIcon);
			}
			clipboard.add(childIcon);
		}
	}
	
	public void copy(Set<Icon> icons, String sourcePath) {
		mode = "copy";
		source = sourcePath;
		add(icons);
		for (Icon icon : icons) {
			if (icon.getFile().isDirectory()) {
				findChildren(icon);
			}
		}
	}
	
	public void cut(Set<Icon> icons, String sourcePath) {
		mode = "cut";
		source = sourcePath;
		add(icons);
		for (Icon icon : icons) {
			if (icon.getFile().isDirectory()) {
				findChildren(icon);
			}
		}
	}
	
	public void paste(String destination) {
		try {
			if (mode.equals("copy")) {
				for (Icon icon : clipboard) {
					File file = new File(icon.getPath() + icon.getFilename());
					if (file.isDirectory()) {
						File path = new File(destination + icon.getPath().replace(source, "") + icon.getFilename());
						path.mkdirs();
					} else {
						File path = new File(destination + icon.getPath().replace(source, ""));
						path.mkdirs();
						Files.copy(Paths.get(icon.getPath() + icon.getFilename()), Paths.get(destination + icon.getPath().replace(source, "") + icon.getFilename()));
					}
				}
			} else if (mode.equals("cut")) {
				for (Icon icon : clipboard) {
					File file = new File(icon.getPath() + icon.getFilename());
					if (file.isDirectory()) {
						File path = new File(destination + icon.getPath().replace(source, "") + icon.getFilename());
						path.mkdirs();
					} else {
						File path = new File(destination + icon.getPath().replace(source, ""));
						path.mkdirs();
						Files.move(Paths.get(icon.getPath() + icon.getFilename()), Paths.get(destination + icon.getPath().replace(source, "") + icon.getFilename()));
					}
					fexp.delete(icon.getFile());
				}	
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void clear() {
		clipboard.clear();
	}
}
