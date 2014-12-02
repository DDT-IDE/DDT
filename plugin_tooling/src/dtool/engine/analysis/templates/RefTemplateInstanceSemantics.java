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

import java.util.Collection;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefTemplateInstance;

public class RefTemplateInstanceSemantics extends ResolvableSemantics {
	
	protected final RefTemplateInstance refTemplateInstance;
	
	public RefTemplateInstanceSemantics(RefTemplateInstance refTemplateInstance, ISemanticContext parentContext) {
		super(refTemplateInstance, parentContext);
		this.refTemplateInstance = refTemplateInstance;
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return this.refTemplateInstance.tplRef.findTargetDefElements(context, findOneOnly);
	}
	
	@Override
	protected ResolvableResult createResolution(ISemanticContext context) {
		Collection<INamedElement> templates = this.refTemplateInstance.tplRef.findTargetDefElements(context, false);
		return createTemplateInstance(templates);
	}
	
	protected ResolvableResult createTemplateInstance(Collection<INamedElement> templates) {
		RefTemplateInstance refTplInstance = this.refTemplateInstance;
		
		// TODO: find best match for template overload
		for (INamedElement namedElement : templates) {
			
			TemplateInstance templateInstance = null;
			
			if(namedElement instanceof DefinitionTemplate) {
				DefinitionTemplate template = (DefinitionTemplate) namedElement;
				
				templateInstance = createTemplateInstance(template, refTplInstance);
				
				if(templateInstance != null) {
					return new ResolvableResult(templateInstance);
				}
			}
		}
		
		return new ResolvableResult(null);
	}
	
	protected TemplateInstance createTemplateInstance(DefinitionTemplate template, 
			RefTemplateInstance refTplInstance) {
		
		Indexable<Resolvable> tplArgs = refTplInstance.getEffectiveArguments();
		
		int paramSize = template.getTemplateParameters().size();
		if(paramSize != tplArgs.size()) {
			return null;
		}
		
		ArrayList2<INamedElementNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			TemplateParameter tplParameter = template.tplParams.get(ix);
			
			instantiatedArgs.add(tplParameter.createTemplateArgument(tplArgs.get(ix)));
		}
		
		return new TemplateInstance(template, instantiatedArgs);
	}
	
}