package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ArrayInitializer;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class InitializerArray extends Initializer {

	public Resolvable[] indexes;
	public Initializer[] values;

		
	public InitializerArray(ArrayInitializer elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.indexes = ExpressionConverter.convertMany(elem.index, convContext); 
		this.values = Initializer.convertMany(elem.value, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexes);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
