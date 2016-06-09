/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import melnorme.lang.ide.ui.text.completion.LangCompletionProposal;
import melnorme.lang.tooling.ToolCompletionProposal;
import melnorme.lang.tooling.common.ISourceBuffer;
import mmrnmhrm.ui.editor.hover.HoverUtil;


/* TODO: DLTK review this code*/
public abstract class AbstractScriptCompletionProposal extends LangCompletionProposal {

	public AbstractScriptCompletionProposal(ISourceBuffer sourceBuffer, ToolCompletionProposal proposal, 
			Image image, IContextInformation contextInformation) {
		super(sourceBuffer, proposal, image, contextInformation);
	}

	/* --------------------------------- */
	
	/** The CSS used to format javadoc information. */
	private static String fgCSSStyles;
	
	/** Returns the style information for displaying HTML (Javadoc) content. */
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.getDDocPreparedCSS();
		}
		return fgCSSStyles;
	}
	
	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		String info= getProposalInfoString(monitor);
		return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
	}
	
	protected abstract String getProposalInfoString(IProgressMonitor monitor);
	
}