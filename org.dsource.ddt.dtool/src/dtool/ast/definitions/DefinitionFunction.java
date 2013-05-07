package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a function.
 */
public class DefinitionFunction extends AbstractFunctionDefinition implements IScopeNode, IDeclaration, IStatement {
	
	public final Reference retType;
	
	public DefinitionFunction(Reference retType, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
			ArrayView<IFunctionParameter> fnParams, ArrayView<FunctionAttributes> fnAttributes, 
			Expression tplConstraint, IFunctionBody fnBody) {
		super(defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
		this.retType = parentize(assertNotNull_(retType));
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
		cp.append(retType, " ");
		toStringAsCode_fromDefId(cp);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	public static final class AutoReturnReference extends Reference {
		
		public AutoReturnReference() {}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.REF_AUTO_RETURN;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("auto");
		}
		
		@Override
		public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
			return null;
		}
		
		@Override
		public boolean canMatch(DefUnitDescriptor defunit) {
			return false;
		}
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
	
}