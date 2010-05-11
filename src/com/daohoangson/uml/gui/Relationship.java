package com.daohoangson.uml.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.daohoangson.uml.structures.Structure;

/**
 * Base Relationship class. No instance of this class should be created.
 * 
 * @author Dao Hoang Son
 * @version 1.2
 * 
 */
abstract public class Relationship {
	/**
	 * The original diagram
	 */
	private Diagram diagram;
	/**
	 * The 2 structures involved in this relationship
	 */
	private Structure from, to;
	/**
	 * The color to be drawn
	 */
	protected Color cfg_color = Color.BLACK;
	/**
	 * The length of dashes. SPecifies 0 to draw straight line (no dash)
	 */
	protected int cfg_dash_length = 0;
	/**
	 * The distance between lines and components
	 */
	protected int cfg_distance = 5;
	/**
	 * Determines if we are in debug mode.
	 */
	static public boolean debugging = false;

	/**
	 * Constructor
	 * 
	 * @param diagram
	 *            the root diagram
	 * @param from
	 *            the source structure
	 * @param to
	 *            the destination structure
	 */
	public Relationship(Diagram diagram, Structure from, Structure to) {
		this.diagram = diagram;
		this.from = from;
		this.to = to;
	}

	/**
	 * Gets the source structure
	 * 
	 * @return
	 */
	public Structure getFrom() {
		return from;
	}

	/**
	 * Gets the destination structure
	 * 
	 * @return
	 */
	public Structure getTo() {
		return to;
	}

	/**
	 * Customized primary drawing method for each relationships. Must be
	 * overridden in subclasses and should call
	 * {@link #drawConnectingPath(Graphics, double, double)} with appropriate
	 * arguments
	 * 
	 * @param g
	 *            the target <code>Graphics</code> to be drawn
	 */
	abstract public void draw(Graphics g);

	/**
	 * Customized drawing method for each relationship. Must be overridden in
	 * subclasses.
	 * 
	 * @param g
	 *            the target graphics (the same passed in
	 *            {@link #draw(Graphics)}
	 * @param ps
	 *            the {@link PointSet} object holding drawing data
	 */
	abstract protected void drawEndPoints(Graphics g, PointSet ps);

	/**
	 * Selects and draws the main connection line(s). Finally, draw end points
	 * by calling subclass's {@link #drawEndPoints(Graphics, PointSet)}
	 * 
	 * @param g
	 *            the target graphics
	 * @param start_length
	 *            the length should be left at the start
	 * @param end_length
	 *            the length should be left at the end
	 */
	protected void drawConnectingPath(Graphics g, double start_length,
			double end_length) {
		if (Relationship.debugging) {
			System.err.println("Drawing from " + getFrom() + " to " + getTo());
		}

		Rectangle fromBound = diagram.getBoundsFor(getFrom());
		Rectangle toBound = diagram.getBoundsFor(getTo());
		if (fromBound == null || toBound == null) {
			return;
		}

		PointSet ps;
		Color original_color = g.getColor();
		g.setColor(cfg_color);

		if (fromBound.equals(toBound)) {
			// fromBound is similar to toBound
			// this happens sometimes
			int x1 = fromBound.x + fromBound.width;
			int y1 = fromBound.y + fromBound.height / 3 * 2;
			int x2 = (int) (x1
					+ Math.max(Math.max(start_length, end_length), 15) + 5);
			int y2 = y1;
			int x4 = fromBound.x + fromBound.width;
			int y4 = fromBound.y + fromBound.height / 3;
			int x3 = x2;
			int y3 = y4;

			drawLine(g, x1, y1, x2, y2, start_length, 0);
			drawLine(g, x2, y2, x2, y3, 0, 0);
			ps = drawLine(g, x3, y3, x4, y4, 0, end_length);
		} else if (fromBound.y + fromBound.height < toBound.y) {
			// fromBound is higher than toBound
			int x1 = fromBound.x + fromBound.width / 2;
			int y1 = fromBound.y + fromBound.height;

			int x3;
			if (fromBound.x < toBound.x) {
				x3 = toBound.x;
			} else {
				x3 = toBound.x + toBound.width;
			}
			int y3 = toBound.y + toBound.height / 2;

			int x2 = x1;
			int y2 = y3;

			drawLine(g, x1, y1, x2, y2, start_length, 0);
			ps = drawLine(g, x2, y2, x3, y3, 0, end_length);
		} else if (fromBound.y > toBound.y + toBound.height) {
			// fromBound is lower than toBound
			int x1 = fromBound.x + fromBound.width / 2;
			int y1 = fromBound.y;
			int x2 = toBound.x + toBound.width / 2;
			int y2 = toBound.y + toBound.height;

			ps = drawPath(g, x1, y1, x2, y2, start_length, end_length, 30);
		} else {
			// the 2 bounds is quite on the same level
			int x1, y1, x2, y2;

			if (fromBound.x + fromBound.width < toBound.x) {
				// fromBound is on the left side
				x2 = toBound.x;
				y2 = toBound.y + toBound.height / 2;
				x1 = fromBound.x + fromBound.width;
				y1 = y2;
			} else {
				// fromBound is on the right side
				x2 = toBound.x + toBound.width;
				y2 = toBound.y + toBound.height / 2;
				x1 = fromBound.x;
				y1 = y2;
			}

			if (fromBound.y + 1 < y1 && y1 < fromBound.y + fromBound.height - 1) {
				ps = drawLine(g, x1, y1, x2, y2, start_length, end_length);
			} else {
				// we want to use the x from toBound in order to draw a
				// straight line but it's out of fromBound
				// so we will have to change it to draw a path instead
				x1 = fromBound.x + fromBound.width / 2;
				if (y1 < fromBound.y) {
					y1 = fromBound.y;
				} else {
					y1 = fromBound.y + fromBound.height;
				}

				ps = drawPath(g, x1, y1, x2, y2, start_length, end_length, 20);
			}
		}

		drawEndPoints(g, ps);
		g.setColor(original_color);
	}

