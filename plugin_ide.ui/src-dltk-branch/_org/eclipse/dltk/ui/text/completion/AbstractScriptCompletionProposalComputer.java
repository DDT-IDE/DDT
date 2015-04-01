/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.dltk.ui.text.completion.IScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class AbstractScriptCompletionProposalComputer {

	/**
	 * Update relevance of template proposals that match with a keyword give
	 * those templates slightly more relevance than the keyword to sort them
	 * first.
	 */
	public void updateTemplateProposalRelevance(
			ScriptContentAssistInvocationContext context,
			ICompletionProposal[] proposals) {
		IScriptCompletionProposal[] keywords = context.getKeywordProposals();
		if (keywords == null || keywords.length == 0) {
			return;
		}
		for (int i = 0; i < proposals.length; ++i) {
			ICompletionProposal cp = proposals[i];
			if (cp instanceof ScriptTemplateProposal) {
				final ScriptTemplateProposal tp = (ScriptTemplateProposal) cp;
				final String name = tp.getPattern();
				for (int j = 0; j < keywords.length; ++j) {
					if (name.startsWith(keywords[j].getDisplayString())) {
						tp.setRelevance(keywords[j].getRelevance());
					}
				}
			}
		}
	}

}
