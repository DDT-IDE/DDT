package melnorme.lang.tooling.engine.resolver;

import java.util.Collection;

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable {
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly);
	
	
	Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr);
	
	public IResolvableSemantics getNodeSemantics();
	
}