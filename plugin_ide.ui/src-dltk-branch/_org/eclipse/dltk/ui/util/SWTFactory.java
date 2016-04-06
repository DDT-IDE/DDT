package _org.eclipse.dltk.ui.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Factory class to create SWT resources.
 */
public class SWTFactory {

	/**
	 * Returns a width hint for a button control.
	 */
	public static int getButtonWidthHint(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter
				.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
				true).x);
	}

	/**
	 * Sets width and height hint for the button control. <b>Note:</b> This is a
	 * NOP if the button's layout data is not an instance of
	 * <code>GridData</code>.
	 * 
	 * @param the
	 *            button for which to set the dimension hint
	 */
	public static void setButtonDimensionHint(Button button) {
		Object gd = button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	public static Button createPushButtonNoLayoutData(Composite parent,
			String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (label != null) {
			button.setText(label);
		}
		return button;
	}

	public static Button createPushButton(Composite parent, String label) {
		return createPushButton(parent, label, null);
	}

	/**
	 * Creates and returns a new push button with the given label and/or image.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param image
	 *            image or <code>null</code>
	 * 
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label,
			Image image) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null) {
			button.setImage(image);
		}
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		SWTFactory.setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Creates and returns a new push button with the given label, tooltip
	 * and/or image.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param tooltip
	 *            the tooltip text for the button or <code>null</code>
	 * @param image
	 *            image of <code>null</code>
	 * 
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label,
			String tooltip, Image image) {
		Button button = createPushButton(parent, label, image);
		button.setToolTipText(tooltip);
		return button;
	}

	/**
	 * Creates and returns a new radio button with the given label.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * 
	 * @return a new radio button
	 */
	public static Button createRadioButton(Composite parent, String label) {
		return createRadioButton(parent, label, 1);
	}

	/**
	 * Creates and returns a new radio button with the given label.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param hspan
	 *            the number of columns to span in the parent composite
	 * 
	 * @return a new radio button
	 */
	public static Button createRadioButton(Composite parent, String label,
			int hspan) {
		Button button = new Button(parent, SWT.RADIO);
		button.setFont(parent.getFont());
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = hspan;
		button.setLayoutData(gd);
		SWTFactory.setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Creates and returns a new radio button with the given label.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * 
	 * @return a new radio button
	 */
	public static Button createRadioButtonNoLayoutData(Composite parent,
			String label) {
		Button button = new Button(parent, SWT.RADIO);
		button.setFont(parent.getFont());
		if (label != null) {
			button.setText(label);
		}
		return button;
	}

	/**
	 * Creates a check box button using the parents' font
	 * 
	 * @param parent
	 *            the parent to add the button to
	 * @param label
	 *            the label for the button
	 * @return a new check box button
	 */
	public static Button createCheckButton(Composite parent, String label) {
		return createCheckButton(parent, label, 1);
	}

	public static Button createCheckButton(Composite parent, String label,
			int hspan) {
		return createCheckButton(parent, label, null, false, hspan);
	}

	/**
	 * Creates a check box button using the parents' font
	 * 
	 * @param parent
	 *            the parent to add the button to
	 * @param label
	 *            the label for the button
	 * @param image
	 *            the image for the button
	 * @param checked
	 *            the initial checked state of the button
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return a new checked button set to the initial checked state
	 */
	public static Button createCheckButton(Composite parent, String label,
			Image image, boolean checked, int hspan) {
		Button button = new Button(parent, SWT.CHECK);
		button.setFont(parent.getFont());
		button.setSelection(checked);
		if (image != null) {
			button.setImage(image);
		}
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		gd.horizontalSpan = hspan;
		button.setLayoutData(gd);
		setButtonDimensionHint(button);
		return button;
	}

	/**
	 * Creates a new label widget
	 * 
	 * @param parent
	 *            the parent composite to add this label widget to
	 * @param text
	 *            the text for the label
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return the new label
	 */
	public static Label createLabel(Composite parent, String text, Font font,
			int hspan) {
		return createLabel(parent, text, 0, font, hspan);
	}

	public static Label createLabel(Composite parent, String text, int indent,
			Font font, int hspan) {
		Label l = new Label(parent, SWT.NONE);
		l.setFont(font);
		l.setText(text);

		GridData gd = new GridData();
		gd.horizontalSpan = hspan;
		gd.horizontalIndent = indent;

		l.setLayoutData(gd);
		return l;
	}

	public static Label createLabel(Composite parent, String text, int indent,
			int hspan) {
		return createLabel(parent, text, indent, parent.getFont(), hspan);
	}

	/**
	 * Creates a new label widget
	 * 
	 * @param parent
	 *            the parent composite to add this label widget to
	 * @param text
	 *            the text for the label
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return the new label
	 */
	public static Label createLabel(Composite parent, String text, int hspan) {
		return createLabel(parent, text, 0, hspan);
	}

	/**
	 * Creates a wrapping label
	 * 
	 * @param parent
	 *            the parent composite to add this label to
	 * @param text
	 *            the text to be displayed in the label
	 * @param hspan
	 *            the horizontal span that label should take up in the parent
	 *            composite
	 * @param wrapwidth
	 *            the width hint that the label should wrap at
	 * @return a new label that wraps at a specified width
	 */
	public static Label createWrapLabel(Composite parent, String text,
			int hspan, int wrapwidth) {
		Label l = createLabel(parent, text, 0, parent.getFont(), hspan);
		((GridData) l.getLayoutData()).widthHint = wrapwidth;

		return l;
	}

	/**
	 * Creates a new text widget
	 * 
	 * @param parent
	 *            the parent composite to add this text widget to
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @return the new text widget
	 */
	public static Text createSingleText(Composite parent, int hspan) {
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);
		t.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		t.setLayoutData(gd);
		return t;
	}

	/**
	 * Creates a new text widget
	 * 
	 * @param parent
	 *            the parent composite to add this text widget to
	 * @param style
	 *            the style bits for the text widget
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @param text
	 *            the initial text, not <code>null</code>
	 * @return the new text widget
	 */
	public static Text createText(Composite parent, int style, int hspan,
			String text) {
		return createText(parent, style, hspan, text, GridData.FILL_HORIZONTAL);
	}

	public static Text createText(Composite parent, int style, int hspan,
			String text, int fill) {
		Text t = new Text(parent, style);
		t.setFont(parent.getFont());

		makeScrollableCompositeAware(parent);

		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;

		t.setLayoutData(gd);
		t.setText(text);
		return t;
	}

	/**
	 * Creates a Group widget
	 * 
	 * @param parent
	 *            the parent composite to add this group to
	 * @param text
	 *            the text for the heading of the group
	 * @param columns
	 *            the number of columns within the group
	 * @param hspan
	 *            the horizontal span the group should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 *            Can be one of <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @return the new group
	 */
	public static Group createGroup(Composite parent, String text, int columns,
			int hspan, int fill) {
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		return createGroup(parent, text, columns, gd);
	}

	/**
	 * @param parent
	 * @param text
	 * @param columns
	 * @param hspan
	 * @param data
	 * @return
	 * @deprecated
	 */
	public static Group createGroup(Composite parent, String text, int columns,
			int hspan, GridData data) {
		return createGroup(parent, text, columns, data);
	}

	public static Group createGroup(Composite parent, String text, int columns,
			GridData data) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setText(text);
		g.setFont(parent.getFont());
		g.setLayoutData(data);
		return g;
	}

