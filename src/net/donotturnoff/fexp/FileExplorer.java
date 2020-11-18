package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.io.*;
import java.lang.Runtime;
import java.lang.Process;
import java.util.HashSet;
import java.util.Set;

public class FileExplorer extends JFrame implements ActionListener, ItemListener, KeyListener, ComponentListener, MouseListener {
	private final ClassLoader ldr = this.getClass().getClassLoader();
	public final ImageIcon DIR_ICON = new ImageIcon(ldr.getResource("icons/dir.png"));
	public final ImageIcon FILE_ICON = new ImageIcon(ldr.getResource("icons/file.png"));

	private final JPanel menuPnl = new JPanel();
	private final JPanel filesPnl = new JPanel();

	private final Set<JPanel> paddingPnls = new HashSet<>();
	private final Set<Icon> icons = new HashSet<>();
	private final Set<Icon> selectedIcons = new HashSet<>();

	ThumbnailLoader thumbnailLoader = new ThumbnailLoader(icons);
	
	final JButton backBtn = new JButton("<");
	final JButton upBtn = new JButton("^");
	final JButton forwardsBtn = new JButton(">");
	final JButton homeBtn = new JButton("Home");
	final JButton refreshBtn = new JButton("@");
	final JTextField pathEntry = new JTextField(72);
	final JButton navigateBtn = new JButton("=>");
	final JCheckBox showHiddenChk = new JCheckBox("Show hidden files");
	
	final JLabel statusLbl = new JLabel("");
	
	final History history = new History();
	
	final Clipboard clipboard;
	
	final Container contentPane = getContentPane();
	
	int itemsCount = 0;

	public JPanel getFilesPnl() {
		return filesPnl;
	}

	public Set<Icon> getIcons() {
		return icons;
	}

	public Set<Icon> getSelectedIcons() {
		return selectedIcons;
	}

	public boolean isCtrlPressed() {
		return false;
	}
	
