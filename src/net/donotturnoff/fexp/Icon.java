package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;

public class Icon implements MouseListener {
	String path;
	String filename;
	JLabel label;
	JPanel panel;
	String mimeType;
	String assocCommand;
	File file;
	String tooltip;
	History history;
	FileExplorer fexp;
	
	public void mouseEntered(MouseEvent event) {
		/*Border blackLine = BorderFactory.createLineBorder(Color.black);
		((JLabel) event.getSource()).setBorder(blackLine);
		((JLabel) event.getSource()).setOpaque(true);*/
		shade((JLabel) event.getSource());
	}
	
	public void mouseExited(MouseEvent event) {
		/*Border empty = BorderFactory.createEmptyBorder();
		((JLabel) event.getSource()).setBorder(empty);
		((JLabel) event.getSource()).setOpaque(false);*/
		
		boolean isSelected = false;
		
		for (int i = 0; i < fexp.selectedIcons.size(); i++) {
			if (fexp.selectedIcons.get(i).label == ((JLabel) event.getSource())) {
				isSelected = true;
				break;
			} 
		}
		if (!isSelected) {
			unshade((JLabel) event.getSource());
		}
	}
	
	public void mousePressed(MouseEvent event) {} 
	public void mouseReleased(MouseEvent event) {}
	 
	public void mouseClicked(MouseEvent event) {
		JLabel fileLabel = (JLabel) event.getSource();
		if (event.getClickCount() == 1) {
			if (!fexp.ctrlPressed) {
				fexp.selectedIcons.clear();
			}
			fexp.selectedIcons.add(this);
			fexp.icons.forEach((x) -> unshade(x.label));
			fexp.selectedIcons.forEach((x) -> shade(x.label));
		} else if (event.getClickCount() >= 2 && !event.isConsumed()) {
			event.consume();
			if (file.isDirectory()) {
				fexp.history.clearTail();
				fexp.history.addPath(path + filename);
				fexp.showFiles(path + filename);
			} else if (file.isFile()) {
				fexp.openFile(path, filename, mimeType, assocCommand);
			}
		}
	}
	
	public void shade(JLabel iconLabel) {
		Border blackLine = BorderFactory.createLineBorder(Color.black);
		iconLabel.setBorder(blackLine);
		iconLabel.setOpaque(true);
	}
		
	public void unshade(JLabel iconLabel) {
		Border empty = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		iconLabel.setBorder(empty);
		iconLabel.setOpaque(false);
	}
	
	public void create() {
		panel = new JPanel();
		label = new JLabel(createFormattedLabel(), createImage(), JLabel.CENTER);
		
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setToolTipText(path + filename);
		label.addMouseListener(this);
		label.addMouseListener(new PopClickListener(fexp));
		label.setPreferredSize(new Dimension(96, 120));
		label.setBackground(new Color(220, 220, 220));
		panel.setOpaque(false);
						
		panel.add(label);
		fexp.filesPnl.add(panel);
	}
	
	public ImageIcon createImage() {
		ImageIcon fileImage = new ImageIcon();
		if (file.isDirectory()) {fileImage = fexp.dirIcon;}
		else if (file.isFile()) {fileImage = fexp.fileIcon;}	
		return fileImage;	
	}
	
	public String createFormattedLabel() {
		String formattedLabel = "";
		for (int i = 0; i < Math.min(filename.length(), 30); i++) {
			formattedLabel += Character.toString(filename.charAt(i)) + "<wbr />";
		}
		formattedLabel = "<html>" + formattedLabel + ((filename.length() > 30) ? "\u2026" : "") + "</html>";
		return formattedLabel;
	}
	
	public String getAssocCommand() {
		String assocCommand = "";
		try {
			File assocFile = new File("associations");
			FileReader fileReader = new FileReader(assocFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			String[] parts;
			String[] mimeParts;
			while ((line = bufferedReader.readLine()) != null) {
				parts = line.split("=");
				if (mimeType.equals(parts[0])) {
					assocCommand = parts[1];
				}
			}
			if (assocCommand.equals("")) {
				fileReader = new FileReader(assocFile);
				bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					parts = line.split("=");
					mimeParts = mimeType.split("/");
					if (mimeParts[0].equals(parts[0])) {
						assocCommand = parts[1];
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Missing MIME type associations file");
		} finally {
			return assocCommand;
		}
	}
	
	public Icon(FileExplorer explorer, String name) {
		fexp = explorer;
		filename = name;
		path = fexp.history.getPath(0);
		file = new File(path + filename);
		try {
			mimeType = Files.probeContentType(Paths.get(path + filename));
		} catch (IOException e) {
			mimeType = null;
		}
		if (mimeType == null) {
			mimeType = "text/plain";
		}
		assocCommand = getAssocCommand();
	}
	
	public Icon(FileExplorer explorer, String name, String filePath) {
		fexp = explorer;
		filename = name;
		path = filePath;
		file = new File(path + filename);
		try {
			mimeType = Files.probeContentType(Paths.get(path + filename));
		} catch (IOException e) {
			mimeType = null;
		}
		if (mimeType == null) {
			mimeType = "text/plain";
		}
		assocCommand = getAssocCommand();
	}
}
