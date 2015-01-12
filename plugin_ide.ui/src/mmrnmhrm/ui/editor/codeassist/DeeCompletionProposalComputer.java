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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.utils.UIOperationExceptionHandler;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.engine_client.DeeCompletionOperation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.ui.IEditorPart;

public class DeeCompletionProposalComputer extends ScriptCompletionProposalComputer
	implements IScriptCompletionProposalComputer {
	
	public DeeCompletionProposalComputer() {
	}
	
	@Override
	protected ScriptCompletionProposalCollector createCollector(ScriptContentAssistInvocationContext context) {
		throw assertFail();
	}
	
	@Override
	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return new DeeTemplateCompletionProcessor(context);
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
			
			String filePath = EditorUtils.getFilePathFromEditorInput(editor.getEditorInput()).toString();
			if (filePath == null) {
				throw LangCore.createCoreException("Error: Could not determine file path for editor.", null);
			}
			
			Path fileLocation;
			try {
				fileLocation = PathUtil.createPath(filePath);
			} catch (InvalidPathExceptionX e) {
				throw LangCore.createCoreException("Invalid editor path.", e);
			}
			
			return doComputeCompletionProposals(offset, fileLocation, viewer.getDocument());
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
		
		CompletionSearchResult completionResult = performCompletionOperation(filePath, offset, document.get());
		
		ArrayList2<CompletionProposal> proposals = new DeeCompletionOperation().
				completionResultAdapt(completionResult, offset);
		
		DeeCompletionProposalCollector collector = new DeeCompletionProposalCollector();
		ArrayList2<ICompletionProposal> completionProposals = new ArrayList2<ICompletionProposal>();
		
		for (CompletionProposal proposal : proposals) {
			completionProposals.add(collector.adaptProposal(proposal));
		}
		
		return completionProposals;
	}
	
	protected CompletionSearchResult performCompletionOperation(final Path filePath, final int offset, String source)
			throws CoreException {
		try {
			DToolClient.getDefault().updateWorkingCopyIfInconsistent2(filePath, source);
			
			ExecutorTaskAgent completionExecutor = new ExecutorTaskAgent("CompletionExecutor");
			
			Future<CompletionSearchResult> future = completionExecutor.submit(new Callable<CompletionSearchResult>() {
				@Override
				public CompletionSearchResult call() throws Exception {
					return DToolClient.getDefault().doCodeCompletion(
						filePath, offset, DeeCompletionOperation.compilerPathOverride);
				}
			});
			
			try {
				return future.get(5, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException e) {
				throw LangCore.createCoreException("Error performing Content Assist.", e);
			} catch (TimeoutException e) {
				throw LangCore.createCoreException("Timeout performing Content Assist.", e);
			} finally {
				completionExecutor.shutdown();
			}
			
		} finally {
			DToolClient.getDefault().discardServerWorkingCopy(filePath);
		}
	}
	
}
