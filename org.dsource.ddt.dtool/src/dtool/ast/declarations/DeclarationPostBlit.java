package dtool.ast.declarations;

import dtool.ast.ASTNodeTypes;
import dtool.ast.statements.IFunctionBody;

public class DeclarationPostBlit extends DeclarationSpecialFunction {
	
	public DeclarationPostBlit(IFunctionBody fnBody) {
		super(SpecialFunctionKind.POST_BLIT, fnBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_POST_BLIT;
	}
	
}