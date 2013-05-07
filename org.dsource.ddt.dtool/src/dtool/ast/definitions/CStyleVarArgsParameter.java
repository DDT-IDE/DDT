package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;


/** 
 * C-style var args paramater, as in: <br>
 * <code> ... </code>
 */
public class CStyleVarArgsParameter extends ASTNode implements IFunctionParameter {
	
	public CStyleVarArgsParameter() {
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.VAR_ARGS_PARAMETER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("...");
	}
	
	@Override
	public boolean isVariadic() {
		return true;
	}
	
	@Override
	public String toStringAsFunctionSignaturePart() {
		return toStringAsCode();
	}
	
	@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return toStringAsCode();
	}
	
	@Override
	public String toStringInitializer() {
		return null;
	}

}