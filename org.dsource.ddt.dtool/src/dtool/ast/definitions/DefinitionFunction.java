package dtool.ast.definitions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.TypeFunction;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTPrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode, IStatement, ICallableElement {
	
	//public Identifier outId;
	public descent.internal.compiler.parser.LINK linkage;
	public Reference rettype;
	public TemplateParameter[] templateParams;	
	public List<IFunctionParameter> params;
	public int varargs;
	
	public IStatement frequire;
	public IStatement fbody;
	public IStatement fensure;
	
	//public descent.internal.compiler.parser.TypeFunction type;
	
	
	public DefinitionFunction(FuncDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		this.frequire = Statement.convert(elem.frequire, convContext);
		this.fensure = Statement.convert(elem.fensure, convContext);
		this.fbody = Statement.convert(elem.fbody, convContext);
		
		TypeFunction elemTypeFunc = ((TypeFunction) elem.type);
		
		/*if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);*/
		Assert.isTrue(elem.parameters == null);
		this.params = DescentASTConverter.convertManyL(elemTypeFunc.parameters, this.params, convContext); 
		
		varargs = convertVarArgs(elemTypeFunc.varargs);
		if(elemTypeFunc.next == null) {
			this.rettype = new AutoFunctionReturnReference();
		} else {
			this.rettype = ReferenceConverter.convertType(elemTypeFunc.next, convContext);
		}
		Assert.isNotNull(this.rettype);
	}
	
	public static ASTNeoNode convertFunctionParameter(Argument elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			if(elem.type != null) {
				return new FunctionParameter(elem, convContext);
			} else {
				// strange case, likely from a syntax error
				return DefinitionConverter.convertNamelessParameter(elem, elem.ident, convContext);
			}
		} else {
			return DefinitionConverter.convertNamelessParameter(elem, convContext);
		}
	}
	
	public static int convertVarArgs(int varargs) {
		Assert.isTrue(varargs >= 0 && varargs <= 2);
		return varargs;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fbody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	@Override
	public IFunctionParameter[] getParameters() {
		return ArrayUtil.createFrom(params, IFunctionParameter.class);
	}
	
	@Override
	public IScopeNode getMembersScope() {
		// FIXME
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		// TODO: function super
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public Iterator<IFunctionParameter> getMembersIterator() {
		return params.iterator();
	}
	
	
	public static String toStringParametersForSignature(
			List<IFunctionParameter> params, int varargs) {
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringAsFunctionSignaturePart();
		}
		if(varargs == 1) strParams += (params.size()==0 ? "..." : ", ...");
		if(varargs == 2) strParams += "...";
		return strParams + ")";
	}
	
	@Override
	public String toStringAsElement() {
		return getName() + toStringParametersForSignature(params, varargs);
	}
	
	
	@Override
	public String toStringForHoverSignature() {
		String str = ""
			+ rettype.toStringAsElement() + " " + getName() 
			+ ASTPrinter.toStringParamListAsElements(templateParams)
			+ toStringParametersForSignature(params, varargs);
		return str;
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName()
			+ ASTPrinter.toStringParamListAsElements(templateParams)
			+ toStringParametersForSignature(params, varargs) 
			+ "  " + rettype.toStringAsElement()
			+ " - " + NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}
	
	public static final class AutoFunctionReturnReference extends Reference {
		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
			}
			visitor.endVisit(this);					}
		
		@Override
		public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
			return null;
		}
		
		@Override
		public String toStringAsElement() {
			return "auto";
		}
		
		@Override
		public boolean canMatch(DefUnit defunit) {
			return false;
		}
	}
	
}
