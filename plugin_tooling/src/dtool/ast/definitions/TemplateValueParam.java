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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.VarSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.AliasElement;

public class TemplateValueParam extends DefUnit implements IConcreteNamedElement, ITemplateParameter {
	
	public final Reference type;
	public final Expression specializationValue;
	public final Expression defaultValue;
	
	public TemplateValueParam(Reference type, DefSymbol defName, Expression specializationValue, 
		Expression defaultValue) {
		super(defName);
		this.type = parentize(assertNotNull(type));
		this.specializationValue = parentize(specializationValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_VALUE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, specializationValue);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TemplateValueParam(clone(type), clone(defName), clone(specializationValue), clone(defaultValue));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defName);
		cp.append(" : ", specializationValue);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new VarSemantics(this, pickedElement) {
			
			@Override
			protected Reference getTypeReference() {
				return type;
			}
			
		};
	}
	
	@Override
	public AliasElement createTemplateArgument(Resolvable argument) {
		return new AliasElement(defName, null); // TODO: correct instantiation
	}
	
}