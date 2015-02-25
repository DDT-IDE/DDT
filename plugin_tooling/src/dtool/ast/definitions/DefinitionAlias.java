/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;


import static dtool.util.NewUtils.assertCast;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics.RefBasedAliasSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

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
	public final NodeVector<DefinitionAliasFragment> aliasFragments;
	
	public DefinitionAlias(Token[] comments, NodeVector<DefinitionAliasFragment> aliasFragments) {
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
	protected CommonASTNode doCloneTree() {
		return new DefinitionAlias(comments, clone(aliasFragments));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.appendList(aliasFragments, ", ", false);
		cp.append(";");
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return IteratorUtil.nonNullIterable(aliasFragments);
	}
	
	public Token[] getDefinitionContainerDocComments() {
		return comments;
	}
	
	public static class DefinitionAliasFragment extends DefUnit {
		
		public final NodeVector<ITemplateParameter> tplParams; // Since 2.064
		public final Reference target;
		
		public DefinitionAliasFragment(DefSymbol defName, NodeVector<ITemplateParameter> tplParams, Reference target) {
			super(defName);
			this.tplParams = parentize(tplParams);
			this.target = parentize(target);
		}
		
		@Override
		public DefinitionAlias getParent_Concrete() {
			return assertCast(getLexicalParent(), DefinitionAlias.class);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_ALIAS_FRAGMENT;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defName);
			acceptVisitor(visitor, tplParams);
			acceptVisitor(visitor, target);
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new DefinitionAliasFragment(clone(defName), clone(tplParams), clone(target));
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defName);
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
		
		/* -----------------  ----------------- */
		
		
		@Override
		protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new RefBasedAliasSemantics(this, pickedElement) {
				@Override
				protected Reference getAliasTarget() {
					return target;
				}
			};
		}
		
	}
	
}