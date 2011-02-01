package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.NewExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpNew extends Expression {
	
	public Resolvable[] allocargs;
	public Reference newtype;
	public Resolvable[] args;
	
	public ExpNew(NewExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.newargs != null) {
			this.allocargs = Expression.convertMany(elem.newargs, convContext);
		} 
		if(elem.newtype == null){
			this.newtype = new Reference.InvalidSyntaxReference();
		} else {
			this.newtype = ReferenceConverter.convertType(elem.newtype, convContext);
		}
		assertNotNull(newtype);
		if(elem.arguments != null)
			this.args = Expression.convertMany(elem.arguments, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, newtype);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}
	
}
