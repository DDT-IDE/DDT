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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.IPartitioningProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockKind;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.FastPartitioner;

import _org.eclipse.dltk.ui.text.folding.DelegatingFoldingStructureProvider.FoldingContent;

/**
 * Abstract implementation of {@link IFoldingBlockProvider} to fold
 * comments/documentation based on document partitioning.
 * 
 * Extend it and in the body of computeFoldableBlocks() make a few calls to
 * {@link #computeBlocksForPartitionType(IFoldingContent, String, IFoldingBlockKind, boolean)}
 * 
 * @since 2.0
 */
public abstract class PartitioningFoldingBlockProvider {

	private final IPartitioningProvider partitioningProvider;

	public PartitioningFoldingBlockProvider(IPartitioningProvider partitioningProvider) {
		this.partitioningProvider = partitioningProvider;
	}

	private int fBlockLinesMin;
	private boolean fDocsFolding;
	private boolean fCommentsFolding;
	private boolean fFoldNewLines;
	private boolean fInitCollapseComments;
	private boolean fInitCollapseDocs;
	private boolean fInitCollapseHeaderComments;

	public void initializePreferences(IPreferenceStore preferenceStore) {
		fBlockLinesMin = preferenceStore
				.getInt(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT);
		fDocsFolding = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED);
		fCommentsFolding = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED);
		fFoldNewLines = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES);
		fInitCollapseComments = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS);
		fInitCollapseHeaderComments = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_HEADER_COMMENTS);
		fInitCollapseDocs = preferenceStore
				.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_DOCS);
	}

	protected boolean isFoldingDocs() {
		return fDocsFolding;
	}

	protected void setFoldingDocs(boolean value) {
		this.fDocsFolding = value;
	}

	protected boolean isFoldingComments() {
		return fCommentsFolding;
	}

	protected void setFoldingComments(boolean value) {
		this.fCommentsFolding = value;
	}

	protected boolean isJoinCommentsSeparatedByEmptyLines() {
		return fFoldNewLines;
	}

	protected void setJoinCommentsSeparatedByEmptyLines(boolean value) {
		this.fFoldNewLines = value;
	}

	protected boolean isCollapseComments() {
		return fInitCollapseComments;
	}

	protected boolean isCollapseHeaderComment() {
		return fInitCollapseHeaderComments;
	}

	protected boolean isCollapseDocs() {
		return fInitCollapseDocs;
	}

	protected void setCollapseComments(boolean value) {
		this.fInitCollapseComments = value;
	}

	protected void setCollapseHeaderComment(boolean value) {
		this.fInitCollapseHeaderComments = value;
	}

	protected void setCollapseDocs(boolean value) {
		this.fInitCollapseDocs = value;
	}

	public int getMinimalLineCount() {
		return fBlockLinesMin;
	}

	protected void setMinimalLineCount(int value) {
		this.fBlockLinesMin = value;
	}

	protected IFoldingBlockRequestor requestor;

	public void setRequestor(IFoldingBlockRequestor requestor) {
		this.requestor = requestor;
	}

	private List<ITypedRegion> computePartitioning(Document d) {
		// TODO TextUtilities.computePartitioning() ?
		List<ITypedRegion> docRegionList = new ArrayList<ITypedRegion>();
		int offset = 0;
		for (;;) {
			try {
				ITypedRegion region = TextUtilities.getPartition(d,
						partitioningProvider.getPartitioning(), offset, true);
				docRegionList.add(region);
				offset = region.getLength() + region.getOffset() + 1;
			} catch (BadLocationException e1) {
				break;
			}
		}
		return docRegionList;
	}

	protected void computeBlocksForPartitionType(FoldingContent content,
			String partition, IFoldingBlockKind kind, boolean collapse) {
		try {
			final String contents = content.getSourceContents();
			if (contents == null || contents.length() == 0) {
				return;
			}
			Document document = new Document(contents);
			installDocumentStuff(document);
			ITypedRegion start = null;
			ITypedRegion lastRegion = null;
			List<IRegion> regions = new ArrayList<IRegion>();
			for (ITypedRegion region : computePartitioning(document)) {
				if (region.getType().equals(partition)
						&& startsAtLineBegin(document, region)) {
					if (start == null)
						start = region;
				} else if (start != null
						&& (isBlankRegion(document, region) || isEmptyRegion(
								document, region)
								&& isJoinCommentsSeparatedByEmptyLines())) {
					// blanks or empty lines
					// TODO introduce line limit for collapseEmptyLines() ?
				} else {
					if (start != null) {
						assert lastRegion != null;
						int offset0 = start.getOffset();
						int length0 = lastRegion.getOffset()
								+ lastRegion.getLength() - offset0 - 1;
						length0 = contents
								.substring(offset0, offset0 + length0).trim()
								.length();
						regions.add(new Region(offset0, length0));
					}
					start = null;
				}
				lastRegion = region;
			}
			if (start != null) {
				assert lastRegion != null;
				int offset0 = start.getOffset();
				int length0 = lastRegion.getOffset() - offset0
						+ lastRegion.getLength() - 1;
				regions.add(new Region(offset0, length0));
			}
			reportRegions(document, regions, kind, collapse);
			removeDocumentStuff(document);
		} catch (BadLocationException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param document
	 * @param regions
	 * @param kind
	 * @param collapse
	 * @throws BadLocationException
	 */
	protected void reportRegions(Document document, List<IRegion> regions,
			IFoldingBlockKind kind, boolean collapse)
			throws BadLocationException {
		for (IRegion region : regions) {
			// TODO
			Object element = null;
			requestor.acceptBlock(region.getOffset(), region.getOffset()
					+ region.getLength(), kind, element, collapse);
		}
	}

	/**
	 * Tests if the specified region contains only space or tab characters.
	 * 
	 * @param document
	 * @param region
	 * @return
	 * @throws BadLocationException
	 */
	protected boolean isBlankRegion(IDocument document, ITypedRegion region)
			throws BadLocationException {
		String value = document.get(region.getOffset(), region.getLength());
		for (int i = 0; i < value.length(); ++i) {
			char ch = value.charAt(i);
			if (ch != ' ' && ch != '\t') {
				return false;
			}
		}
		return true;
	}

	private boolean startsAtLineBegin(Document d, ITypedRegion region)
			throws BadLocationException {
		int lineStart = d.getLineOffset(d.getLineOfOffset(region.getOffset()));
		if (lineStart != region.getOffset()) {
			if (!isEmptyRegion(d, lineStart, region.getOffset() - lineStart)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isEmptyRegion(IDocument d, ITypedRegion r)
			throws BadLocationException {
		return isEmptyRegion(d, r.getOffset(), r.getLength());
	}

	protected boolean isEmptyRegion(IDocument d, int offset, int length)
			throws BadLocationException {
		return d.get(offset, length).trim().length() == 0;
	}

	/**
	 * Installs a partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private void installDocumentStuff(Document document) {
		final IDocumentPartitioner partitioner = new FastPartitioner(
				partitioningProvider.createPartitionScanner(),
				partitioningProvider.getPartitionContentTypes());
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioningProvider.getPartitioning(),
				partitioner);
	}

	/**
	 * Removes partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private void removeDocumentStuff(Document document) {
		final String partitioning = partitioningProvider.getPartitioning();
		final IDocumentPartitioner partitioner = document
				.getDocumentPartitioner(partitioning);
		if (partitioner != null) {
			document.setDocumentPartitioner(partitioning, null);
			partitioner.disconnect();
		}
	}

}