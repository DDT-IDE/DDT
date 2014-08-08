package dtool.ast.references;

import java.util.ArrayList;
import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.IResolvable;
import dtool.resolver.api.DefUnitDescriptor;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable implements IResolvable {
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
	@Override
	public abstract Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly);
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		Collection<INamedElement> resolvedElements = findTargetDefElements(mr, false);
		
		ArrayList<INamedElement> resolvedTypeForValueContext = new ArrayList<>();
		for (INamedElement defElement : resolvedElements) {
			INamedElement resolveTypeForValueContext = defElement.resolveTypeForValueContext(mr);
			if(resolvedTypeForValueContext != null) {
				resolvedTypeForValueContext.add(resolveTypeForValueContext);
			}
		}
		return resolvedTypeForValueContext; 
	}
	
	protected static Collection<INamedElement> resolveToInvalidValue() {
		return null; 
	}
	
}