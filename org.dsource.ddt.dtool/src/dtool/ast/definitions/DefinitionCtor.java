package dtool.ast.definitions;

import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DefinitionCtor extends ASTNeoNode {

	public List<IFunctionParameter> params;
	public int varargs;

	public IStatement fbody;

	
	public DefinitionCtor(CtorDeclaration elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params, convContext);
		this.fbody = Statement.convert(elem.fbody, convContext);
		varargs = DefinitionFunction.convertVarArgs(elem.varargs);
	}
	
	public DefinitionCtor(FuncDeclaration elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params, convContext);
		this.fbody = Statement.convert(elem.fbody, convContext);
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}

}