	/**
	 * Calculates and draw a straight line from 2 points (must be a vertical
	 * line or a horizontal line)
	 * 
	 * @param g
	 *            the <code>Graphics</code> object to be drawn
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * @param start_length
	 *            the length to be left at the start
	 * @param end_length
	 *            the length to be left at the end
	 * @return a {@link PointSet} containing left lines and the delta angles
	 */
	private PointSet drawLine(Graphics g, int x1, int y1, int x2, int y2,
			double start_length, double end_length) {
		if (Relationship.debugging) {
			System.err.println("Drawing from " + x1 + "," + y1 + " to " + x2
					+ "," + y2 + ". Start = " + start_length + ". End = "
					+ end_length);
		}

		double delta;
		int x3, y3, x4, y4;

		if (y1 != y2) {
			if (x1 < x2) {
				delta = Math.atan((x2 - x1) / (double) (y2 - y1)) + Math.PI;
			} else {
				delta = Math.atan((x2 - x1) / (double) (y2 - y1));
			}
		} else {
			if (x1 < x2) {
				delta = Math.PI * 0.5;
			} else {
				delta = Math.PI * 0.5 * -1;
			}
		}

		x3 = (int) Math.ceil(x1 - start_length * Math.sin(delta));
		y3 = (int) Math.ceil(y1 + start_length * Math.cos(delta));

		x4 = (int) Math.ceil(x2 - end_length * Math.sin(delta));
		y4 = (int) Math.ceil(y2 + end_length * Math.cos(delta));

		drawLineSmart(g, x3, y3, x4, y4);

		if (Relationship.debugging) {
			System.err.println("Drawing from " + x3 + "," + y3 + " to " + x4
					+ "," + y4 + ". Delta = " + delta);
		}

		return new PointSet(x1, y1, x3, y3, delta, x4, y4, x2, y2, delta);
	}

	/**
	 * Draws a straight line betwen 2 points but avoid all components on the way
	 * 
	 * @param g
	 *            the <code>Graphics</code> object to be drawn
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * 
	 * @see #onTheWay(Rectangle[], int, int, int, int)
	 */
	private void drawLineSmart(Graphics g, int x1, int y1, int x2, int y2) {
		if (x1 == x2 && y1 == y2) {
			// oops, some kind of pseudo line?
			return;
		}

		Rectangle[] onTheWay = Relationship.onTheWay(diagram.getBoundsForAll(),
				x1, y1, x2, y2);

		int x = x1;
		int y = y1;

		for (int i = 0; i < onTheWay.length; i++) {
			Rectangle rect = onTheWay[i];

			if (x1 == x2) {
				// vertical line
				int p1y, p2y, p3y;
				int px = rect.x - cfg_distance;
				boolean use3legs = true;
				if (y1 < y2) {
					// coming from above
					p1y = y;
					p2y = rect.y - cfg_distance;
					p3y = rect.y + rect.height + cfg_distance;
				} else {
					// coming from below
					p1y = y;
					p2y = rect.y + rect.height + cfg_distance;
					p3y = rect.y - cfg_distance;
				}
				if (Relationship.isBetween(p3y, y1, y2)) {
					// oops
					// this rectangle is at one of the end points
					// use 2 leg drawing now
					use3legs = false;
				}

				if (use3legs) {
					// normal case
					drawLinePlain(g, x, p1y, x, p2y);
					drawLinePlain(g, x, p2y, px, p2y);
					drawLinePlain(g, px, p2y, px, p3y);
					drawLinePlain(g, px, p3y, x, p3y);
					y = p3y; // update the current position
				} else {
					// 2 legs only
					drawLinePlain(g, x, y, x, p3y);
					y = p3y; // update the current position
				}
			} else {
				// horizontal line
				int p1x, p2x, p3x;
				int py = rect.y - cfg_distance;
				boolean use3legs = true;
				if (x1 < x2) {
					// coming from the left
					p1x = x;
					p2x = rect.x - cfg_distance;
					p3x = rect.x + rect.width + cfg_distance;
				} else {
					// coming from the right
					p1x = x;
					p2x = rect.x + rect.width + cfg_distance;
					p3x = rect.x - cfg_distance;
				}

				if (!Relationship.isBetween(p3x, x1, x2)) {
					// oops
					// this rectangle is at one of the end points
					// use 2 leg drawing now
					use3legs = false;
				}

				if (use3legs) {
					// normal case
					drawLinePlain(g, p1x, y, p2x, y);
					drawLinePlain(g, p2x, y, p2x, py);
					drawLinePlain(g, p2x, py, p3x, py);
					drawLinePlain(g, p3x, py, p3x, y);
					x = p3x; // update the current position
				} else {
					// 2 legs only
					// TODO
					drawLinePlain(g, x, y, p3x, y);
					x = p3x; // update the current position
				}
			}
		}

		if (x != x2 || y != y2) {
			drawLinePlain(g, x, y, x2, y2);
		}
	}

