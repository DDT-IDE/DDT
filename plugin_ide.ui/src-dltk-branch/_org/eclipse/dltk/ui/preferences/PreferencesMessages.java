/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *     John Kaplan, johnkaplantech@gmail.com - 108071 [code templates] template for body of newly created class
 *******************************************************************************/
package _org.eclipse.dltk.ui.preferences;

import org.eclipse.osgi.util.NLS;

public final class PreferencesMessages extends NLS {

	static {
		NLS.initializeMessages(PreferencesMessages.class.getName(), PreferencesMessages.class);
	}

	private PreferencesMessages() {
		// Do not instantiate
	}

	public static String AbstractConfigurationBlock_emptyPath;
	public static String AbstractConfigurationBlock_fileDoesntExist;
	public static String AbstractConfigurationBlock_pathIsntAFile;
	public static String AbstractConfigurationBlock_valuesItemsAndLabelMustNotBeNull;
	public static String BuildPathsPropertyPage_no_script_project_message;
	public static String BuildPathsPropertyPage_error_message;
	public static String BuildPathsPropertyPage_error_title;
	public static String BuildPathsPropertyPage_job_title;

	public static String BuildPathsPropertyPage_closed_project_message;
	public static String BuildPathsPropertyPage_unsavedchanges_title;
	public static String BuildPathsPropertyPage_unsavedchanges_message;
	public static String BuildPathsPropertyPage_unsavedchanges_button_save;
	public static String BuildPathsPropertyPage_unsavedchanges_button_discard;
	public static String BuildPathsPropertyPage_unsavedchanges_button_ignore;

	public static String CodeTemplatesPreferencePage_title;
	public static String CodeTemplateBlock_preview;

	public static String CodeTemplateBlock_templates_new_button;
	public static String CodeTemplateBlock_templates_edit_button;
	public static String CodeTemplateBlock_templates_remove_button;
	public static String CodeTemplateBlock_templates_import_button;
	public static String CodeTemplateBlock_templates_export_button;
	public static String CodeTemplateBlock_templates_exportall_button;

	public static String CodeTemplateBlock_templates_label;

	public static String CodeTemplateBlock_import_title;
	public static String CodeTemplateBlock_import_extension;

	public static String CodeTemplateBlock_export_title;
	public static String CodeTemplateBlock_export_filename;
	public static String CodeTemplateBlock_export_extension;
	public static String CodeTemplateBlock_export_exists_title;
	public static String CodeTemplateBlock_export_exists_message;

	public static String CodeTemplateBlock_error_read_title;
	public static String CodeTemplateBlock_error_read_message;
	public static String CodeTemplateBlock_error_parse_message;
	public static String CodeTemplateBlock_error_write_title;
	public static String CodeTemplateBlock_error_write_message;
	public static String CodeTemplateBlock_export_error_title;
	public static String CodeTemplateBlock_export_error_hidden;
	public static String CodeTemplateBlock_export_error_canNotWrite;

	public static String EditTemplateDialog_error_noname;
	public static String EditTemplateDialog_error_invalidName;
	public static String EditTemplateDialog_title_new;
	public static String EditTemplateDialog_title_edit;
	public static String EditTemplateDialog_name;
	public static String EditTemplateDialog_description;
	public static String EditTemplateDialog_context;
	public static String EditTemplateDialog_pattern;
	public static String EditTemplateDialog_insert_variable;
	public static String EditTemplateDialog_undo;
	public static String EditTemplateDialog_redo;
	public static String EditTemplateDialog_cut;
	public static String EditTemplateDialog_copy;
	public static String EditTemplateDialog_paste;
	public static String EditTemplateDialog_select_all;
	public static String EditTemplateDialog_content_assist;
	public static String EditTemplateDialog_autoinsert;

	public static String TemplateVariableProposal_error_title;

	@Deprecated
	public static String DLTKEditorPreferencePage_default;
	/**
	 * @since 2.0
	 */
	public static String DLTKEditorPreferencePage_others;
	public static String DLTKEditorPreferencePage_singleLineComment;
	public static String DLTKEditorPreferencePage_returnKeyword;
	public static String DLTKEditorPreferencePage_keywords;
	public static String DLTKEditorPreferencePage_strings;
	public static String DLTKEditorPreferencePage_regexps;
	public static String DLTKEditorPreferencePage_evaluated_expressions;
	public static String DLTKEditorPreferencePage_variables;
	public static String DLTKEditorPreferencePage_numbers;
	public static String DLTKEditorPreferencePage_arguments;
	public static String DLTKEditorPreferencePage_base_classes;

