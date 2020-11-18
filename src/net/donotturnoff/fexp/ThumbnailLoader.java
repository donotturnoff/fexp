package net.donotturnoff.fexp;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import javax.imageio.*;
import java.awt.image.*;
import javax.imageio.stream.ImageOutputStream;

import java.awt.geom.AffineTransform;
import java.util.Set;

public class ThumbnailLoader extends Thread {
	private final Set<Icon> icons;

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
		return gc.createCompatibleImage(w, h);
	}
	
	private void compressImage(Icon icon, int w, int h) throws IOException {
		File input = icon.getFile();
		
		File outDirs = new File("thumbnails" + icon.getPath());
		if (!outDirs.exists()) {
			outDirs.mkdirs();
		}

		File output = new File("thumbnails" + icon.getPath() + icon.getFilename());
		
		boolean thumbnailNeedsUpdating = false;
		
		if (output.exists()) {
			if (input.lastModified() > output.lastModified()) {thumbnailNeedsUpdating = true;}
		}
		else {thumbnailNeedsUpdating = true;}
		
		if (thumbnailNeedsUpdating) {
			OutputStream out = new FileOutputStream(output);
		
			if (!icon.getMimeType().equals("image/gif")) {
				BufferedImage image = scale(ImageIO.read(input), w, h);

				ImageWriter writer =  ImageIO.getImageWritersByFormatName(icon.getMimeType().split("/")[1]).next();
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
		if (icon.getMimeType() != null && icon.getMimeType().split("/")[0].equals("image")) {
			int imgWidth, imgHeight, newWidth = 64, newHeight = 64;
			float scaleFactor = 1f;
			try {
				BufferedImage bImg = ImageIO.read(icon.getFile());
				imgWidth = bImg.getWidth();
				imgHeight = bImg.getHeight();
				
				if (imgWidth > newWidth && imgHeight > newHeight) {scaleFactor = Math.max(imgWidth/newWidth, imgHeight/newHeight);}

				newWidth = (int) (imgWidth/scaleFactor);
				newHeight = (int) (imgHeight/scaleFactor);
				compressImage(icon, newWidth, newHeight);
				
				icon.getLabel().setIcon(new ImageIcon("thumbnails" + icon.getPath() + icon.getFilename()));
			}
			catch (Exception e) {
				System.out.println("Error reading file " + icon.getPath() + icon.getFilename() + ": " + e.getMessage());
			}
		}
	}
	
	public void terminate() {
		icons.clear();
	}
	
	public void run() {
		for (Icon icon : icons) {
			scaleImage(icon);
		}
	}
	
	public ThumbnailLoader(Set<Icon> icons) {
		this.icons = icons;
	}
}
