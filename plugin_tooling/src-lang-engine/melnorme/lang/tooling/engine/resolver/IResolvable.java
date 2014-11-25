package melnorme.lang.tooling.engine.resolver;

import java.util.Collection;

import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable extends ISemanticElement {
	
	public IResolvableSemantics getSemantics();
	
	
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr); 
//	{
//		return getSemantics().findTargetDefElements(mr, true);
//	}
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly);
	
	String toStringAsCode();
	
}