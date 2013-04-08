package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.ISourceRepresentation;
import dtool.ast.NodeUtil;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.parser.DeeTokens;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a function.
 * TODO: special funcs
 */
public class DefinitionFunction extends Definition implements IScopeNode, IStatement, ICallableElement {
	
	public final Reference retType;
	public final ArrayView<TemplateParameter> tplParams;
	public final ArrayView<IFunctionParameter> params;
	public final ArrayView<FunctionAttributes> fnAttributes;
	public final Expression tplConstraint;
	public final IFunctionBody fnBody;
	
	public DefinitionFunction(DefUnitTuple defunitData, ArrayView<TemplateParameter> tplParams, Reference retType,
			ArrayView<IFunctionParameter> params, ArrayView<FunctionAttributes> fnAttributes, 
			Expression tplConstraint, IFunctionBody fnBody) {
		super(defunitData);
		assertNotNull(retType);
		
		this.retType = parentize(retType);
		
		this.tplParams = parentize(tplParams);
		this.params = parentizeI(params);
		this.fnAttributes = fnAttributes;
		this.tplConstraint = parentize(tplConstraint);
		this.fnBody = parentizeI(fnBody);
	}
	
	public ArrayView<ASTNeoNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
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
			TreeVisitor.acceptChildren(visitor, tplParams);
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, tplConstraint);
			TreeVisitor.acceptChildren(visitor, fnBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(retType, " ");
		cp.appendNode(defname);
		cp.appendNodeList("(", tplParams, ",", ") ");
		cp.appendNodeList("(", getParams_asNodes(), ",", ") ");
		cp.appendList(fnAttributes, " ", true);
		if(tplConstraint instanceof MissingParenthesesExpression) {
			cp.appendNode("if", tplConstraint);
		} else {
			cp.appendNode("if(", tplConstraint, ")");
		}
		cp.appendNode(fnBody);
	}
	
	public static enum FunctionAttributes implements ISourceRepresentation {
		CONST(DeeTokens.KW_CONST.getSourceValue()), 
		IMMUTABLE(DeeTokens.KW_IMMUTABLE.getSourceValue()), 
		INOUT(DeeTokens.KW_INOUT.getSourceValue()), 
		SHARED(DeeTokens.KW_SHARED.getSourceValue()),
		
		PURE(DeeTokens.KW_PURE.getSourceValue()),
		NOTHROW(DeeTokens.KW_NOTHROW.getSourceValue()),
		
		AT_PROPERTY("@property"),
		AT_SAFE("@safe"),
		AT_TRUSTED("@trusted"),
		AT_SYSTEM("@system"),
		AT_DISABLE("@disable"),
		;
		public final String sourceValue;
		
		private FunctionAttributes(String sourceValue) {
			this.sourceValue = sourceValue;
		}
		
		@Override
		public String getSourceValue() {
			return sourceValue;
		}
		
		public static FunctionAttributes fromToken(DeeTokens token) {
			switch (token) {
			case KW_CONST: return CONST;
			case KW_IMMUTABLE: return IMMUTABLE;
			case KW_INOUT: return INOUT;
			case KW_SHARED: return SHARED;
			case KW_PURE: return PURE;
			case KW_NOTHROW: return NOTHROW;
			
			default: return null;
			}
		}
		
		//This could be slightly optimized with a hash table
		public static FunctionAttributes fromCustomAttribId(String customAttribName) {
			if(customAttribName.equals("property")) return AT_PROPERTY;
			if(customAttribName.equals("safe")) return AT_SAFE;
			if(customAttribName.equals("trusted")) return AT_TRUSTED;
			if(customAttribName.equals("system")) return AT_SYSTEM;
			if(customAttribName.equals("disable")) return AT_DISABLE;
			return null;
		}
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
			+ ASTCodePrinter.toStringParamListAsElements(tplParams)
			+ toStringParametersForSignature(params);
		return str;
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName()
			+ ASTCodePrinter.toStringParamListAsElements(tplParams)
			+ toStringParametersForSignature(params) 
			+ "  " + retType.toStringAsElement()
			+ " - " + NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}
	
	// TODO
	public static final class AutoFunctionReturnReference extends Reference {
		@Override
		public void accept0(IASTVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
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