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
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.engine.analysis.DeeLanguageIntrinsics;
import dtool.parser.common.Token;

/**
 * A definition of a class aggregate.
 */
public abstract class DefinitionClass_Common extends DefinitionAggregate {
	
	public final NodeVector<Reference> baseClasses;
	public final boolean baseClassesAfterConstraint;
	
	public DefinitionClass_Common(Token[] comments, DefSymbol defId, NodeVector<ITemplateParameter> tplParams,
		Expression tplConstraint, NodeVector<Reference> baseClasses, boolean baseClassesAfterConstraint, 
		IAggregateBody aggrBody) 
	{
		super(comments, defId, tplParams, tplConstraint, aggrBody);
		this.baseClasses = parentize(baseClasses);
		this.baseClassesAfterConstraint = baseClassesAfterConstraint;
	}
	
	@Override
	public final void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionClass(comments, clone(defName), clone(tplParams), clone(tplConstraint), 
			clone(baseClasses), baseClassesAfterConstraint, clone(aggrBody));
	}
	
	public void classLikeToStringAsCode(ASTCodePrinter cp, String keyword) {
		cp.append(keyword);
		cp.append(defName, " ");
		cp.appendList("(", tplParams, ",", ") ");
		if(baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.appendList(": ", baseClasses, ",", " ");
		if(!baseClassesAfterConstraint) DefinitionTemplate.tplConstraintToStringAsCode(cp, tplConstraint);
		cp.append(aggrBody, "\n");
	}
	
	@Override
	protected void acceptNodeChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, tplParams);
		if(baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, baseClasses);
		if(!baseClassesAfterConstraint)
			acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, aggrBody);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(tplParams, false) {
			@Override
			public void evaluateSuperScopes(CommonScopeLookup lookup) {
				getSemantics(lookup.context).getMembersScope().resolveLookupInSuperScopes(lookup);
			}
		};
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public final ClassSemantics getSemantics(ISemanticContext parentContext) {
		return (ClassSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected ClassSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ClassSemantics(this, pickedElement);
	}
	
	public class ClassSemantics extends AggregateSemantics {
		
		public ClassSemantics(DefinitionClass_Common classElement, PickedElement<?> pickedElement) {
			super(classElement, pickedElement,
				new DefClassMembersScope(getAggregateMembers()),
				DeeLanguageIntrinsics.D2_063_intrinsics.createObjectPropertiesScope(classElement));
		}
		
		@Override
		public DefClassMembersScope getMembersScope() {
			return (DefClassMembersScope) super.getMembersScope();
		}
		
		public INamedElement resolveSuperClass(ISemanticContext mr) {
			
			for (Reference baseClassRef : nonNullIterable(baseClasses)) {
				INamedElement baseClass = baseClassRef.resolveTargetElement(mr);
				
				if(baseClass.getArcheType() == EArcheType.Interface) {
					continue;
				}
				if(baseClass instanceof DefinitionClass) {
					return baseClass;
				}
			}
			// TODO test implicit object reference
			return DeeLanguageIntrinsics.OBJECT_CLASS_REF.getSemantics(mr).resolveTargetElement().result;
		}
		
	}
	
	public class DefClassMembersScope extends MembersScopeElement {
		
		public DefClassMembersScope(Iterable<? extends ILanguageElement> membersIterable) {
			super(membersIterable);
		}
		
		@Override
		public void resolveLookupInSuperScopes(CommonScopeLookup search) {
			ISemanticContext context = search.context;
			
			for(Reference baseclass : CoreUtil.nullToEmpty(baseClasses)) {
				INamedElement baseClassElem = baseclass.resolveTargetElement(context);
				if(baseClassElem == null)
					continue;
				
				if(baseClassElem instanceof DefinitionClass) {
					DefinitionClass baseClassDef = (DefinitionClass) baseClassElem;
					search.evaluateScope(baseClassDef.getSemantics(context).getMembersScope());
				}
			}
		}
	}
	
}