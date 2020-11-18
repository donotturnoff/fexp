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

public class ThumbnailLoader extends Thread {
	ArrayList<Icon> icons = new ArrayList<Icon>();
	ImageIcon fileLblIcon = null;
	
	private BufferedImage scale(BufferedImage source, int w, int h) {
		BufferedImage bi = getCompatibleImage(w, h);
		Graphics2D g2d = bi.createGraphics();
		double xScale = (double) w / source.getWidth();
		double yScale = (double) h / source.getHeight();
		AffineTransform at = AffineTransform.getScaleInstance(xScale,yScale);
		g2d.drawRenderedImage(source, at);
		g2d.dispose();
		return bi;
	}

	private BufferedImage getCompatibleImage(int w, int h) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(w, h);
		return image;
	}
	
	private void compressImage(Icon icon, int w, int h) throws IOException {
		File input = icon.file;
		
		File outDirs = new File("thumbnails" + icon.path);
		if (!outDirs.exists()) {
			outDirs.mkdirs();
		}

		File output = new File("thumbnails" + icon.path + icon.filename);
		
		boolean thumbnailNeedsUpdating = false;
		
		if (output.exists()) {
			if (input.lastModified() > output.lastModified()) {thumbnailNeedsUpdating = true;}
		}
		else {thumbnailNeedsUpdating = true;}
		
		if (thumbnailNeedsUpdating) {
			OutputStream out = new FileOutputStream(output);
		
			if (!icon.mimeType.equals("image/gif")) {
				BufferedImage image = scale(ImageIO.read(input), w, h);

				ImageWriter writer =  ImageIO.getImageWritersByFormatName(icon.mimeType.split("/")[1]).next();
				ImageOutputStream ios = ImageIO.createImageOutputStream(out);
				writer.setOutput(ios);

				ImageWriteParam param = writer.getDefaultWriteParam();
				if (param.canWriteCompressed()){
					param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					param.setCompressionQuality(0.8f);
				}

				writer.write(null, new IIOImage(image, null, null), param);

				out.close();
				ios.close();
				writer.dispose();
			}
			else {
				InputStream in = new FileInputStream(input);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				out.close();
				BufferedImage image = scale(ImageIO.read(output), w, h);
				ImageIO.write(image, "gif", output);
			}
		}
	}
	
	public void scaleImage(Icon icon) {
		if (icon.mimeType != null && icon.mimeType.split("/")[0].equals("image")) {
			int imgWidth = 0, imgHeight = 0, newWidth = 64, newHeight = 64;
			float scaleFactor = 1f;
			try {
				BufferedImage bImg = ImageIO.read(icon.file);
				imgWidth = bImg.getWidth();
				imgHeight = bImg.getHeight();
				
				if (imgWidth > newWidth && imgHeight > newHeight) {scaleFactor = Math.max(imgWidth/newWidth, imgHeight/newHeight);}
				
				newWidth = imgWidth;
				newHeight = imgHeight;
				newWidth = (int) (imgWidth/scaleFactor);
				newHeight = (int) (imgHeight/scaleFactor);
				compressImage(icon, newWidth, newHeight);
				
				icon.label.setIcon(new ImageIcon("thumbnails" + icon.path + icon.filename));
			}
			catch (Exception e) {
				System.out.println("Error reading file " + icon.path + icon.filename + ": " + e.getMessage());
			}
		}
	}
	
	public void terminate() {
		icons.clear();
	}
	
	public void run() {
		for (int i = 0; i < icons.size(); i++) {
			scaleImage(icons.get(i));
		}
	}
	
	public ThumbnailLoader(ArrayList<Icon> iconList) {
		icons = iconList;
	}
}
