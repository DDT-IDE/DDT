/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;

public class ASTUtils {
	
	public static ISourceReference adaptNodeToReference(final ASTNeoNode node) {
		return new ISourceReference() {
			
			@Override
			final public ISourceRange getSourceRange() {
				return new ISourceRange() {
					@Override
					final public int getOffset() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getOffset();
						return node.getOffset();
					}
					
					@Override
					final public int getLength() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getLength();
						return node.getLength();
					}
					
				};
			}
			
			@Override
			final public boolean exists() {
				return true;
			}
			
			@Override
			final public String getSource() throws ModelException {
				return null;
			}
		};
	}
	
}
