package com.daohoangson.uml.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.daohoangson.uml.structures.Structure;

/**
 * The transferable class to use with Drag and Drop functionality. There is a
 * public static DataFlavor which can be used with ease.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
class TransferableStructure implements Transferable {
	private Structure structure;
	static DataFlavor df = new DataFlavor(Structure.class, "UML Structure");

	TransferableStructure(Structure structure) {
		this.structure = structure;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(TransferableStructure.df)) {
			return structure;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] dfs = new DataFlavor[1];
		dfs[0] = TransferableStructure.df;
		return dfs;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(TransferableStructure.df);
	}

}
