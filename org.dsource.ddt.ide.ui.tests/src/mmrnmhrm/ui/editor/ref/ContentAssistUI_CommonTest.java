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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;

import java.lang.reflect.Method;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.core.Function;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposal;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;

import dtool.ast.definitions.DefUnit;

public class ContentAssistUI_CommonTest extends BaseDeeUITest {
	
	protected final ISourceModule srcModule;
	protected final ScriptEditor editor;
	
	public ContentAssistUI_CommonTest(IFile file) {
		this.editor = BaseDeeUITest.openDeeEditorForFile(file);
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	public ContentAssistUI_CommonTest(ISourceModule sourceModule) {
		this.srcModule = sourceModule;
		this.editor = BaseDeeUITest.openDeeEditorForFile((IFile) sourceModule.getResource());
	}
	
	protected int getMarkerStartPos(String markerString) {
		String source = editor.getScriptSourceViewer().getDocument().get();
		int ccOffset = source.indexOf(markerString);
		assertTrue(ccOffset >= 0);
		return ccOffset;
	}
	
	protected int getMarkerEndPos(String markerString) {
		String source = editor.getScriptSourceViewer().getDocument().get();
		int ccOffset = source.indexOf(markerString);
		assertTrue(ccOffset >= 0);
		return ccOffset + markerString.length();
	}
	
	public static void invokeContentAssist(ScriptEditor editor, int offset) {
		editor.getViewer().setSelectedRange(offset, 0);
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
	
	protected static boolean isProposalPopupActive(ContentAssistant ca) {
		Method method = ReflectionUtils.getAvailableMethod(ca.getClass(), "isProposalPopupActive");
		return ReflectionUtils.uncheckedInvoke(ca, method);
	}
	
	
	protected static ICompletionProposal[] getProposals(ContentAssistant ca) {
		// A bit of hack
		Object proposalPopup = ReflectionUtils.readField(ca, "fProposalPopup");
		return downCast(ReflectionUtils.readField(proposalPopup, "fFilteredProposals"));
	}
	
	/* ----------------------------------- */
	
	public final static Function<ICompletionProposal, DefUnit> proposalToDefunit = 
			new Function<ICompletionProposal, DefUnit>() {
		@Override
		public DefUnit evaluate(ICompletionProposal obj) {
			assertNotNull(obj);
			DeeCompletionProposal deeProposal = CoreUtil.tryCast(obj, DeeCompletionProposal.class);
			return deeProposal == null ? null : deeProposal.defUnit;
		}
	};
	
}