	public static String DLTKEditorPreferencePage_decorators;
	public static String DLTKEditorPreferencePage_class_colors;
	public static String DLTKEditorPreferencePage_function_colors;
	public static String DLTKEditorPreferencePage_color;
	public static String DLTKEditorPreferencePage_bold;
	public static String DLTKEditorPreferencePage_italic;
	public static String DLTKEditorPreferencePage_strikethrough;
	public static String DLTKEditorPreferencePage_underline;
	public static String DLTKEditorPreferencePage_enable;
	public static String DLTKEditorPreferencePage_preview;
	public static String DLTKEditorPreferencePage_insertSingleProposalsAutomatically;
	public static String DLTKEditorPreferencePage_showOnlyProposalsVisibleInTheInvocationContext;
	public static String DLTKEditorPreferencePage_presentProposalsInAlphabeticalOrder;
	public static String DLTKEditorPreferencePage_coloring_element;
	public static String DLTKEditorPreferencePage_enableAutoActivation;
	public static String DLTKEditorPreferencePage_completionInserts;
	public static String DLTKEditorPreferencePage_completionOverwrites;
	public static String DLTKEditorPreferencePage_completionToggleHint;
	public static String DLTKEditorPreferencePage_autoActivationDelay;
	public static String DLTKEditorPreferencePage_colors;
	public static String DLTKEditorPreferencePage_empty_input;
	public static String DLTKEditorPreferencePage_invalid_input;
	public static String DLTKEditorPreferencePage_hoverTab_title;
	public static String DLTKEditorColoringConfigurationBlock_link;
	public static String DLTKEditorHoverConfigurationBlock_annotationRollover;
	public static String DLTKEditorHoverConfigurationBlock_hoverPreferences;
	public static String DLTKEditorHoverConfigurationBlock_keyModifier;
	public static String DLTKEditorHoverConfigurationBlock_description;
	public static String DLTKEditorHoverConfigurationBlock_modifierIsNotValid;
	public static String DLTKEditorHoverConfigurationBlock_modifierIsNotValidForHover;
	public static String DLTKEditorHoverConfigurationBlock_duplicateModifier;
	public static String DLTKEditorHoverConfigurationBlock_nameColumnTitle;
	public static String DLTKEditorHoverConfigurationBlock_modifierColumnTitle;
	public static String DLTKEditorHoverConfigurationBlock_delimiter;
	public static String DLTKEditorHoverConfigurationBlock_insertDelimiterAndModifierAndDelimiter;
	public static String DLTKEditorHoverConfigurationBlock_insertModifierAndDelimiter;
	public static String DLTKEditorHoverConfigurationBlock_insertDelimiterAndModifier;
	public static String DLTKEditorHoverConfigurationBlock_showAffordance;
	public static String FoldingConfigurationBlock_noFoldingPreferenceBlock;
	public static String TodoTaskConfigurationBlock_enableTaskTags;

	public static String TodoTaskConfigurationBlock_markers_tasks_high_priority;
	public static String TodoTaskConfigurationBlock_markers_tasks_normal_priority;
	public static String TodoTaskConfigurationBlock_markers_tasks_low_priority;
	public static String TodoTaskConfigurationBlock_markers_tasks_add_button;
	public static String TodoTaskConfigurationBlock_markers_tasks_remove_button;
	public static String TodoTaskConfigurationBlock_markers_tasks_edit_button;
	public static String TodoTaskConfigurationBlock_markers_tasks_name_column;
	public static String TodoTaskConfigurationBlock_markers_tasks_priority_column;
	public static String TodoTaskConfigurationBlock_casesensitive_label;
	public static String TodoTaskInputDialog_new_title;
	public static String TodoTaskInputDialog_edit_title;
	public static String TodoTaskInputDialog_name_label;
	public static String TodoTaskInputDialog_priority_label;
	public static String TodoTaskInputDialog_priority_high;
	public static String TodoTaskInputDialog_priority_normal;
	public static String TodoTaskInputDialog_priority_low;
	public static String TodoTaskInputDialog_error_enterName;
	public static String TodoTaskInputDialog_error_comma;
	public static String TodoTaskInputDialog_error_entryExists;
	public static String TodoTaskInputDialog_error_noSpace;
	public static String TodoTaskConfigurationBlock_needsbuild_title;
	// public static String TodoTaskConfigurationBlock_tasks_default;
	public static String TodoTaskConfigurationBlock_needsbuild_message;

