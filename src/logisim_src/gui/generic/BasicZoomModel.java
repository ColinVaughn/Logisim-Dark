/* Copyright (c) 2010, Carl Burch. License information is located in the
 * logisim_src.Main source code and at www.cburch.com/logisim/. */

package logisim_src.gui.generic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import logisim_src.gui.generic.ZoomModel;
import logisim_src.prefs.PrefMonitor;

public class BasicZoomModel implements ZoomModel {
	private double[] zoomOptions;
	
	private PropertyChangeSupport support;
	private double zoomFactor;
	private boolean showGrid;
	
	public BasicZoomModel(PrefMonitor<Boolean> gridPref,
			PrefMonitor<Double> zoomPref, double[] zoomOpts) {
		zoomOptions = zoomOpts;
		support = new PropertyChangeSupport(this);
		zoomFactor = 1.0;
		showGrid = true;

		setZoomFactor(zoomPref.get().doubleValue());
		setShowGrid(gridPref.getBoolean());
	}

	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		support.addPropertyChangeListener(prop, l);
	}

	public void removePropertyChangeListener(String prop,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}

	public boolean getShowGrid() {
		return showGrid;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public double[] getZoomOptions() {
		return zoomOptions;
	}

	public void setShowGrid(boolean value) {
		if (value != showGrid) {
			showGrid = value;
			support.firePropertyChange(SHOW_GRID, !value, value);
		}
	}

	public void setZoomFactor(double value) {
		double oldValue = zoomFactor;
		if (value != oldValue) {
			zoomFactor = value;
			support.firePropertyChange(ZOOM, Double.valueOf(oldValue),
					Double.valueOf(value));
		}
	}
}
