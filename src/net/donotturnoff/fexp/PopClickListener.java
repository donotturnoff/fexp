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

public class PopClickListener extends MouseAdapter {
	FileExplorer fexp;
	String filesPanelOptions = "v nm";
	String iconOptions = "cx r nm td";

    public void mousePressed(MouseEvent e) {
    	boolean isSelected = false;
    	Icon icon = null;
    	if (e.getSource().getClass() == JPanel.class) {
			if ((JPanel) e.getSource() == fexp.filesPnl) {
				if (e.isPopupTrigger()) {doPop(e, filesPanelOptions);}   		
			}
		}
    	else {
			for (int i = 0; i < fexp.selectedIcons.size(); i++) {
				if ((JLabel) e.getSource() == fexp.selectedIcons.get(i).label) {
					isSelected = true;
					icon = fexp.selectedIcons.get(i);
					break;
				}
			}
			for (int j = 0; j < fexp.icons.size(); j++) {
				if ((JLabel) e.getSource() == fexp.icons.get(j).label) {
					icon = fexp.icons.get(j);
					break;
				}
			}
			if (!isSelected) {
				fexp.selectedIcons.clear();
				fexp.selectedIcons.add(icon);
			}
			if (e.isPopupTrigger()) {doPop(e, iconOptions);}
		}
    }

    public void mouseReleased(MouseEvent e) {
    	if (e.getSource().getClass() == JPanel.class) {
			if ((JPanel) e.getSource() == fexp.filesPnl) {
				if (e.isPopupTrigger()) {doPop(e, filesPanelOptions);}   		
			}
    	}
    	else {
        	if (e.isPopupTrigger()) {doPop(e, iconOptions);}
        }
    }

    private void doPop(MouseEvent e, String options){
        RightClickMenu menu = new RightClickMenu(fexp, options);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    public PopClickListener (FileExplorer explorer) {
    	fexp = explorer;
    }
}
