package dtool.ast.definitions;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of an alias, in the new syntax:
 * <code>alias Identifier = Type [, Identifier = Type]* ;</code>
 * 
 * Not an actual {@link CommonDefinition} class, might change in future.
 * 
 * @see http://dlang.org/declaration.html#AliasDeclaration
 */
public class DefinitionAlias extends ASTNode implements IDeclaration, IStatement {
	
	public final ArrayView<DefinitionAliasFragment> aliasFragments;

	public DefinitionAlias(ArrayView<DefinitionAliasFragment> aliasFragments) {
		this.aliasFragments = parentize(aliasFragments);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ALIAS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, aliasFragments);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.appendList(aliasFragments, ", ", false);
		cp.append(";");
	}
	
	public static class DefinitionAliasFragment extends DefUnit {
		
		public final Reference target;
		
		public DefinitionAliasFragment(ProtoDefSymbol defId, Reference target) {
			super(defId);
			this.target = parentize(target);
		}
		
		@Override
		protected void checkNewParent() {
			assertTrue(parent instanceof DefinitionAlias);
		}
		
		public DefinitionAlias getDefinitionAliasParent() {
			return (DefinitionAlias) parent;
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_ALIAS_FRAGMENT;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
			acceptVisitor(visitor, target);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
			cp.append(" = ", target);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			return target.getTargetScope(moduleResolver);
		}
		
		@Override
		public String toStringForCodeCompletion() {
			return getName() + " -> " + target.toStringAsElement();
		}
		
	}
	
}