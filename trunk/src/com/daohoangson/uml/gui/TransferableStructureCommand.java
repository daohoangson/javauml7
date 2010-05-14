package com.daohoangson.uml.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.nguyenthanhan.uml.gui.StructureForm;

/**
 * A structure command which is transferable. The transferable class to use with
 * Drag and Drop functionality. There is a public static DataFlavor which can be
 * used with ease.
 * 
 * @author Dao Hoang Son
 * @version 1.0
 * 
 */
public class TransferableStructureCommand implements Transferable {
	/**
	 * The command needs transferring
	 */
	private String action;
	/**
	 * A data flavor for structure transferring
	 */
	static public DataFlavor df = new DataFlavor(StructureForm.class,
			"UML Structure Form");

	public TransferableStructureCommand(String action) {
		this.action = action;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(TransferableStructureCommand.df)) {
			return action;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] dfs = { TransferableStructureCommand.df };
		return dfs;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(TransferableStructureCommand.df);
	}

}
