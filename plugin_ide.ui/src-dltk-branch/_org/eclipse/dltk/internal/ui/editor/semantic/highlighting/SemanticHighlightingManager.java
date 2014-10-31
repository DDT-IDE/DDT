/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package _org.eclipse.dltk.internal.ui.editor.semantic.highlighting;

import java.util.Collections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.dltk.ui.ColorPreferenceConverter;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.editor.highlighting.HighlightedPosition;
import org.eclipse.dltk.ui.editor.highlighting.HighlightingStyle;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlightingUpdater;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlightingUpdater.UpdateResult;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.IColorManagerExtension;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;
import _org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;

/**
 * Semantic highlighting manager
 */
public class SemanticHighlightingManager implements IPropertyChangeListener {

	private final ISemanticHighlightingUpdater fHighlightingUpdater;

	public SemanticHighlightingManager(
			ISemanticHighlightingUpdater highlightingUpdater) {
		Assert.isNotNull(highlightingUpdater);
		this.fHighlightingUpdater = highlightingUpdater;
		this.fSemanticHighlightings = fHighlightingUpdater
				.getSemanticHighlightings();
	}

	/**
	 * Highlighted ranges.
	 */
	public static class HighlightedRange extends Region {
		/**
		 * The highlighting key as returned by
		 * {@link SemanticHighlighting#getPreferenceKey()}.
		 */
		private String fKey;

		/**
		 * Initialize with the given offset, length and highlighting key.
		 * 
		 * @param offset
		 * @param length
		 * @param key
		 *            the highlighting key as returned by
		 *            {@link SemanticHighlighting#getPreferenceKey()}
		 */
		public HighlightedRange(int offset, int length, String key) {
			super(offset, length);
			fKey = key;
		}

		/**
		 * @return the highlighting key as returned by
		 *         {@link SemanticHighlighting#getPreferenceKey()}
		 */
		public String getKey() {
			return fKey;
		}

		/*
		 * @see org.eclipse.jface.text.Region#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			return super.equals(o) && o instanceof HighlightedRange
					&& fKey.equals(((HighlightedRange) o).getKey());
		}

		/*
		 * @see org.eclipse.jface.text.Region#hashCode()
		 */
		@Override
		public int hashCode() {
			return super.hashCode() | fKey.hashCode();
		}
	}

	/** Semantic highlighting presenter */
	private SemanticHighlightingPresenter fPresenter;
	/** Semantic highlighting reconciler */
	private SemanticHighlightingReconciler fReconciler;

	/** Semantic highlightings */
	private SemanticHighlighting[] fSemanticHighlightings;
	/** Highlightings */
	private HighlightingStyle[] fHighlightings;

	/** The editor */
	private ScriptEditor2 fEditor;
	/** The source viewer */
	private ScriptSourceViewer fSourceViewer;
	/** The color manager */
	private IColorManager fColorManager;
	/** The preference store */
	private IPreferenceStore fPreferenceStore;
	/** The source viewer configuration */
	private ScriptSourceViewerConfiguration fConfiguration;
	/** The presentation reconciler */
	private ScriptPresentationReconciler fPresentationReconciler;

	/**
	 * Install the semantic highlighting on the given editor infrastructure
	 * 
	 * @param editor
	 *            The Script editor
	 * @param sourceViewer
	 *            The source viewer
	 * @param colorManager
	 *            The color manager
	 * @param preferenceStore
	 *            The preference store
	 */
	public void install(ScriptEditor2 editor, ScriptSourceViewer sourceViewer,
			IColorManager colorManager, IPreferenceStore preferenceStore) {
		fEditor = editor;
		fSourceViewer = sourceViewer;
		fColorManager = colorManager;
		fPreferenceStore = preferenceStore;
		final ScriptTextTools textTools = getTextTools();
		if (textTools != null) {
			fConfiguration = textTools.createSourceViewerConfiguraton(
					preferenceStore, editor);
		}
		if (fEditor != null) {
			Assert.isNotNull(fConfiguration);
			IPresentationReconciler presReconciler = fConfiguration
					.getPresentationReconciler(sourceViewer);
			if (presReconciler instanceof ScriptPresentationReconciler) {
				fPresentationReconciler = (ScriptPresentationReconciler) presReconciler;
			} else {
				fPresentationReconciler = null;
			}
		} else {
			fConfiguration = null;
			fPresentationReconciler = null;
		}

		fPreferenceStore.addPropertyChangeListener(this);

		if (isEnabled())
			enable();
	}

	/**
	 * Install the semantic highlighting on the given source viewer
	 * infrastructure. No reconciliation will be performed.
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @param colorManager
	 *            the color manager
	 * @param preferenceStore
	 *            the preference store
	 */
	public void install(ScriptSourceViewer sourceViewer,
			IColorManager colorManager, IPreferenceStore preferenceStore) {
		install(null, sourceViewer, colorManager, preferenceStore);
	}

