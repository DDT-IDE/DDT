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
package org.dsource.ddt.lang.ui.editor;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * An extension that adds some util code to work with scanners
 */
public abstract class ScriptSourceViewerConfigurationExtension extends ScriptSourceViewerConfiguration {
	
	protected Set<AbstractScriptScanner> scanners;
	protected Map<String, AbstractScriptScanner> scannersByContentType;
	
	public ScriptSourceViewerConfigurationExtension(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}
	
	@Override
	protected void initializeScanners() {
		scanners = new HashSet<AbstractScriptScanner>();
		scannersByContentType = new HashMap<String, AbstractScriptScanner>();
		createScanners();
		scanners = Collections.unmodifiableSet(scanners);
		scannersByContentType = Collections.unmodifiableMap(scannersByContentType);

	}
	
	protected void createScanners() {
		// Default implementation
	}
	
	protected void addScanner(AbstractScriptScanner scanner, String... contentTypes ) {
		scanners.add(scanner);
		for (String contentType : contentTypes) {
			scannersByContentType.put(contentType, scanner);
		}
	}
	
	protected SingleTokenScriptScanner createSingleTokenScriptScanner(String tokenProperty) {
		return new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, tokenProperty);
	}
	
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		setupPresentationReconciler(reconciler);
		return reconciler;
	}
	
	protected void setupPresentationReconciler(PresentationReconciler reconciler) {
		for (Entry<String, AbstractScriptScanner> entry : scannersByContentType.entrySet()) {
			String contentType = entry.getKey();
			AbstractScriptScanner scanner = entry.getValue();
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, contentType);
			reconciler.setRepairer(dr, contentType);
		}
	}
	
	@Override
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		for (AbstractScriptScanner scanner : scanners) {
			if(scanner.affectsBehavior(event))
				return true;
		}
		return false;
	}
	
	@Override
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		for (AbstractScriptScanner scanner : scanners) {
			if (scanner.affectsBehavior(event)) {
				scanner.adaptToPreferenceChange(event);
			}
		}
	}
	
}