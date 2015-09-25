/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
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
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.ITemplatableElement;
import dtool.ast.definitions.ITemplateParameter;
import dtool.ast.definitions.TemplateTupleParam;
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
		
		final ArrayList2<ITemplatableElement> templates = new ArrayList2<>();
		
		if(isTemplate(resolvedTemplate)) {
			ITemplatableElement defTemplate = (ITemplatableElement) resolvedTemplate;
			templates.add(defTemplate);
		} else if(resolvedTemplate instanceof OverloadedNamedElement) {
			OverloadedNamedElement overload = (OverloadedNamedElement) resolvedTemplate;
			for (INamedElement overloadElement : overload.getOverloadedElements()) {
				if(isTemplate(overloadElement)) {
					ITemplatableElement defTemplate = (ITemplatableElement) overloadElement;
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
	
	protected static boolean isTemplate(INamedElement resolvedTemplate) {
		if(resolvedTemplate instanceof ITemplatableElement) {
			ITemplatableElement templatableElement = (ITemplatableElement) resolvedTemplate;
			return templatableElement.isTemplated();
		}
		return false;
	}
	
	protected INamedElement instantiateTemplate(final ArrayList2<ITemplatableElement> originalTemplates) {
		
		Indexable<Resolvable> tplArgs = refTemplateInstance.getEffectiveArguments();
		
		ArrayList2<ITemplatableElement> matchingTemplates = originalTemplates;
		
		int tplArgsSize = tplArgs.size();
		for (int ix = 0; ix < tplArgsSize; ix++) {
			
			Resolvable tplArg = tplArgs.get(ix);
			
			TplMatchLevel matchLevel = TplMatchLevel.NONE;
			
			ArrayList2<ITemplatableElement> templates = matchingTemplates;
			matchingTemplates = new ArrayList2<>();
			
			for (ITemplatableElement defTemplate : templates) {
				if(defTemplate.isTemplated() == false) 
					continue;
				
				TplMatchLevel newMatchLevel = getMatchLevel(defTemplate, ix, tplArg, context);
				
				if(newMatchLevel == TplMatchLevel.NONE)
					continue;
				
				if(newMatchLevel.isHigherPriority(matchLevel)) {
					matchLevel = newMatchLevel;
					matchingTemplates.clear(); // Clear previous less-priority matches
				}
				
				matchingTemplates.add(defTemplate);
			}
			
		}
		
		
		ArrayList2<ITemplatableElement> templates = matchingTemplates;
		matchingTemplates = new ArrayList2<>();
		
		// Match remaining templates against default args.
		for (ITemplatableElement defTemplate : templates) {
			if(canMatchRemainingParameters(tplArgsSize, defTemplate)) {
				matchingTemplates.add(defTemplate);
			}
		}
		
		if(matchingTemplates.size() == 0) {
			return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, refTemplateInstance, null);
		} else if(matchingTemplates.size() > 1) {
			// This should be an error, but because there are limitations in our matching code,
			// we ignore this error and just use the first match as the effective match.
		}
		
		ITemplatableElement defTemplate = matchingTemplates.get(0);
		
		TemplateInstance templateInstance = createTemplateInstance(defTemplate);
		if(templateInstance == null) {
			return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, refTemplateInstance, null);
		}
		return templateInstance.instantiatedElement;
		
	}
	
	protected boolean canMatchRemainingParameters(int tplArgsSize, ITemplatableElement defTemplate) {
		
		for (int ix = tplArgsSize; ix < defTemplate.getTemplateParameters().size(); ix++) {
			
			TplMatchLevel newMatchLevel = getMatchLevel(defTemplate, ix, null, context);
			
			if(newMatchLevel == TplMatchLevel.NONE) {
				return false;
			}
		}
		return true;
	}
	
	protected static TplMatchLevel getMatchLevel(ITemplatableElement defTemplate, int paramIndex, Resolvable tplArg, 
			ISemanticContext context) {
		assertTrue(defTemplate.isTemplated());
		ArrayView<ITemplateParameter> tplParams = defTemplate.getTemplateParameters();
		
		if(tplParams.size() <= paramIndex) {
			
			if(tplParams.size() > 0) {
				ITemplateParameter lastParam = tplParams.get(tplParams.size()-1);
				if(lastParam instanceof TemplateTupleParam) {
					return TplMatchLevel.TUPLE;
				}
			}
			
			return TplMatchLevel.NONE; 
		}
		
		ITemplateParameter tplParam = tplParams.get(paramIndex);
		return tplParam.getParameterAnalyser().getMatchPriority(tplArg, context);
	}
	
	protected TemplateInstance createTemplateInstance(ITemplatableElement templateDef) {
		RefTemplateInstance templateRef = refTemplateInstance;
		
		Indexable<Resolvable> tplArgs = templateRef.getEffectiveArguments();
		NodeVector<ITemplateParameter> templateParams = templateDef.getTemplateParameters();
		
		// TODO: cache for template instance
		
		int paramSize = templateParams.size();
		
		ArrayList2<INamedElementNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			ITemplateParameter tplParameter = templateParams.get(ix);
			
			INamedElementNode templateArgument = tplParameter.getParameterAnalyser().createTemplateArgument(
				tplArgs, ix, context);
			
			if(templateArgument == null) {
				return null;
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