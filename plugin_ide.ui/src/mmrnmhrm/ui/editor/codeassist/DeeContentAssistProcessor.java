/*******************************************************************************
 * Copyright (c) 2013, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.codeassist;

import java.util.List;

import melnorme.lang.ide.ui.text.completion.LangContentAssistProcessor;
import melnorme.lang.ide.ui.text.completion.ILangCompletionProposalComputer;
import melnorme.lang.ide.ui.text.completion.LangContentAssistInvocationContext;
import melnorme.utilbox.core.CoreUtil;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.ui.IEditorPart;

public class DeeContentAssistProcessor extends LangContentAssistProcessor {
	
	public static class DeeContentAssistCategoriesBuilder extends ContentAssistCategoriesBuilder {
		@Override
		protected DeeCompletionProposalComputer createDefaultSymbolsProposalComputer() {
			return new DeeCompletionProposalComputer();
		}
		
		@Override
		protected ILangCompletionProposalComputer createSnippetsProposalComputer() {
			return new DeeCompletionProposalComputer() {
				@Override
				public List<ICompletionProposal> computeCompletionProposals(LangContentAssistInvocationContext context) {
					TemplateCompletionProcessor tplProcessor = new DeeSnippetCompletionProcessor(context);
					ICompletionProposal[] proposals = tplProcessor.computeCompletionProposals(
						context.getViewer(), context.getInvocationOffset());
					
					return CoreUtil.listFrom(proposals);
				}
			};
		}
	}
	
	public DeeContentAssistProcessor(IEditorPart fEditor, ContentAssistant assistant) {
		super(assistant, fEditor, new DeeContentAssistCategoriesBuilder().getCategories());
	}
	
}