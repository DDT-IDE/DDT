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
package mmrnmhrm.ui.editor.folding;

import org.eclipse.dltk.ui.text.folding.IFoldingBlockKind;

public enum DeeFoldingBlockKind implements IFoldingBlockKind {
	COMMENT(true),
	DOCCOMMENT(true),
	FUNCTION(false),
	AGGREGATE(false),
	MULTILINESTRING(false);
	
	
	public final boolean _isComment;
	
	DeeFoldingBlockKind(boolean isComment) {
		this._isComment = isComment;
	}
	
	@Override
	public boolean isComment() {
		return _isComment;
	}
	
}