	/**
	 * Draws a straight line or a dashed line between 2 points
	 * 
	 * @param g
	 *            the <code>Graphics</code> object to be drawn
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * 
	 * @see #cfg_dash_length
	 */
	private void drawLinePlain(Graphics g, int x1, int y1, int x2, int y2) {
		if (cfg_dash_length == 0) {
			g.drawLine(x1, y1, x2, y2);
		} else {
			int xx, yy, x, y;

			if (x1 == x2) {
				// vertical line
				xx = 0;
				yy = cfg_dash_length;
				if (y1 > y2) {
					yy *= -1;
				}
			} else {
				// horizontal line
				xx = cfg_dash_length;
				yy = 0;
				if (x1 > x2) {
					xx *= -1;
				}
			}

			x = x1;
			y = y1;

			boolean space = false;

			while (Relationship.isBetween(x, x1, x2)
					&& Relationship.isBetween(y, y1, y2)) {
				int xnext = x + xx;
				int ynext = y + yy;
				if (Relationship.isBetween(xnext, x1, x2)
						&& Relationship.isBetween(ynext, y1, y2)) {
					if (space) {
						g.drawLine(x, y, xnext, ynext);
					}
				}
				x = xnext;
				y = ynext;
				space = !space;
			}
		}
	}

	/**
	 * Draws the path between 2 points
	 * 
	 * @param g
	 *            the <code>Graphics</code> object to be drawn
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * @param start_length
	 *            the length to be left at the start
	 * @param end_length
	 *            the length to be left at the end
	 * @param mode
	 *            the mode for path. Possible values are
	 *            <ul>
	 *            <li>30: 3 lines. Go half the height before turning</li>
	 *            <li>31: 3 lines. Go half the width before turning</li>
	 *            <li>20: 2 lines. Keep the x coordinate as long as possible</li>
	 *            <li>21: 2 lines. Keep the y coordinate as long as possible</li>
	 *            </ul>
	 * @return a {@link PointSet} containing left lines and the delta angles
	 */
	private PointSet drawPath(Graphics g, int x1, int y1, int x2, int y2,
			double start_length, double end_length, int mode) {
		int tmp_x1, tmp_y1, tmp_x2, tmp_y2;
		switch (mode) {
		case 30:
			tmp_x1 = x1;
			tmp_y1 = (y1 + y2) / 2;
			tmp_x2 = x2;
			tmp_y2 = tmp_y1;
			break;
		case 31:
			tmp_x1 = (x1 + x2) / 2;
			tmp_y1 = y1;
			tmp_x2 = tmp_x1;
			tmp_y2 = y2;
			break;
		case 20:
			tmp_x1 = x1;
			tmp_y1 = y2;
			tmp_x2 = tmp_x1;
			tmp_y2 = tmp_y1;
			break;
		case 21:
		default:
			tmp_x1 = x2;
			tmp_y1 = y1;
			tmp_x2 = tmp_x1;
			tmp_y2 = tmp_y1;
			break;
		}

		PointSet p1 = drawLine(g, x1, y1, tmp_x1, tmp_y1, start_length, 0);
		drawLine(g, tmp_x1, tmp_y1, tmp_x2, tmp_y2, 0, 0);
		PointSet p2 = drawLine(g, tmp_x2, tmp_y2, x2, y2, 0, end_length);

		return new PointSet(x1, y1, p1.x2, p1.y2, p1.delta1, p2.x3, p2.y3, x2,
				y2, p2.delta2);
	}

