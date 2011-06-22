package dtool.ast.definitions;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class DefinitionCtor extends ASTNeoNode implements ICallableElement {
	
	public static enum SpecialFunctionKind {
		CONSTRUCTOR("this"),
		DESTRUCTOR("~this"),
		ALLOCATOR("new"),
		DEALLOCATOR("delete"),
		;
		public final String specialName;
		
		private SpecialFunctionKind(String specialName) {
			this.specialName = specialName;
		}
	}
	
	public final SpecialFunctionKind kind; // whether it is a constructor or destructor
	public final IFunctionParameter[] params;
	public final int varargs;
	public final IStatement fbody;
	public final int nameStart;
	
	public DefinitionCtor(SpecialFunctionKind kind, IFunctionParameter[] params, int varargs, IStatement fbody, int thisStart, SourceRange sourceRange) {
		this.kind = kind;
		this.params = params;
		this.varargs = varargs;
		this.fbody = fbody;
		
		this.nameStart = thisStart;
		initSourceRange(sourceRange);	
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public IFunctionParameter[] getParameters() {
		return ArrayUtil.copyFrom(params);
	}
	
}
