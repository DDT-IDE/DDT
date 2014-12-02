package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics.ExpSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.engine.analysis.DeeLanguageIntrinsics;

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
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics(ISemanticContext parentContext) {
		return new ExpSemantics(this, parentContext) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(boolean findOneOnly) {
			return Collections.<INamedElement>singleton(DeeLanguageIntrinsics.D2_063_intrinsics.dynArrayType);
		}
		
	};
	}
	
}