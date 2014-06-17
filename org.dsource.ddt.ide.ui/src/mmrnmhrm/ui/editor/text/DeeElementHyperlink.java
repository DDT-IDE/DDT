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
package mmrnmhrm.ui.editor.text;

import mmrnmhrm.ui.actions.OpenDefinitionOperation;
import mmrnmhrm.ui.actions.OpenDefinitionOperation.EOpenNewEditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * Lang element hyperlink.
 */
public class DeeElementHyperlink implements IHyperlink {

	private final IRegion fRegion;
	private final ITextEditor fTextEditor;
	private int offset;

	/**
	 * Creates a new Lang element hyperlink.
	 */
	public DeeElementHyperlink(int offset, IRegion region, ITextEditor textEditor) {
		Assert.isNotNull(textEditor);
		Assert.isNotNull(region);

		this.offset = offset;
		fRegion= region;
		fTextEditor= textEditor;
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}
	
	@Override
	public void open() {
		new OpenDefinitionOperation(fTextEditor, EOpenNewEditor.TRY_REUSING_EXISTING_EDITORS, offset).execute();
	}
	
	@Override
	public String getTypeLabel() {
		return "Go to D element definition";
	}
	
	@Override
	public String getHyperlinkText() {
		return "Go to D element definition";
	}
}