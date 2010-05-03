package com.daohoangson.uml.gui;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The observer designed specifically to use with
 * {@link Diagram#saveImage(ImageObserver)}. This will save the image to a file
 * on disk.
 * 
 * @author Dao Hoang Son
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
		} catch (IOException e) {
			// simply ignore
		}

		return false;
	}

}