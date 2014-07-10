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
package mmrnmhrm.ui;

import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.preferences.pages.DeeEditorContentAssistPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeEditorPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeEditorSmartTypingPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeFoldingPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeSourceColoringPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeTemplatePreferencePage;
import mmrnmhrm.ui.text.DeePartitions;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;

public class DeeUILanguageToolkit extends AbstractDLTKUILanguageToolkit implements IDLTKUILanguageToolkit {
	
	private static final DeeUILanguageToolkit instance = new DeeUILanguageToolkit();
	private static final DeeScriptElementLabels elementLabels = new DeeScriptElementLabels(); 
	
	
	public static DeeUILanguageToolkit getDefault() {
		return instance ;
	}
	
	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		return DeeUIPlugin.getInstance().getPreferenceStore();
	}
	
	@Override
	public String getEditorId(Object inputElement) {
		return DeeEditor.EDITOR_ID;
	}
	
	@Override
	public String getPartitioningId() {
		return DeePartitions.PARTITIONING_ID;
	}
	
	@Override
	public ScriptTextTools getTextTools() {
		return DeeUIPlugin.getDefault().getTextTools();
	}
	
	@Deprecated
	@Override
	public DeeSimpleSourceViewerConfiguration createSourceViewerConfiguration() {
		return new DeeSimpleSourceViewerConfiguration(getTextTools().getColorManager(),
				getPreferenceStore(), null, getPartitioningId(), false);
	}
	
	@Override
	public ScriptUILabelProvider createScriptUILabelProvider() {
		// XXX: DLTK review this later and see if it will be used again
		return null;
	}
	
	@Override
	public DeeScriptElementLabels getScriptElementLabels() {
		return elementLabels; 
	}
	
	@Override
	public String[] getEditorPreferencePages() {
		return new String[]{ 
				DeeEditorPreferencePage.PAGE_ID, 
				DeeEditorContentAssistPreferencePage.PAGE_ID,
				DeeEditorSmartTypingPreferencePage.PAGE_ID,
				DeeFoldingPreferencePage.PAGE_ID,
				DeeTemplatePreferencePage.PAGE_ID,
				DeeSourceColoringPreferencePage.PAGE_ID};
	}
	
	@Override
	public String getDebugPreferencePage() {
		// TODO DLTK getDebugPreferencePage
		return null;
	}
	
	
	@Override
	public String getInterpreterContainerId() {
		return "mmrnmrhm.core.launching.INTERPRETER_CONTAINER";
	}
	
	@Override
	public boolean getProvideMembers(ISourceModule element) {
		return true;
	}
	
}