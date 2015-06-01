/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.folding;

/**
 * <p>
 * The kind of the folding block - class, method, comment, etc. The instances
 * are compared using <b>==</b>, so make sure you pass the same instance every
 * time.
 * </p>
 * 
 * <p>
 * The recommended way to implement is java enumeration, for example:
 * </p>
 * 
 * <pre>
 * public enum FoldingBlockKind implements IFoldingBlockKind {
 * 	CLASS, FUNCTION, COMMENT;
 * 
 * 	public boolean isComment() {
 * 		return this == COMMENT;
 * 	}
 * }
 * </pre>
 */
public interface IFoldingBlockKind {

	boolean isComment();

}
