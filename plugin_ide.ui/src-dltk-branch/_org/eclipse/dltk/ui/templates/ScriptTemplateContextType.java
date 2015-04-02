/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.templates;

import org.eclipse.dltk.ui.templates.TemplateMessages;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;

/**
 * A very simple context type.
 * 
 * <p>
 * Subclasses must provide an implementation for all constructors provided by
 * this class.
 * </p>
 */
public abstract class ScriptTemplateContextType extends TemplateContextType {

	public ScriptTemplateContextType() {
		setupResolvers();
	}

	public ScriptTemplateContextType(String id) {
		super(id);
		setupResolvers();
	}

	public ScriptTemplateContextType(String id, String name) {
		super(id, name);
		setupResolvers();
	}

	public abstract ScriptTemplateContext createContext(IDocument document,
			int completionPosition, int length);

	public ScriptTemplateContext createContext(IDocument document, Position position) {
		// TODO abstract and let everyone implement it
		return createContext(document, position.getOffset(), position.getLength());
	}

	@Override
	protected void validateVariables(TemplateVariable[] variables)
			throws TemplateException {
		// Check for multiple cursor variables
		for (int i = 0; i < variables.length; i++) {
			TemplateVariable var = variables[i];
			if (var.getType().equals(GlobalTemplateVariables.Cursor.NAME)) {
				if (var.getOffsets().length > 1) {
					throw new TemplateException(
							TemplateMessages.Validation_SeveralCursorPositions);
				}
			}
		}
	}

	/**
	 * Adds global template variable resolvers
	 */
	protected void addGlobalResolvers() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
	}

	/**
	 * Adds script template variable resolvers
	 * 
	 * <p>
	 * Subclasses may override this method if they wish to add additional
	 * resolvers.
	 * </p>
	 */
	protected void addScriptResolvers() {
		addResolver(new ScriptTemplateVariables.File());
		addResolver(new ScriptTemplateVariables.Language());
	}

	private void setupResolvers() {
		addGlobalResolvers();
		addScriptResolvers();
	}
}