//	/**
//	 * This method allows us to open the preference dialog on the specific page
//	 * 
//	 * @param id
//	 *            the id of pref page to show
//	 */
//	public static void showPreferencePage(String id) {
//		showPreferencePage(id, new String[] { id });
//	}

//	/**
//	 * This method allows users to open a specific preference page and supply a
//	 * custom set of page filter items.
//	 * 
//	 * This alternative to <code>showPreferencePage(String)</code> allows other
//	 * related pref pages to be shown at the same time at the developers/context
//	 * discretion. All pages can be shown if <code>null</code> is passed.
//	 * 
//	 * @param page_id
//	 *            the id for the page to open
//	 * @param page_filters
//	 *            the listing of pages to be shown in the dialog
//	 */
//	public static void showPreferencePage(String page_id, String[] page_filters) {
//		PreferencesUtil.createPreferenceDialogOn(
//				DLTKUIPlugin.getActiveWorkbenchShell(), page_id, page_filters,
//				null).open();
//	}

	/**
	 * Creates a Composite widget
	 * 
	 * @param parent
	 *            the parent composite to add this composite to
	 * @param columns
	 *            the number of columns within the composite
	 * @param hspan
	 *            the horizontal span the composite should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 *            Can be one of <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @return the new group
	 */
	public static Composite createComposite(Composite parent, Font font,
			int columns, int hspan, int fill) {
		Composite g = new Composite(parent, SWT.NONE);
		g.setLayout(new GridLayout(columns, false));
		g.setFont(font);
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
	}

	/**
	 * Creates a Composite widget
	 * 
	 * @param parent
	 *            the parent composite to add this composite to
	 * @param columns
	 *            the number of columns within the composite
	 * @param hspan
	 *            the horizontal span the composite should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 *            Can be one of <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @param marginwidth
	 *            the width of the margin to place around the composite (default
	 *            is 5, specified by GridLayout)
	 * @param marginheight
	 *            the height of the margin to place around the composite
	 *            (default is 5, specified by GridLayout)
	 * @return the new group
	 */
	public static Composite createComposite(Composite parent, Font font,
			int columns, int hspan, int fill, int marginwidth, int marginheight) {
		Composite g = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(columns, false);
		layout.marginWidth = marginwidth;
		layout.marginHeight = marginheight;
		g.setLayout(layout);
		g.setFont(font);
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return g;
	}

	/**
	 * creates a vertical spacer for separating components
	 * 
	 * @param comp
	 * @param numlines
	 */
	public static void createVerticalSpacer(Composite comp, int numlines) {
		Label lbl = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = numlines;
		lbl.setLayoutData(gd);
	}

	/**
	 * creates a horizontal spacer for separating components
	 * 
	 * @param comp
	 * @param hspan
	 */
	public static void createHorizontalSpacer(Composite comp, int hspan) {
		Label lbl = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		lbl.setLayoutData(gd);
	}

	/**
	 * This method is used to make a combo box
	 * 
	 * @param parent
	 *            the parent composite to add the new combo to
	 * @param style
	 *            the style for the Combo
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @param fill
	 *            how the combo will fill into the composite Can be one of
	 *            <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @param items
	 *            the item to put into the combo
	 * @return a new Combo instance
	 */
	public static Combo createCombo(Composite parent, int style, int hspan,
			int fill, String[] items) {
		Combo c = new Combo(parent, style);
		c.setFont(parent.getFont());
		GridData gd = new GridData(fill);
		gd.horizontalSpan = hspan;
		c.setLayoutData(gd);
		c.setItems(items);
		c.select(0);
		return c;
	}

	/**
	 * This method is used to make a combo box with a default fill style of
	 * GridData.FILL_HORIZONTAL
	 * 
	 * @param parent
	 *            the parent composite to add the new combo to
	 * @param style
	 *            the style for the Combo
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @param items
	 *            the item to put into the combo
	 * @return a new Combo instance
	 */
	public static Combo createCombo(Composite parent, int style, int hspan,
			String[] items) {
		return createCombo(parent, style, hspan, GridData.FILL_HORIZONTAL,
				items);
	}

