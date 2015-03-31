/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.java;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;

/**
 * A alphabetic proposal based sorter.
 *
 * @since 3.2
 */
public final class AlphabeticSorter extends AbstractProposalSorter {
	
	protected final CompletionProposalComparator fComparator= new CompletionProposalComparator(true);
	
	public AlphabeticSorter() {
	}
	
	@Override
	public int compare(ICompletionProposal p1, ICompletionProposal p2) {
		return fComparator.compare(p1, p2);
	}
	
}