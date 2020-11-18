package net.donotturnoff.fexp;

import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Clipboard {
	FileExplorer fexp;
	ArrayList<Icon> clipboard;
	String mode;
	String source;
	
	public Clipboard(FileExplorer explorer) {
		fexp = explorer;
		clipboard = new ArrayList<Icon>();
	}
	
	public void add(ArrayList<Icon> icons) {
		icons.forEach((x) -> clipboard.add(x));
	}
	
	public void findChildren(Icon icon) {
		String[] files = icon.file.list();
		for (int i = 0; i < files.length; i++) {
			Icon childIcon = new Icon(fexp, files[i], icon.path + icon.filename + "/");
			if (childIcon.file.isDirectory()) {
				findChildren(childIcon);
			}
			clipboard.add(childIcon);
		}
	}
	
	public void copy(ArrayList<Icon> icons, String sourcePath) {
		mode = "copy";
		source = sourcePath;
		add(icons);
		for (int i = 0; i < icons.size(); i++) {
			if (icons.get(i).file.isDirectory()) {
				findChildren(icons.get(i));
			}
		}
	}
	
	public void cut(ArrayList<Icon> icons, String sourcePath) {
		mode = "cut";
		source = sourcePath;
		add(icons);
		for (int i = 0; i < icons.size(); i++) {
			if (icons.get(i).file.isDirectory()) {
				findChildren(icons.get(i));
			}
		}
	}
	
	public void paste(String destination) {
		try {
			if (mode.equals("copy")) {
				for (int i = 0; i < clipboard.size(); i++) {
					File file = new File(clipboard.get(i).path + clipboard.get(i).filename);
					if (file.isDirectory()) {
						File path = new File(destination + clipboard.get(i).path.replace(source, "") + clipboard.get(i).filename);
						path.mkdirs();
					} else {
						File path = new File(destination + clipboard.get(i).path.replace(source, ""));
						path.mkdirs();
						Files.copy(Paths.get(clipboard.get(i).path + clipboard.get(i).filename), Paths.get(destination + clipboard.get(i).path.replace(source, "") + clipboard.get(i).filename));
					}
				}
			} else if (mode.equals("cut")) {
				for (int i = 0; i < clipboard.size(); i++) {
					File file = new File(clipboard.get(i).path + clipboard.get(i).filename);
					if (file.isDirectory()) {
						File path = new File(destination + clipboard.get(i).path.replace(source, "") + clipboard.get(i).filename);
						path.mkdirs();
					} else {
						File path = new File(destination + clipboard.get(i).path.replace(source, ""));
						path.mkdirs();
						Files.move(Paths.get(clipboard.get(i).path + clipboard.get(i).filename), Paths.get(destination + clipboard.get(i).path.replace(source, "") + clipboard.get(i).filename));
					}
					fexp.delete(clipboard.get(i).file);	
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
