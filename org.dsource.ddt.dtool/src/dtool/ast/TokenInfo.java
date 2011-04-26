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


/**
 * Simple value type representing a string within a larger text range. 
 */
public final class TokenInfo {
	
	public String value;
	public int offset;
	
	public TokenInfo(String value, int offset) {
		this.value = value;
		this.offset = offset;
	}
	
	public TokenInfo(String name) {
		this.value = name;
		this.offset = -1;
	}
	
	public SourceRange getRange() {
		return new SourceRange(offset, value.length());
	}
	
}