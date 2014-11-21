/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.VarSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Resolvable;

public class VarElement extends InstantiatedDefUnit implements IConcreteNamedElement {
	
	public final Resolvable type;
	
	public VarElement(DefSymbol defname, Resolvable type) {
		super(defname);
		this.type = type; /*FIXME: BUG here NPE/parenting*/
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM__INSTANCE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append(" : ", type);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	protected final VarSemantics nodeSemantics = new VarSemantics(this) {
		
		@Override
		protected Resolvable getTypeReference() {
			return type;
		};
	};
	
	@Override
	public INamedElementSemantics getSemantics() {
		return nodeSemantics;
	}
	
}