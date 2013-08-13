package dtool.resolver;

import java.util.Iterator;
import java.util.List;

import dtool.ast.IASTNode;
import dtool.resolver.api.IModuleResolver;

/**
 * A scope is a list of declarations and or statements.
 * Some of those declarations may be DefUnits.
 * A scope may have several super scopes, and has exactly one outer scope.
 * A scope may be a statement block, which has different lookup rules.
 * @deprecated in favor of {@link IResolveParticipant} 
 */
public interface IScope extends IBaseScope {

	/** Gets all members of this scope, DefUnit or not. 
	 * Used to iterate and find DefUnits.
	 * The iterator must iterate the members in order! (accourding to source position) */
	Iterator<? extends IASTNode> getMembersIterator(IModuleResolver moduleResolver);
	
	/** Returns the super (as in superclass) scopes of this scope.
	 * Scopes should be ordered according to priority.
	 * FIXME: a scope can be null for now. */
	List<IScope> getSuperScopes(IModuleResolver moduleResolver);
	
	/** Gets the module of the scope. Cannot be null. */
	INamedScope getModuleScope();
	
	/** Returns whether this scope has a sequential lookup, 
	 * such as statement scopes. */
	boolean hasSequentialLookup();
	
}