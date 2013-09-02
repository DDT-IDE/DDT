package dtool.ast.expressions;


import java.util.Collection;
import java.util.Collections;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.resolver.api.IModuleResolver;

public abstract class Expression extends Resolvable implements IQualifierNode, IInitializer {
	
	// deprecate
	public Collection<INamedElement> getType(IModuleResolver moduleResolver) {
		return findTargetDefElements(moduleResolver, false);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.emptySet();
	}
	
}