//	/**
//	 * Creates an ExpandibleComposite widget
//	 * 
//	 * @param parent
//	 *            the parent to add this widget to
//	 * @param style
//	 *            the style for ExpandibleComposite expanding handle, and layout
//	 * @param label
//	 *            the label for the widget
//	 * @param hspan
//	 *            how many columns to span in the parent
//	 * @param fill
//	 *            the fill style for the widget Can be one of
//	 *            <code>GridData.FILL_HORIZONAL</code>,
//	 *            <code>GridData.FILL_BOTH</code> or
//	 *            <code>GridData.FILL_VERTICAL</code>
//	 * @return a new ExpandibleComposite widget
//	 */
//	public static ExpandableComposite createExpandibleComposite(
//			Composite parent, int style, String label, int hspan, int fill) {
//		ExpandableComposite ex = new ExpandableComposite(parent, SWT.NONE,
//				style);
//		ex.setText(label);
//		ex.setFont(JFaceResources.getFontRegistry().getBold(
//				JFaceResources.DIALOG_FONT));
//		GridData gd = new GridData(fill);
//		gd.horizontalSpan = hspan;
//		ex.setLayoutData(gd);
//		return ex;
//	}

	@SuppressWarnings("unused") 
	private static void makeScrollableCompositeAware(Control control) {
//		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
//		if (parentScrolledComposite != null) {
//			parentScrolledComposite.adaptChild(control);
//		}
	}

}
