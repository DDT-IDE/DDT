/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Alex Panchenko <alex@xored.com>
 *******************************************************************************/

package _org.eclipse.dltk.internal.ui.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextPresentationListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ScriptSourceViewer extends ProjectionViewer implements
		IPropertyChangeListener {

	/**
	 * Text operation code for requesting the outline for the current input.
	 */
	public static final int SHOW_OUTLINE = 51;

	/**
	 * Text operation code for requesting the outline for the element at the
	 * current position.
	 */
	public static final int OPEN_STRUCTURE = 52;

	/**
	 * Text operation code for requesting the hierarchy for the current input.
	 */
	public static final int SHOW_HIERARCHY = 53;

	private IInformationPresenter fOutlinePresenter;

	private IInformationPresenter fStructurePresenter;

	private IInformationPresenter fHierarchyPresenter;

	/**
	 * This viewer's foreground color.
	 */
	private Color fForegroundColor;

	/**
	 * The viewer's background color.
	 */
	private Color fBackgroundColor;

	/**
	 * This viewer's selection foreground color.
	 */
	private Color fSelectionForegroundColor;

	/**
	 * The viewer's selection background color.
	 */
	private Color fSelectionBackgroundColor;

	/**
	 * The preference store.
	 */
	private IPreferenceStore fPreferenceStore;

	/**
	 * Is this source viewer configured?
	 */
	private boolean fIsConfigured;

	// /**
	// * The backspace manager of this viewer.
	// *
	// */
	// private SmartBackspaceManager fBackspaceManager;

	/**
	 * Whether to delay setting the visual document until the projection has
	 * been computed.
	 * <p>
	 * Added for performance optimization.
	 * </p>
	 * 
	 * @see #prepareDelayedProjection()
	 */
	private boolean fIsSetVisibleDocumentDelayed = false;

	public ScriptSourceViewer(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview,
			int styles, IPreferenceStore store) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview,
				styles);
		setPreferenceStore(store);
	}

	/*
	 * @see ITextOperationTarget#doOperation(int)
	 */
	@Override
	public void doOperation(int operation) {
		if (getTextWidget() == null)
			return;

		switch (operation) {
		case SHOW_OUTLINE:
			if (fOutlinePresenter != null)
				fOutlinePresenter.showInformation();
			return;
		case OPEN_STRUCTURE:
			if (fStructurePresenter != null)
				fStructurePresenter.showInformation();
			return;
		case SHOW_HIERARCHY:
			if (fHierarchyPresenter != null)
				fHierarchyPresenter.showInformation();
			return;
		}

		super.doOperation(operation);
	}

	/*
	 * @see ITextOperationTarget#canDoOperation(int)
	 */
	@Override
	public boolean canDoOperation(int operation) {
		if (operation == SHOW_OUTLINE)
			return fOutlinePresenter != null;
		if (operation == OPEN_STRUCTURE)
			return fStructurePresenter != null;
		if (operation == SHOW_HIERARCHY)
			return fHierarchyPresenter != null;

		return super.canDoOperation(operation);
	}

	/*
	 * @see ISourceViewer#configure(SourceViewerConfiguration)
	 */
	@Override
	public void configure(SourceViewerConfiguration configuration) {

		/*
		 * Prevent access to colors disposed in unconfigure(), see:
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=53641
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=86177
		 */
		StyledText textWidget = getTextWidget();
		if (textWidget != null && !textWidget.isDisposed()) {
			Color foregroundColor = textWidget.getForeground();
			if (foregroundColor != null && foregroundColor.isDisposed())
				textWidget.setForeground(null);
			Color backgroundColor = textWidget.getBackground();
			if (backgroundColor != null && backgroundColor.isDisposed())
				textWidget.setBackground(null);
		}

		super.configure(configuration);
		if (configuration instanceof ScriptSourceViewerConfiguration) {
			ScriptSourceViewerConfiguration dltkSVCconfiguration = (ScriptSourceViewerConfiguration) configuration;
			fOutlinePresenter = dltkSVCconfiguration.getOutlinePresenter(this,
					false);
			if (fOutlinePresenter != null)
				fOutlinePresenter.install(this);

			fStructurePresenter = dltkSVCconfiguration.getOutlinePresenter(
					this, true);
			if (fStructurePresenter != null)
				fStructurePresenter.install(this);

//			fHierarchyPresenter = dltkSVCconfiguration.getHierarchyPresenter(
//					this, true);
			dltkSVCconfiguration.getHierarchyPresenter(null, true);
			if (fHierarchyPresenter != null)
				fHierarchyPresenter.install(this);
			if (textWidget != null) {
				textWidget.setFont(JFaceResources.getFont(dltkSVCconfiguration
						.getFontPropertyPreferenceKey()));
			}
		}

		if (fPreferenceStore != null) {
			fPreferenceStore.addPropertyChangeListener(this);
			initializeViewerColors();
		}

		fIsConfigured = true;
	}

	public void initializeViewerColors() {
		if (fPreferenceStore != null) {

			StyledText styledText = getTextWidget();

			// ----------- foreground color --------------------
			Color color = fPreferenceStore
					.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT) ? null
					: createColor(fPreferenceStore,
							AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND,
							styledText.getDisplay());
			styledText.setForeground(color);

			if (fForegroundColor != null)
				fForegroundColor.dispose();

			fForegroundColor = color;

			// ---------- background color ----------------------
			color = fPreferenceStore
					.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT) ? null
					: createColor(fPreferenceStore,
							AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND,
							styledText.getDisplay());
			styledText.setBackground(color);

			if (fBackgroundColor != null)
				fBackgroundColor.dispose();

			fBackgroundColor = color;

			// ----------- selection foreground color --------------------
			color = fPreferenceStore
					.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR) ? null
					: createColor(
							fPreferenceStore,
							AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR,
							styledText.getDisplay());
			styledText.setSelectionForeground(color);

			if (fSelectionForegroundColor != null)
				fSelectionForegroundColor.dispose();

			fSelectionForegroundColor = color;

			// ---------- selection background color ----------------------
			color = fPreferenceStore
					.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR) ? null
					: createColor(
							fPreferenceStore,
							AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR,
							styledText.getDisplay());
			styledText.setSelectionBackground(color);

			if (fSelectionBackgroundColor != null)
				fSelectionBackgroundColor.dispose();

			fSelectionBackgroundColor = color;
		}
	}

	/**
	 * Creates a color from the information stored in the given preference
	 * store. Returns <code>null</code> if there is no such information
	 * available.
	 * 
	 * @param store
	 *            the store to read from
	 * @param key
	 *            the key used for the lookup in the preference store
	 * @param display
	 *            the display used create the color
	 * @return the created color according to the specification in the
	 *         preference store
	 */
	protected Color createColor(IPreferenceStore store, String key,
			Display display) {

		RGB rgb = null;

		if (store.contains(key)) {

			if (store.isDefault(key))
				rgb = PreferenceConverter.getDefaultColor(store, key);
			else
				rgb = PreferenceConverter.getColor(store, key);

			if (rgb != null)
				return new Color(display, rgb);
		}

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.ISourceViewerExtension2#unconfigure()
	 */
	@Override
	public void unconfigure() {
		if (fOutlinePresenter != null) {
			fOutlinePresenter.uninstall();
			fOutlinePresenter = null;
		}
		if (fStructurePresenter != null) {
			fStructurePresenter.uninstall();
			fStructurePresenter = null;
		}
		if (fHierarchyPresenter != null) {
			fHierarchyPresenter.uninstall();
			fHierarchyPresenter = null;
		}
		if (fForegroundColor != null) {
			fForegroundColor.dispose();
			fForegroundColor = null;
		}
		if (fBackgroundColor != null) {
			fBackgroundColor.dispose();
			fBackgroundColor = null;
		}

		if (fPreferenceStore != null)
			fPreferenceStore.removePropertyChangeListener(this);

		super.unconfigure();

		fIsConfigured = false;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#rememberSelection()
	 */
	@Override
	public Point rememberSelection() {
		return super.rememberSelection();
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#restoreSelection()
	 */
	@Override
	public void restoreSelection() {
		super.restoreSelection();
	}

	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(property)
				|| AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT
						.equals(property)
				|| AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND
						.equals(property)
				|| AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT
						.equals(property)
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR
						.equals(property)
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR
						.equals(property)
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR
						.equals(property)
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR
						.equals(property)) {
			initializeViewerColors();
		}
	}

	/**
	 * Sets the preference store on this viewer.
	 * 
	 * @param store
	 *            the preference store
	 */
	public void setPreferenceStore(IPreferenceStore store) {
		if (fIsConfigured && fPreferenceStore != null)
			fPreferenceStore.removePropertyChangeListener(this);

		fPreferenceStore = store;

		if (fIsConfigured && fPreferenceStore != null) {
			fPreferenceStore.addPropertyChangeListener(this);
			initializeViewerColors();
		}
	}

	/*
	 * @see org.eclipse.jface.text.ITextViewer#resetVisibleRegion()
	 */
	@Override
	public void resetVisibleRegion() {
		super.resetVisibleRegion();
		// re-enable folding if ProjectionViewer failed to due so
		// TODO: Add editor folding option here.
		if (fPreferenceStore != null && !isProjectionMode())
			enableProjection();
		/*
		 * && fPreferenceStore.getBoolean(PreferenceConstants.
		 * EDITOR_FOLDING_ENABLED)
		 */
	}

	/*
	 * @see SourceViewer#createControl(Composite, int)
	 */
	@Override
	protected void createControl(Composite parent, int styles) {

		// Use LEFT_TO_RIGHT unless otherwise specified.
		if ((styles & SWT.RIGHT_TO_LEFT) == 0
				&& (styles & SWT.LEFT_TO_RIGHT) == 0)
			styles |= SWT.LEFT_TO_RIGHT;

		super.createControl(parent, styles);

		// System.err.println("Add backup manager");
		// fBackspaceManager= new SmartBackspaceManager();
		// fBackspaceManager.install(this);
	}

	// /**
	// * Returns the backspace manager for this viewer.
	// *
	// * @return the backspace manager for this viewer, or <code>null</code> if
	// * there is none
	// */
	// public SmartBackspaceManager getBackspaceManager() {
	// return fBackspaceManager;
	// }

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#handleDispose()
	 */
	@Override
	protected void handleDispose() {
		// if (fBackspaceManager != null) {
		// fBackspaceManager.uninstall();
		// fBackspaceManager = null;
		// }

		super.handleDispose();
	}

	/**
	 * Prepends the text presentation listener at the beginning of the viewer's
	 * list of text presentation listeners. If the listener is already
	 * registered with the viewer this call moves the listener to the beginning
	 * of the list.
	 * 
	 * @param listener
	 *            the text presentation listener
	 */
	public void prependTextPresentationListener(
			ITextPresentationListener listener) {

		Assert.isNotNull(listener);

		if (fTextPresentationListeners == null)
			fTextPresentationListeners = new ArrayList();

		fTextPresentationListeners.remove(listener);
		fTextPresentationListeners.add(0, listener);
	}

	/**
	 * Sets the given reconciler.
	 * 
	 * @param reconciler
	 *            the reconciler
	 */
	void setReconciler(IReconciler reconciler) {
		fReconciler = reconciler;
	}

	/**
	 * Returns the reconciler.
	 * 
	 * @return the reconciler or <code>null</code> if not set
	 */
	IReconciler getReconciler() {
		return fReconciler;
	}

	/**
	 * Delays setting the visual document until after the projection has been
	 * computed. This method must only be called before the document is set on
	 * the viewer.
	 * <p>
	 * This is a performance optimization to reduce the computation of the text
	 * presentation triggered by <code>setVisibleDocument(IDocument)</code>.
	 * </p>
	 * 
	 * @see #setVisibleDocument(IDocument)
	 */
	void prepareDelayedProjection() {
		Assert.isTrue(!fIsSetVisibleDocumentDelayed);
		fIsSetVisibleDocumentDelayed = true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This is a performance optimization to reduce the computation of the text
	 * presentation triggered by {@link #setVisibleDocument(IDocument)}
	 * </p>
	 * 
	 * @see #prepareDelayedProjection()
	 */
	@Override
	protected void setVisibleDocument(IDocument document) {
		if (fIsSetVisibleDocumentDelayed) {
			fIsSetVisibleDocumentDelayed = false;
			IDocument previous = getVisibleDocument();
			// will set the visible document if anything is folded
			enableProjection();
			IDocument current = getVisibleDocument();
			// if the visible document was not replaced, continue as usual
			if (current != null && current != previous)
				return;
		}

		super.setVisibleDocument(document);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Performance optimization: since we know at this place that none of the
	 * clients expects the given range to be untouched we reuse the given range
	 * as return value.
	 * </p>
	 */
	@Override
	protected StyleRange modelStyleRange2WidgetStyleRange(StyleRange range) {
		IRegion region = modelRange2WidgetRange(new Region(range.start,
				range.length));
		if (region != null) {
			// don't clone the style range, but simply reuse it.
			range.start = region.getOffset();
			range.length = region.getLength();
			return range;
		}
		return null;
	}
}
