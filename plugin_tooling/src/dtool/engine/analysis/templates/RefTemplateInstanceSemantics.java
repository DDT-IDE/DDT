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

import java.util.LinkedList;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.InvalidRefErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotATypeErrorElement;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
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

public class RefTemplateInstanceSemantics extends ReferenceSemantics {
	
	protected final RefTemplateInstance refTemplateInstance;
	
	public RefTemplateInstanceSemantics(RefTemplateInstance refTemplateInstance, PickedElement<?> pickedElement) {
		super(refTemplateInstance, pickedElement);
		this.refTemplateInstance = refTemplateInstance;
	}
	
	@Override
	public INamedElement doResolveTargetElement() {
		INamedElement resolvedTemplate = refTemplateInstance.tplRef.getSemantics(context).resolveTargetElement_();
		if(true) {
			/* FIXME: TODO */
			return createTemplateInstance(resolvedTemplate);
		} else {
			return resolvedTemplate;
		}
	}
	
	protected INamedElement createTemplateInstance(INamedElement resolvedTemplate) {
		RefTemplateInstance refTplInstance = this.refTemplateInstance;
		
		// TODO: find best match for template overload
			
		INamedElement templateInstance = null;
		
		final LinkedList<DefinitionTemplate> templates = new LinkedList<>();
		
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
		
		for (DefinitionTemplate defTemplate : templates) {
			
			templateInstance = createTemplateInstance(defTemplate, refTplInstance);
			
			if(templateInstance != null) {
				return templateInstance;
			}
		}
		
		return new ErrorElement(ERROR__TPL_REF_MATCHED_NONE, refTemplateInstance, null);
	}
	
	protected INamedElement createTemplateInstance(DefinitionTemplate templateDef, RefTemplateInstance templateRef) {
		
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
			INamedElementNode templateArgument = tplParameter.createTemplateArgument(argument, context);
			
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
	
	public static ITypeNamedElement resolveTargetType(Resolvable target, ISemanticContext parentContext) {
		assertNotNull(target);
		
		if(target instanceof Reference) {
			Reference reference = (Reference) target;
			
			IConcreteNamedElement targetType = resolveConcreteElement(reference, parentContext);
			
			if(targetType instanceof ITypeNamedElement) {
				return (ITypeNamedElement) targetType;
			} else {
				return new NotATypeErrorElement(reference, targetType);
			}
		} else {
			return new ErrorElement(ERROR__TPL_ARG__NotAType, target, null);
		}
	}
	
}