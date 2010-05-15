package com.daohoangson.uml.gui;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 * The observer designed specifically to use with
 * {@link Diagram#saveImage(ImageObserver)}. This will save the image to a file
 * on disk.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class DiagramImageObserver implements ImageObserver {
	private String imagepath;

	/**
	 * Constructor. Accepts a path to save the image when it's ready
	 * 
	 * @param imagepath
	 *            the target file to save to
	 */
	public DiagramImageObserver(String imagepath) {
		String last4 = imagepath.substring(imagepath.length() - 4)
				.toLowerCase();
		if (!last4.equals(".jpg") && !last4.equals(".png")) {
			imagepath += ".png";
		}

		this.imagepath = imagepath;
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height) {
		try {
			ImageIO.write((RenderedImage) img, imagepath.substring(
					imagepath.length() - 3).toLowerCase(), new File(imagepath));
			return true;
		} catch (IOException e) {
			// simply ignore
		}

		return false;
	}

}

/**
 * A file filter which accepts .jpg and .png files only
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class DiagramImageFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String path = f.getAbsolutePath();
		String ext = path.substring(path.length() - 4).toLowerCase();
		return ext.equals(".jpg") || ext.equals(".png");
	}

	@Override
	public String getDescription() {
		return "Supported Image Formats (.JPG, .PNG)";
	}

}