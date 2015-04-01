/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.codeassist;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.utils.UIOperationExceptionHandler;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.engine_client.DeeCompletionOperation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.ui.IEditorPart;

import _org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;

public class DeeCompletionProposalComputer extends ScriptCompletionProposalComputer
	implements IScriptCompletionProposalComputer {
	
	protected DToolClient dtoolclient = DToolClient.getDefault();
	
	public DeeCompletionProposalComputer() {
	}
	
	protected String errorMessage;
	protected IEditorPart editor;
	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	@Override
	protected List<ICompletionProposal> computeScriptCompletionProposals(int offset,
			ScriptContentAssistInvocationContext context, IProgressMonitor monitor) {
		errorMessage = null;
		editor = context.getEditor();
		return computeCompletionProposals(context.getViewer(), offset);
	}

	public List<ICompletionProposal> computeCompletionProposals(ITextViewer viewer, int offset) {
		errorMessage = null;
		try {
			if(editor == null) {
				throw LangCore.createCoreException("Error, no editor available for operation.", null);
			}
			
			Location fileLocation = EditorUtils.getLocationFromEditorInput(editor.getEditorInput());
			if(fileLocation == null) {
				throw LangCore.createCoreException("Error, invalid location for editor input.", null);
			}
			
			return doComputeCompletionProposals(offset, fileLocation.path, viewer.getDocument());
		} catch (CoreException ce) {
			if(DeeCompletionOperation.compilerPathOverride == null) {
				UIOperationExceptionHandler.handleOperationStatus("Content Assist", ce);
			} else {
				// We are in tests mode
			}
			errorMessage = ce.getMessage();
			return Collections.EMPTY_LIST;
		}
	}
	
	protected List<ICompletionProposal> doComputeCompletionProposals(int offset, Path filePath, 
			IDocument document) throws CoreException {
		
		CompletionSearchResult completionResult = dtoolclient.performCompletionOperation(
			filePath, offset, document.get(), 5000);
		
		ArrayList2<CompletionProposal> proposals = new DeeCompletionOperation().
				completionResultAdapt(completionResult, offset);
		
		DeeCompletionProposalCollector collector = new DeeCompletionProposalCollector();
		ArrayList2<ICompletionProposal> completionProposals = new ArrayList2<ICompletionProposal>();
		
		for (CompletionProposal proposal : proposals) {
			completionProposals.add(collector.adaptProposal(proposal));
		}
		
		return completionProposals;
	}
	
	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new DeeTemplateCompletionProcessor(context);
	}
	
	
	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		return super.computeContextInformation(context, monitor);
	}
	
}