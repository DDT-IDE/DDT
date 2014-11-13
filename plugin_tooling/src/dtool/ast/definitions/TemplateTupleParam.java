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

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;

public class TemplateTupleParam extends TemplateParameter {
	
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
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		// TODO return intrinsic universal
		return;
	}
	
	@Override
	public IDeeNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.returnError_ElementIsNotAValue(this);
	}
	
}