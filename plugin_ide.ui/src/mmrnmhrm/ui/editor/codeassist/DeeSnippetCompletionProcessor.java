/*******************************************************************************
 * Copyright (c) 2011, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.codeassist;

import melnorme.lang.ide.ui.editor.actions.SourceOperationContext;
import mmrnmhrm.ui.editor.templates.DeeTemplateAccess;
import mmrnmhrm.ui.editor.templates.DeeUniversalTemplateContextType;
import _org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import _org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;

public class DeeSnippetCompletionProcessor extends ScriptTemplateCompletionProcessor {
	
	private static char[] IGNORE = { '.' };
	
	public DeeSnippetCompletionProcessor(SourceOperationContext context) {
		super(context);
	}
	
	@Override
	protected String getContextTypeId() {
		return DeeUniversalTemplateContextType.CONTEXT_TYPE_ID;
	}
	
	@Override
	protected char[] getIgnore() {
		return IGNORE;
	}
	
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return DeeTemplateAccess.getInstance();
	}
	
}