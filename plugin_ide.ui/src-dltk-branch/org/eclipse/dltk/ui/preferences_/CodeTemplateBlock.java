/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.preferences_;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import melnorme.lang.ide.ui.TextSettings_Actual;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.dltk.internal.ui.preferences_.ScriptSourcePreviewerUpdater;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.preferences.EditTemplateDialog;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.text.templates.ICodeTemplateAccess;
import org.eclipse.dltk.ui.text.templates.ICodeTemplateCategory;
import org.eclipse.dltk.ui.text.templates.ProjectTemplateStore;
import org.eclipse.dltk.ui.text.templates.TemplateVariableProcessor;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.dltk.ui.viewsupport.BasicElementLabels;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateReaderWriter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class CodeTemplateBlock extends OptionsConfigurationBlock {

	private class CodeTemplateAdapter extends ViewerComparator implements
			ITreeListAdapter, IDialogFieldListener {

		private final Object[] NO_CHILDREN = new Object[0];

		@Override
		public void customButtonPressed(TreeListDialogField field, int index) {
			doButtonPressed(index, field.getSelectedElements());
		}

		@Override
		public void selectionChanged(TreeListDialogField field) {
			List selected = field.getSelectedElements();
			field.enableButton(IDX_ADD, canAdd(selected));
			field.enableButton(IDX_EDIT, canEdit(selected));
			field.enableButton(IDX_REMOVE, canRemove(selected));
			field.enableButton(IDX_EXPORT, !selected.isEmpty());

			updateSourceViewerInput(selected);
		}

		@Override
		public void doubleClicked(TreeListDialogField field) {
			List selected = field.getSelectedElements();
			if (canEdit(selected)) {
				doButtonPressed(IDX_EDIT, selected);
			}
		}

		@Override
		public Object[] getChildren(TreeListDialogField field, Object element) {
			if (element instanceof ICodeTemplateCategory) {
				ICodeTemplateCategory category = (ICodeTemplateCategory) element;
				if (category.isGroup()) {
					return getTemplateContextTypes(category);
				} else {
					return getTemplatesOfCategory(category);
				}
			} else if (element instanceof TemplateContextType) {
				return getTemplatesOfContextType(((TemplateContextType) element)
						.getId());
			}
			return NO_CHILDREN;
		}

		@Override
		public Object getParent(TreeListDialogField field, Object element) {
			if (element instanceof TemplatePersistenceData) {
				final TemplatePersistenceData data = (TemplatePersistenceData) element;
				final String contextTypeId = data.getTemplate()
						.getContextTypeId();
				final ICodeTemplateCategory category = codeTemplateAccess
						.getCategoryOfContextType(contextTypeId);
				if (category == null) {
					return null;
				}
				if (category.isGroup()) {
					return codeTemplateAccess.getContextTypeRegistry()
							.getContextType(contextTypeId);
				} else {
					return category;
				}
			} else if (element instanceof TemplateContextType) {
				return codeTemplateAccess
						.getCategoryOfContextType(((TemplateContextType) element)
								.getId());
			}
			return null;
		}

		@Override
		public boolean hasChildren(TreeListDialogField field, Object element) {
			return element instanceof ICodeTemplateCategory
					|| element instanceof TemplateContextType;
		}

		@Override
		public void dialogFieldChanged(DialogField field) {
			// if (field == fGenerateComments) {
			// setValue(PREF_GENERATE_COMMENTS, fGenerateComments.isSelected());
			// }
		}

		@Override
		public void keyPressed(TreeListDialogField field, KeyEvent event) {
		}

		/*
		 * @see ViewerSorter#category(java.lang.Object)
		 */
		@Override
		public int category(Object element) {
			if (element instanceof ICodeTemplateCategory) {
				return ((ICodeTemplateCategory) element).getPriority();
			}
			return 1000;
		}
	}

	private static class CodeTemplateLabelProvider extends LabelProvider {

		/*
		 * @see ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof ICodeTemplateCategory) {
				return ((ICodeTemplateCategory) element).getName();
			} else if (element instanceof TemplateContextType) {
				return ((TemplateContextType) element).getName();
			} else if (element instanceof TemplatePersistenceData) {
				final TemplatePersistenceData data = (TemplatePersistenceData) element;
				return data.getTemplate().getDescription();
			} else {
				return element.toString();
			}
		}
	}

	// private static final PreferenceKey PREF_GENERATE_COMMENTS =
	// getJDTUIKey(PreferenceConstants.CODEGEN_ADD_COMMENTS);

	private static PreferenceKey[] getAllKeys() {
		return new PreferenceKey[] { /* PREF_GENERATE_COMMENTS */};
	}

	private final static int IDX_ADD = 0;
	private final static int IDX_EDIT = 1;
	private final static int IDX_REMOVE = 2;
	private final static int IDX_IMPORT = 3;
	private final static int IDX_EXPORT = 4;
	private final static int IDX_EXPORTALL = 5;

	// protected final static Object COMMENT_NODE =
	// PreferencesMessages.CodeTemplateBlock_templates_comment_node;
	// protected final static Object CODE_NODE =
	// PreferencesMessages.CodeTemplateBlock_templates_code_node;

	private TreeListDialogField fCodeTemplateTree;
	// private SelectionButtonDialogField fGenerateComments;

	protected ProjectTemplateStore fTemplateStore;

	private PixelConverter fPixelConverter;
	private SourceViewer fPatternViewer;

	private final IDLTKUILanguageToolkit toolkit;
	private final ICodeTemplateAccess codeTemplateAccess;
	private TemplateVariableProcessor fTemplateProcessor;

	public CodeTemplateBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container,
			IDLTKUILanguageToolkit toolkit,
			ICodeTemplateAccess codeTemplateAccess) {
		super(context, project, getAllKeys(), container);
		this.toolkit = toolkit;
		this.codeTemplateAccess = codeTemplateAccess;

		fTemplateStore = new ProjectTemplateStore(codeTemplateAccess, project);
		try {
			fTemplateStore.load();
		} catch (IOException e) {
			DLTKUIPlugin.log(e);
		}

		fTemplateProcessor = new TemplateVariableProcessor();

		CodeTemplateAdapter adapter = new CodeTemplateAdapter();

		String[] buttonLabels = new String[] {
				PreferencesMessages.CodeTemplateBlock_templates_new_button,
				PreferencesMessages.CodeTemplateBlock_templates_edit_button,
				PreferencesMessages.CodeTemplateBlock_templates_remove_button,
				PreferencesMessages.CodeTemplateBlock_templates_import_button,
				PreferencesMessages.CodeTemplateBlock_templates_export_button,
				PreferencesMessages.CodeTemplateBlock_templates_exportall_button };
		fCodeTemplateTree = new TreeListDialogField(adapter, buttonLabels,
				new CodeTemplateLabelProvider());
		fCodeTemplateTree.setDialogFieldListener(adapter);
		fCodeTemplateTree
				.setLabelText(PreferencesMessages.CodeTemplateBlock_templates_label);
		fCodeTemplateTree.setViewerComparator(adapter);

		fCodeTemplateTree.enableButton(IDX_EXPORT, false);
		fCodeTemplateTree.enableButton(IDX_ADD, false);
		fCodeTemplateTree.enableButton(IDX_EDIT, false);
		fCodeTemplateTree.enableButton(IDX_REMOVE, false);

		fCodeTemplateTree.addElements(Arrays.asList(codeTemplateAccess
				.getCategories()));

		fCodeTemplateTree.selectFirstElement();

		// fGenerateComments = new SelectionButtonDialogField(SWT.CHECK |
		// SWT.WRAP);
		// fGenerateComments.setDialogFieldListener(adapter);
		// fGenerateComments
		// .setLabelText(PreferencesMessages.CodeTemplateBlock_createcomment_label);

		updateControls();
	}

	public void postSetSelection(Object element) {
		fCodeTemplateTree.postSetSelection(new StructuredSelection(element));
	}

	@Override
	public boolean hasProjectSpecificOptions(IProject project) {
		if (super.hasProjectSpecificOptions(project))
			return true;

		if (project != null) {
			return fTemplateStore.hasProjectSpecificTempates(project);
		}
		return false;
	}

	/*
	 * @see OptionsConfigurationBlock# useProjectSpecificSettings(boolean)
	 */
	@Override
	public void useProjectSpecificSettings(boolean enable) {
		fCodeTemplateTree.setEnabled(enable);
		// need to set because super implementation only updates controls
		super.useProjectSpecificSettings(enable);
	}

	@Override
	protected Control createContents(Composite parent) {
		fPixelConverter = new PixelConverter(parent);

		setShell(parent.getShell());

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);

		fCodeTemplateTree.doFillIntoGrid(composite, 3);
		LayoutUtil
				.setHorizontalSpan(fCodeTemplateTree.getLabelControl(null), 2);
		LayoutUtil
				.setHorizontalGrabbing(fCodeTemplateTree.getTreeControl(null));

		fPatternViewer = createViewer(composite, 2);

		// fGenerateComments.doFillIntoGrid(composite, 2);

		return composite;
	}

	/*
	 * @see OptionsConfigurationBlock#updateControls()
	 */
	protected void updateControls() {
		// fGenerateComments.setSelection(getBooleanValue(PREF_GENERATE_COMMENTS));
	}

	private SourceViewer createViewer(Composite parent, int nColumns) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(PreferencesMessages.CodeTemplateBlock_preview);
		GridData data = new GridData();
		data.horizontalSpan = nColumns;
		label.setLayoutData(data);

		IDocument document = new Document();
		DeeTextTools tools = DeeUIPlugin.getDefault().getTextTools();
		tools.setupDocumentPartitioner(document);
		IPreferenceStore store = toolkit.getCombinedPreferenceStore();
		SourceViewer viewer = new ScriptSourceViewer(parent, null, null, false,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, store);
		DeeSourceViewerConfiguration configuration = TextSettings_Actual.createSourceViewerConfiguration(store, null);
		viewer.configure(configuration);

		viewer.setEditable(false);
		Cursor arrowCursor = viewer.getTextWidget().getDisplay()
				.getSystemCursor(SWT.CURSOR_ARROW);
		viewer.getTextWidget().setCursor(arrowCursor);
		viewer.getTextWidget().setCaret(null);

		viewer.setDocument(document);

		Font font = JFaceResources.getFont(configuration
				.getFontPropertyPreferenceKey());
		viewer.getTextWidget().setFont(font);
		new ScriptSourcePreviewerUpdater(viewer, configuration, store);

		Control control = viewer.getControl();
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL);
		data.horizontalSpan = nColumns;
		data.heightHint = fPixelConverter.convertHeightInCharsToPixels(5);
		control.setLayoutData(data);

		return viewer;
	}

	protected TemplatePersistenceData[] getTemplatesOfCategory(
			ICodeTemplateCategory category) {
		ArrayList res = new ArrayList();
		TemplatePersistenceData[] templates = fTemplateStore.getTemplateData();
		for (int i = 0; i < templates.length; i++) {
			TemplatePersistenceData curr = templates[i];
			// FIXME if (isComment == curr.getTemplate().getName().endsWith(
			// CodeTemplateContextType.COMMENT_SUFFIX)) {
			res.add(curr);
			// }
		}
		return (TemplatePersistenceData[]) res
				.toArray(new TemplatePersistenceData[res.size()]);
	}

	private TemplatePersistenceData[] getTemplatesOfContextType(
			TemplateContextType contextType) {
		return getTemplatesOfContextType(contextType.getId());
	}

	protected TemplatePersistenceData[] getTemplatesOfContextType(
			String contextTypeId) {
		List res = new ArrayList();
		TemplatePersistenceData[] templates = fTemplateStore.getTemplateData();
		for (int i = 0; i < templates.length; ++i) {
			TemplatePersistenceData curr = templates[i];
			if (contextTypeId.equals(curr.getTemplate().getContextTypeId())) {
				res.add(curr);
			}
		}
		return (TemplatePersistenceData[]) res
				.toArray(new TemplatePersistenceData[res.size()]);
	}

	protected TemplateContextType[] getTemplateContextTypes(
			ICodeTemplateCategory category) {
		ArrayList result = new ArrayList();
		TemplateContextType[] contextTypes = category.getTemplateContextTypes();
		for (int i = 0; i < contextTypes.length; ++i) {
			TemplateContextType contextType = contextTypes[i];
			if (getTemplatesOfContextType(contextType).length > 0) {
				result.add(contextType);
			}
		}
		return (TemplateContextType[]) result
				.toArray(new TemplateContextType[result.size()]);
	}

	protected boolean canAdd(List selected) {
		if (selected.size() == 1) {
			Object element = selected.get(0);
			if (element instanceof TemplateContextType
					|| element instanceof ICodeTemplateCategory
					&& ((ICodeTemplateCategory) element).isGroup()) {
				return true;
			}
			if (element instanceof TemplatePersistenceData) {
				final TemplatePersistenceData data = (TemplatePersistenceData) element;
				final ICodeTemplateCategory category = codeTemplateAccess
						.getCategoryOfContextType(data.getTemplate()
								.getContextTypeId());
				if (category != null && category.isGroup()) {
					return true;
				}
			}
		}
		return false;
	}

	protected static boolean canEdit(List selected) {
		return selected.size() == 1
				&& (selected.get(0) instanceof TemplatePersistenceData);
	}

	protected static boolean canRemove(List selected) {
		if (selected.size() == 1
				&& (selected.get(0) instanceof TemplatePersistenceData)) {
			TemplatePersistenceData data = (TemplatePersistenceData) selected
					.get(0);
			return data.isUserAdded();
		}
		return false;
	}

	protected void updateSourceViewerInput(List selection) {
		if (fPatternViewer == null
				|| fPatternViewer.getTextWidget().isDisposed()) {
			return;
		}
		if (selection.size() == 1
				&& selection.get(0) instanceof TemplatePersistenceData) {
			TemplatePersistenceData data = (TemplatePersistenceData) selection
					.get(0);
			Template template = data.getTemplate();
			TemplateContextType type = codeTemplateAccess
					.getContextTypeRegistry().getContextType(
							template.getContextTypeId());
			fTemplateProcessor.setContextType(type);
			fPatternViewer.getDocument().set(template.getPattern());
		} else {
			fPatternViewer.getDocument().set(""); //$NON-NLS-1$
		}
	}

	protected void doButtonPressed(int buttonIndex, List selected) {
		switch (buttonIndex) {
		case IDX_EDIT:
			edit((TemplatePersistenceData) selected.get(0), false);
			break;
		case IDX_ADD: {
			Object element = selected.get(0);
			Template orig = null;
			String contextTypeId;
			if (element instanceof TemplatePersistenceData) {
				orig = ((TemplatePersistenceData) element).getTemplate();
				contextTypeId = orig.getContextTypeId();
			} else if (element instanceof TemplateContextType) {
				TemplateContextType type = (TemplateContextType) selected
						.get(0);
				contextTypeId = type.getId();
			} else if (element instanceof ICodeTemplateCategory) {
				// default: text file
				contextTypeId = ((ICodeTemplateCategory) element)
						.getTemplateContextTypes()[0].getId();
			} else {
				return;
			}
			Template newTemplate;
			if (orig != null) {
				newTemplate = new Template(
						"", "", contextTypeId, orig.getPattern(), false); //$NON-NLS-1$//$NON-NLS-2$
			} else {
				newTemplate = new Template("", "", contextTypeId, "", false); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
			}
			TemplatePersistenceData newData = new TemplatePersistenceData(
					newTemplate, true);
			edit(newData, true);
			break;
		}
		case IDX_REMOVE:
			remove((TemplatePersistenceData) selected.get(0));
			break;
		case IDX_EXPORT:
			export(selected);
			break;
		case IDX_EXPORTALL:
			exportAll();
			break;
		case IDX_IMPORT:
			import_();
			break;
		}
	}

	private void remove(TemplatePersistenceData data) {
		if (data.isUserAdded()) {
			fTemplateStore.delete(data);
			fCodeTemplateTree.refresh();
		}
	}

	private void edit(TemplatePersistenceData data, boolean isNew) {
		final Template newTemplate = new Template(data.getTemplate());
		final ICodeTemplateCategory category = codeTemplateAccess
				.getCategoryOfContextType(newTemplate.getContextTypeId());
		if (category == null) {
			return;
		}
		final ContextTypeRegistry contextTypeRegistry;
		if (category.isGroup()) {
			contextTypeRegistry = new ContextTypeRegistry();
			TemplateContextType[] contextTypes = category
					.getTemplateContextTypes();
			for (int i = 0; i < contextTypes.length; ++i) {
				contextTypeRegistry.addContextType(contextTypes[i]);
			}
		} else {
			contextTypeRegistry = codeTemplateAccess.getContextTypeRegistry();
		}
		EditTemplateDialog dialog = new EditTemplateDialog(toolkit, getShell(),
				newTemplate, !isNew, data.isUserAdded(), category.isGroup(),
				contextTypeRegistry);
		if (dialog.open() == Window.OK) {
			// changed
			data.setTemplate(dialog.getTemplate());
			if (isNew) {
				// add to store
				fTemplateStore.addTemplateData(data);
			}
			if (isNew || category.isGroup()) {
				fCodeTemplateTree.refresh();
			} else {
				fCodeTemplateTree.refresh(data);
			}
			fCodeTemplateTree.selectElements(new StructuredSelection(data));
		}
	}

	private void import_() {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setText(PreferencesMessages.CodeTemplateBlock_import_title);
		dialog
				.setFilterExtensions(new String[] { PreferencesMessages.CodeTemplateBlock_import_extension });
		String path = dialog.open();

		if (path == null)
			return;

		try {
			TemplateReaderWriter reader = new TemplateReaderWriter();
			File file = new File(path);
			if (file.exists()) {
				InputStream input = new BufferedInputStream(
						new FileInputStream(file));
				try {
					TemplatePersistenceData[] datas = reader.read(input, null);
					for (int i = 0; i < datas.length; i++) {
						updateTemplate(datas[i]);
					}
				} finally {
					try {
						input.close();
					} catch (IOException x) {
					}
				}
			}

			fCodeTemplateTree.refresh();
			updateSourceViewerInput(fCodeTemplateTree.getSelectedElements());

		} catch (FileNotFoundException e) {
			openReadErrorDialog(e);
		} catch (IOException e) {
			openReadErrorDialog(e);
		}

	}

	private void updateTemplate(TemplatePersistenceData data) {
		String dataId = data.getId();
		TemplatePersistenceData[] datas = fTemplateStore.getTemplateData();
		if (dataId != null) {
			// predefined
			for (int i = 0; i < datas.length; ++i) {
				TemplatePersistenceData data2 = datas[i];
				String id = data2.getId();
				if (id != null && id.equals(dataId)) {
					data2.setTemplate(data.getTemplate());
					return;
				}
			}
		} else {
			// user added
			String dataName = data.getTemplate().getName();
			for (int i = 0; i < datas.length; ++i) {
				TemplatePersistenceData data2 = datas[i];
				if (data2.getId() == null) {
					String name = data2.getTemplate().getName();
					String contextTypeId = data2.getTemplate()
							.getContextTypeId();
					if (name != null
							&& name.equals(dataName)
							&& contextTypeId.equals(data.getTemplate()
									.getContextTypeId())) {
						data2.setTemplate(data.getTemplate());
						return;
					}
				}
			}
			// new
			fTemplateStore.addTemplateData(data);
		}
	}

	private void exportAll() {
		export(fTemplateStore.getTemplateData());
	}

	private void export(List selected) {
		Set datas = new HashSet();
		for (int i = 0; i < selected.size(); i++) {
			Object curr = selected.get(i);
			if (curr instanceof TemplatePersistenceData) {
				datas.add(curr);
			} else if (curr instanceof TemplateContextType) {
				TemplatePersistenceData[] cat = getTemplatesOfContextType((TemplateContextType) curr);
				datas.addAll(Arrays.asList(cat));
			} else if (curr instanceof ICodeTemplateCategory) {
				ICodeTemplateCategory category = (ICodeTemplateCategory) curr;
				if (category.isGroup()) {
					TemplateContextType[] types = getTemplateContextTypes(category);
					for (int j = 0; j < types.length; ++j) {
						TemplateContextType contextType = types[j];
						TemplatePersistenceData[] cat = getTemplatesOfContextType(contextType);
						datas.addAll(Arrays.asList(cat));
					}
				} else {
					TemplatePersistenceData[] cat = getTemplatesOfCategory(category);
					datas.addAll(Arrays.asList(cat));
				}
			}
		}
		export((TemplatePersistenceData[]) datas
				.toArray(new TemplatePersistenceData[datas.size()]));
	}

	private void export(TemplatePersistenceData[] templates) {
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		dialog.setText(NLS.bind(
				PreferencesMessages.CodeTemplateBlock_export_title, String
						.valueOf(templates.length)));
		dialog
				.setFilterExtensions(new String[] { PreferencesMessages.CodeTemplateBlock_export_extension });
		dialog
				.setFileName(PreferencesMessages.CodeTemplateBlock_export_filename);
		String path = dialog.open();

		if (path == null)
			return;

		File file = new File(path);

		if (file.isHidden()) {
			String title = PreferencesMessages.CodeTemplateBlock_export_error_title;
			String message = NLS.bind(
					PreferencesMessages.CodeTemplateBlock_export_error_hidden,
					BasicElementLabels.getPathLabel(file));
			MessageDialog.openError(getShell(), title, message);
			return;
		}

		if (file.exists() && !file.canWrite()) {
			String title = PreferencesMessages.CodeTemplateBlock_export_error_title;
			String message = NLS
					.bind(
							PreferencesMessages.CodeTemplateBlock_export_error_canNotWrite,
							BasicElementLabels.getPathLabel(file));
			MessageDialog.openError(getShell(), title, message);
			return;
		}

		if (!file.exists() || confirmOverwrite(file)) {
			OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(file));
				TemplateReaderWriter writer = new TemplateReaderWriter();
				writer.save(templates, output);
				output.close();
			} catch (IOException e) {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e2) {
						// ignore
					}
				}
				openWriteErrorDialog();
			}
		}

	}

	private boolean confirmOverwrite(File file) {
		return MessageDialog
				.openQuestion(
						getShell(),
						PreferencesMessages.CodeTemplateBlock_export_exists_title,
						NLS
								.bind(
										PreferencesMessages.CodeTemplateBlock_export_exists_message,
										BasicElementLabels.getPathLabel(file)));
	}

	@Override
	public void performDefaults() {
		fTemplateStore.restoreDefaults();

		// refresh
		fCodeTemplateTree.refresh();

		super.performDefaults();
	}

	public boolean performOk(boolean enabled) {
		boolean res = super.performOk();
		if (!res)
			return false;

		if (fProject != null) {
			TemplatePersistenceData[] templateData = fTemplateStore
					.getTemplateData();
			for (int i = 0; i < templateData.length; i++) {
				fTemplateStore.setProjectSpecific(templateData[i].getId(),
						enabled);
			}
		}
		try {
			fTemplateStore.save();
		} catch (IOException e) {
			DLTKUIPlugin.log(e);
			openWriteErrorDialog();
		}
		return true;
	}

	public void performCancel() {
		try {
			fTemplateStore.revertChanges();
		} catch (IOException e) {
			openReadErrorDialog(e);
		}
	}

	private void openReadErrorDialog(Exception e) {
		String title = PreferencesMessages.CodeTemplateBlock_error_read_title;

		String message = e.getLocalizedMessage();
		if (message != null)
			message = NLS.bind(
					PreferencesMessages.CodeTemplateBlock_error_parse_message,
					message);
		else
			message = PreferencesMessages.CodeTemplateBlock_error_read_message;
		MessageDialog.openError(getShell(), title, message);
	}

	private void openWriteErrorDialog() {
		String title = PreferencesMessages.CodeTemplateBlock_error_write_title;
		String message = PreferencesMessages.CodeTemplateBlock_error_write_message;
		MessageDialog.openError(getShell(), title, message);
	}

}
