package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;


/** 
 * A nameless function parameter, such as in: <br>
 * <code> void func(int, int); </code>
 */
public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {
	
	public final FnParameterAttributes paramAttribs;
	public final Reference type;
	public final Expression defaultValue;
	public final boolean isVariadic;
	
	public NamelessParameter(ArrayView<FunctionParamAttribKinds> attribList, Reference type, Expression defaultValue, 
		boolean isVariadic, SourceRange sourceRange) {
		this.paramAttribs = FnParameterAttributes.create(attribList); 
		this.type = parentize(assertNotNull_(type));
		this.defaultValue = parentize(defaultValue);
		this.isVariadic = isVariadic;
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.NAMELESS_PARAMETER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		paramAttribs.toStringAsCode(cp);
		cp.append(type);
		cp.appendNode(" = ", defaultValue);
		cp.append(isVariadic, "...");
	}
	
	@Override
	public boolean isVariadic() {
		return isVariadic;
	}
	
	@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement();
	}
	
	@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return type.toStringAsElement();
	}
	
	@Override
	public String toStringInitializer() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsElement();
	}
	
}