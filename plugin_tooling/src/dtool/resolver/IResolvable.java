package dtool.resolver;

import java.util.Collection;

import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.IModuleResolver;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable {
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	Collection<ILangNamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly);
	
}