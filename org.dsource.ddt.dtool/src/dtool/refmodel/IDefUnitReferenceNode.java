package dtool.refmodel;

import descent.internal.compiler.parser.ast.IASTNode;


/** A node that references a DefUnit. */
public interface IDefUnitReferenceNode extends IDefUnitReference, IASTNode {

	@Override
	public String toStringAsElement();
}
