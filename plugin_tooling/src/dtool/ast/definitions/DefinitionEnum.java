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

import static dtool.resolver.LanguageIntrinsics.D2_063_intrinsics;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.intrinsics.InstrinsicsScope;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;

public class DefinitionEnum extends CommonDefinition implements IDeclaration, IStatement {
	
	public final Reference type;
	public final EnumBody body;
	
	public DefinitionEnum(Token[] comments, ProtoDefSymbol defId, Reference type, EnumBody body) {
		super(comments, defId);
		this.type = parentize(type);
		this.body = parentize(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_ENUM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("enum ");
		cp.append(defname, " ");
		cp.append(": ", type);
		cp.append(body);
	}
	
	public static class EnumBody extends ASTNode implements IScopeNode {
		
		public final NodeListView<EnumMember> nodeList;
		
		public EnumBody(NodeListView<EnumMember> nodeList) {
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
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNodeList("{", nodeList, ", ", "}");
		}
		
		@Override
		public void resolveSearchInScope(CommonDefUnitSearch search) {
			ReferenceResolver.findInNodeList(search, nodeList, false);
		}
		
	}
	
	public static class NoEnumBody extends EnumBody {
		
		public static NodeListView<EnumMember> NULL_DECLS = new NodeListView<>(new EnumMember[0], false);
		
		public NoEnumBody() {
			super(NULL_DECLS);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ENUM_BODY;
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
	
	protected final InstrinsicsScope commonTypeScope = createAggregateCommonTypeScope();
	protected InstrinsicsScope createAggregateCommonTypeScope() {
		return new InstrinsicsScope(D2_063_intrinsics.createCommonProperties(this));
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		if(body != null) {
			ReferenceResolver.findInNodeList(search, body.nodeList, false);
		}
		commonTypeScope.resolveSearchInScope(search);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.returnError_ElementIsNotAValue(this);
	}
	
}