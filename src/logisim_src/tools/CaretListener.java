/* Copyright (c) 2010, Carl Burch. License information is located in the
 * logisim_src.Main source code and at www.cburch.com/logisim/. */

package logisim_src.tools;

public interface CaretListener {
	public void editingCanceled(CaretEvent e);
	public void editingStopped(CaretEvent e);
}
