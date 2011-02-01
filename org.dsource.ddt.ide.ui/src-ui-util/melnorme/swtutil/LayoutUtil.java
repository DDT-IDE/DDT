/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.swtutil;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;


/**
 * SWT Layout Utils v2.
 */
public class LayoutUtil {
	
	public static GridLayout createGridLayout() {
		return createGridLayout(1);
	}
	
	public static GridLayout createGridLayout(int numColumns) {
		return createGridLayout(numColumns, true);
	}

	/** Creates a default GridLayout with given numColumns , margins, baseControl*/
	public static GridLayout createGridLayout(int numColumns, boolean margins) {
		GridLayout gd = new GridLayout(numColumns, false);
		if(!margins) {
			gd.marginWidth = 0;
			gd.marginHeight = 0;
		}
		return gd;
	}
	
	/* ================ Layout Data Utils ================ */
	
	
	protected static GridData getControlGridData(Control control) {
		Object ld = control.getLayoutData();
		assertTrue(ld instanceof GridData);
		return (GridData) ld;
	}

	public static Point calcSimpleSizeMetrics(Control control) {
		Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return size;
	}
	
	public static Point calcSimpleSizeMetrics(Control control, double widthRatio, double heightRatio) {
		Point size = calcSimpleSizeMetrics(control);
		size.x = (int) (size.x * widthRatio); 
		size.y = (int) (size.y * heightRatio);
		return size;
	}

	
	public static Control setGridDataSize(Control control, int widthHint, int heightHint) {
		GridData gd = getControlGridData(control);
		gd.widthHint = widthHint;
		gd.heightHint = heightHint;
		return control;
	}
	
	
	public static Control setGridDataHeight(Control control, int heightHint) {
		GridData gd = getControlGridData(control);
		gd.heightHint = heightHint;
		return control;
	}
	
	public static Control setGridDataWidth(Control control, int widthHint) {
		GridData gd = getControlGridData(control);
		gd.widthHint = widthHint;
		return control;
	}
	
	
	
	public static Control setGridDataSize(Control control, Point sizeHint) {
		return setGridDataSize(control, sizeHint.x, sizeHint.y);
	}

	public static GridData newRowGridData() {
		return newGridData(true);
	}

	
	public static GridData newGridData() {
		return newHGridData(1, false, false);
	}
	
	public static GridData newGridData(boolean grabHorizontal) {
		return newHGridData(1, grabHorizontal);
	}
	
	public static GridData newGridData(boolean grabHorizontal, boolean grabVertical) {
		return newHGridData(1, grabHorizontal, grabVertical);
	}
	
	public static GridData newGridData(int widthHint, int heightHint) {
		return newHGridData(1, widthHint, heightHint);
	}
	
	
	
	public static GridData newHGridData(int horizontalSpan) {
		return newHGridData(horizontalSpan, false, false);
	}

	public static GridData newHGridData(int horizontalSpan, boolean grabHorizontal) {
		return newHGridData(horizontalSpan, grabHorizontal, false);
	}

	public static GridData newHGridData(int horizontalSpan, boolean grabHorizontal,
			boolean grabVertical) {
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = grabHorizontal;
		gd.grabExcessVerticalSpace = grabVertical;
		return gd;
	}

	public static GridData newHGridData(int horizontalSpan, int widthHint, int heightHint) {
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.widthHint = widthHint;
		gd.heightHint = heightHint;
		return gd;
	}

}
