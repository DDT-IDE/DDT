package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;

/**
 * Declaration of a template mixin with an associated identifier:
 * http://dlang.org/template-mixin.html#TemplateMixinDeclaration (with MixinIdentifier)
 */
public class DefinitionMixinInstance extends CommonDefinition implements IStatement {
	
	public final Reference templateInstance;
	
	public DefinitionMixinInstance(Token[] comments, ProtoDefSymbol defId, Reference templateInstance) {
		super(comments, defId);
		this.templateInstance = parentize(templateInstance);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_MIXIN_INSTANCE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, templateInstance);
		acceptVisitor(visitor, defname);
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
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		Reference.resolveSearchInReferedMembersScope(search, templateInstance);
	}
	
}