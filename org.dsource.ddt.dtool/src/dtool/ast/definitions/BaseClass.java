package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class BaseClass extends ASTNeoNode {
	
	public PROT prot;
	public Reference type;
	
	public BaseClass(descent.internal.compiler.parser.BaseClass elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.hasNoSourceRangeInfo()) 
			convertNode(elem.type); // Try to have some range
			
		this.prot = elem.protection;
		this.type = ReferenceConverter.convertType(elem.type, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);	 			
	}
}