package dtool.ast.expressions;

import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.FuncExp;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.TypeFunction;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpLiteralFunc extends Expression {
	
	public Reference rettype;
	public IFunctionParameter[] params;
	public int varargs;

	public IStatement frequire;
	public IStatement fbody;
	public IStatement fensure;

	public ExpLiteralFunc(FuncExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		FuncLiteralDeclaration fd = elem.fd;
		
		this.frequire = Statement.convert(fd.frequire, convContext);
		this.fensure = Statement.convert(fd.fensure, convContext);
		this.fbody = Statement.convert(fd.fbody, convContext);
		
		TypeFunction elemTypeFunc = ((TypeFunction) fd.type);

		Assert.isTrue(fd.parameters == null);
		this.params = DescentASTConverter.convertMany(elemTypeFunc.parameters, IFunctionParameter.class, convContext); 

		varargs = DefinitionFunction.convertVarArgs(elemTypeFunc.varargs);
		this.rettype = ReferenceConverter.convertType(elemTypeFunc.next, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);	 
	}

}
