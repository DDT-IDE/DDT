/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui;

public class PreferenceConstants {

	/**
	 * A named preference that controls if segmented view (show selected element
	 * only) is turned on or off.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_SHOW_SEGMENTS = "org.eclipse.dltk.ui.editor.showSegments"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether folding is enabled in the Script
	 * editor.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_ENABLED = "editor_folding_enabled"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether comment folding is enabled in
	 * the script editor.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_COMMENTS_FOLDING_ENABLED = "editor_comments_folding_enabled"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether doc folding is enabled in the
	 * script editor.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_DOCS_FOLDING_ENABLED = "editor_docs_folding_enabled"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether comments are initially folded
	 * when the editor is opened.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_INIT_COMMENTS = "editor_folding_init_comments"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether docs are initially folded when
	 * the editor is opened.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_INIT_DOCS = "editor_folding_init_docs"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether header comments are initially
	 * folded when the editor is opened.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_INIT_HEADER_COMMENTS = "editor_folding_init_header_comments"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether classes (packages, modules, etc)
	 * are initially folded when the editor is opened.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_INIT_CLASSES = "editor_folding_init_classes"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether methods are initially folded
	 * when the editor is opened.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_INIT_METHODS = "editor_folding_init_methods"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether comments separated by newlines
	 * are joined together to form a single comment folding block.
	 * 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String EDITOR_COMMENT_FOLDING_JOIN_NEWLINES = "editor_comments_folding_join_newlines";

	/**
	 * A named preference that controls minimal number of lines in block to be
	 * folded.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public static final String EDITOR_FOLDING_LINES_LIMIT = "editor_folding_lines_limit"; //$NON-NLS-1$

}
