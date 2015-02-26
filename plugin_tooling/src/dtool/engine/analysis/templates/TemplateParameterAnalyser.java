/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;

public abstract class TemplateParameterAnalyser {
	
	public static enum TplMatchLevel {
		NONE,
		THIS,
		TUPLE,
		ALIAS,
		VALUE,
		TYPE,
		TYPE_SPECIALIZED;
		
		public boolean isHigherPriority(TplMatchLevel other) {
			return ordinal() > other.ordinal();
		}
	}
	
	/**
	 * @return and integer indicating how well this parameter matches given argument. 
	 * Highest means more priority.
	 */
	public abstract TplMatchLevel getMatchPriority(Resolvable tplArg, ISemanticContext context);
	
	/** 
	 * Create template argument element for given argument 
	 * @param tplArgs non-null
	 * @param tplRefContext non-null.
	 * @return the created tamplate argument, or null if the given argument is not applicable to the parameter.
	 */
	public abstract INamedElementNode createTemplateArgument(Indexable<Resolvable> tplArgs, int argIndex, 
			ISemanticContext tplRefContext);
	
	
	protected Resolvable getArgument(Indexable<Resolvable> tplArgs, int argIndex, Resolvable defaultValue) {
		Resolvable result = argIndex < tplArgs.size() ? tplArgs.get(argIndex) : defaultValue;
		
		if(result == null) {
			// This shouldn't happen, but protected against a NPE just in case.
			MissingExpression missingExpression = new MissingExpression();
			missingExpression.setSourceRange(0, 0);
			missingExpression.setParsedStatus().setElementReady();
			return missingExpression;
		}
		return result;
	}
	
	public static class NotInstantiatedErrorElement extends ErrorElement {
		
		public static final String NAME = ERROR_PREFIX + "NotInstantiated";
		
		public NotInstantiatedErrorElement(ILanguageElement ownerElement, ElementDoc doc) {
			super(NAME, ownerElement, doc);
		}
		
	}

}