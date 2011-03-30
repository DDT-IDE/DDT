/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.ref;


import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.tests.ui.BaseDeeUITest;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;

public class ContentAssistUI_CommonTest extends BaseDeeUITest {
	
	protected IFile file;
	protected ScriptEditor editor;
	
	public ContentAssistUI_CommonTest(IFile file) {
		this.file = file;
		this.editor = BaseDeeUITest.openDeeEditorForFile(file);
	}
	
	protected void invokeContentAssist() {
		ITextOperationTarget target= (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		if (target != null && target.canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS)) {
			target.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
		}
	}
	
	public static ContentAssistant getContentAssistant(ScriptEditor scriptEditor) {
		// Need to do this because AdaptedSourceViewer is not extendable
		Object caField = ReflectionUtils.readField(scriptEditor.getScriptSourceViewer(), "fContentAssistant");
		ContentAssistant ca = (ContentAssistant) caField;
		return ca;
	}
	
}