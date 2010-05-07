package com.daohoangson.uml.gui;

public class Diagram implements StructureListener {
	static private long serialVersionUID;
	private List<Structure> structures;
	private List<Relationship> relationships;
	private Component[] built;
	private boolean[][] dependencies;
	private Image image;
	private ImageObserver observer;
	static public boolean debuging;
	public boolean cfg_draw_on_change;
	private int cfg_gap_vertical;
	private int cfg_gap_horizontal;

	public Diagram() {
		// TODO Auto-generated constructor stub
	}

	public Component getScrollable() {
		// TODO Auto-generated method stub
	}

	public Structure[] getStructures() {
		// TODO Auto-generated method stub
	}

	public void saveImage(ImageObserver observer) {
		// TODO Auto-generated method stub
	}

	public void add(Structure structure) {
		// TODO Auto-generated method stub
	}

	public void remove(Structure structure) {
		// TODO Auto-generated method stub
	}

	public void clear() {
		// TODO Auto-generated method stub
	}

	public void paint(Graphics g) {
		// TODO Auto-generated method stub
	}

	Component getComponentOf(Structure structure) {
		// TODO Auto-generated method stub
	}

	Rectangle getBound(Component c) {
		// TODO Auto-generated method stub
	}

	public void draw() {
		// TODO Auto-generated method stub
	}

	private Component build(int i) {
		// TODO Auto-generated method stub
	}

	Component build(Structure s) {
		// TODO Auto-generated method stub
	}

	JComponent wrap(Component c, Component[] dc) {
		// TODO Auto-generated method stub
	}

	private void prepare() {
		// TODO Auto-generated method stub
	}

	public void structureChanged(StructureEvent e) {
		// TODO Auto-generated method stub
	}
}