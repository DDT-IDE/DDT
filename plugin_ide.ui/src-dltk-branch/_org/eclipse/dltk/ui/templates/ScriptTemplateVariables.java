/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package _org.eclipse.dltk.ui.templates;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.templates.TemplateMessages;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

public final class ScriptTemplateVariables {
	private ScriptTemplateVariables() {
	}

	public static class File extends TemplateVariableResolver {
		public static final String NAME = "file"; //$NON-NLS-1$

		public File() {
			super(NAME, TemplateMessages.Variable_File_Description);
		}

		@Override
		protected String resolve(TemplateContext context) {
//			ISourceModule module = getSourceModule(context);
//			return (module == null) ? null : module.getElementName();
			return null;
		}

		@Override
		protected boolean isUnambiguous(TemplateContext context) {
			return resolve(context) != null;
		}
	}

	public static class Language extends TemplateVariableResolver {
		public static final String NAME = "language"; //$NON-NLS-1$

		public Language() {
			super(NAME, TemplateMessages.Variable_Language_Description);
		}

		@Override
		protected String resolve(TemplateContext context) {
			IDLTKLanguageToolkit toolkit = DeeLanguageToolkit.getDefault();
			return toolkit.getLanguageName();
		}

		@Override
		protected boolean isUnambiguous(TemplateContext context) {
			return resolve(context) != null;
		}
	}

}
