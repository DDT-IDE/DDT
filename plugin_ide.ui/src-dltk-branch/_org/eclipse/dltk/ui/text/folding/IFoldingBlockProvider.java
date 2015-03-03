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

import org.eclipse.dltk.ui.text.folding.AbortFoldingException;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.jface.preference.IPreferenceStore;

import _org.eclipse.dltk.ui.text.folding.DelegatingFoldingStructureProvider.FoldingContent;

/**
 * Folding block provider. Implementations should be contributed to
 * <code>org.eclipse.dltk.ui.folding/blockProvider</code> extension point.
 */
public interface IFoldingBlockProvider {

	void initializePreferences(IPreferenceStore preferenceStore);

	void setRequestor(IFoldingBlockRequestor requestor);

	/**
	 * Returns the minimal number of lines for reported blocks. Blocks
	 * containing fewer lines won't be folded. Single line blocks couldn't be
	 * folded anyway.
	 * 
	 * If you want finer control over blocks return 0 here and make decision in
	 * your code.
	 * 
	 * @return
	 */
	int getMinimalLineCount();

	/**
	 * Compute foldable blocks and report them to the requestor provided via
	 * separate call. If current folding operation should be interrupted (e.g.
	 * because of unrecoverable syntax error) then provider should throw
	 * {@link AbortFoldingException}
	 * 
	 * @param content
	 * @throws AbortFoldingException
	 */
	void computeFoldableBlocks(FoldingContent content);

}
