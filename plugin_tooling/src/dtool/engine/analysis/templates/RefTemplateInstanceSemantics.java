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
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotATypeErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefinitionTemplate;
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
	
	protected TemplateInstance createTemplateInstance(INamedElement resolvedTemplate) {
		RefTemplateInstance refTplInstance = this.refTemplateInstance;
		
		// TODO: find best match for template overload
			
		TemplateInstance templateInstance = null;
		
		if(resolvedTemplate instanceof DefinitionTemplate) {
			DefinitionTemplate template = (DefinitionTemplate) resolvedTemplate;
			
			templateInstance = createTemplateInstance(template, refTplInstance);
			
			if(templateInstance != null) {
				return templateInstance;
			}
		}
		
		return null;
	}
	
	protected TemplateInstance createTemplateInstance(DefinitionTemplate templateDef, RefTemplateInstance templateRef) {
		
		Indexable<Resolvable> tplArgs = templateRef.getEffectiveArguments();
		
		int paramSize = templateDef.getITemplateParameters().size();
		if(paramSize != tplArgs.size()) {
			return null;
		}
		
		ArrayList2<INamedElementNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			ITemplateParameter tplParameter = templateDef.tplParams.get(ix);
			
			Resolvable argument = tplArgs.get(ix);
			assertNotNull(argument);
			instantiatedArgs.add(tplParameter.createTemplateArgument(argument, context));
		}
		
		return new TemplateInstance(templateRef, context, templateDef, instantiatedArgs);
	}
	
	/* -----------------  ----------------- */
	
	public static final String ERROR__TPL_ARG__NotAType = ErrorElement.ERROR_PREFIX + "TemplateArgumentIsNotAType";
	
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