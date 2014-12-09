package melnorme.lang.tooling.engine.resolver;

import java.util.Collection;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable extends ILanguageElement {
	
	@Override
	public IResolvableSemantics getSemantics(ISemanticContext parentContext);
	
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr); 
	
	/** Finds the named element matching this {@link IResolvable}. 
	 * If no results are found, return null. */
	Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly);
	
	String toStringAsCode();
	
}