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

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

/**
 * Declaration of a template mixin with an associated identifier:
 * http://dlang.org/template-mixin.html#TemplateMixinDeclaration (with MixinIdentifier)
 */
public class DefinitionMixinInstance extends CommonDefinition implements IStatement, IConcreteNamedElement {
	
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
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			resolveSearchInReferredContainer(search, templateInstance);
		}
		
	};
	
}