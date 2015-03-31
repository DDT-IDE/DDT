/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import melnorme.lang.ide.ui.LangUIPlugin;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;



public abstract class ContentAssistPreference {

	/** Preference key for content assist auto activation */
	private final static String AUTOACTIVATION=  PreferenceConstants.CODEASSIST_AUTOACTIVATION;
	/** Preference key for content assist auto activation delay */
	private final static String AUTOACTIVATION_DELAY=  PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY;
	/** Preference key for content assist proposal color */
	private final static String PROPOSALS_FOREGROUND=  PreferenceConstants.CODEASSIST_PROPOSALS_FOREGROUND;
	/** Preference key for content assist proposal color */
	private final static String PROPOSALS_BACKGROUND=  PreferenceConstants.CODEASSIST_PROPOSALS_BACKGROUND;
	/** Preference key for content assist parameters color */
	private final static String PARAMETERS_FOREGROUND=  PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND;
	/** Preference key for content assist parameters color */
	private final static String PARAMETERS_BACKGROUND=  PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND;
	/** Preference key for content assist auto insert */
	private final static String AUTOINSERT= PreferenceConstants.CODEASSIST_AUTOINSERT;

	/** Preference key for script content assist auto activation triggers */
	private final static String AUTOACTIVATION_TRIGGERS= PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS;

	/** Preference key for visibility of proposals */
	private final static String SHOW_VISIBLE_PROPOSALS= PreferenceConstants.CODEASSIST_SHOW_VISIBLE_PROPOSALS;
	/** Preference key for case sensitivity of proposals */
	private final static String CASE_SENSITIVITY= PreferenceConstants.CODEASSIST_CASE_SENSITIVITY;
	/** Preference key for adding imports on code assist */
	/** Preference key for filling argument names on method completion */
	private static final String FILL_METHOD_ARGUMENTS= PreferenceConstants.CODEASSIST_FILL_ARGUMENT_NAMES;
	/** Preference key for prefix completion. */
	private static final String PREFIX_COMPLETION= PreferenceConstants.CODEASSIST_PREFIX_COMPLETION;

	
	public ContentAssistPreference() {
	}
	
	protected IColorManager getColorManager() {
		return LangUIPlugin.getInstance().getColorManager();
	}
	
	private static Color getColor(IPreferenceStore store, String key, IColorManager manager) {
		RGB rgb= PreferenceConverter.getColor(store, key);
		return manager.getColor(rgb);
	}	
	
	private Color getColor(IPreferenceStore store, String key) {
		return getColor(store, key, getColorManager());
	}

	private ScriptCompletionProcessor getScriptProcessor(ContentAssistant assistant) {
		IContentAssistProcessor p= assistant.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		if (p instanceof ScriptCompletionProcessor)
			return  (ScriptCompletionProcessor) p;
		return null;
	}

	private void configureScriptProcessor(ContentAssistant assistant, IPreferenceStore store) {
		ScriptCompletionProcessor jcp= getScriptProcessor(assistant);
		if (jcp == null)
			return;

		String triggers= store.getString(AUTOACTIVATION_TRIGGERS);
		if (triggers != null)
			jcp.setCompletionProposalAutoActivationCharacters(triggers.toCharArray());

		boolean enabled= store.getBoolean(SHOW_VISIBLE_PROPOSALS);
		jcp.restrictProposalsToVisibility(enabled);

		enabled= store.getBoolean(CASE_SENSITIVITY);
		jcp.restrictProposalsToMatchingCases(enabled);
	}

