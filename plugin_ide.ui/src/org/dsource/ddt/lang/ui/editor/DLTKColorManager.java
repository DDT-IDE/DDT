package org.dsource.ddt.lang.ui.editor;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public final class DLTKColorManager implements IColorManager {
	public final org.eclipse.cdt.ui.text.IColorManager colorManager2;
	
	DLTKColorManager(org.eclipse.cdt.ui.text.IColorManager colorManager2) {
		this.colorManager2 = colorManager2;
	}
	
	@Override
	public Color getColor(RGB rgb) {
		return colorManager2.getColor(rgb);
	}
	
	@Override
	public Color getColor(String key) {
		return colorManager2.getColor(key);
	}
	
	@Override
	public void dispose() {
		colorManager2.dispose();
	}
}