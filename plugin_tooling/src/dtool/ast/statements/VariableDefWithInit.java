/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.IVarDefinitionLike;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.operations.CommonDefVarSemantics;
import dtool.resolver.CommonDefUnitSearch;

public class VariableDefWithInit extends DefUnit implements IVarDefinitionLike {
	
	public final Reference type;
	public final Expression defaultValue;
	
	public VariableDefWithInit(Reference type, ProtoDefSymbol defId, Expression defaultValue) {
		super(defId);
		this.type = parentize(assertNotNull(type));
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.VARIABLE_DEF_WITH_INIT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, type);
	}
	
	@Override
	public ILangNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.resolveTypeForValueContext(mr, type);
	}
	
	@Override
	public Reference getDeclaredType() {
		return type;
	}
	
	@Override
	public IInitializer getDeclaredInitializer() {
		return defaultValue;
	}
	
	protected final CommonDefVarSemantics nodeSemantics = new CommonDefVarSemantics(this) { };
	
	@Override
	public CommonDefVarSemantics getNodeSemantics() {
		return nodeSemantics;
	}
	
}