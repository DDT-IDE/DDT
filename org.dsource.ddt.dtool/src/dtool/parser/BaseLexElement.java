/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.misc.StringUtil.collToString;
import dtool.ast.SourceRange;
import dtool.parser.LexElement.MissingLexElement;


/**
 * Base class for a lex element. Can be a {@link LexElement} or {@link MissingLexElement}
 */
public abstract class BaseLexElement {
	
	protected final Token[] ignoredPrecedingTokens;
	
	public BaseLexElement(Token[] ignoredPrecedingTokens) {
		this.ignoredPrecedingTokens = ignoredPrecedingTokens;
	}
	
	public abstract boolean isMissingElement();
	
	/** Retrieve main token. If failOnMissing, receiver must not be missingElement. */
	public abstract Token getToken(boolean failOnMissing);
	
	public final Token getToken() {
		return getToken(true);
	}
	
	public abstract String getSourceValue();
	
	public abstract int getStartPos();
	
	public abstract int getEndPos();
	
	public abstract SourceRange getSourceRange();
	
	public final int getFullRangeStartPos() {
		if(ignoredPrecedingTokens != null && ignoredPrecedingTokens.length > 0) {
			return ignoredPrecedingTokens[0].getStartPos();
		}
		return getStartPos();
	}
	
	@Override
	public String toString() {
		return ignoredPrecedingTokens != null ? "【"+collToString(ignoredPrecedingTokens, "●")+"】" : "";
	}
	
}