	/**
	 * Configure the given content assistant from the given store.
	 */
	public void configure(ContentAssistant assistant, IPreferenceStore store) {

		IColorManager manager= getColorManager();

		boolean enabled= store.getBoolean(AUTOACTIVATION);
		assistant.enableAutoActivation(enabled);

		int delay= store.getInt(AUTOACTIVATION_DELAY);
		assistant.setAutoActivationDelay(delay);

		Color c= getColor(store, PROPOSALS_FOREGROUND, manager);
		assistant.setProposalSelectorForeground(c);

		c= getColor(store, PROPOSALS_BACKGROUND, manager);
		assistant.setProposalSelectorBackground(c);

		c= getColor(store, PARAMETERS_FOREGROUND, manager);
		assistant.setContextInformationPopupForeground(c);
		assistant.setContextSelectorForeground(c);

		c= getColor(store, PARAMETERS_BACKGROUND, manager);
		assistant.setContextInformationPopupBackground(c);
		assistant.setContextSelectorBackground(c);

		enabled= store.getBoolean(AUTOINSERT);
		assistant.enableAutoInsert(enabled);

		enabled= store.getBoolean(PREFIX_COMPLETION);
		assistant.enablePrefixCompletion(enabled);

		configureScriptProcessor(assistant, store);
	}
	
	private void changeScriptProcessor(ContentAssistant assistant, IPreferenceStore store, String key) {
		ScriptCompletionProcessor jcp= getScriptProcessor(assistant);
		if (jcp == null)
			return;
				
		if (AUTOACTIVATION_TRIGGERS.equals(key)) {
			String triggers= store.getString(AUTOACTIVATION_TRIGGERS);
			if (triggers != null)
				jcp.setCompletionProposalAutoActivationCharacters(triggers.toCharArray());
		} else if (SHOW_VISIBLE_PROPOSALS.equals(key)) {
			boolean enabled= store.getBoolean(SHOW_VISIBLE_PROPOSALS);
			jcp.restrictProposalsToVisibility(enabled);
		} else if (CASE_SENSITIVITY.equals(key)) {
			boolean enabled= store.getBoolean(CASE_SENSITIVITY);
			jcp.restrictProposalsToMatchingCases(enabled);
		}
	}

	/**
	 * Changes the configuration of the given content assistant according to the given property
	 * change event and the given preference store.
	 */
	public void changeConfiguration(ContentAssistant assistant, IPreferenceStore store, PropertyChangeEvent event) {

		String p= event.getProperty();

		if (AUTOACTIVATION.equals(p)) {
			boolean enabled= store.getBoolean(AUTOACTIVATION);
			assistant.enableAutoActivation(enabled);
		} else if (AUTOACTIVATION_DELAY.equals(p)) {
			int delay= store.getInt(AUTOACTIVATION_DELAY);
			assistant.setAutoActivationDelay(delay);
		} else if (PROPOSALS_FOREGROUND.equals(p)) {
			Color c= getColor(store, PROPOSALS_FOREGROUND);
			assistant.setProposalSelectorForeground(c);
		} else if (PROPOSALS_BACKGROUND.equals(p)) {
			Color c= getColor(store, PROPOSALS_BACKGROUND);
			assistant.setProposalSelectorBackground(c);
		} else if (PARAMETERS_FOREGROUND.equals(p)) {
			Color c= getColor(store, PARAMETERS_FOREGROUND);
			assistant.setContextInformationPopupForeground(c);
			assistant.setContextSelectorForeground(c);
		} else if (PARAMETERS_BACKGROUND.equals(p)) {
			Color c= getColor(store, PARAMETERS_BACKGROUND);
			assistant.setContextInformationPopupBackground(c);
			assistant.setContextSelectorBackground(c);
		} else if (AUTOINSERT.equals(p)) {
			boolean enabled= store.getBoolean(AUTOINSERT);
			assistant.enableAutoInsert(enabled);
		} else if (PREFIX_COMPLETION.equals(p)) {
			boolean enabled= store.getBoolean(PREFIX_COMPLETION);
			assistant.enablePrefixCompletion(enabled);
		}

		changeScriptProcessor(assistant, store, p);	
	}

	public boolean fillArgumentsOnMethodCompletion(IPreferenceStore store) {
		return store.getBoolean(FILL_METHOD_ARGUMENTS);
	}
}

