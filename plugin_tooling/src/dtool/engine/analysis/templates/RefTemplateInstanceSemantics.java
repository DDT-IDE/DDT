/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ListIterator;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.InvalidRefErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.Invalid_TypeErrorElement;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableUtil;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.ITemplateParameter;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.TemplateParameterAnalyser.TplMatchLevel;

public class RefTemplateInstanceSemantics extends ReferenceSemantics {
	
	protected final RefTemplateInstance refTemplateInstance;
	
	public RefTemplateInstanceSemantics(RefTemplateInstance refTemplateInstance, PickedElement<?> pickedElement) {
		super(refTemplateInstance, pickedElement);
		this.refTemplateInstance = refTemplateInstance;
	}
	
	@Override
	public INamedElement doResolveTargetElement() {
		return createTemplateInstance();
	}
	
	protected INamedElement createTemplateInstance() {
		INamedElement resolvedTemplate = refTemplateInstance.tplRef.getSemantics(context).resolveTargetElement_();
		
		final ArrayList2<DefinitionTemplate> templates = new ArrayList2<>();
		
		if(resolvedTemplate instanceof DefinitionTemplate) {
			DefinitionTemplate defTemplate = (DefinitionTemplate) resolvedTemplate;
			templates.add(defTemplate);
		} else if(resolvedTemplate instanceof OverloadedNamedElement) {
			OverloadedNamedElement overload = (OverloadedNamedElement) resolvedTemplate;
			for (INamedElement overloadElement : overload.getOverloadedElements()) {
				if(overloadElement instanceof DefinitionTemplate) {
					DefinitionTemplate defTemplate = (DefinitionTemplate) overloadElement;
					templates.add(defTemplate);
				} else {
					return overload;
				}
			}
		} else {
			if(resolvedTemplate.getArcheType() == EArcheType.Error) {
				return resolvedTemplate;
			}
			return new InvalidRefErrorElement(ERROR__NotATemplate, refTemplateInstance, resolvedTemplate, null);	
		}
		
		return instantiateTemplate(templates);
	}
	
	protected INamedElement instantiateTemplate(ArrayList2<DefinitionTemplate> templates) {
		
		Indexable<Resolvable> tplArgs = refTemplateInstance.getEffectiveArguments();
		
		ArrayList2<DefinitionTemplate> matchingTemplates = templates;
		
		for (int ix = 0; ix < tplArgs.size(); ix++) {
			TplMatchLevel matchLevel = TplMatchLevel.NONE;
			
			Resolvable tplArg = tplArgs.get(0); /*FIXME: BUG here*/
			
			templates = matchingTemplates;
			matchingTemplates = new ArrayList2<>();
			
			for (ListIterator<DefinitionTemplate> iterator = templates.listIterator(); iterator.hasNext(); ) {
				DefinitionTemplate defTemplate = iterator.next();
				
				TplMatchLevel newMatchLevel = getMatchLevel(ix, defTemplate, tplArg);
				
				if(newMatchLevel == TplMatchLevel.NONE) {
					continue;
				}
				
				if(newMatchLevel.isHigherPriority(matchLevel)) {
					matchLevel = newMatchLevel;
					matchingTemplates.clear(); // Clear previous less-priority matches
				}
				
				matchingTemplates.add(defTemplate);
			}
			
		}
		
		if(matchingTemplates.size() == 0) {
			return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, refTemplateInstance, null);
		} else if(matchingTemplates.size() > 1) {
			// This should be an error, but because there are limitations in our matching code,
			// we ignore this error and just use the first match as the effective match.
		}
		
		DefinitionTemplate defTemplate = matchingTemplates.get(0);
			
		return createTemplateInstance(defTemplate);
		
	}
	
	protected TplMatchLevel getMatchLevel(int argIndex, DefinitionTemplate defTemplate, Resolvable tplArg) {
		NodeVector<ITemplateParameter> tplParams = defTemplate.tplParams;
		
		if(tplParams.size() <= argIndex) {
			return TplMatchLevel.NONE; /*FIXME: BUG here for tuples */
		}
		
		ITemplateParameter tplParam = tplParams.get(argIndex);
		return tplParam.getParameterAnalyser().getMatchPriority(tplArg, context);
	}

	protected INamedElement createTemplateInstance(DefinitionTemplate templateDef) {
		RefTemplateInstance templateRef = refTemplateInstance;
		
		Indexable<Resolvable> tplArgs = templateRef.getEffectiveArguments();
		
		int paramSize = templateDef.getITemplateParameters().size();
		if(paramSize != tplArgs.size()) {
			return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, templateRef, null);
		}
		
		ArrayList2<INamedElementNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			ITemplateParameter tplParameter = templateDef.tplParams.get(ix);
			
			Resolvable argument = tplArgs.get(ix);
			assertNotNull(argument);
			INamedElementNode templateArgument = tplParameter.getParameterAnalyser().createTemplateArgument(
				argument, context);
			
			if(templateArgument == null) {
				return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, templateRef, null);
			}
			
			instantiatedArgs.add(templateArgument);
		}
		
		return new TemplateInstance(templateRef, context, templateDef, instantiatedArgs);
	}
	
	/* -----------------  ----------------- */
	
	public static final String ERROR__NotATemplate = ErrorElement.ERROR_PREFIX + "NotATemplate";
	public static final String ERROR__TPL_ARG__NotAType = ErrorElement.ERROR_PREFIX + "TemplateArgumentIsNotAType";
	public static final String ERROR__TPL_REF_MATCHED_NONE = ErrorElement.ERROR_PREFIX +
			"InstantiationDidNotMatchAnyTemplate";
	
	public static ITypeNamedElement resolveTargetTypeOfArg(Resolvable target, ISemanticContext parentContext) {
		assertNotNull(target);
		
		if(target instanceof Reference) {
			Reference reference = (Reference) target;
			return ResolvableUtil.resolveTargetType(reference, parentContext);
		} else {
			return new Invalid_TypeErrorElement(ERROR__TPL_ARG__NotAType, target, null, null);
		}
	}
	
}