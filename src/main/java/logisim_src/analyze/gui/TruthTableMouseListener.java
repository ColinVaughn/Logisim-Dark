/* Copyright (c) 2010, Carl Burch. License information is located in the
 * logisim_src.Main source code and at www.cburch.com/logisim/. */

package logisim_src.analyze.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import logisim_src.analyze.model.Entry;
import logisim_src.analyze.model.TruthTable;

class TruthTableMouseListener implements MouseListener {
	private int cellX;
	private int cellY;
	private Entry oldValue;
	private Entry newValue;
	
	public void mousePressed(MouseEvent event) {
	}
	public void mouseReleased(MouseEvent event) {
		TruthTablePanel source = (TruthTablePanel) event.getSource();
		TruthTable model = source.getTruthTable();
		int cols = model.getInputColumnCount() + model.getOutputColumnCount();
		int rows = model.getRowCount();
		if (cellX < 0 || cellY < 0 || cellX >= cols || cellY >= rows) return;

		int x = source.getOutputColumn(event);
		int y = source.getRow(event);
		TruthTable table = source.getTruthTable();
		if (x == cellX && y == cellY) {
			table.setOutputEntry(y, x, newValue);
		}
		source.setEntryProvisional(cellY, cellX, null);
		cellX = -1;
		cellY = -1;
	}

	public void mouseClicked(MouseEvent event) {
		TruthTablePanel source = (TruthTablePanel) event.getSource();
		TruthTable model = source.getTruthTable();
		int cols = model.getInputColumnCount() + model.getOutputColumnCount();
		int rows = model.getRowCount();
		cellX = source.getOutputColumn(event);
		cellY = source.getRow(event);
		if (event.getButton() == MouseEvent.BUTTON1) {
			 newValue = Entry.ONE;
			source.setEntryProvisional(cellY, cellX, newValue);
		}
		else if (event.getButton()==MouseEvent.BUTTON2) {
			newValue=Entry.DONT_CARE;
			source.setEntryProvisional(cellY, cellX, newValue);

		}
		else if (event.getButton() == MouseEvent.BUTTON3) {
			newValue = Entry.ZERO;
			source.setEntryProvisional(cellY, cellX, newValue);
		}


	}
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { }

}
