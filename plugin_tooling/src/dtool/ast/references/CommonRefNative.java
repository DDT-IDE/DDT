package dtool.ast.references;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.api.DefUnitDescriptor;

// TODO: review this hierarchy
public abstract class CommonRefNative extends Reference {
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		if(defunit.isNative())
			return true;
		return false;
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr) {
		return resolveToInvalidValue(); // This ref refers to a type, not a value
	}
	
}