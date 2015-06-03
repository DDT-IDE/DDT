/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.folding;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.fields.IFieldValueListener;
import mmrnmhrm.ui.editor.folding.DeeCodeFoldingBlockProvider;
import mmrnmhrm.ui.editor.folding.DeeCommentFoldingBlockProvider;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.IProjectionPosition;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.IDocumentProvider;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import _org.eclipse.dltk.ui.DLTKUIPlugin;
import _org.eclipse.jdt.internal.ui.text.DocumentCharacterIterator;

/**
 * This implementation of {@link IFoldingStructureProvider} delegates the actual
 * work to the contributed {@link IFoldingBlockProvider}s
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
/* XXX: DLTK: review this code */
public class DelegatingFoldingStructureProvider implements
		IFoldingStructureProvider, IFoldingStructureProviderExtension {

	private static final boolean DEBUG = false;

	/**
	 * A context that contains the information needed to compute the folding
	 * structure of an  ISourceModule . Computed folding regions are
	 * collected via
	 * {@link #addProjectionRange(DelegatingFoldingStructureProvider.ScriptProjectionAnnotation, Position)
	 * addProjectionRange}.
	 */
	public static final class FoldingStructureComputationContext {
		private final ProjectionAnnotationModel fModel;
		private final IDocument fDocument;
		private final boolean fAllowCollapsing;
		protected LinkedHashMap<Annotation, Position> fMap = new LinkedHashMap<Annotation, Position>();
		
		protected final SourceFileStructure sourceFileStructure;
		
		public FoldingStructureComputationContext(IDocument document,
				ProjectionAnnotationModel model, boolean allowCollapsing, SourceFileStructure sourceFileStructure) {
			fDocument = document;
			fModel = model;
			fAllowCollapsing = allowCollapsing;
			this.sourceFileStructure = sourceFileStructure;
		}

		public Map<Annotation, Position> getMap() {
			return fMap;
		}

		/**
		 * Returns <code>true</code> if newly created folding regions may be
		 * collapsed, <code>false</code> if not. This is usually
		 * <code>false</code> when updating the folding structure while typing;
		 * it may be <code>true</code> when computing or restoring the initial
		 * folding structure.
		 * 
		 * @return <code>true</code> if newly created folding regions may be
		 *         collapsed, <code>false</code> if not
		 */
		public boolean allowCollapsing() {
			return fAllowCollapsing;
		}

		/**
		 * Returns the document which contains the code being folded.
		 * 
		 * @return the document which contains the code being folded
		 */
		IDocument getDocument() {
			return fDocument;
		}

		ProjectionAnnotationModel getModel() {
			return fModel;
		}

		/**
		 * Adds a projection (folding) region to this context. The created
		 * annotation / position pair will be added to the
		 * {@link ProjectionAnnotationModel} of the {@link ProjectionViewer} of
		 * the editor.
		 * 
		 * @param annotation
		 *            the annotation to add
		 * @param position
		 *            the corresponding position
		 */
		public void addProjectionRange(ScriptProjectionAnnotation annotation,
				Position position) {
			fMap.put(annotation, position);
		}
	}

	protected static final class AnnotationKey {

		final IFoldingBlockKind kind;
		final Object element;

		public AnnotationKey(IFoldingBlockKind kind, Object element) {
			this.kind = kind;
			this.element = element;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AnnotationKey) {
				final AnnotationKey other = (AnnotationKey) obj;
				return kind == other.kind && element.equals(other.element);
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return element.hashCode();
		}

		@Override
		public String toString() {
			return kind.toString() + " " + element.toString();
		}
	}

	/**
	 * A {@link ProjectionAnnotation} for code.
	 */
	private static final class ScriptProjectionAnnotation extends
			ProjectionAnnotation {

		final AnnotationKey stamp;

		/**
		 * Creates a new projection annotation.
		 * 
		 * @param isCollapsed
		 *            <code>true</code> to set the initial state to collapsed,
		 *            <code>false</code> to set it to expanded
		 * @param kind
		 *            foldable element kind
		 * @param element
		 *            foldable element identity
		 */
		public ScriptProjectionAnnotation(boolean isCollapsed,
				IFoldingBlockKind kind, Object element) {
			super(isCollapsed);
			this.stamp = new AnnotationKey(kind, element);
		}

		public Object getElement() {
			return stamp.element;
		}

		IFoldingBlockKind getKind() {
			return stamp.kind;
		}

		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ScriptProjectionAnnotation(" + //$NON-NLS-1$
					"collapsed: " + isCollapsed() + ", " + //$NON-NLS-1$ //$NON-NLS-2$
					"element:" + getElement() + ", " + //$NON-NLS-1$ //$NON-NLS-2$
					"kind: " + getKind() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static final class Tuple {
		final ScriptProjectionAnnotation annotation;
		final Position position;

		Tuple(ScriptProjectionAnnotation annotation, Position position) {
			this.annotation = annotation;
			this.position = position;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + position.toString() + "]";
		}
	}

	/**
	 * Filter for annotations.
	 */
	private static interface Filter {
		boolean match(ScriptProjectionAnnotation annotation);
	}

	/**
	 * Matches comments.
	 */
	private static final class CommentFilter implements Filter {
		public CommentFilter() {
		}

		@Override
		public boolean match(ScriptProjectionAnnotation annotation) {
			if (annotation.getKind().isComment()
					&& !annotation.isMarkedDeleted()) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Projection position that will return two foldable regions: one folding
	 * away the region from after the '/**' to the beginning of the content, the
	 * other from after the first content line until after the comment.
	 */
	private static final class CommentPosition extends Position implements
			IProjectionPosition {
		CommentPosition(int offset, int length) {
			super(offset, length);
		}

		/*
		 * @seeorg.eclipse.jface.text.source.projection.IProjectionPosition#
		 * computeFoldingRegions(org.eclipse.jface.text.IDocument)
		 */
		@Override
		public IRegion[] computeProjectionRegions(IDocument document)
				throws BadLocationException {
			DocumentCharacterIterator sequence = new DocumentCharacterIterator(
					document, offset, offset + length);
			int prefixEnd = 0;
			int contentStart = findFirstContent(sequence, prefixEnd);
			int firstLine = document.getLineOfOffset(offset + prefixEnd);
			int captionLine = document.getLineOfOffset(offset + contentStart);
			int lastLine = document.getLineOfOffset(offset + length);
			// Assert.isTrue(firstLine <= captionLine, "first folded line is
			// greater than the caption line"); //$NON-NLS-1$
			// Assert.isTrue(captionLine <= lastLine, "caption line is greater
			// than the last folded line"); //$NON-NLS-1$
			IRegion preRegion;
			if (firstLine < captionLine) {
				// preRegion= new Region(offset + prefixEnd, contentStart -
				// prefixEnd);
				int preOffset = document.getLineOffset(firstLine);
				IRegion preEndLineInfo = document
						.getLineInformation(captionLine);
				int preEnd = preEndLineInfo.getOffset();
				preRegion = new Region(preOffset, preEnd - preOffset);
			} else {
				preRegion = null;
			}
			if (captionLine < lastLine) {
				int postOffset = document.getLineOffset(captionLine + 1);
				IRegion postRegion = new Region(postOffset, offset + length
						- postOffset);
				if (preRegion == null)
					return new IRegion[] { postRegion };
				return new IRegion[] { preRegion, postRegion };
			}
			if (preRegion != null)
				return new IRegion[] { preRegion };
			return null;
		}

		/**
		 * Finds the offset of the first identifier part within
		 * <code>content</code>. Returns 0 if none is found.
		 * 
		 * @param content
		 *            the content to search
		 * @return the first index of a unicode identifier part, or zero if none
		 *         can be found
		 */
		private int findFirstContent(final CharSequence content, int prefixEnd) {
			int lenght = content.length();
			for (int i = prefixEnd; i < lenght; i++) {
				if (Character.isUnicodeIdentifierPart(content.charAt(i)))
					return i;
			}
			return 0;
		}

		@Override
		public int computeCaptionOffset(IDocument document) throws BadLocationException {
			DocumentCharacterIterator sequence = new DocumentCharacterIterator(
					document, offset, offset + length);
			return findFirstContent(sequence, 0);
		}
	}

	/**
	 * Projection position that will return two foldable regions: one folding
	 * away the lines before the one containing the simple name of the script
	 * element, one folding away any lines after the caption.
	 */
	private static final class ScriptElementPosition extends Position implements
			IProjectionPosition {
		public ScriptElementPosition(int offset, int length) {
			super(offset, length);
		}

		/*
		 * @seeorg.eclipse.jface.text.source.projection.IProjectionPosition#
		 * computeFoldingRegions(org.eclipse.jface.text.IDocument)
		 */
		@Override
		public IRegion[] computeProjectionRegions(IDocument document)
				throws BadLocationException {
			int nameStart = offset;
			int firstLine = document.getLineOfOffset(offset);
			int captionLine = document.getLineOfOffset(nameStart);
			int lastLine = document.getLineOfOffset(offset + length);
			/*
			 * see comment above - adjust the caption line to be inside the
			 * entire folded region, and rely on later element deltas to correct
			 * the name range.
			 */
			if (captionLine < firstLine)
				captionLine = firstLine;
			if (captionLine > lastLine)
				captionLine = lastLine;
			IRegion preRegion;
			if (firstLine < captionLine) {
				int preOffset = document.getLineOffset(firstLine);
				IRegion preEndLineInfo = document
						.getLineInformation(captionLine);
				int preEnd = preEndLineInfo.getOffset();
				preRegion = new Region(preOffset, preEnd - preOffset);
			} else {
				preRegion = null;
			}
			if (captionLine < lastLine) {
				int postOffset = document.getLineOffset(captionLine + 1);
				IRegion postRegion = new Region(postOffset, offset + length
						- postOffset);
				if (preRegion == null)
					return new IRegion[] { postRegion };
				return new IRegion[] { preRegion, postRegion };
			}
			if (preRegion != null)
				return new IRegion[] { preRegion };
			return null;
		}

		/*
		 * @seeorg.eclipse.jface.text.source.projection.IProjectionPosition#
		 * computeCaptionOffset(org.eclipse.jface.text.IDocument)
		 */
		@Override
		public int computeCaptionOffset(IDocument document) {
			return 0;
		}
	}

	/**
	 * Internal projection listener.
	 */
	private final class ProjectionListener implements IProjectionListener {
		private ProjectionViewer fViewer;

		/**
		 * Registers the listener with the viewer.
		 * 
		 * @param viewer
		 *            the viewer to register a listener with
		 */
		public ProjectionListener(ProjectionViewer viewer) {
			fViewer = viewer;
			fViewer.addProjectionListener(this);
		}

		/**
		 * Disposes of this listener and removes the projection listener from
		 * the viewer.
		 */
		public void dispose() {
			if (fViewer != null) {
				fViewer.removeProjectionListener(this);
				fViewer = null;
			}
		}

		/*
		 * @seeorg.eclipse.jface.text.source.projection.IProjectionListener#
		 * projectionEnabled()
		 */
		@Override
		public void projectionEnabled() {
			handleProjectionEnabled();
		}

		/*
		 * @seeorg.eclipse.jface.text.source.projection.IProjectionListener#
		 * projectionDisabled()
		 */
		@Override
		public void projectionDisabled() {
			handleProjectionDisabled();
		}
	}

	protected final ScriptEditor editor; // XXX: this field is used in a different way than fEditor, it can't be null
	
	private ScriptEditor fEditor;
	private IFoldingBlockProvider[] blockProviders;
	private ProjectionListener fProjectionListener;
	/** Comment filter, matches comments. */
	private final Filter fCommentFilter = new CommentFilter();
	private IPreferenceStore fStore;

	public DelegatingFoldingStructureProvider(ScriptEditor editor) {
		this.editor = assertNotNull(editor);
		// empty constructor
	}

	public void install(ScriptEditor editor, ProjectionViewer viewer, IPreferenceStore store) {
		internalUninstall();
		fStore = store;
		fEditor = assertNotNull(editor);
		fProjectionListener = new ProjectionListener(viewer);
		blockProviders = new IFoldingBlockProvider[] {
			new DeeCommentFoldingBlockProvider(), new DeeCodeFoldingBlockProvider() 
		};
		
	}

	@Override
	public void uninstall() {
		internalUninstall();
	}

	/**
	 * Internal implementation of {@link #uninstall()}.
	 */
	private void internalUninstall() {
		if (isInstalled()) {
			handleProjectionDisabled();
			fProjectionListener.dispose();
			fProjectionListener = null;
			fEditor = null;
			blockProviders = null;
		}
	}

	/**
	 * Returns <code>true</code> if the provider is installed,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the provider is installed,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isInstalled() {
		return fEditor != null;
	}

	/**
	 * Called whenever projection is enabled, for example when the viewer issues
	 * a {@link IProjectionListener#projectionEnabled() projectionEnabled}
	 * message. When the provider is already enabled when this method is called,
	 * it is first {@link #handleProjectionDisabled() disabled}.
	 * <p>
	 * Subclasses may extend.
	 * </p>
	 */
	protected void handleProjectionEnabled() {
		handleProjectionDisabled();
		if(fEditor != null) {
			initialize();
			
			elementListener = new IFieldValueListener() {
				@Override
				public void fieldValueChanged() {
					update(createContext(false, fEditor.getSourceStructure()));
				}
			};
			editor.getStructureField().addListener(elementListener);
		}
	}
	
	private IFieldValueListener elementListener;
	

	/**
	 * Called whenever projection is disabled, for example when the provider is
	 * {@link #uninstall() uninstalled}, when the viewer issues a
	 * {@link IProjectionListener#projectionDisabled() projectionDisabled}
	 * message and before {@link #handleProjectionEnabled() enabling} the
	 * provider. Implementations must be prepared to handle multiple calls to
	 * this method even if the provider is already disabled.
	 * <p>
	 * Subclasses may extend.
	 * </p>
	 */
	protected void handleProjectionDisabled() {
		if (elementListener != null) {
			editor.getStructureField().removeListener(elementListener);
			elementListener = null;
		}
	}

	@Override
	public final void initialize() {
		initialize(false);
	}

	@Override
	public final void initialize(boolean isReinit) {
		// don't auto collapse if reinitializing
		update(createInitialContext(isReinit));
	}

	protected FoldingStructureComputationContext createInitialContext(boolean isReinit) {
		if (blockProviders != null) {
			for (IFoldingBlockProvider provider : blockProviders) {
				provider.initializePreferences(fStore);
			}
		}
		
		// don't auto collapse if reinitializing
		SourceFileStructure structure = editor.getSourceStructure();
		return createContext((isReinit) ? false : true, structure);
	}
	
	protected FoldingStructureComputationContext createContext(boolean allowCollapse, 
			SourceFileStructure sourceFileStructure) {
		if (!isInstalled())
			return null;
		ProjectionAnnotationModel model = getModel();
		if (model == null)
			return null;
		IDocument doc = getDocument();
		if (doc == null)
			return null;
		return new FoldingStructureComputationContext(doc, model, allowCollapse, sourceFileStructure);
	}

	static class Lock {

		private boolean locked;

		/**
		 * Tries to lock and returns <code>true</code> if attempt was successful
		 */
		synchronized boolean lock() {
			if (!locked) {
				locked = true;
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Unlocks
		 */
		synchronized void unlock() {
			locked = false;
		}

	}

	private final Lock lock = new Lock();

	protected void update(FoldingStructureComputationContext ctx) {
		if (ctx == null)
			return;
		if (lock.lock()) {
			try {
				update0(ctx);
			} finally {
				lock.unlock();
			}
		}
	}

	private void update0(FoldingStructureComputationContext ctx) {
		Map<Annotation, Position> additions = new HashMap<Annotation, Position>();
		List<Annotation> deletions = new ArrayList<Annotation>();
		List<Annotation> updates = new ArrayList<Annotation>();
		if (!computeFoldingStructure(ctx)) {
			return;
		}
		Map<Annotation, Position> updated = ctx.fMap;
		Map<AnnotationKey, List<Tuple>> previous = computeCurrentStructure(ctx);
		for (Iterator<Annotation> e = updated.keySet().iterator(); e.hasNext();) {
			ScriptProjectionAnnotation newAnnotation = (ScriptProjectionAnnotation) e
					.next();
			AnnotationKey stamp = newAnnotation.stamp;
			Position newPosition = updated.get(newAnnotation);
			List<Tuple> annotations = previous.get(stamp);
			if (annotations == null) {
				additions.put(newAnnotation, newPosition);
			} else {
				Iterator<Tuple> x = annotations.iterator();
				boolean matched = false;
				while (x.hasNext()) {
					Tuple tuple = x.next();
					ScriptProjectionAnnotation existingAnnotation = tuple.annotation;
					Position existingPosition = tuple.position;
					if (existingPosition != null
							&& (!newPosition.equals(existingPosition) || ctx
									.allowCollapsing()
									&& existingAnnotation.isCollapsed() != newAnnotation
											.isCollapsed())) {
						existingPosition.setOffset(newPosition.getOffset());
						existingPosition.setLength(newPosition.getLength());
						if (ctx.allowCollapsing()
								&& existingAnnotation.isCollapsed() != newAnnotation
										.isCollapsed())
							if (newAnnotation.isCollapsed())
								existingAnnotation.markCollapsed();
							else
								existingAnnotation.markExpanded();
						updates.add(existingAnnotation);
					}
					matched = true;
					x.remove();
					break;
				}
				if (!matched)
					additions.put(newAnnotation, newPosition);
				if (annotations.isEmpty())
					previous.remove(stamp);
			}
		}
		for (List<Tuple> list : previous.values()) {
			int size = list.size();
			for (int i = 0; i < size; i++)
				deletions.add(list.get(i).annotation);
		}
		if (DEBUG) {
			System.out.println(getClass().getSimpleName() + ".update:"
					+ " additions=" + additions.size() + " deletions="
					+ deletions.size() + " updates=" + updates.size());
		}
		if (!(deletions.isEmpty() && additions.isEmpty() && updated.isEmpty())) {
			ctx.getModel().modifyAnnotations(
					deletions.toArray(new Annotation[deletions.size()]),
					additions, updates.toArray(new Annotation[updates.size()]));
		}
	}

	private boolean computeFoldingStructure(FoldingStructureComputationContext ctx) {
		try {
			if (blockProviders == null || fEditor == null) {
				return false;
			}
			Path filePath = EditorUtils.getFilePathFromEditorInput(fEditor.getEditorInput());
			IDocument document = fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
			
			final FoldingContent content = new FoldingContent(document.get(), filePath);
			final Requestor requestor = new Requestor(content, ctx);
			
			if(ctx.sourceFileStructure != null){
				for (IFoldingBlockProvider provider : blockProviders) {
					provider.setRequestor(requestor);
					requestor.lineCountDelta = Math.max(1, provider.getMinimalLineCount() - 1);
					provider.computeFoldableBlocks(content, ctx.sourceFileStructure);
					provider.setRequestor(null);
				}
			}
			return true;
		} catch (AbortFoldingException e) {
			return false;
		} catch (RuntimeException e) {
			DLTKUIPlugin.logError("Error in FoldingBlockProvider", e);
			blockProviders = null;
			return false;
		}
	}


	protected boolean isEmptyRegion(IDocument d, ITypedRegion r)
			throws BadLocationException {
		return isEmptyRegion(d, r.getOffset(), r.getLength());
	}

	/**
	 * Tests if the specified region contains only space or tab characters.
	 * 
	 * @param document
	 * @param region
	 * @return
	 * @throws BadLocationException
	 * @since 2.0
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

	protected boolean isEmptyRegion(IDocument d, int offset, int length)
			throws BadLocationException {
		return d.get(offset, length).trim().length() == 0;
	}

	/**
	 * Creates a comment folding position from an
	 * {@link #alignRegion(IRegion, DelegatingFoldingStructureProvider.FoldingStructureComputationContext)
	 * aligned} region.
	 * 
	 * @param aligned
	 *            an aligned region
	 * @return a folding position corresponding to <code>aligned</code>
	 */
	protected static final Position createCommentPosition(IRegion aligned) {
		return new CommentPosition(aligned.getOffset(), aligned.getLength());
	}

	/**
	 * Creates a folding position that remembers its member from an
	 * {@link #alignRegion(IRegion, DelegatingFoldingStructureProvider.FoldingStructureComputationContext)
	 * aligned} region.
	 * 
	 * @param aligned
	 *            an aligned region
	 * 
	 * @return a folding position corresponding to <code>aligned</code>
	 */
	protected static final Position createMemberPosition(IRegion aligned) {
		return new ScriptElementPosition(aligned.getOffset(),
				aligned.getLength());
	}

	/**
	 * Aligns <code>region</code> to start and end at a line offset. The
	 * region's start is decreased to the next line offset, and the end offset
	 * increased to the next line start or the end of the document.
	 * <code>null</code> is returned if <code>region</code> is <code>null</code>
	 * itself or does not comprise at least one line delimiter, as a single line
	 * cannot be folded.
	 * 
	 * @param region
	 *            the region to align, may be <code>null</code>
	 * @param ctx
	 *            the folding context
	 * @return a region equal or greater than <code>region</code> that is
	 *         aligned with line offsets, <code>null</code> if the region is too
	 *         small to be foldable (e.g. covers only one line)
	 */
	protected static IRegion alignRegion(IRegion region,
			FoldingStructureComputationContext ctx, int lineCountDelta) {
		if (region == null)
			return null;
		IDocument document = ctx.getDocument();
		try {
			int start = document.getLineOfOffset(region.getOffset());
			int end = document.getLineOfOffset(region.getOffset()
					+ region.getLength());
			if (start + lineCountDelta > end)
				return null;
			int offset = document.getLineOffset(start);
			int endOffset;
			if (document.getNumberOfLines() > end + 1) {
				endOffset = document.getLineOffset(end + 1);
			} else {
				endOffset = document.getLineOffset(end)
						+ document.getLineLength(end);
			}
			return new Region(offset, endOffset - offset);
		} catch (BadLocationException x) {
			// concurrent modification
			return null;
		}
	}

	private ProjectionAnnotationModel getModel() {
		return (ProjectionAnnotationModel) fEditor
				.getAdapter(ProjectionAnnotationModel.class);
	}

	private IDocument getDocument() {
		IDocumentProvider provider = fEditor.getDocumentProvider();
		return provider.getDocument(fEditor.getEditorInput());
	}

	private Map<AnnotationKey, List<Tuple>> computeCurrentStructure(FoldingStructureComputationContext ctx) {
		Map<AnnotationKey, List<Tuple>> map = new HashMap<AnnotationKey, List<Tuple>>();
		ProjectionAnnotationModel model = ctx.getModel();
		Iterator<?> e = model.getAnnotationIterator();
		while (e.hasNext()) {
			Object annotation = e.next();
			if (annotation instanceof ScriptProjectionAnnotation) {
				ScriptProjectionAnnotation ann = (ScriptProjectionAnnotation) annotation;
				Position position = model.getPosition(ann);
				List<Tuple> list = map.get(ann.stamp);
				if (list == null) {
					list = new ArrayList<Tuple>(2);
					map.put(ann.stamp, list);
				}
				list.add(new Tuple(ann, position));
			}
		}
		Comparator<Tuple> comparator = new Comparator<Tuple>() {
			@Override
			public int compare(Tuple o1, Tuple o2) {
				return o1.position.getOffset() - o2.position.getOffset();
			}
		};
		for (Iterator<List<Tuple>> it = map.values().iterator(); it.hasNext();) {
			List<Tuple> list = it.next();
			Collections.sort(list, comparator);
		}
		return map;
	}

	@Override
	public final void collapseMembers() {
		// Not supported
//		modifyFiltered(fMemberFilter, false);
	}

	/*
	 * @see IScriptFoldingStructureProviderExtension#collapseComments()
	 */
	@Override
	public final void collapseComments() {
		modifyFiltered(fCommentFilter, false);
	}

	/**
	 * Collapses or expands all annotations matched by the passed filter.
	 * 
	 * @param filter
	 *            the filter to use to select which annotations to collapse
	 * @param expand
	 *            <code>true</code> to expand the matched annotations,
	 *            <code>false</code> to collapse them
	 */
	private void modifyFiltered(Filter filter, boolean expand) {
		if (!isInstalled())
			return;
		ProjectionAnnotationModel model = getModel();
		if (model == null)
			return;
		List<Annotation> modified = new ArrayList<Annotation>();
		Iterator<?> iter = model.getAnnotationIterator();
		while (iter.hasNext()) {
			Object annotation = iter.next();
			if (annotation instanceof ScriptProjectionAnnotation) {
				ScriptProjectionAnnotation annot = (ScriptProjectionAnnotation) annotation;
				if (expand == annot.isCollapsed() && filter.match(annot)) {
					if (expand)
						annot.markExpanded();
					else
						annot.markCollapsed();
					modified.add(annot);
				}
			}
		}
		model.modifyAnnotations(null, null,
				modified.toArray(new Annotation[modified.size()]));
	}

	private static class SourceRangeStamp {
		final int length;
		final int hashCode;

		public SourceRangeStamp(int length, int hashCode) {
			this.length = length;
			this.hashCode = hashCode;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SourceRangeStamp) {
				final SourceRangeStamp other = (SourceRangeStamp) obj;
				return length == other.length && hashCode == other.hashCode;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "@"
					+ Integer.toHexString(hashCode);
		}

	}

	public static class FoldingContent {

		protected final String contents;
		protected final Path filePath;

		public FoldingContent(String contents, Path filePath) {
			this.contents = contents;
			this.filePath = filePath;
		}

		public String get() {
			return contents;
		}

		public String get(int offset, int length) {
			return contents.substring(offset, offset + length);
		}

		public String substring(int beginIndex, int endIndex) {
			return contents.substring(beginIndex, endIndex);
		}

		public String get(IRegion region) {
			return get(region.getOffset(), region.getLength());
		}

		public char[] getContentsAsCharArray() {
			return get().toCharArray();
		}

		public Path getFilePath() {
			return filePath;
		}

		public String getSourceContents() {
			return get();
		}

	}

	private static class Requestor implements IFoldingBlockRequestor {

		final FoldingContent content;
		final FoldingStructureComputationContext ctx;
		int lineCountDelta;

		public Requestor(FoldingContent content, FoldingStructureComputationContext ctx) {
			this.content = content;
			this.ctx = ctx;
		}

		@Override
		public void acceptBlock(int start, int end, IFoldingBlockKind kind,
				Object element, boolean collapse) {
			try {
				IRegion region = new Region(start, end - start);
				final IRegion normalized = alignRegion(region, ctx,
						lineCountDelta);
				if (normalized == null) {
					return;
				}
				if (element == null) {
					element = new SourceRangeStamp(region.getLength(), content
							.get(region).hashCode());
				}
				Position position = kind.isComment() ? createCommentPosition(normalized)
						: createMemberPosition(normalized);
				if (position == null) {
					return;
				}
				ctx.addProjectionRange(
						new ScriptProjectionAnnotation(ctx.allowCollapsing()
								&& collapse, kind, element), position);
			} catch (StringIndexOutOfBoundsException e) {
				LangUIPlugin.logInternalError(e);
			}
		}
	}

}