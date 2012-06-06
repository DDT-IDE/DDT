package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTPrinter;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode, IStatement, ICallableElement {
	
	//public Identifier outId;
	public descent.internal.compiler.parser.LINK linkage; // ???
	public final Reference rettype;
	public TemplateParameter[] templateParams; // ???
	public final ArrayView<IFunctionParameter> params;
	public final int varargs;
	
	public final IStatement frequire;
	public final IStatement fbody;
	public final IStatement fensure;
	
	//public descent.internal.compiler.parser.TypeFunction type;
	
	public DefinitionFunction(DefUnitDataTuple defunitData, PROT prot, Reference rettype, IFunctionParameter[] params, int varargs, IStatement frequire, IStatement fensure,
			IStatement fbody) {
		super(defunitData, prot);
		assertNotNull(rettype);
		
		this.rettype = rettype; parentize(this.rettype);
		this.params = ArrayView.create(params); parentizeI(this.params);
		this.varargs = varargs;
		this.frequire = frequire; parentizeI(this.frequire);
		this.fensure = fensure; parentizeI(this.fensure);
		this.fbody = fbody; parentizeI(fbody);
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
	public ArrayView<IFunctionParameter> getParameters() {
		return params;
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
	
	
	public static String toStringParametersForSignature(ArrayView<IFunctionParameter> params, int varargs) {
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
		public boolean canMatch(DefUnitDescriptor defunit) {
			return false;
		}
	}
	
}
