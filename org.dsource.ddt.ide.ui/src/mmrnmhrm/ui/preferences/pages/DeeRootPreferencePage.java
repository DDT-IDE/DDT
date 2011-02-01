package mmrnmhrm.ui.preferences.pages;

import melnorme.swtutil.GridComposite;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root/base preference page for Dee 
 */
public class DeeRootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{
	
	private Button fAdaptedMalformedAST;
	private ComboDialogField fLangVersion;
	private String[] labels;
	private String[] labelValues;
	
	public DeeRootPreferencePage() {
		super("Base preference page");
		setDescription("D root preference page.");
	}
	
	@Override
	public void init(IWorkbench workbench) {
		// Nothing to do
	}
	
	@Override
	protected Control createContents(Composite parent) {
		GridComposite content = new GridComposite(parent);
		
		fAdaptedMalformedAST = new Button(content, SWT.CHECK);
		fAdaptedMalformedAST.setText("Adapt source AST when there are syntax errors.");
		/*fAdaptedMalformedAST.addSelectionListener(new SimpleSelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			}
		});*/
		/*
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = 20;
		gd.horizontalSpan = 2;
		fStrikethroughCheckBox.setLayoutData(gd);
		 */
		
		GridComposite holder = new GridComposite(content, 2);
		
		fLangVersion = new ComboDialogField(SWT.READ_ONLY);
		fLangVersion.setLabelText("Default Parser Language Version:");
		labels = new String[] {"D1", "D2"};
		labelValues = new String[] {"1", "2"};
		fLangVersion.setItems(labels);
		fLangVersion.doFillIntoGrid(holder, 2);
		
		performDefaults();
		return content;
	}
	
	/** Gets the preference store for this page. */
	@Override
	public IPreferenceStore getPreferenceStore() {
		return DeePlugin.getPrefStore();
	}
	
	@Override
	protected void performDefaults() {
		fAdaptedMalformedAST.setSelection(DeeCorePreferences.getBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST));
		int ix = ArrayUtil.indexOfUsingEquals(labelValues, 
				DeeCorePreferences.getString(DeeCorePreferences.LANG_VERSION));
		fLangVersion.setTextWithoutUpdate(labels[ix]);
		super.performDefaults();
	}
	
	@Override
	public boolean performOk() {
		DeeCorePreferences.setBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST, fAdaptedMalformedAST.getSelection());
		
		int ix = ArrayUtil.indexOfUsingEquals(labels, fLangVersion.getText());
		DeeCorePreferences.setString(DeeCorePreferences.LANG_VERSION, labelValues[ix]);
		
		return super.performOk();
	}
	
}