	/*
	Event handlers
	*/
	
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	public void mousePressed(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
	public void mouseClicked(MouseEvent event) {
		selectedIcons.forEach((x) -> x.unshade(x.getLabel()));
	}
	
	public void itemStateChanged(ItemEvent event) {
		if (event.getItemSelectable() == showHiddenChk) {
			showFiles(history.getPath(0));
		}
	}
	
	public void keyTyped(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}
    
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == 10 && event.getSource() == pathEntry) {
			navigateFromPathEntry();
			showFiles(history.getPath(0));
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == upBtn) {
			history.up();
		} else if (event.getSource() == navigateBtn) {
			navigateFromPathEntry();
		} else if (event.getSource() == backBtn) {
			history.jumpByOffset(-1);
		} else if (event.getSource() == forwardsBtn) {
			history.jumpByOffset(1);
		} else if (event.getSource() == homeBtn) {
			goHome();
		}
		showFiles(history.getPath(0));
	}
	
	public void componentHidden(ComponentEvent event) {}
    public void componentMoved(ComponentEvent event) {}
    public void componentShown(ComponentEvent event) {}
    public void componentResized(ComponentEvent event) {showFiles(history.getPath(0));}
	
	/*
	
	*/
	
	public void copy() {
		clipboard.clear();
		clipboard.copy(selectedIcons, history.getPath(0));
	}
	
	public void cut() {
		clipboard.clear();
		clipboard.cut(selectedIcons, history.getPath(0));
	}
	
	public void paste() {
		clipboard.paste(history.getPath(0));
		showFiles(history.getPath(0));
	}
	
	public void rename() {
		selectedIcons.forEach((x) -> rename(x.getFile()));
		showFiles(history.getPath(0));
	}
	
	public void rename(File file) {
		boolean valid = false;
		String newName = "";
		while (!valid) {
			newName = JOptionPane.showInputDialog(this, "Enter a new name for the file " + file.getName(), "Rename file", JOptionPane.PLAIN_MESSAGE);
			if (newName == null) {
				break;
			} else {
				if (!newName.contains("/")) {
					valid = true;
				}
			}
		}
		String newPath = history.getPath(0) + "/" + newName;
		if (!file.renameTo(new File(newPath))) {
			JOptionPane.showMessageDialog(this, "Could not rename " + file.getName() + " to " + newName + ".", "Rename failed", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void newFile() {
		boolean valid = false;
		String filename = "";
		while (!valid) {
			filename = JOptionPane.showInputDialog(this, "Enter a name for the new file", "New file", JOptionPane.PLAIN_MESSAGE);
			if (filename == null) {
				break;
			} else {
				if (!filename.contains("/")) {
					valid = true;
				}
			}
		}
		try {
			if (valid) {
				if (!filename.equals("")) {
					File newFile = new File(history.getPath(0) + filename);
					if (!newFile.createNewFile()) {
						throw new IOException("Failed to create file");
					}
					showFiles(history.getPath(0));
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void newDir() {
		boolean valid = false;
		String dirname = "";
		while (!valid) {
			dirname = JOptionPane.showInputDialog(this, "Enter a name for the new directory", "New directory", JOptionPane.PLAIN_MESSAGE);
			if (dirname == null) {
				break;
			} else {
				if (!dirname.contains("/")) {
					valid = true;
				}
			}
		}
		try {
			if (valid) {
				if (!dirname.equals("")) {
					File newDir = new File(history.getPath(0) + dirname);
					if (!newDir.mkdir()) {
						throw new IOException("Failed to create directory");
					}
					showFiles(history.getPath(0));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void delete(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) { //some JVMs return null for empty dirs
				for (File value : files) {
					if (value.isDirectory()) {
						delete(value);
					} else {
						value.delete();
					}
				}
			}
		}
		file.delete();
	}
	
	public void delete() {
		int confirmationChoice = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete the selected directories/files?", "Permanently delete selection?", JOptionPane.YES_NO_OPTION);
		if (confirmationChoice == 0) {
			selectedIcons.forEach((x) -> delete(x.getFile()));
			showFiles(history.getPath(0));
		}
	}
	
	public void rubbish() {
		try {
			selectedIcons.forEach((x) -> Desktop.getDesktop().moveToTrash(x.getFile()));
			showFiles(history.getPath(0));
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(this, "Cannot move files to rubbish bin - operation not supported on this platform.", "Cannot move to bin", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void openFile(String path, String filename, String mimeType, String assocCommand) {
		try {
			if (assocCommand.equals("")) {
				String command = JOptionPane.showInputDialog(this, path + filename + " is a file of type " + mimeType + ".\nThere is currently no program associated with this MIME type.\nEnter the command with which you want to open the file here", "Unassociated file type", JOptionPane.INFORMATION_MESSAGE);
				if (command != null && command.length() > 0) {
					FileWriter fw = new FileWriter("associations", true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(mimeType + "=" + command);
					bw.newLine();
					bw.close();
					assocCommand = command;
				}
			}
			if (!assocCommand.equals("")) {
				String[] fileOpenCommand = {assocCommand, path + filename};
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec(fileOpenCommand);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failure to execute command " + assocCommand + " with file " + path + filename, "Could not execute command", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void navigateFromPathEntry() {
		File file = new File(pathEntry.getText());
		if (file.isDirectory()) {
			history.clearTail();
			history.addPath(pathEntry.getText());
		}
		else if (file.isFile()) {
			JOptionPane.showMessageDialog(this, pathEntry.getText() + " is a file.", "File specified", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(this, pathEntry.getText() + " is not a valid path.", "Invalid path", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void goHome() {
		String homeDir = System.getProperty("user.home");
		history.addPath(homeDir);
	}
	
	public void populateFiles(String path) {
		try {
			thumbnailLoader.terminate();
			thumbnailLoader.join();
		} catch (Exception e) {
			System.out.println("Error killing thumbnail loader thread: " + e.getMessage());
		}
		icons.clear();
		try {
			File dir = new File(path);
			if (dir.exists()) {
				String[] files = dir.list();
				if (files == null) {
					throw new IOException("Failed to list files");
				}
				Arrays.sort(files);
				for (String file : files) {
					if ((!file.startsWith(".") && !file.endsWith("~")) || showHiddenChk.isSelected()) {
						Icon icon = new Icon(this, file);
						icons.add(icon);
						icon.create();
						itemsCount++;
					}
				}
				for (int j = 0; j < ((filesPnl.getBounds().width / 96) * (filesPnl.getBounds().height / 120) - paddingPnls.size() - files.length); j++) {
					JPanel paddingPnl = new JPanel();
					paddingPnl.setOpaque(false);
					paddingPnls.add(paddingPnl);
				}
				for (JPanel paddingPnl : paddingPnls) {
					filesPnl.add(paddingPnl);
				}
				thumbnailLoader = new ThumbnailLoader(icons);
				thumbnailLoader.start();
				paddingPnls.clear();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "You do not have sufficient privileges to access " + path, "Insufficient privileges", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void showFiles(String path) {
		itemsCount = 0;
		filesPnl.removeAll();
		paddingPnls.forEach(filesPnl::remove);
			
		filesPnl.setLayout(new GridLayout(0, this.getBounds().width / 108));

		populateFiles(path);
		
		backBtn.setEnabled(history.populated() && history.position() > 0);
		upBtn.setEnabled(history.pathLength(0) > 0);
		forwardsBtn.setEnabled((history.populated() && history.position() < history.length()-1));
		pathEntry.setText(path);
		
		filesPnl.repaint();
		filesPnl.revalidate();
		
		statusLbl.setText(itemsCount + " items shown (hidden items " + ((showHiddenChk.isSelected()) ? "" : "not ") + "shown)");
	}
	
	public void initMenuBar() {
		menuPnl.add(backBtn);
		menuPnl.add(upBtn);
		menuPnl.add(forwardsBtn);
		menuPnl.add(homeBtn);
		menuPnl.add(refreshBtn);
		menuPnl.add(pathEntry);
		menuPnl.add(navigateBtn);
		menuPnl.add(showHiddenChk);
		
		backBtn.setToolTipText("Back to previous location");
		upBtn.setToolTipText("Go to parent directory");
		forwardsBtn.setToolTipText("Forwards to next location");
		homeBtn.setToolTipText("Go to your home directory");
		refreshBtn.setToolTipText("Refresh current view");
		navigateBtn.setToolTipText("Navigate to path");
		showHiddenChk.setToolTipText("Toggle hidden files");
		
		backBtn.addActionListener(this);
		upBtn.addActionListener(this);
		forwardsBtn.addActionListener(this);
		homeBtn.addActionListener(this);
		refreshBtn.addActionListener(this);
		pathEntry.addKeyListener(this);
		navigateBtn.addActionListener(this);
		showHiddenChk.addItemListener(this);
	}
	
	public FileExplorer() {
		super("fexp");
		setSize(640, 640);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		initMenuBar();

		JScrollPane filesPane = new JScrollPane(filesPnl);
		filesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		filesPane.getVerticalScrollBar().setUnitIncrement(24);
		
		contentPane.add("North", menuPnl);
		JPanel treePnl = new JPanel();
		contentPane.add("West", treePnl);
		contentPane.add("Center", filesPane);
		JPanel statusPnl = new JPanel();
		contentPane.add("South", statusPnl);
		
		statusPnl.add(statusLbl);
		filesPnl.setBackground(Color.white);
		this.addComponentListener(this);
		filesPnl.addMouseListener(this);
		this.addKeyListener(this);
		filesPnl.addMouseListener(new PopClickListener(this));
		
		clipboard = new Clipboard(this);
		
		goHome();
		thumbnailLoader.start();
		//showFiles(history.getPath(0));
	}
	
	public static void main(String[] args) {
		new FileExplorer();
	}	
}
