package melnorme.util.ui.fields;

import melnorme.swtutil.GridComposite;
import melnorme.util.ui.swt.SWTLayoutUtil;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class FieldUtil {

	/** Calculates the number of columns needed by field editors */
	public static int getNumberOfColumns(DialogField[] editors) {
		int nCulumns = 0;
		for (int i = 0; i < editors.length; i++) {
			nCulumns = Math.max(editors[i].getNumberOfControls(), nCulumns);
		}
		return nCulumns;
	}

	/** Fills in the given editors in the parent Composite. 
	 *  Sets a layout with no margins no margins.
	 */
	public static void doDefaultLayout(Composite parent, DialogField[] editors,
			boolean labelOnTop) {
		FieldUtil.doDefaultLayout(parent, editors, labelOnTop, 0, 0);
	}

	/** Fills in the given editors in the parent Composite.
	 * @param labelOnTop Defines if the label of all fields should be on top of
	 *            the fields
	 * @param marginWidth The margin width to be used by the composite
	 * @param marginHeight The margin height to be used by the composite
	 */
	public static void doDefaultLayout(Composite parent, DialogField[] editors,
			boolean labelOnTop, int marginWidth, int marginHeight) {
		int nCulumns = getNumberOfColumns(editors);
		Control[][] controls = new Control[editors.length][];
		for (int i = 0; i < editors.length; i++) {
			controls[i] = editors[i].doFillIntoGrid(parent, nCulumns);
		}
		if (labelOnTop) {
			nCulumns--;
			modifyLabelSpans(controls, nCulumns);
		}
		GridLayout layout = null;
		if (parent.getLayout() instanceof GridLayout) {
			layout = (GridLayout) parent.getLayout();
		} else {
			layout = new GridLayout();
		}
		if (marginWidth != SWT.DEFAULT) {
			layout.marginWidth = marginWidth;
		}
		if (marginHeight != SWT.DEFAULT) {
			layout.marginHeight = marginHeight;
		}
		layout.numColumns = nCulumns;
		parent.setLayout(layout);
	}

	private static void modifyLabelSpans(Control[][] controls, int nCulumns) {
		for (int i = 0; i < controls.length; i++) {
			SWTLayoutUtil.setHorizontalSpan(controls[i][0], nCulumns);
		}
	}

	/** Does {@link #doDefaultLayout(Composite, DialogField[], boolean)} .*/
	public static void doDefaultLayout2(Composite parent, boolean labelOnTop, DialogField... fields) {
		doDefaultLayout(parent, fields, labelOnTop);
	}

	/** Does {@link #doDefaultLayout(Composite, DialogField[], boolean, int, int)} . */
	public static void doDefaultLayout2(Composite parent, boolean labelOnTop,
			int marginWidth, int marginHeight, DialogField... fields) {
		doDefaultLayout(parent, fields, labelOnTop, marginWidth, marginHeight);
	}

	/** Creates a Composite and fills in the given fields. */
	public static Composite createCompose(Composite parent, boolean labelOnTop, DialogField field) {
		Composite content = new GridComposite(parent, field.getNumberOfControls());
		FieldUtil.doDefaultLayout2(content, labelOnTop, field);
		return content;
	}
}
