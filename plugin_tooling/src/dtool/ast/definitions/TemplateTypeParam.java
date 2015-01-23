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

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics.TypeAliasSemantics;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.engine.analysis.templates.RefTemplateInstanceSemantics;
import dtool.engine.analysis.templates.TemplateParameterAnalyser;
import dtool.engine.analysis.templates.TypeAliasElement;

public class TemplateTypeParam extends DefUnit implements ITemplateParameter {
	
	public final Reference specializationType;
	public final Reference defaultType;
	
	public TemplateTypeParam(DefSymbol defName, Reference specializationType, Reference defaultType){
		super(defName);
		this.specializationType = parentize(specializationType);
		this.defaultType = parentize(defaultType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, specializationType);
		acceptVisitor(visitor, defaultType);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new TemplateTypeParam(clone(defName), clone(specializationType), clone(defaultType));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defName);
		cp.append(" : ", specializationType);
		cp.append(" = ", defaultType);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.TypeParameter;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new TypeAliasSemantics(this, pickedElement) {
			
			@Override
			protected IConcreteNamedElement resolveAliasTarget_nonNull() {
				IReference aliasTarget = getAliasTarget();
				if(isSyntaxError(aliasTarget)) {
					return ErrorElement.newUnsupportedError(element, null);
				}
				
				return ReferenceSemantics.resolveConcreteElement(aliasTarget, context);
			}
			
			@Override
			protected IReference getAliasTarget() {
				return specializationType;
			}
		};
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public TemplateParameterAnalyser getParameterAnalyser() {
		return templateParameterAnalyser;
	}
	
	protected final TemplateParameterAnalyser templateParameterAnalyser = new TemplateParameterAnalyser() {
		
		@Override
		public TplMatchLevel getMatchPriority(Resolvable tplArg, ISemanticContext context) {
			ITypeNamedElement targetType = RefTemplateInstanceSemantics.resolveTargetTypeOfArg(tplArg, context);
			
			if(targetType.getArcheType() == EArcheType.Error) {
				return TplMatchLevel.NONE;
			}
			
			if(specializationType == null) {
				return TplMatchLevel.TYPE;
			}
			
			ITypeNamedElement specType = RefTemplateInstanceSemantics.resolveTargetType(specializationType, context);
			// FIXME: not entirely accurate, needed to check superTypes
			if(specType == targetType) {
				return TplMatchLevel.TYPE_SPECIALIZED;
			}
			
			return TplMatchLevel.NONE;
		}
		
		@Override
		public INamedElementNode createTemplateArgument(Resolvable tplArg, ISemanticContext tplRefContext) {
			ITypeNamedElement argTarget = RefTemplateInstanceSemantics.resolveTargetTypeOfArg(tplArg, tplRefContext);
			return new TypeAliasElement(defName, argTarget);
		}
	};
	
}