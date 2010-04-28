package com.daohoangson.uml.gui;

import java.awt.Graphics;

import com.daohoangson.uml.structures.Structure;

public interface Relationship {
	public Structure getFrom();
	public Structure getTo();
	public void draw(Graphics g);
}
