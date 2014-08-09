package dtool.ast.definitions;


import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.Attribute;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.INonScopedContainer;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.util.ArrayView;

/**
 * A definition of an alias, in the old syntax:
 * <code>alias BasicType Declarator ;</code>
 * when declarator declares one or more variable-like aliases.
 * 
 * @see http://dlang.org/declaration.html#AliasDeclaration
 */
// Note implementation similarities with {@link DefinitionVariable} and {@link DefVarFragment}
public class DefinitionAliasVarDecl extends CommonDefinition implements IDeclaration, IStatement, INonScopedContainer {
	
	public final ArrayView<Attribute> aliasedAttributes;
	public final Reference target;
	public final Reference cstyleSuffix;
	public final ArrayView<AliasVarDeclFragment> fragments;
	
	public DefinitionAliasVarDecl(Token[] comments, ArrayView<Attribute> aliasedAttributes, Reference target,
		ProtoDefSymbol defId, Reference cstyleSuffix, ArrayView<AliasVarDeclFragment> fragments) {
		super(comments, defId);
		this.aliasedAttributes = parentize(aliasedAttributes);
		this.target = parentize(target);
		this.cstyleSuffix = parentizeI(cstyleSuffix);
		this.fragments = parentizeI(fragments);
		assertTrue(fragments == null || fragments.size() > 0);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ALIAS_VAR_DECL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, aliasedAttributes);
		acceptVisitor(visitor, target);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, cstyleSuffix);
		acceptVisitor(visitor, fragments);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.appendList(aliasedAttributes, " ", true);
		cp.append(target, " ");
		cp.append(defname);
		cp.append(cstyleSuffix);
		cp.appendList(", ", fragments, ", ", "");
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, target);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.resolveTypeForValueContext_AliasBUG(mr, target);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return IteratorUtil.nonNullIterator(fragments);
	}
	
	public static class AliasVarDeclFragment extends DefUnit {
		
		public AliasVarDeclFragment(ProtoDefSymbol defId) {
			super(defId);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ALIAS_VAR_DECL_FRAGMENT;
		}
		
		@Override
		public DefinitionAliasVarDecl getParent_Concrete() {
			return assertCast(parent, DefinitionAliasVarDecl.class);
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		protected Reference getAliasTarget() {
			return getParent_Concrete().target;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			resolveSearchInReferredContainer(search, getAliasTarget());
		}
		
		@Override
		public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
			return DefElementCommon.resolveTypeForValueContext_AliasBUG(mr, getAliasTarget());
		}
		
	}
	
}