	/**
	 * Install the semantic highlighting on the given source viewer
	 * infrastructure. No reconciliation will be performed.
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @param colorManager
	 *            the color manager
	 * @param preferenceStore
	 *            the preference store
	 * @param hardcodedRanges
	 *            the hard-coded ranges to be highlighted
	 * @deprecated
	 */
	public void install(ScriptSourceViewer sourceViewer,
			IColorManager colorManager, IPreferenceStore preferenceStore,
			HighlightedRange[][] hardcodedRanges) {
		install(null, sourceViewer, colorManager, preferenceStore);
	}

	/**
	 * Enable semantic highlighting.
	 */
	private void enable() {
		initializeHighlightings();

		fPresenter = new SemanticHighlightingPresenter();
		fPresenter.install(fSourceViewer, fPresentationReconciler);

		if (fEditor != null) {
			fReconciler = new SemanticHighlightingReconciler();
			fReconciler.install(fEditor, fSourceViewer, fHighlightingUpdater,
					fPresenter, fSemanticHighlightings, fHighlightings);
		} else {
			fHighlightingUpdater.initialize(fPresenter, fHighlightings);
			final ModuleSource code = new ModuleSource(fSourceViewer
					.getDocument().get());
			UpdateResult result = fHighlightingUpdater.reconcile(code,
					Collections.<HighlightedPosition> emptyList());
			fPresenter.updatePresentation(null, result.addedPositions,
					HighlightedPosition.NO_POSITIONS);
		}
	}

	public HighlightingStyle getHighlighting(int pos) {
		return fHighlightings[pos];
	}

	/**
	 * Uninstall the semantic highlighting
	 */
	public void uninstall() {
		disable();

		if (fPreferenceStore != null) {
			fPreferenceStore.removePropertyChangeListener(this);
			fPreferenceStore = null;
		}

		fSemanticHighlightings = null;
		fEditor = null;
		fSourceViewer = null;
		fColorManager = null;
		fConfiguration = null;
		fPresentationReconciler = null;
	}

	/**
	 * Disable semantic highlighting.
	 */
	private void disable() {
		if (fReconciler != null) {
			fReconciler.uninstall();
			fReconciler = null;
		}

		if (fPresenter != null) {
			fPresenter.uninstall();
			fPresenter = null;
		}

		if (fHighlightings != null)
			disposeHighlightings();
	}

	/**
	 * @return <code>true</code> iff semantic highlighting is enabled in the
	 *         preferences
	 */
	private boolean isEnabled() {
		if (fSemanticHighlightings == null) {
			return false;
		}
		for (int i = 0; i < fSemanticHighlightings.length; ++i) {
			final SemanticHighlighting sh = fSemanticHighlightings[i];
			if (!sh.isSemanticOnly()) {
				return true;
			}
			if (fPreferenceStore.getBoolean(sh.getEnabledPreferenceKey())) {
				return true;
			}
		}
		return false;
	}

	private TextAttribute createTextAttribute(IColorManager manager,
			IPreferenceStore ps, String colorKey, String boldKey,
			String italicKey, String strikethroughKey, String underlineKey,
			String bgKey) {

		addColor(colorKey);
		if (bgKey != null) {
			addColor(bgKey);
		}
		Color color = null;
		Color bgcolor = null;
		if (colorKey != null)
			color = manager.getColor(colorKey);
		if (bgKey != null)
			bgcolor = manager.getColor(bgKey);

		int style = ps.getBoolean(boldKey) ? SWT.BOLD : SWT.NORMAL;
		if (ps.getBoolean(italicKey))
			style |= SWT.ITALIC;

		if (ps.getBoolean(strikethroughKey))
			style |= TextAttribute.STRIKETHROUGH;

		if (ps.getBoolean(underlineKey))
			style |= TextAttribute.UNDERLINE;

		return new TextAttribute(color, bgcolor, style);
	}

	private TextAttribute createTextAttribute(IColorManager manager,
			IPreferenceStore ps, String colorKey, String bgKey) {
		return createTextAttribute(manager, ps, colorKey, colorKey
				+ PreferenceConstants.EDITOR_BOLD_SUFFIX, colorKey
				+ PreferenceConstants.EDITOR_ITALIC_SUFFIX, colorKey
				+ PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX, colorKey
				+ PreferenceConstants.EDITOR_UNDERLINE_SUFFIX, bgKey);
	}

