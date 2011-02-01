package dtool.refmodel;

import java.util.Iterator;
import java.util.List;

import descent.internal.compiler.parser.ast.IASTNode;

/**
 * A scope is a list of declarations and or statements.
 * Some of those declarations may be DefUnits.
 * A scope may have several super scopes, and has exactly one outer scope.
 * A scope may be a statement block, which has different lookup rules. 
 */
public interface IScope {


	/** Gets all members of this scope, DefUnit or not. 
	 * Used to iterate and find DefUnits .*/
	Iterator<? extends IASTNode> getMembersIterator();
	
	/** Returns the super (as in superclass) scopes of this scope.
	 * Scopes should be ordered according to priority.
	 * FIXME: a scope can be null for now. */
	List<IScope> getSuperScopes();
	
	/** Gets the module of the scope. Cannot be null. */
	IScope getModuleScope();
	
	/** Returns whether this scope has a sequential lookup, 
	 * such as statement scopes. */
	boolean hasSequentialLookup();
	
	/** For UI printing */
	String toStringAsElement();


}