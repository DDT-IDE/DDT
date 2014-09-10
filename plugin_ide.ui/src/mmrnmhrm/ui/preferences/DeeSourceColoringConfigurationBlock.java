/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import static melnorme.utilbox.core.CoreUtil.array;

import java.io.InputStream;

import melnorme.lang.ide.ui.text.coloring.AbstractSourceColoringConfigurationBlock;
import melnorme.util.swt.jface.LabeledTreeElement;
import mmrnmhrm.ui.editor.DeeSimpleSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeColorPreferences;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceColoringConfigurationBlock extends AbstractSourceColoringConfigurationBlock {
	
	private static final String PREVIEW_FILE_NAME = "SourceColoringPreviewFile.d";
	
	protected static final LabeledTreeElement[] treeElements = array(
		new SourceColoringCategory("Source", array(
			new SourceColoringElement("Default", DeeColorPreferences.DEFAULT.key),
			new SourceColoringElement("Keywords", DeeColorPreferences.KEYWORDS.key),
			new SourceColoringElement("Keywords - Basic Types", DeeColorPreferences.BASICTYPES.key),
			new SourceColoringElement("Keywords - Literals", DeeColorPreferences.LITERALS.key),
			new SourceColoringElement("Character Literals", DeeColorPreferences.CHARACTER_LITERALS.key),
			new SourceColoringElement("Strings", DeeColorPreferences.STRING.key),
			new SourceColoringElement("Delimited String", DeeColorPreferences.DELIM_STRING.key),
			new SourceColoringElement("@Annotations", DeeColorPreferences.ANNOTATIONS.key)
		)),
		new SourceColoringCategory("Comments", array(
			new SourceColoringElement("Comment", DeeColorPreferences.COMMENT.key),
			new SourceColoringElement("Doc Comment", DeeColorPreferences.DOCCOMMENT.key)
		))
	);
	
	public DeeSourceColoringConfigurationBlock(IPreferenceStore store) {
		super(store);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	protected LabeledTreeElement[] getTreeElements() {
		return treeElements;
	}
	
	@Override
	protected InputStream getPreviewContentAsStream() {
		return getClass().getResourceAsStream(PREVIEW_FILE_NAME);
	}
	
	@Override
	protected ProjectionViewer createPreviewViewer(Composite parent, boolean showAnnotationsOverview,
			int styles, IPreferenceStore store) {
		ScriptSourceViewer sourceViewer = new ScriptSourceViewer(parent, null, null,
			showAnnotationsOverview, styles, store);
		DeeSimpleSourceViewerConfiguration configuration = createSimpleSourceViewerConfiguration(store, null);
		sourceViewer.configure(configuration);
		configuration.setupViewerForTextPresentationPrefChanges(sourceViewer);
		return sourceViewer;
	}
	
	protected DeeSimpleSourceViewerConfiguration createSimpleSourceViewerConfiguration( 
			IPreferenceStore preferenceStore, ITextEditor editor) {
		return new DeeSimpleSourceViewerConfiguration(colorManager, preferenceStore, editor, false);
	}
	
}