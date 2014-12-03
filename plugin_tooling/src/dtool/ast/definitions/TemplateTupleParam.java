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
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.engine.analysis.templates.AliasElement;

public class TemplateTupleParam extends TemplateParameter implements IConcreteNamedElement {
	
	public TemplateTupleParam(ProtoDefSymbol defId) {
		super(defId);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TUPLE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append("...");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Tuple;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics createSemantics(ISemanticContext context) {
		return new TypeSemantics(this, context) {
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				// TODO return intrinsic universal
				return;
			}
			
		};
	}
	
	@Override
	public AliasElement createTemplateArgument(Resolvable argument) {
		return new AliasElement(defname, null);  // TODO: correct instantiation
	}
	
}