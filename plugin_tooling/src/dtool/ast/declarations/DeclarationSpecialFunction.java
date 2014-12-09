package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.statements.IFunctionBody;

public class DeclarationSpecialFunction extends ASTNode implements IDeclaration {
	
	public static enum SpecialFunctionKind {
		POST_BLIT("this(this)"),
		
		DESTRUCTOR("~this()"),
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
	public final ArrayView<FunctionAttributes> fnAttributes;
	public final IFunctionBody fnBody;
	
	public DeclarationSpecialFunction(SpecialFunctionKind kind, ArrayView<FunctionAttributes> fnAttributes, 
		IFunctionBody fnBody) {
		this.kind = assertNotNull(kind);
		this.fnAttributes = fnAttributes;
		this.fnBody = parentize(fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_SPECIAL_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(kind.toStringAsCode());
		cp.appendTokenList(fnAttributes, " ", true);
		cp.append(fnBody);
	}
	
}