	/**
	 * Checks if a number is between 2 other numbers
	 * 
	 * @param x
	 *            the number need checking
	 * @param x1
	 *            the range first edge
	 * @param x2
	 *            the range second edge
	 * @return true if it is between (inclusive)
	 */
	static private boolean isBetween(int x, int x1, int x2) {
		if (x1 > x2) {
			return x1 >= x && x >= x2;
		} else {
			return x1 <= x && x <= x2;
		}
	}

	/**
	 * Checks if a range of numbers is overlap another range
	 * 
	 * @param range1_x1
	 *            the first range first edge
	 * @param range1_x2
	 *            the first range second edge
	 * @param range2_x1
	 *            the second range first edge
	 * @param range2_x2
	 *            the second range second edge
	 * @return true if the 2 ranges are overlapped (not inclusive)
	 */
	static private boolean isOverlap(int range1_x1, int range1_x2,
			int range2_x1, int range2_x2) {
		if (range2_x1 < range2_x2) {
			range2_x1++;
			range2_x2--;
		} else {
			range2_x1--;
			range2_x2++;
		}

		return Relationship.isBetween(range1_x1, range2_x1, range2_x2)
				|| Relationship.isBetween(range1_x2, range2_x1, range2_x2);
	}

	/**
	 * Finds all rectangles on the way of a line between 2 points
	 * 
	 * @param rectangles
	 *            an array of rectangles to check against
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * @return an array of found rectangles
	 */
	static private Rectangle[] onTheWay(Rectangle[] rectangles, int x1, int y1,
			int x2, int y2) {
		List<Rectangle> found = new LinkedList<Rectangle>();

		for (int i = 0; i < rectangles.length; i++) {
			Rectangle rect = rectangles[i];
			int xmin = rect.x;
			int xmax = rect.x + rect.width;
			int ymin = rect.y;
			int ymax = rect.y + rect.height;
			boolean isOnTheWay = false;

			if (x1 == x2) {
				// check for x
				if (xmin < x1
						&& x1 < xmax
						&& Relationship.isOverlap(rect.y, rect.y + rect.height,
								y1, y2)) {
					isOnTheWay = true;
				}
			}
			if (y1 == y2) {
				// check for y
				if (ymin < y1
						&& y1 < ymax
						&& Relationship.isOverlap(rect.x, rect.x + rect.width,
								x1, x2)) {
					isOnTheWay = true;
				}
			}

			if (isOnTheWay) {
				found.add(rect);
			}
		}

		int comparator_direction;
		if (x1 < x2 || y1 < y2) {
			comparator_direction = 1;
		} else {
			comparator_direction = -1;
		}
		Collections.sort(found, new RectangleComparator(comparator_direction));

		if (Relationship.debugging) {
			System.err.println(x1 + "," + y1 + " ~> " + x2 + "," + y2 + ": "
					+ found);
		}

		return found.toArray(new Rectangle[0]);
	}
}

/**
 * A temporary class to hold some important data
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 * @see Relationship#drawConnectingPath(Graphics, double, double)
 * 
 */
class PointSet {
	int x1, y1, x2, y2, x3, y3, x4, y4;
	double delta1, delta2;

	PointSet(int x1, int y1, int x2, int y2, double delta1, int x3, int y3,
			int x4, int y4, double delta2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.delta1 = delta1;

		this.x3 = x3;
		this.y3 = y3;
		this.x4 = x4;
		this.y4 = y4;
		this.delta2 = delta2;
	}

	PointSet(int x1, int y1, int x2, int y2, double delta1) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.delta1 = delta1;

		x3 = x1;
		y3 = y1;
		x4 = x2;
		y4 = y2;
		delta2 = delta1;
	}
}

class RectangleComparator implements Comparator<Rectangle> {
	private int direction;

	public RectangleComparator(int direction) {
		this.direction = direction;
	}

	public RectangleComparator() {
		this(1);
	}

	@Override
	public int compare(Rectangle s1, Rectangle s2) {
		int result = cmp(s1.x, s2.x);
		if (result == 0) {
			result = cmp(s1.y, s2.y);
		}
		if (result == 0) {
			result = cmp(s1.width, s2.width);
		}
		if (result == 0) {
			result = cmp(s1.height, s2.height);
		}

		return result * direction;
	}

	private int cmp(int x, int y) {
		if (x == y) {
			return 0;
		} else if (x > y) {
			return 1;
		} else {
			return -1;
		}
	}
}
