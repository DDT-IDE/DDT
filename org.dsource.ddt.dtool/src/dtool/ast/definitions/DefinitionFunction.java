package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;
import dtool.ast.statements.BodyStatement;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode, IStatement, ICallableElement {
	
	//public Identifier outId;
	public final Reference retType;
	public final ArrayView<TemplateParameter> templateParams;
	public final ArrayView<IFunctionParameter> params;
	public final ArrayView<ASTNeoNode> params_asNodes;
	
	public final IStatement frequire;
	public final BodyStatement fnBody;
	public final IStatement fensure;
	
	//public descent.internal.compiler.parser.TypeFunction type;
	
	public DefinitionFunction(DefUnitTuple defunitData, PROT prot, Reference retType,
			ArrayView<IFunctionParameter> params, IStatement frequire, IStatement fensure, 
			BodyStatement fbody, SourceRange sourceRange) {
		super(defunitData, prot);
		assertNotNull(retType);
		
		this.retType = parentize(retType);
		this.templateParams = null; // TODO BUG here
		this.params = parentizeI(params);
		this.params_asNodes = CoreUtil.blindCast(params);
		this.frequire = parentizeI(frequire);
		this.fensure = parentizeI(fensure);
		this.fnBody = parentizeI(fbody);
		
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_FUNCTION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, retType);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, params);
			//TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, frequire);
			TreeVisitor.acceptChildren(visitor, fnBody);
			TreeVisitor.acceptChildren(visitor, fensure);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(retType, " ");
		cp.append(defname);
		cp.appendArgList("(", params_asNodes, ",", ")");
		cp.appendNode(fnBody);
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		// FIXME
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		// TODO: function super
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	@Override
	public Iterator<IFunctionParameter> getMembersIterator(IModuleResolver moduleResolver) {
		return params.iterator();
	}
	
	
	public static String toStringParametersForSignature(ArrayView<IFunctionParameter> params) {
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringAsFunctionSignaturePart();
		}
		return strParams + ")";
	}
	
	@Override
	public String toStringAsElement() {
		return getName() + toStringParametersForSignature(params);
	}
	
	
	@Override
	public String toStringForHoverSignature() {
		String str = ""
			+ retType.toStringAsElement() + " " + getName() 
			+ ASTCodePrinter.toStringParamListAsElements(templateParams)
			+ toStringParametersForSignature(params);
		return str;
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName()
			+ ASTCodePrinter.toStringParamListAsElements(templateParams)
			+ toStringParametersForSignature(params) 
			+ "  " + retType.toStringAsElement()
			+ " - " + NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}
	
	public static final class AutoFunctionReturnReference extends Reference {
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
			}
			visitor.endVisit(this);					}
		
		@Override
		public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
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