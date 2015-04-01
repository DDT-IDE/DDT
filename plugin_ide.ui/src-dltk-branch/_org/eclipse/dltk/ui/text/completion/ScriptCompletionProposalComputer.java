/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.ui.text.completion.ContentAssistInvocationContext;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

/**
 * Computes Script completion proposals and context infos.
 */
public abstract class ScriptCompletionProposalComputer extends
		AbstractScriptCompletionProposalComputer implements
		IScriptCompletionProposalComputer {

	private String fErrorMessage;

	public ScriptCompletionProposalComputer() {
	}

	@Override
	public String getErrorMessage() {
		return fErrorMessage;
	}

	@Override
	public void sessionStarted() {
	}

	@Override
	public void sessionEnded() {
		fErrorMessage = null;
	}
	
	/* -----------------  ----------------- */

	// Script language specific completion proposals like types or keywords
	protected abstract List<ICompletionProposal> computeScriptCompletionProposals(
			int offset, ScriptContentAssistInvocationContext context,
			IProgressMonitor monitor);

	// Completion proposals
	@Override
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {

		if (context instanceof ScriptContentAssistInvocationContext) {
			ScriptContentAssistInvocationContext scriptContext = (ScriptContentAssistInvocationContext) context;

			List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

			// Language specific proposals (already sorted and etc.)
			proposals.addAll(computeScriptCompletionProposals(
					context.getInvocationOffset(), scriptContext, monitor));

			// Template proposals (already sorted and etc.)
			proposals.addAll(computeTemplateCompletionProposals(context.getInvocationOffset(), scriptContext, monitor));

			return proposals;
		}

		return Collections.emptyList();
	}

	// Code template completion proposals for script language
	protected List<ICompletionProposal> computeTemplateCompletionProposals(
			int offset, ScriptContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		TemplateCompletionProcessor templateProcessor = createTemplateProposalComputer(context);
		if (templateProcessor != null) {
			ICompletionProposal[] proposals = templateProcessor
					.computeCompletionProposals(context.getViewer(), offset);
			if (proposals != null && proposals.length != 0) {
				updateTemplateProposalRelevance(context, proposals);
			}
			return Arrays.asList(proposals);
		}

		return Collections.emptyList();
	}

	protected TemplateCompletionProcessor createTemplateProposalComputer(ScriptContentAssistInvocationContext context) {
		return null;
	}
	
	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {//
		return Collections.emptyList();
	}

}