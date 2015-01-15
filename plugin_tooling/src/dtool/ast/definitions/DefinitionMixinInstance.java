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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

/**
 * Declaration of a template mixin with an associated identifier:
 * http://dlang.org/template-mixin.html#TemplateMixinDeclaration (with MixinIdentifier)
 */
public class DefinitionMixinInstance extends CommonDefinition implements IStatement, IConcreteNamedElement {
	
	public final Reference templateInstance;
	
	public DefinitionMixinInstance(Token[] comments, DefSymbol defName, Reference templateInstance) {
		super(comments, defName);
		this.templateInstance = parentize(templateInstance);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_MIXIN_INSTANCE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, templateInstance);
		acceptVisitor(visitor, defName);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionMixinInstance(comments, clone(defName), clone(templateInstance));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("mixin ");
		cp.append(templateInstance, " ");
		cp.append(defName);
		cp.append(";");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new DefMixinSemanticsExtension(this, pickedElement);
	}
	
	public class DefMixinSemanticsExtension extends NonValueConcreteElementSemantics {
		protected DefMixinSemanticsExtension(IConcreteNamedElement concreteElement,
				PickedElement<?> pickedElement) {
			super(concreteElement, pickedElement);
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			if(templateInstance != null) {
				// TODO: add fake element for missing syntax
				
				INamedElement result = templateInstance.getSemantics(context).resolveTargetElement().result;
				search.evaluateInMembersScope(result);
			}
		}
	}
	
}