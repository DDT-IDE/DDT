package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

/**
 * Declaration of a template mixin with an associated identifier:
 * http://dlang.org/template-mixin.html#TemplateMixinDeclaration (with MixinIdentifier)
 */
public class DefinitionNamedMixin extends CommonDefinition implements IStatement {
	
	public final Reference templateInstance;
	
	public DefinitionNamedMixin(Token[] comments, ProtoDefSymbol defId, Reference templateInstance) {
		super(comments, defId);
		this.templateInstance = parentize(templateInstance);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.NAMED_MIXIN;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, templateInstance);
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("mixin ");
		cp.append(templateInstance, " ");
		cp.append(defname);
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return templateInstance.getTargetScope(moduleResolver);
	}
	
}