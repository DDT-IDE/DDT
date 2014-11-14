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

import static melnorme.utilbox.misc.IteratorUtil.nonNullIterable;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.intrinsics.InstrinsicsScope;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.LanguageIntrinsics;
import dtool.resolver.ReferenceResolver;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {
	
	public final ArrayView<Reference> baseClasses;
	public final boolean baseClassesAfterConstraint;
	
	public DefinitionClass(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, ArrayView<Reference> baseClasses, boolean baseClassesAfterConstraint, 
		IAggregateBody aggrBody) 
	{
		super(comments, defId, tplParams, tplConstraint, aggrBody);
		this.baseClasses = parentize(baseClasses);
		this.baseClassesAfterConstraint = baseClassesAfterConstraint;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CLASS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "class ");
	}
	
	public void classLikeToStringAsCode(ASTCodePrinter cp, String keyword) {
		cp.append(keyword);
		cp.append(defname, " ");
		cp.appendList("(", tplParams, ",", ") ");
		if(baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendList(": ", baseClasses, ",", " ");
		if(!baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append(aggrBody, "\n");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		if(baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, baseClasses);
		if(!baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	@Override
	protected InstrinsicsScope createAggregateCommonTypeScope() {
		return LanguageIntrinsics.D2_063_intrinsics.createObjectPropertiesScope(this);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInHierarchyScope(search);
		commonTypeScope.resolveSearchInScope(search);
	}
	
	public void resolveSearchInHierarchyScope(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInScope(search, getBodyScope());
		if(getBodyScope() == null) {
			// Even without a body scope, we can resolve in super scopes
			resolveSearchInSuperScopes(search);
		}
	}
	
	public void resolveSearchInSuperScopes(CommonDefUnitSearch search) {
		IModuleResolver mr = search.getModuleResolver();
		
		for(Reference baseclass : CoreUtil.nullToEmpty(baseClasses)) {
			INamedElement baseClassElem = baseclass.findTargetDefElement(mr);
			if(baseClassElem == null)
				continue;
			
			if(baseClassElem instanceof DefinitionClass) {
				DefinitionClass baseClassDef = (DefinitionClass) baseClassElem;
				baseClassDef.resolveSearchInHierarchyScope(search);
			}
		}
	}
	
	public INamedElement resolveSuperClass(IModuleResolver mr) {
		
		for (Reference baseClassRef : nonNullIterable(baseClasses)) {
			INamedElement baseClass = baseClassRef.findTargetDefElement(mr);
			
			if(baseClass.getArcheType() == EArcheType.Interface) {
				continue;
			}
			if(baseClass instanceof DefinitionClass) {
				return baseClass;
			}
		}
		// TODO test implicit object reference
		return LanguageIntrinsics.OBJECT_CLASS_REF.findTargetDefElement(mr);
	}
	
}