	/**
	 * Initialize semantic highlightings.
	 */
	private void initializeHighlightings() {
		Assert.isNotNull(fSemanticHighlightings);
		fHighlightings = new HighlightingStyle[fSemanticHighlightings.length];
		for (int a = 0; a < fSemanticHighlightings.length; a++) {
			final SemanticHighlighting sh = fSemanticHighlightings[a];
			final TextAttribute ta = createTextAttribute(fColorManager,
					fPreferenceStore, sh.getPreferenceKey(),
					sh.getBackgroundPreferenceKey());
			final boolean isEnabled = !sh.isSemanticOnly()
					|| fPreferenceStore
							.getBoolean(sh.getEnabledPreferenceKey());
			fHighlightings[a] = new HighlightingStyle(ta, isEnabled, sh);
		}
	}

	private ScriptTextTools getTextTools() {
		return fEditor != null ? fEditor.getTextTools() : null;
	}

	/**
	 * Dispose the semantic highlightings.
	 */
	private void disposeHighlightings() {
		for (int i = 0, n = fSemanticHighlightings.length; i < n; i++)
			removeColor(fSemanticHighlightings[i].getPreferenceKey());

		fHighlightings = null;
	}

	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		handlePropertyChangeEvent(event);
	}

	/**
	 * Handle the given property change event
	 * 
	 * @param event
	 *            The event
	 */
	private void handlePropertyChangeEvent(PropertyChangeEvent event) {
		if (fPreferenceStore == null)
			return; // Uninstalled during event notification

		if (fConfiguration != null)
			fConfiguration.handlePropertyChangeEvent(event);

		if (affectsEnablement(event)) {
			if (isEnabled())
				enable();
			else
				disable();
		}

		if (!isEnabled())
			return;

		boolean refreshNeeded = false;

		for (int i = 0, n = fSemanticHighlightings.length; i < n; i++) {
			SemanticHighlighting semanticHighlighting = fSemanticHighlightings[i];

			String preferenceKey = semanticHighlighting.getPreferenceKey();
			if (preferenceKey != null)
				if (preferenceKey.equals(event.getProperty())) {
					adaptToTextForegroundChange(fHighlightings[i], event);
					fPresenter.highlightingStyleChanged(fHighlightings[i]);
					refreshNeeded = true;
					continue;
				}
			String bpreferenceKey = semanticHighlighting
					.getBackgroundPreferenceKey();
			if (bpreferenceKey != null)
				if (bpreferenceKey.equals(event.getProperty())) {
					adaptToTextBackgroundChange(fHighlightings[i], event);
					fPresenter.highlightingStyleChanged(fHighlightings[i]);
					refreshNeeded = true;
					continue;
				}

			String boldKey = preferenceKey
					+ PreferenceConstants.EDITOR_BOLD_SUFFIX;
			if (boldKey.equals(event.getProperty())) {
				adaptToTextStyleChange(fHighlightings[i], event, SWT.BOLD);
				fPresenter.highlightingStyleChanged(fHighlightings[i]);
				refreshNeeded = true;
				continue;
			}

			String italicKey = preferenceKey
					+ PreferenceConstants.EDITOR_ITALIC_SUFFIX;
			if (italicKey.equals(event.getProperty())) {
				adaptToTextStyleChange(fHighlightings[i], event, SWT.ITALIC);
				fPresenter.highlightingStyleChanged(fHighlightings[i]);
				refreshNeeded = true;
				continue;
			}

			String strikethroughKey = preferenceKey
					+ PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX;
			if (strikethroughKey.equals(event.getProperty())) {
				adaptToTextStyleChange(fHighlightings[i], event,
						TextAttribute.STRIKETHROUGH);
				fPresenter.highlightingStyleChanged(fHighlightings[i]);
				refreshNeeded = true;
				continue;
			}

			String underlineKey = preferenceKey
					+ PreferenceConstants.EDITOR_UNDERLINE_SUFFIX;
			if (underlineKey.equals(event.getProperty())) {
				adaptToTextStyleChange(fHighlightings[i], event,
						TextAttribute.UNDERLINE);
				fPresenter.highlightingStyleChanged(fHighlightings[i]);
				refreshNeeded = true;
				continue;
			}

			if (semanticHighlighting.isSemanticOnly()) {
				if (semanticHighlighting.getEnabledPreferenceKey().equals(
						event.getProperty())) {
					adaptToEnablementChange(fHighlightings[i], event);
					fPresenter.highlightingStyleChanged(fHighlightings[i]);
					refreshNeeded = true;
					continue;
				}
			}
		}

		if (refreshNeeded && fReconciler != null)
			fReconciler.refresh();
	}

	/**
	 * Tests whether <code>event</code> affects the enablement of semantic
	 * highlighting.
	 * 
	 * @param event
	 *            the property change under examination
	 * @return <code>true</code> if <code>event</code> changed semantic
	 *         highlighting enablement, <code>false</code> if it did not
	 */
	private boolean affectsEnablement(PropertyChangeEvent event) {
		if (fSemanticHighlightings == null) {
			return false;
		}
		String relevantKey = null;
		for (int i = 0; i < fSemanticHighlightings.length; i++) {
			if (event.getProperty().equals(
					fSemanticHighlightings[i].getEnabledPreferenceKey())) {
				relevantKey = event.getProperty();
				break;
			}
		}
		if (relevantKey == null)
			return false;

		for (int i = 0; i < fSemanticHighlightings.length; i++) {
			String key = fSemanticHighlightings[i].getEnabledPreferenceKey();
			if (key.equals(relevantKey))
				continue;
			if (!fSemanticHighlightings[i].isSemanticOnly())
				return false;
			if (fPreferenceStore.getBoolean(key))
				return false; // another is still enabled or was enabled before
		}
		// all others are disabled, so toggling relevantKey affects the
		// enablement
		return true;
	}

	private void adaptToTextBackgroundChange(HighlightingStyle highlighting,
			PropertyChangeEvent event) {
		RGB rgb = ColorPreferenceConverter.asRGB(event.getNewValue());
		if (rgb != null) {

			String property = event.getProperty();
			Color color = fColorManager.getColor(property);

			if ((color == null || !rgb.equals(color.getRGB()))
					&& fColorManager instanceof IColorManagerExtension) {
				IColorManagerExtension ext = (IColorManagerExtension) fColorManager;
				ext.unbindColor(property);
				ext.bindColor(property, rgb);
				color = fColorManager.getColor(property);
			}

			TextAttribute oldAttr = highlighting.getTextAttribute();
			highlighting.setTextAttribute(new TextAttribute(oldAttr
					.getForeground(), color, oldAttr.getStyle()));
		}
	}

	private void adaptToEnablementChange(HighlightingStyle highlighting,
			PropertyChangeEvent event) {
		Object value = event.getNewValue();
		boolean eventValue;
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();
		else if (IPreferenceStore.TRUE.equals(value))
			eventValue = true;
		else
			eventValue = false;
		highlighting.setEnabled(eventValue);
	}

	private void adaptToTextForegroundChange(HighlightingStyle highlighting,
			PropertyChangeEvent event) {
		RGB rgb = ColorPreferenceConverter.asRGB(event.getNewValue());
		if (rgb != null) {

			String property = event.getProperty();
			Color color = fColorManager.getColor(property);

			if ((color == null || !rgb.equals(color.getRGB()))
					&& fColorManager instanceof IColorManagerExtension) {
				IColorManagerExtension ext = (IColorManagerExtension) fColorManager;
				ext.unbindColor(property);
				ext.bindColor(property, rgb);
				color = fColorManager.getColor(property);
			}

			TextAttribute oldAttr = highlighting.getTextAttribute();
			highlighting.setTextAttribute(new TextAttribute(color, oldAttr
					.getBackground(), oldAttr.getStyle()));
		}
	}

	private void adaptToTextStyleChange(HighlightingStyle highlighting,
			PropertyChangeEvent event, int styleAttribute) {
		boolean eventValue = false;
		Object value = event.getNewValue();
		if (value instanceof Boolean)
			eventValue = ((Boolean) value).booleanValue();
		else if (IPreferenceStore.TRUE.equals(value))
			eventValue = true;

		TextAttribute oldAttr = highlighting.getTextAttribute();
		boolean activeValue = (oldAttr.getStyle() & styleAttribute) == styleAttribute;

		if (activeValue != eventValue)
			highlighting.setTextAttribute(new TextAttribute(oldAttr
					.getForeground(), oldAttr.getBackground(),
					eventValue ? oldAttr.getStyle() | styleAttribute : oldAttr
							.getStyle() & ~styleAttribute));
	}

	private void addColor(String colorKey) {
		if (fColorManager != null && colorKey != null
				&& fColorManager.getColor(colorKey) == null) {
			RGB rgb = PreferenceConverter.getColor(fPreferenceStore, colorKey);
			if (fColorManager instanceof IColorManagerExtension) {
				IColorManagerExtension ext = (IColorManagerExtension) fColorManager;
				ext.unbindColor(colorKey);
				ext.bindColor(colorKey, rgb);
			}
		}
	}

	private void removeColor(String colorKey) {
		if (fColorManager instanceof IColorManagerExtension)
			((IColorManagerExtension) fColorManager).unbindColor(colorKey);
	}

	/**
	 * Returns this hightlighter's reconciler.
	 * 
	 * @return the semantic highlighter reconciler or <code>null</code> if none
	 * @since 3.3
	 */
	public SemanticHighlightingReconciler getReconciler() {
		return fReconciler;
	}
}
