package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.resolver.LanguageIntrinsics;

public class ExpLiteralArray extends Expression {
	
	public final NodeListView<Expression> elements;
	
	public ExpLiteralArray(NodeListView<Expression> elements) {
		this.elements = parentize(elements);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, elements);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("[ ", elements, ", " , " ]");
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return Collections.<INamedElement>singleton(LanguageIntrinsics.D2_063_intrinsics.dynArrayType);
	}
	
}