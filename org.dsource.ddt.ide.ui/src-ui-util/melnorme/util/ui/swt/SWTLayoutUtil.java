/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package melnorme.util.ui.swt;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;


/**
 * SWT Layout Utils.
 * See also org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil
 */
public class SWTLayoutUtil {

	/**
	 * Sets the span of a control. Assumes that GridData is used.
	 */
	public static void setHorizontalSpan(Control control, int span) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).horizontalSpan = span;
		} else if (span != 1) {
			GridData gd = new GridData();
			gd.horizontalSpan = span;
			control.setLayoutData(gd);
		}
	}

	/**
	 * Sets the width hint of a control. Assumes that GridData is used.
	 */
	public static void setWidthHint(Control control, int widthHint) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).widthHint = widthHint;
		}
	}

	/**
	 * Sets the heightHint hint of a control. Assumes that GridData is used.
	 */
	public static void setHeightHint(Control control, int heightHint) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).heightHint = heightHint;
		}
	}

	/**
	 * Sets the horizontal indent of a control. Assumes that GridData is used.
	 */
	public static void setHorizontalIndent(Control control, int horizontalIndent) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).horizontalIndent = horizontalIndent;
		}
	}

	/** Enables the horizontal grabbing of a control, if GridData is used. */
	public static void enableHorizontalGrabbing(Control control) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).grabExcessHorizontalSpace = true;
		}
	}

	/** Enables the vertical grabbing of a control, if GridData is used. */
	public static void enableVerticalGrabbing(Control control) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).grabExcessVerticalSpace = true;
		}
	}

	/*** Enables vertical and horizontal grabbing of a control, if GridData is
	 * used. */
	public static void enableDiagonalGrabbing(Control control) {
		Object ld = control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).grabExcessHorizontalSpace = true;
			((GridData) ld).grabExcessVerticalSpace = true;
		}
	}


	/** Enables a diagonal expand GridData (grab and fill in both directions).
	 *  Creates a GridData if one doesn't exist already. */
	public static void enableDiagonalExpand(Control control) {
		GridData gd = getGD(control);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
	}
	
	/** Sets horizontal and vertical grabbing.
	 *  Creates a GridData if one doesn't exist already. */
	public static void setHVGrabbing(Control control, boolean grabHorizontal, boolean grabVertical) {
		GridData gd = getGD(control);
		gd.grabExcessHorizontalSpace = grabHorizontal;
		gd.grabExcessVerticalSpace = grabVertical;
	}
	

	/** Get's the control's GridData. Creates one if one doesn't exist already. */
	public static GridData getGD(Control control) {
		Object ld = control.getLayoutData();
		GridData gd;
		if(ld == null) {
			gd = new GridData();
			control.setLayoutData(gd);
		} else {
			assertTrue(ld instanceof GridData);
			gd = (GridData) ld;
		}
		return gd;
	}


}
