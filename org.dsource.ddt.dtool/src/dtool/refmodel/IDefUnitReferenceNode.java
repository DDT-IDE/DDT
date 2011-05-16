package dtool.refmodel;

import dtool.ast.IASTNeoNode;


/** A node that references a DefUnit. */
public interface IDefUnitReferenceNode extends IDefUnitReference, IASTNeoNode {

	@Override
	public String toStringAsElement();
}
