package dtool.ast.definitions;


import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of an alias, in the old syntax:
 * <code>alias BasicType Declarator ;</code>
 * 
 * @see http://dlang.org/declaration.html#AliasDeclaration
 */
public class DefinitionAliasDecl extends Definition implements IDeclaration, IStatement {
	
	public final Reference target;
	public final ArrayView<IFunctionParameter> fnParams;
	public final ArrayView<FunctionAttributes> fnAttributes;
	
	public DefinitionAliasDecl(Reference target, ProtoDefSymbol defId, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes) {
		super(defId);
		this.target = parentize(target);
		this.fnParams = parentizeI(fnParams);
		this.fnAttributes = fnAttributes;
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ALIAS_DECL;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, target);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, fnParams);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.append(target, " ");
		cp.append(defname);
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return target.getTargetScope(moduleResolver); // XXX: Not correct for functional variant of alias
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " -> " + target.toStringAsElement();
	}
	
}