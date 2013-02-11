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
package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;


/**
 * Simple value type representing a string and an associated source range. 
 */
@Deprecated
public final class TokenInfo {
	
	public static final String SYNTAX_ERROR = "<syntax_error>";
	
	public final String value;
	public final int offset;
	
	public TokenInfo(String value, int offset) {
		assertNotNull(value);
		this.value = value;
		this.offset = offset;
	}
	
	public String getString() {
		return value;
	}
	
	public SourceRange getSourceRange() {
		return (offset == -1 || value == SYNTAX_ERROR) ? null : 
			new SourceRange(offset, value.length());
	}
	
}