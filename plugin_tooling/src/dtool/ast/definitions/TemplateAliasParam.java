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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.analysis.templates.AliasElement;

public class TemplateAliasParam extends DefUnit implements ITemplateParameter {
	
	public final Resolvable specializationValue;
	public final Resolvable defaultValue;
	
	public TemplateAliasParam(DefSymbol defName, Resolvable specializationValue, Resolvable defaultValue){
		super(defName);
		this.specializationValue = parentize(specializationValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_ALIAS_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, specializationValue);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TemplateAliasParam(clone(defName), clone(specializationValue), clone(defaultValue));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.append(defName);
		cp.append(" : ", specializationValue);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new TemplateAliasParamSemantics(this, pickedElement);
	}
	
	public static class TemplateAliasParamSemantics extends NamedElementSemantics {
		
		protected final ErrorElement error;
		
		public TemplateAliasParamSemantics(INamedElement element, PickedElement<?> pickedElement) {
			super(element, pickedElement);
			this.error = new NotInstantiatedErrorElement(element, null);
		}
		
		@Override
		protected IConcreteNamedElement doResolveConcreteElement() {
			return error;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			 // Do nothing
		}
		
		@Override
		public INamedElement resolveTypeForValueContext() {
			return error;
		}
	}
	
	@Override
	public AliasElement createTemplateArgument(Resolvable argument, ISemanticContext tplRefContext) {
		return new AliasElement(defName, argument);
	}
	
}