	public static String PreferenceChange_rebuildProjectMessageTemplate;
	public static String PreferenceChange_rebuildWorkspaceMessageTemplate;
	public static String PropertyAndPreferencePage_useworkspacesettings_change;
	public static String PropertyAndPreferencePage_showprojectspecificsettings_label;
	public static String PropertyAndPreferencePage_useprojectsettings_label;

	public static String UserLibraryPreferencePage_title;
	public static String UserLibraryPreferencePage_description;
	public static String UserLibraryPreferencePage_libraries_label;
	public static String UserLibraryPreferencePage_libraries_new_button;
	public static String UserLibraryPreferencePage_libraries_edit_button;
	public static String UserLibraryPreferencePage_libraries_addzip_button;
	public static String UserLibraryPreferencePage_libraries_addext_button;
	public static String UserLibraryPreferencePage_libraries_remove_button;
	public static String UserLibraryPreferencePage_libraries_load_button;
	public static String UserLibraryPreferencePage_libraries_save_button;
	public static String UserLibraryPreferencePage_operation;
	public static String UserLibraryPreferencePage_operation_error;
	public static String UserLibraryPreferencePage_config_error_title;
	public static String UserLibraryPreferencePage_config_error_message;
	public static String UserLibraryPreferencePage_browsejar_new_title;
	public static String UserLibraryPreferencePage_browsejar_edit_title;
	public static String UserLibraryPreferencePage_browseext_new_title;
	public static String UserLibraryPreferencePage_browseext_edit_title;
	public static String UserLibraryPreferencePage_LibraryNameDialog_new_title;
	public static String UserLibraryPreferencePage_LibraryNameDialog_edit_title;
	public static String UserLibraryPreferencePage_LibraryNameDialog_name_label;
	public static String UserLibraryPreferencePage_LibraryNameDialog_issystem_label;
	public static String UserLibraryPreferencePage_LibraryNameDialog_name_error_entername;
	public static String UserLibraryPreferencePage_LibraryNameDialog_name_error_exists;
	public static String UserLibraryPreferencePage_LoadSaveDialog_save_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_save_ok_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_load_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_label;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_button;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_selectall_button;
	public static String UserLibraryPreferencePage_LoadSaveDialog_load_replace_message;
	public static String UserLibraryPreferencePage_LoadSaveDialog_load_replace_multiple_message;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_deselectall_button;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_save_label;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_load_label;
	public static String UserLibraryPreferencePage_LoadSaveDialog_filedialog_save_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_filedialog_load_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_error_empty;
	public static String UserLibraryPreferencePage_LoadSaveDialog_error_invalidfile;
	public static String UserLibraryPreferencePage_LoadSaveDialog_overwrite_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_save_ok_message;
	public static String UserLibraryPreferencePage_LoadSaveDialog_overwrite_message;
	public static String UserLibraryPreferencePage_LoadSaveDialog_save_errordialog_title;
	public static String UserLibraryPreferencePage_LoadSaveDialog_save_errordialog_message;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_error_save_enterlocation;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_error_save_invalid;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_error_save_nothingselected;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_error_load_enterlocation;
	public static String UserLibraryPreferencePage_LoadSaveDialog_location_error_load_invalid;
	public static String UserLibraryPreferencePage_LoadSaveDialog_list_error_load_nothingselected;
	public static String UserLibraryPreferencePage_LoadSaveDialog_load_badformat;
	public static String UserLibraryPreferencePage_LoadSaveDialog_load_replace_title;

	public static String FoldingConfigurationBlock_docTabName;
	public static String FoldingConfigurationBlock_srcTabName;

	public static String FoldingConfigurationBlock_enable;
	public static String FoldingConfigurationBlock_commentsEnable;
	public static String FoldingConfigurationBlock_joinComments;
	public static String FoldingConfigurationBlock_initiallyFold;
	public static String FoldingConfigurationBlock_initiallyFoldDocs;
	public static String FoldingConfigurationBlock_initiallyFoldComments;
	public static String FoldingConfigurationBlock_initiallyFoldHeaderComments;
	public static String FoldingConfigurationBlock_minLinesToEnableFolding;
	public static String FoldingConfigurationBlock_initiallyFoldClasses;
	public static String FoldingConfigurationBlock_initiallyFoldMethods;

