package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IFunctionBody;

public class DeclarationSpecialFunction extends ASTNode {
	
	public static enum SpecialFunctionKind {
		POST_BLIT("this(this)"),
		
		DESTRUCTOR("~this()"),
		STATIC_CONSTRUCTOR("static this()"),
		STATIC_DESTRUCTOR("static ~this()"),
		SHARED_STATIC_CONSTRUCTOR("shared static this()"),
		SHARED_STATIC_DESTRUCTOR("shared static ~this()"),
		;
		public final String sourceValue;
		
		private SpecialFunctionKind(String sourceValue) {
			this.sourceValue = sourceValue;
		}
		
		public String toStringAsCode() {
			return sourceValue;
		}
	}
	
	public final SpecialFunctionKind kind;
	public final IFunctionBody fnBody;
	
	public DeclarationSpecialFunction(SpecialFunctionKind kind, IFunctionBody fnBody) {
		this.kind = assertNotNull_(kind);
		this.fnBody = parentizeI(fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_SPECIAL_FUNCTION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fnBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(kind.toStringAsCode());
		cp.appendNode(fnBody);
	}
	
}