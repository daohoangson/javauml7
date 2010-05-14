package com.daohoangson.uml.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.daohoangson.uml.structures.Structure;

/**
 * A structure which is transferable. The transferable class to use with Drag
 * and Drop functionality. There is a public static DataFlavor which can be used
 * with ease.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TransferableStructure implements Transferable {
	/**
	 * The structure needs transferring
	 */
	private Structure structure;
	/**
	 * A data flavor for structure transferring
	 */
	static public DataFlavor df = new DataFlavor(Structure.class,
			"UML Structure");

	public TransferableStructure(Structure structure) {
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
		DataFlavor[] dfs = { TransferableStructure.df };
		return dfs;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(TransferableStructure.df);
	}

}
