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
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.ErrorElement.NotAValueErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.EArcheType;
import dtool.engine.util.NamedElementUtil;

public class TypeAliasElement extends InstantiatedDefUnit {
	
	public final ITypeNamedElement target; // non-children member
	
	public TypeAliasElement(DefSymbol defName, ITypeNamedElement concreteTarget) {
		super(defName);
		this.target = assertNotNull(checkIsSemanticReady(concreteTarget));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM__INSTANCE;
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TypeAliasElement(clone(defName), target);
	}
	
	@Override
	public void toStringAsCode_instantiatedDefUnit(ASTCodePrinter cp) {
		cp.append("@", defName);
		cp.append(" = ");
		cp.append(NamedElementUtil.getElementTypedLabel(target, true));
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public AliasSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new AliasSemantics(this, pickedElement) {
			
			protected final NotAValueErrorElement notAValueError = new NotAValueErrorElement(TypeAliasElement.this);
			
			@Override
			public INamedElement getTypeForValueContext_do() {
				return notAValueError;
			};
			
			@Override
			protected IConcreteNamedElement resolveAliasTarget_nonNull() {
				return target;
			}
			
		};
	}
	
}