package dtool.ast.definitions;

import dtool.ast.IASTNeoNode;

public interface IFunctionParameter extends IASTNeoNode {

	/** Basicly, returns the type string for this function parameter. */
	String toStringAsFunctionSimpleSignaturePart();

	/** A String representation for the initializer*/
	String toStringInitializer();
	
	/** Returns a string to be used as part of function full signature.  */
	String toStringAsFunctionSignaturePart();

}