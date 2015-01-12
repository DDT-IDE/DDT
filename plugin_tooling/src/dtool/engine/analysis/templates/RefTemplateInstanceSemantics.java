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

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.ITemplateParameter;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefTemplateInstance;

public class RefTemplateInstanceSemantics extends ResolvableSemantics {
	
	protected final RefTemplateInstance refTemplateInstance;
	
	public RefTemplateInstanceSemantics(RefTemplateInstance refTemplateInstance, PickedElement<?> pickedElement) {
		super(refTemplateInstance, pickedElement);
		this.refTemplateInstance = refTemplateInstance;
	}
	
	@Override
	public INamedElement doResolveTargetElement() {
		INamedElement resolvedTemplate = this.refTemplateInstance.tplRef.resolveTargetElement(context);
		if(false) {
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
	
	protected TemplateInstance createTemplateInstance(DefinitionTemplate template, 
			RefTemplateInstance refTplInstance) {
		
		Indexable<Resolvable> tplArgs = refTplInstance.getEffectiveArguments();
		
		int paramSize = template.getITemplateParameters().size();
		if(paramSize != tplArgs.size()) {
			return null;
		}
		
		ArrayList2<INamedElementNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			ITemplateParameter tplParameter = template.tplParams.get(ix);
			
			instantiatedArgs.add(tplParameter.createTemplateArgument(tplArgs.get(ix)));
		}
		
		return new TemplateInstance(template, instantiatedArgs);
	}
	
}