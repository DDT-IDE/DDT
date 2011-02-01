package dtool.ast.declarations;

import java.util.Collections;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.UnitTestDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationUnitTest extends ASTNeoNode {

	public BlockStatement body;
	
	public DeclarationUnitTest(UnitTestDeclaration elem, ASTConversionContext convContext) {
		convertNode(elem);
		IStatement convert = Statement.convert(elem.fbody, convContext);
		if (convert instanceof BlockStatement) {
			this.body = (BlockStatement) convert;
		} else {
			// Syntax errors
			this.body = new BlockStatement(Collections.singleton(convert) , false);
			this.body.setSourceRange(elem);
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
