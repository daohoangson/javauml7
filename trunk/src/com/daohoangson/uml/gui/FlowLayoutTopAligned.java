package com.daohoangson.uml.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * A custom manager. Everything it does is arranging
 * all components in 1 row, top align. Simple enough!
 * @author Dao Hoang Son
 * 
 * @version 1.0
 *
 */
class FlowLayoutTopAligned extends FlowLayout {
	private static final long serialVersionUID = 7364915589199056759L;
	
	public FlowLayoutTopAligned(int hgap, int vgap) {
		super(FlowLayout.CENTER,hgap,vgap);
	}

	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
	        int nmembers = target.getComponentCount();
	        int x = 0;
	        int y = getVgap();
	        boolean ltr = target.getComponentOrientation().isLeftToRight();
			
			for (int i = 0 ; i < nmembers ; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					m.setSize(d);
					
					x += getHgap();
					
					if (ltr) {
						m.setLocation(x, y);
					} else {
						m.setLocation(target.getWidth() - x - m.getWidth(), y);
					}
					
					x += d.width;
				}
			}
		}
	}
}
