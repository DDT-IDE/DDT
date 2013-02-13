package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;


/** 
 * A nameless function parameter, such as in: <br>
 * <code> void func(int, int); </code>
 */
public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {
	
	public final Reference type;
	public final int storageClass;
	public final Resolvable defaultValue;
	
	public NamelessParameter(Reference type, int storageClass, Resolvable defaultValue, SourceRange sourceRange) {
		assertNotNull(type);
		initSourceRange(sourceRange);
		this.type = parentize(type);
		this.storageClass = storageClass;
		this.defaultValue = parentize(defaultValue);
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