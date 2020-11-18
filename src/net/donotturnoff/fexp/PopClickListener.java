package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.event.*;

public class PopClickListener extends MouseAdapter {
	private final FileExplorer fexp;
	private final String filesPanelOptions = "v nm";
	private final String iconOptions = "cx r nm td";

    public void mousePressed(MouseEvent e) {
    	boolean isSelected = false;
    	Icon icon = null;
    	if (e.getSource().getClass() == JPanel.class) {
				if (e.getSource() == fexp.getFilesPnl()) {
					if (e.isPopupTrigger()) {
						doPop(e, filesPanelOptions);
					}
				}
			} else {
				for (Icon i: fexp.getSelectedIcons()) {
					if (e.getSource() == i.getLabel()) {
						isSelected = true;
						icon = i;
						break;
					}
				}
				for (Icon i: fexp.getIcons()) {
					if (e.getSource() == i.getLabel()) {
						icon = i;
						break;
					}
				}
				if (!isSelected) {
					fexp.getSelectedIcons().clear();
					fexp.getSelectedIcons().add(icon);
				}
				if (e.isPopupTrigger()) {
					doPop(e, iconOptions);
				}
			}
    }

    public void mouseReleased(MouseEvent e) {
    	if (e.getSource().getClass() == JPanel.class) {
				if (e.getSource() == fexp.getFilesPnl()) {
					if (e.isPopupTrigger()) {
						doPop(e, filesPanelOptions);
					}
				}
    	}
    	else {
    		if (e.isPopupTrigger()) {
    			doPop(e, iconOptions);
    		}
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
