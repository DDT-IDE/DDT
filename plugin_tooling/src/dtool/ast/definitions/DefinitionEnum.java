/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static dtool.engine.analysis.DeeLanguageIntrinsics.D2_063_intrinsics;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

public class DefinitionEnum extends CommonDefinition 
	implements IDeclaration, IStatement, IConcreteNamedElement, ITypeNamedElement {
	
	public final Reference type;
	public final EnumBody body;
	
	public DefinitionEnum(Token[] comments, DefSymbol defName, Reference type, EnumBody body) {
		super(comments, defName);
		this.type = parentize(type);
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ENUM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, body);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionEnum(comments, clone(defName), clone(type), clone(body));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.append(defName, " ");
		cp.append(": ", type);
		cp.append(body);
	}
	
	public static class EnumBody extends ASTNode implements IScopeElement {
		
		public final NodeVector<EnumMember> nodeList;
		
		public EnumBody(NodeVector<EnumMember> nodeList) {
			this.nodeList = parentize(assertNotNull(nodeList));
		}
		
		@Override
		protected ASTNode getParent_Concrete() {
			assertTrue(parent instanceof DeclarationEnum || parent instanceof DefinitionEnum);
			return parent;
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, nodeList);
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new EnumBody(clone(nodeList));
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNodeList("{", nodeList, ", ", "}");
		}
		
		@Override
		public ScopeTraverser getScopeTraverser() {
			return new ScopeTraverser(nodeList, true);
		}
		
	}
	
	public static class NoEnumBody extends EnumBody {
		
		public static NodeVector<EnumMember> NULL_DECLS = new NodeVector<>(new EnumMember[0], false);
		
		public NoEnumBody() {
			super(NULL_DECLS);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
		}
		
		@Override
		protected CommonASTNode doCloneTree() {
			return new NoEnumBody();
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(";");
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Enum;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public TypeSemantics getSemantics(ISemanticContext parentContext) {
		return (TypeSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		MembersScopeElement membersScopeElement = new MembersScopeElement(body == null ? null : body.nodeList);
		return new TypeSemantics(this, pickedElement, membersScopeElement) {
		
			protected final NamedElementsScope commonTypeScope = createAggregateCommonTypeScope();
			
			protected NamedElementsScope createAggregateCommonTypeScope() {
				return new NamedElementsScope(D2_063_intrinsics.createCommonProperties(getTypeElement()));
			}
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				super.resolveSearchInMembersScope(search);
				search.evaluateScope(commonTypeScope);
			}
			
		};
	}
	
}