/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Alex Panchenko <alex@xored.com>
 *******************************************************************************/

package _org.eclipse.dltk.internal.ui.preferences;

import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import _org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;

/**
 * Handles dltk editor font changes for script source preview viewers.
 */
public class ScriptSourcePreviewerUpdater {

	/**
	 * Creates a Java source preview updater for the given viewer, configuration and preference store.
	 *
	 * @param viewer the viewer
	 * @param configuration the configuration
	 * @param preferenceStore the preference store
	 */
	public ScriptSourcePreviewerUpdater(final SourceViewer viewer,
			final DeeSourceViewerConfiguration configuration, 
			final IPreferenceStore preferenceStore) {
		Assert.isNotNull(viewer);
		Assert.isNotNull(configuration);
		Assert.isNotNull(preferenceStore);
		final IPropertyChangeListener fontChangeListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				final String fontKey = configuration.getFontPropertyPreferenceKey();
				// TODO: need review
				// final String fontKey = PreferenceConstants.EDITOR_TEXT_FONT;
				if (event.getProperty().equals(fontKey)) {
					Font font = JFaceResources.getFont(fontKey);
					viewer.getTextWidget().setFont(font);
				}
			}
		};
		final IPropertyChangeListener propertyChangeListener= new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (configuration.affectsTextPresentation(event)) {
					configuration.handlePropertyChangeEvent(event);
					viewer.invalidateTextPresentation();
				}
			}
		};
		viewer.getTextWidget().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				preferenceStore.removePropertyChangeListener(propertyChangeListener);
				JFaceResources.getFontRegistry().removeListener(fontChangeListener);
			}
		});
		JFaceResources.getFontRegistry().addListener(fontChangeListener);
		preferenceStore.addPropertyChangeListener(propertyChangeListener);
	}
}
