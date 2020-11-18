package net.donotturnoff.fexp;

import java.util.ArrayList;
import java.util.List;

public class History {
	private final List<String> history = new ArrayList<>();
	private int pos = -1;

	public int position() {
		return pos;
	}
	
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
		StringBuilder newPath = new StringBuilder();
		for (int i = 0; i < pathParts.length - 1; i++) {
			newPath.append(pathParts[i]).append("/");
		}
		clearTail();
		addPath(newPath.toString());
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