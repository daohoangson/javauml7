package com.daohoangson.uml.structures;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class TransferableStructure implements Transferable {
	private Structure structure;
	public static DataFlavor df = new DataFlavor(Structure.class,"UML Structure");;
	
	public TransferableStructure(Structure structure) {
		this.structure = structure;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(df)) {
			return structure;
		} else throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] dfs = new DataFlavor[1];
		dfs[0] = df;
		return dfs;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(df);
	}

}
