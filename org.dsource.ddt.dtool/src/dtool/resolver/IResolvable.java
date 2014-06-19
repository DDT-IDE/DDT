package dtool.resolver;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable {
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly);
	
}