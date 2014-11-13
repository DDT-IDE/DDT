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
package dtool.ast.references;

import static dtool.util.NewUtils.assertInstance;
import static dtool.util.NewUtils.exactlyOneIsNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.expressions.Resolvable;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.engine.ISemanticResolution;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.common.ResolutionResult;
import dtool.engine.modules.IModuleResolver;

public class RefTemplateInstance extends Reference implements IQualifierNode, ITemplateRefNode {
	
	public final Reference tplRef;
	public final Resolvable tplSingleArg;
	public final NodeListView<Resolvable> tplArgs;
	
	public RefTemplateInstance(ITemplateRefNode tplRef, Resolvable tplSingleArg, NodeListView<Resolvable> tplArgs) {
		this.tplRef = parentizeI(assertInstance(tplRef, Reference.class));
		assertTrue(exactlyOneIsNull(tplSingleArg, tplArgs));
		this.tplSingleArg = parentize(tplSingleArg);
		this.tplArgs = parentize(tplArgs);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TEMPLATE_INSTANCE;
	}
	
	public boolean isSingleArgSyntax() {
		return tplSingleArg != null;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, tplRef);
		acceptVisitor(visitor, tplSingleArg);
		acceptVisitor(visitor, tplArgs);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(tplRef, "!");
		if(isSingleArgSyntax()) {
			cp.append(tplSingleArg);
		} else {
			cp.appendNodeList("(", tplArgs, ", ", ")");
		}
	}
	
	@Override
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findOneOnly) {
		// Not accurate, this will ignore the template parameters:
		return tplRef.findTargetDefElements(moduleResolver, findOneOnly);
	}
	
	@Override
	public ResolutionResult resolveTargetElement(ISemanticResolution sr) {
		return super.resolveTargetElement(sr);
	}
	
}