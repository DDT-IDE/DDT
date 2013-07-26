/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.array;
import melnorme.swtutil.LayoutUtil;
import melnorme.utilbox.core.CoreUtil;
import mmrnmhrm.core.model_elements.DeeModelConstants;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeeUILanguageToolkit;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;
import mmrnmhrm.ui.views.DeeModelElementLabelProvider;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.MethodParameterInfo_Copy;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The root preference page for DDT 
 */
public class DeeAppearancePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private static final String LABEL_PROVIDER_STYLE = 
			"Icon style for D elements in viewers:";
	private static final String LABEL_PROVIDER_STYLE_DDT = 
			"DDT default style (protection is overlayed for all element kinds)";
	private static final String LABEL_PROVIDER_STYLE_JDT = 
			"JDT style (methods and variables have protection dependent base icons)";

	public DeeAppearancePreferencePage() {
		super(GRID);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(DeePlugin.getInstance().getPreferenceStore());
	}
	
	protected ElementIconsStyle selectedIconStyle;
	protected RadioGroupFieldEditor iconStyleEditor;
	protected PreviewGroup previewGroup;
	
	@Override
	protected void createFieldEditors() {
		String[][] labelAndValues = new String[][] { 
				{ LABEL_PROVIDER_STYLE_DDT, DeeUIPreferenceConstants.ElementIconsStyle.DDT.toString() },
				{ LABEL_PROVIDER_STYLE_JDT, DeeUIPreferenceConstants.ElementIconsStyle.JDTLIKE.toString() },
		};
		iconStyleEditor = new RadioGroupFieldEditor(
				DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE, LABEL_PROVIDER_STYLE, 1, 
				labelAndValues, getFieldEditorParent());
		addField(iconStyleEditor);
		//This should not be necessary, editor should have property initialized:
		selectedIconStyle = ElementIconsStyle.DDTLEAN; 
		
		previewGroup = new PreviewGroup();
		previewGroup.createPreviewGroup();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if (event.getProperty().equals(FieldEditor.VALUE) && event.getSource() == iconStyleEditor) {
			
			Object newValue = event.getNewValue();
			if(newValue != null && newValue instanceof String) {
				selectedIconStyle = ElementIconsStyle.fromString((String) newValue, null);
			}
			
			previewGroup.refreshPreview();
		}
	}
	
	@Override
	public boolean performOk() {
		boolean performOk = super.performOk();
		refreshIDEViewers();
		return performOk;
	}
	
	/** Triggers a refresh on all viewers with model element label providers. 
	 * (Uses a workaround to trigger refresh in {@link AppearanceAwareLabelProvider} ) */
	protected void refreshIDEViewers() {
		IPreferenceStore prefStore = DeePlugin.getInstance().getPreferenceStore();
		String value = prefStore.getString(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE);
		prefStore.firePropertyChangeEvent(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE, value, value);
	}
	
	public class PreviewGroup {
		
		private TreeViewer previewTree;

		protected TreeViewer createPreviewGroup() {
			Group group = new Group(getFieldEditorParent(), SWT.NONE);
			group.setText("Preview:");
			
			group.setLayout(LayoutUtil.createFillLayout(2, 2, 0));
			GridData gd = new GridData(GridData.FILL_BOTH);
			group.setLayoutData(gd);
			group.setLayoutData(GridDataFactory.fillDefaults().minSize(SWT.DEFAULT, SWT.DEFAULT).create());
			
			
			previewTree = new TreeViewer(group);
			previewTree.setContentProvider(new PreviewContentProvider());
			final ScriptElementLabels scriptElementLabels = DeeUILanguageToolkit.getDefault().getScriptElementLabels();
			DeeModelElementLabelProvider labelProvider = new DeeModelElementLabelProvider() {
				@Override
				public String getText(Object object) {
					
					// In the future we need to make sure these flags are calculated properly according to preferences
					long labelFlags = AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
							| ScriptElementLabels.ALL_CATEGORY
							| ScriptElementLabels.F_APP_TYPE_SIGNATURE
							| ScriptElementLabels.M_APP_RETURNTYPE;
					
					StringBuffer buf = new StringBuffer(61);
					scriptElementLabels.getElementLabel((IModelElement) object, labelFlags, buf);
					return buf.toString();
				}
				
				@Override
				protected ElementIconsStyle getIconStylePreference() {
					return assertNotNull(selectedIconStyle);
				}
				
			};
			previewTree.setLabelProvider(new DecoratingLabelProvider(labelProvider, null));
			previewTree.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
			assertNotNull(selectedIconStyle);
			previewTree.setInput(this);
			return previewTree;
		}
		
		public void refreshPreview() {
			previewTree.refresh();
		}
		
	}
	
	
	protected class PreviewContentProvider extends TreeNodeContentProvider implements DeeModelConstants {
		
		protected IModelElement[] treeModel = createTreeModel();
		
		protected IModelElement[] createTreeModel() {
			IScriptModel model = DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IScriptProject scriptProj = model.getScriptProject("#__PreviewProject");
			
			IProjectFragment projectFragment = scriptProj.getProjectFragment(scriptProj.getProject().getFolder("src"));
			assertNotNull(projectFragment);
			IScriptFolder scriptFolder = projectFragment.getScriptFolder("pack.sub");
			String[] ns = array("pack", "sub");
			assertNotNull(scriptFolder);
			ISourceModule sourceModule1 = scriptFolder.getSourceModule("sourceModule.d");
			assertNotNull(sourceModule1);

			FakeMember.FakeType module 
				= new FakeMember.FakeType(sourceModule1, "preview_module", Modifiers.AccModule, ns);
			
			ns = array("pack", "sub", "preview_module");
			
			final int FINAL = Modifiers.AccFinal;
			final int SF = Modifiers.AccStatic | Modifiers.AccFinal;
			
			new FakeMember.FakeField(module, "varA", FLAG_PROTECTION_PUBLIC, ns, "int");
			new FakeMember.FakeField(module, "varB", FLAG_PROTECTION_PROTECTED, ns, "int");
			new FakeMember.FakeField(module, "varC", FLAG_PROTECTION_PACKAGE | SF, ns, "int");
			new FakeMember.FakeField(module, "varD", FLAG_PROTECTION_PRIVATE, ns, "int");
			
			IParameter[] paramsE = array();
			IParameter[] paramsI = array(new MethodParameterInfo_Copy("blah", "int", null));
			IParameter[] paramsO = array(new MethodParameterInfo_Copy("foo", "Object", null));
			new FakeMember.FakeMethod(module, "function1", FLAG_PROTECTION_PUBLIC | Modifiers.AccSynthetic, 
					ns, paramsI, false, "int");
			new FakeMember.FakeMethod(module, "function2", FLAG_PROTECTION_PROTECTED, ns, paramsI, false, "int");
			new FakeMember.FakeMethod(module, "function3", FLAG_PROTECTION_PACKAGE, ns, paramsI, false, "int");
			new FakeMember.FakeMethod(module, "function4", FLAG_PROTECTION_PRIVATE, ns, paramsI, false, "int");
			
			FakeMember.FakeType sampleClass = new FakeMember.FakeType(module, "Class", FLAG_KIND_CLASS, ns);
			
			new FakeMember.FakeMethod(sampleClass, "this", FLAG_PROTECTION_PUBLIC, ns, paramsO, true, "int");
			new FakeMember.FakeMethod(sampleClass, "~this", FLAG_PROTECTION_PRIVATE, ns, paramsO, true, "int");
			new FakeMember.FakeMethod(sampleClass, "method1", FLAG_PROTECTION_PUBLIC | SF, ns, paramsE, false, "int");
			new FakeMember.FakeMethod(sampleClass, "method2", FLAG_PROTECTION_PROTECTED, ns, paramsE, false, "void");
			
			new FakeMember.FakeType(sampleClass, "Class2", FLAG_KIND_CLASS | FLAG_PROTECTION_PROTECTED | FINAL, ns);
			new FakeMember.FakeType(sampleClass, "Class3", FLAG_KIND_CLASS | FLAG_PROTECTION_PRIVATE | SF, ns);
			
			new FakeMember.FakeType(module, "Struct", FLAG_KIND_STRUCT, ns);
			new FakeMember.FakeType(module, "Union", FLAG_KIND_UNION, ns);
			new FakeMember.FakeType(module, "Class", FLAG_KIND_CLASS | FLAG_PROTECTION_PUBLIC, ns);
			new FakeMember.FakeType(module, "Class", FLAG_KIND_CLASS | FLAG_PROTECTION_PROTECTED, ns);
			new FakeMember.FakeType(module, "Class", FLAG_KIND_CLASS | FLAG_PROTECTION_PACKAGE, ns);
			new FakeMember.FakeType(module, "Class", FLAG_KIND_CLASS | FLAG_PROTECTION_PRIVATE, ns);
			new FakeMember.FakeType(module, "Interface", FLAG_KIND_INTERFACE, ns);
			new FakeMember.FakeType(module, "Template", FLAG_KIND_TEMPLATE, ns);
			new FakeMember.FakeType(module, "Mixin", FLAG_KIND_MIXIN, ns);
			new FakeMember.FakeType(module, "Enum", FLAG_KIND_ENUM, ns);
			new FakeMember.FakeType(module, "Alias", FLAG_KIND_ALIAS, ns);
			
			IModelElement[] treeModel = CoreUtil.<IModelElement>array(module);
			return treeModel;
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return treeModel;
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			return ((FakeMember) parentElement).getChildren();
		}
		
		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof FakeMember) {
				return ((FakeMember) element).hasChildren();
			}
			return false;
		}
		
		@Override
		public Object getParent(Object element) {
			return null;
		}
		
	}

}
