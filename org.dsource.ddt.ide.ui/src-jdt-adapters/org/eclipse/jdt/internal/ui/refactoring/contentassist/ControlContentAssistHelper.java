/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.ui.refactoring.contentassist;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.widgets.Text;

/**
 * @since 3.0
 */
public class ControlContentAssistHelper {
	
	/**
	 * @param text the text field to install ContentAssist
	 * @param processor the <code>IContentAssistProcessor</code>
	 */
	public static void createTextContentAssistant(final Text text, IContentAssistProcessor processor) {
		//ContentAssistHandler.createHandlerForText(text, createJavaContentAssistant(processor));
	}


/*
	public static SubjectControlContentAssistant createJavaContentAssistant(IContentAssistProcessor processor) {
		final SubjectControlContentAssistant contentAssistant= new SubjectControlContentAssistant();
		
		contentAssistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		
		return contentAssistant;
	}
	*/
}
