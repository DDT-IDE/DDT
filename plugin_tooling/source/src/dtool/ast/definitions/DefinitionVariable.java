/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.engine.analysis.CommonDefVarSemantics;
import dtool.engine.analysis.IVarDefinitionLike;
import dtool.parser.common.Token;

/**
 * A variable definition. 
 * Optionally has multiple variables defined with the multi-identifier syntax.
 */
public class DefinitionVariable extends CommonDefinition 
	implements IDeclaration, IStatement, INonScopedContainer, IVarDefinitionLike { 
	
	public static final ArrayView<DefVarFragment> NO_FRAGMENTS = ArrayView.create(new DefVarFragment[0]);
	
	public final Reference type; // Can be null
	public final NodeVector<ITemplateParameter> tplParams;
	public final Reference cstyleSuffix;
	public final IInitializer initializer;
	protected final NodeVector<DefVarFragment> fragments;
	
	public DefinitionVariable(Token[] comments, DefSymbol defName, Reference type, 
			NodeVector<ITemplateParameter> tplParams, Reference cstyleSuffix,
		IInitializer initializer, NodeVector<DefVarFragment> fragments)
	{
		super(comments, defName);
		this.type = parentize(type);
		this.tplParams = parentize(tplParams);
		this.cstyleSuffix = parentize(cstyleSuffix);
		this.initializer = parentize(initializer);
		this.fragments = parentize(fragments);
		assertTrue(fragments == null || fragments.size() > 0);
		assertTrue(cstyleSuffix == null || tplParams == null);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VARIABLE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, cstyleSuffix);
		acceptVisitor(visitor, initializer);
		
		acceptVisitor(visitor, fragments);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionVariable(comments, clone(defName), clone(type), clone(tplParams), clone(cstyleSuffix), 
			clone(initializer), clone(fragments));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defName);
		cp.appendList("(", tplParams, ",", ") ");
		cp.append(cstyleSuffix);
		cp.append(" = ", initializer);
		cp.appendList(", ", fragments, ", ", "");
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public ArrayView<DefVarFragment> getFragments() {
		return fragments == null ? NO_FRAGMENTS : fragments;
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return getFragments();
	}
	
	@Override
	public Reference getDeclaredType() {
		return type;
	}
	
	@Override
	public IInitializer getDeclaredInitializer() {
		return initializer;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected CommonDefVarSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new CommonDefVarSemantics(this, pickedElement);
	}
	
	/* -----------------  ----------------- */
	
	public static class DefinitionAutoVariable extends DefinitionVariable {
		
		public DefinitionAutoVariable(Token[] comments, DefSymbol defName, NodeVector<ITemplateParameter> tplParams, 
				IInitializer initializer, NodeVector<DefVarFragment> fragments) {
			super(comments, defName, null, tplParams, null, initializer, fragments);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_AUTO_VARIABLE;
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new DefinitionAutoVariable(comments, clone(defName), clone(tplParams), clone(initializer), 
				clone(fragments));
		}
		
	}
	
}