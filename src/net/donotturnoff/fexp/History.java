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

public class History {
	ArrayList<String> history = new ArrayList<String>();
	int pos = -1;
	
	public void addPath(String path) {
		if (path.charAt(path.length() - 1) != '/') {
			path += "/";
		}
		history.add(path);
		pos++;
	}
	
	public void appendPart(String part) {
		addPath(getPath(0) + part + "/");
	}
	
	public String getPath(int offset) {
		if (populated()) {
			return history.get(pos+offset);
		}
		else {
			return "";
		}
	}
	
	public void jumpToPosition(int position) {
		if (position < length() && position > -1) {
			pos = position;
		}
	}
	
	public void jumpByOffset(int offset) {
		if (pos + offset < length() && pos + offset > -1) {
			pos += offset;
		}
	}
	
	public void clearTail() {
		for (int i = pos + 1; i < length(); i++) {
			history.remove(i);
		}
	}
	
	public void up() {
		String[] pathParts = getPath(0).split("/");
		String newPath = "";
		for (int i = 0; i < pathParts.length - 1; i++) {
			newPath += pathParts[i] + "/";
		}
		clearTail();
		addPath(newPath);
	}
	
	public int length() {
		return history.size();
	}
	
	public boolean populated() {
		return length() > 0;
	}
	
	public int pathLength(int offset) {
		return getPath(offset).split("/").length;
	}
	
	public void dump() {
		for (int i = 0; i < length(); i++) {
			System.out.print(history.get(i) + ", ");
		}
		System.out.println(pos);
	}
}