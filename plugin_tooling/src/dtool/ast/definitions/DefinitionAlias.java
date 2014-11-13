package dtool.ast.definitions;


import static dtool.util.NewUtils.assertCast;

import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.INonScopedContainer;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;

/**
 * A definition of an alias, in the new syntax:
 * <code>alias Identifier = Type [, Identifier = Type]* ;</code>
 * 
 * Not an actual {@link CommonDefinition} class, might change in future.
 * 
 * @see http://dlang.org/declaration.html#AliasDeclaration
 */
public class DefinitionAlias extends ASTNode implements IDeclaration, IStatement, INonScopedContainer {
	
	public final Token[] comments;
	public final ArrayView<DefinitionAliasFragment> aliasFragments;
	
	public DefinitionAlias(Token[] comments, ArrayView<DefinitionAliasFragment> aliasFragments) {
		this.comments = comments;
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
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return IteratorUtil.nonNullIterator(aliasFragments);
	}
	
	public Token[] getDefinitionContainerDocComments() {
		return comments;
	}
	
	public static class DefinitionAliasFragment extends DefUnit {
		
		public final ArrayView<TemplateParameter> tplParams; // Since 2.064
		public final Reference target;
		
		public DefinitionAliasFragment(ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams, 
				Reference target) {
			super(defId);
			this.tplParams = parentize(tplParams);
			this.target = parentize(target);
		}
		
		@Override
		public DefinitionAlias getParent_Concrete() {
			return assertCast(getParent(), DefinitionAlias.class);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_ALIAS_FRAGMENT;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
			acceptVisitor(visitor, tplParams);
			acceptVisitor(visitor, target);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
			cp.appendList("(", tplParams, ",", ") ");
			cp.append(" = ", target);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		@Override
		public Token[] getDocComments() {
			return getParent_Concrete().getDefinitionContainerDocComments();
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			resolveSearchInReferredContainer(search, target);
		}
		
		@Override
		public ILangNamedElement resolveTypeForValueContext(IModuleResolver mr) {
			return DefElementCommon.resolveTypeForValueContext_Alias(mr, target);
		}
		
	}
	
}