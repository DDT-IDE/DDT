package dtool.ast.definitions;

import descent.core.ddoc.Ddoc;
import dtool.engine.common.IDefElement;
import dtool.engine.common.ILanguageNode;

/**
 * A handle to a defined, named language element. 
 * May exists in source or outside source, it can be implicitly or explicitly defined.
 * Implementation may be an AST node such as {@link DefUnit} (that is the more common case).
 */
public interface INamedElement extends ILanguageNode, IDefElement {
	
	/** Gets the archetype (the kind) of this DefElement. */
	EArcheType getArcheType();
	
	/** @return the DefUnit this named element represents. In most cases this is the same as the receiver, 
	 * but this method allows proxy {@link INamedElement} classes to resolve to their proxied {@link DefUnit}. 
	 * It may still return null since the underlying defunit may not exist at all (implicitly defined named elements).
	 */
	DefUnit resolveDefUnit();
	
	/** Resolve the underlying element and return its DDoc. See {@link #resolveDefUnit()}.
	 * Can be null. */
	Ddoc resolveDDoc();
	
}