	public static String FoldingConfigurationBlock_info_no_preferences;

	public static String SmartTypingConfigurationBlock_autoclose_title;
	public static String SmartTypingConfigurationBlock_tabs_title;
	public static String SmartTypingConfigurationBlock_tabs_message_tab_text;
	public static String SmartTypingConfigurationBlock_tabs_message_others_text;
	public static String SmartTypingConfigurationBlock_tabs_message_tooltip;
	public static String SmartTypingConfigurationBlock_tabs_message_spaces;
	public static String SmartTypingConfigurationBlock_tabs_message_tabs;
	public static String SmartTypingConfigurationBlock_tabs_message_tabsAndSpaces;
	public static String SmartTypingConfigurationBlock_pasting_title;
	public static String SmartTypingConfigurationBlock_strings_title;
	public static String CodeAssistConfigurationBlock_sortingSection_title;
	public static String CodeAssistConfigurationBlock_filteringSection_title;
	public static String CodeAssistConfigurationBlock_autoactivationSection_title;
	public static String CodeAssistConfigurationBlock_insertionSection_title;
	public static String DLTKEditorPreferencePage_coloring_category_DLTK;
	public static String DLTKEditorPreferencePage_coloring_category_DLTKdoc;
	public static String DLTKEditorPreferencePage_coloring_category_comments;
	public static String DLTKEditorPreferencePage_coloring_semantic_not_configurable;
	public static String DLTKEditorPreferencePage_CommentTaskTags;

	public static String ProjectSelectionDialog_title;
	public static String ProjectSelectionDialog_desciption;
	public static String ProjectSelectionDialog_filter;

	public static String UserLibraryPreferencePage_UserLibraryPreferencePage_libraries_up_button;
	public static String UserLibraryPreferencePage_UserLibraryPreferencePage_libraries_down_button;

	public static String BuildpathVariablesPreferencePage_title;
	public static String BuildpathVariablesPreferencePage_description;
	public static String BuildpathVariablesPreferencePage_savechanges_title;
	public static String BuildpathVariablesPreferencePage_savechanges_message;

	public static String EditorPreferencePage_title0;
	public static String EditorPreferencePage_smartHomeEnd;
	public static String EditorPreferencePage_subWordNavigation;
	public static String EditorPreferencePage_smartIndent;
	public static String EditorPreferencePage_tabAlwaysIndent;
	public static String EditorPreferencePage_evaluateTemporaryProblems;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_title1;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_matchingBrackets;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_matchingBracketsHighlightColor;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_showSegments;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_backgroundForMethodParameters;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_foregroundForMethodParameters;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_backgroundForCompletionReplacement;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_foregroundForCompletionReplacement;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_sourceHoverBackgroundColor;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_color;
	/**
	 * @since 2.0
	 */
	public static String EditorPreferencePage_systemDefault;

	public static String NewScriptProjectPreferencePage_title;
	public static String NewScriptProjectPreferencePage_description;
	public static String NewScriptProjectPreferencePage_sourcefolder_label;
	public static String NewScriptProjectPreferencePage_sourcefolder_project;
	public static String NewScriptProjectPreferencePage_sourcefolder_folder;
	public static String NewScriptProjectPreferencePage_folders_src;
	public static String NewScriptProjectPreferencePage_folders_error_namesempty;
	public static String NewScriptProjectPreferencePage_folders_error_invalidsrcname;
	public static String NewScriptProjectPreferencePage_folders_error_invalidcp;
	public static String NewScriptProjectPreferencePage_error_decode;
	public static String EditorPreferencePage_folding_title;

	public static String SourceParsers_groupLabel;
	public static String SourceParsers_nameLabel;
	public static String SourceParsers_LinkToPreferences;

	public static String MarkOccurrencesConfigurationBlock_title;
	public static String MarkOccurrencesConfigurationBlock_link;
	public static String MarkOccurrencesConfigurationBlock_link_tooltip;
	public static String MarkOccurrencesConfigurationBlock_stickyOccurrences;
	public static String MarkOccurrencesConfigurationBlock_markOccurrences;
}
