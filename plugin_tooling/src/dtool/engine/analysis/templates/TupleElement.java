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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Resolvable;

public class TupleElement extends InstantiatedDefUnit implements IConcreteNamedElement {
	
	public final NodeVector<Resolvable> values; // non-children element
	
	public TupleElement(DefSymbol defname, NodeVector<Resolvable> values) {
		super(defname);
		this.values = assertNotNull(checkAllCompleted(values));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM__INSTANCE;
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TupleElement(clone(defName), values);
	}
	
	@Override
	public void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp) {
		cp.append("@ ", defName, "... = ");
		cp.appendList("(", values, ",", ")");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}
	
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NonValueConcreteElementSemantics(this, pickedElement) {
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				// Do nothing;
			}
		};
	}
	
}