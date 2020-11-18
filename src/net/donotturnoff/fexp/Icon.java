package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.border.*;

public class Icon implements MouseListener {
	private JLabel label;
	private final String path, filename, assocCommand;
	private String mimeType;
	private final File file;
	private final FileExplorer fexp;

	public String getPath() {
		return path;
	}

	public File getFile() {
		return file;
	}

	public String getFilename() {
		return filename;
	}

	public JLabel getLabel() {
		return label;
	}

	public String getMimeType() {
		return mimeType;
	}
	
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
		
		for (Icon icon: fexp.getSelectedIcons()) {
			if (icon.label == event.getSource()) {
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
		if (event.getClickCount() == 1) {
			if (!fexp.isCtrlPressed()) {
				fexp.getSelectedIcons().clear();
			}
			fexp.getSelectedIcons().add(this);
			fexp.getIcons().forEach((x) -> unshade(x.label));
			fexp.getSelectedIcons().forEach((x) -> shade(x.label));
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
		JPanel panel = new JPanel();
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
		fexp.getFilesPnl().add(panel);
	}
	
	public ImageIcon createImage() {
		ImageIcon fileImage = new ImageIcon();
		if (file.isDirectory()) {fileImage = fexp.DIR_ICON;}
		else if (file.isFile()) {fileImage = fexp.FILE_ICON;}
		return fileImage;	
	}
	
	public String createFormattedLabel() {
		StringBuilder formattedLabel = new StringBuilder();
		for (int i = 0; i < Math.min(filename.length(), 30); i++) {
			formattedLabel.append(filename.charAt(i)).append("<wbr />");
		}
		formattedLabel = new StringBuilder("<html>" + formattedLabel + ((filename.length() > 30) ? "\u2026" : "") + "</html>");
		return formattedLabel.toString();
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
		}
		return assocCommand;
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
