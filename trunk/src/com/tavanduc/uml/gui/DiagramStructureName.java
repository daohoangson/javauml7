package com.tavanduc.uml.gui;

import java.awt.Font;

import javax.swing.JLabel;

import com.daohoangson.uml.gui.StructureBased;
import com.daohoangson.uml.structures.Structure;

/**
 * Primary component for {@linkplain Structure structure}s
 * 
 * @author Dao Hoang Son
 * @version 1.2
 * 
 */
public class DiagramStructureName extends JLabel implements StructureBased {
	private static final long serialVersionUID = -6281032125570028093L;
	/**
	 * The corresponding structure of the label
	 */
	private Structure structure;

	/**
	 * Constructor. Accepts a structure. Creates the label using
	 * <code>JLabel</code> constructor. Also prepares itself ready to be
	 * dragged.
	 * 
	 * @param structure
	 */
	public DiagramStructureName(Structure structure, float size_factor) {
		super(structure.toString());

		this.structure = structure;

		if (structure.checkCanHaveChildren()) {
			setFont(getFont().deriveFont(Font.BOLD, (12 * size_factor)));
		} else {
			setFont(getFont().deriveFont(Font.PLAIN, (10 * size_factor)));
		}
	}

	@Override
	public String toString() {
		return "DnDLabel of " + structure;
	}

	/**
	 * Gets the structure in associate with this label
	 * 
	 * @return the structure
	 */
	@Override
	public Structure getStructure() {
		return structure;
	}
}
