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

import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ISemanticResolution;
import melnorme.lang.tooling.engine.resolver.AbstractResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolutionResult;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.RandomAccess2;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefTemplateInstance;

public class RefTemplateInstanceSemantics extends AbstractResolvableSemantics {
	
	protected final RefTemplateInstance refTemplateInstance;
	
	public RefTemplateInstanceSemantics(RefTemplateInstance refTemplateInstance) {
		this.refTemplateInstance = refTemplateInstance;
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return this.refTemplateInstance.tplRef.findTargetDefElements(moduleResolver, findOneOnly);
	}
	
	@Override
	public ResolutionResult resolveTargetElement(ISemanticResolution sr) {
		Collection<INamedElement> templates = this.refTemplateInstance.tplRef.findTargetDefElements(sr, false);
		return createTemplateInstance(templates);
	}
	
	protected ResolutionResult createTemplateInstance(Collection<INamedElement> templates) {
		RefTemplateInstance refTplInstance = this.refTemplateInstance;
		
		// TODO: find best match for template overload
		for (INamedElement namedElement : templates) {
			
			TemplateInstance templateInstance = null;
			
			if(namedElement instanceof DefinitionTemplate) {
				DefinitionTemplate template = (DefinitionTemplate) namedElement;
				
				templateInstance = createTemplateInstance(template, refTplInstance);
				
				if(templateInstance != null) {
					return new ResolutionResult(templateInstance);
				}
			}
		}
		
		return new ResolutionResult();
	}
	
	protected TemplateInstance createTemplateInstance(DefinitionTemplate template, 
			RefTemplateInstance refTplInstance) {
		
		RandomAccess2<Resolvable> tplArgs = refTplInstance.getEffectiveArguments();
		
		int paramSize = template.getTemplateParameters().size();
		if(paramSize != tplArgs.size()) {
			return null;
		}
		
		ArrayList2<ASTNode> instantiatedArgs = new ArrayList2<>();
		
		for (int ix = 0; ix < paramSize; ix++) {
			TemplateParameter tplParameter = template.tplParams.get(ix);
			
			instantiatedArgs.add(tplParameter.createTemplateArgument(tplArgs.get(ix)));
		}
		
		return new TemplateInstance(template, instantiatedArgs);
	}
	
}