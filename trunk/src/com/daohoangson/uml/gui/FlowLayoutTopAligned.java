package com.daohoangson.uml.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * A custom manager. Everything it does is arranging all components in 1 row,
 * top align. Simple enough!
 * 
 * @author Dao Hoang Son
 * 
 * @version 1.0
 * 
 */
class FlowLayoutTopAligned extends FlowLayout {
	private static final long serialVersionUID = 7364915589199056759L;
	private boolean inclusive;

	public FlowLayoutTopAligned(int hgap, int vgap, boolean inclusive) {
		super(FlowLayout.CENTER, hgap, vgap);

		this.inclusive = inclusive;
	}

	@Override
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			int nmembers = target.getComponentCount();
			int x = 0;
			int y = 0;
			boolean ltr = target.getComponentOrientation().isLeftToRight();

			Dimension requiredSize = FlowLayoutTopAligned
					.calculateRequiredSize(target, inclusive, getHgap(),
							getVgap());
			Dimension targetSize = target.getSize();
			if (requiredSize.width < targetSize.width) {
				x = (targetSize.width - requiredSize.width) / 2;
			}
			if (requiredSize.height < targetSize.height) {
				y = (targetSize.height - requiredSize.height) / 2;
			}
			if (inclusive) {
				x = Math.max(x, getHgap());
			}
			if (inclusive) {
				y = Math.max(y, getVgap());
			}

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getSize();

					if (i > 0) {
						x += getHgap();
					}
					if (ltr) {
						m.setLocation(x, y);
					} else {
						m.setLocation(targetSize.width - x - d.width, y);
					}

					x += d.width;
				}
			}
		}
	}

	/**
	 * The helper method which can calculate the required size of a container to
	 * display all of its' children nice and neat using this Layout Manager
	 * 
	 * @param target
	 *            the container
	 * @param inclusive
	 *            the container includes a border or not
	 * @param hgap
	 *            the horizontal gap
	 * @param vgap
	 *            the vertical gap
	 * @return the computed size
	 */
	public static Dimension calculateRequiredSize(Container target,
			boolean inclusive, int hgap, int vgap) {
		synchronized (target.getTreeLock()) {
			int nmembers = target.getComponentCount();
			int required_width = 0;
			int required_height = 0;
			int visibles = 0;

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					m.setSize(d);
					required_width += d.width;
					required_height = Math.max(required_height, d.height);
					visibles++;
				}
			}
			required_width += (visibles - 1) * hgap;
			if (visibles > 0 && inclusive) {
				required_width += 2 * hgap;
				required_height += 2 * vgap;
			}

			Dimension required = new Dimension(required_width, required_height);
			return required;
		}
	}
}
