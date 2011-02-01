package dtool.ast.expressions;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AssocArrayLiteralExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpLiteralMapArray extends Expression {

	public final Resolvable[] keys;
	public final Resolvable[] values;
	
	public ExpLiteralMapArray(AssocArrayLiteralExp node, ASTConversionContext convContext) {
		convertNode(node);
		Assert.isTrue(node.keys.size() == node.values.size());
		this.keys = new Resolvable[node.keys.size()];
		DescentASTConverter.convertMany(node.keys, this.keys, convContext);
		this.values = new Resolvable[node.values.size()];
		DescentASTConverter.convertMany(node.keys, this.keys, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);	 
	}

}
