/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser.structure;

import java.util.EnumSet;

import melnorme.lang.tooling.CompletionProposalKind;
import melnorme.lang.tooling.EAttributeFlag;
import melnorme.lang.tooling.EProtection;
import melnorme.lang.tooling.ElementAttributes;
import melnorme.lang.tooling.ElementLabelInfo;
import melnorme.lang.tooling.structure.StructureElementKind;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.EArcheType.ArchetypeCastVisitor;
import dtool.ast.definitions.ITemplatableElement;
import dtool.ast.references.Reference;
import dtool.engine.analysis.IVarDefinitionLike;


public class DeeLabelInfoProvider extends ArchetypeCastVisitor {
	
	protected StructureElementKind structureKind = null;
	protected CompletionProposalKind proposalKind = null;
	protected String type = null;
	protected ElementAttributes elementAttribs = null;
	
	public ElementLabelInfo getLabelInfo(INamedElement element) {
		
		visit(element, element.getArcheType());
		
		if(element.isBuiltinElement()) {
			structureKind = null;
			proposalKind = CompletionProposalKind.NATIVE;
		}
		
		if(element instanceof IVarDefinitionLike) {
			IVarDefinitionLike varDef = (IVarDefinitionLike) element;
			type = getTypeDesc(varDef.getDeclaredType());
		}
		
		elementAttribs = getAttributes(element);
		
		return new ElementLabelInfo(structureKind, proposalKind, type, elementAttribs);
	}
	
	
	@Override
	protected void visitModule(INamedElement element) {
		structureKind = StructureElementKind.MODULEDEC;
		proposalKind = CompletionProposalKind.MODULEDEC;
	}
	@Override
	protected void visitPackage(INamedElement element) {
		structureKind = null; // Not a structural element
		proposalKind = CompletionProposalKind.PACKAGE;
	}
	
	
	@Override
	protected void visitVariable(INamedElement element) {
		structureKind = StructureElementKind.VARIABLE;
		proposalKind = CompletionProposalKind.VARIABLE;
	}
	@Override
	protected void visitEnumMember(INamedElement element) {
		structureKind = StructureElementKind.VARIABLE;
		proposalKind = CompletionProposalKind.VARIABLE;
	}
	
	@Override
	public void doVisit(DefinitionFunction defFunction) {
		structureKind = StructureElementKind.FUNCTION;
		proposalKind = CompletionProposalKind.FUNCTION;
		type = getTypeDesc(defFunction.retType);
	}
	
	@Override
	protected void visitConstructor(INamedElement element) {
		structureKind = StructureElementKind.CONSTRUCTOR;
		proposalKind = CompletionProposalKind.CONSTRUCTOR;
	}
	
	
	@Override
	protected void visitStruct(INamedElement element) {
		structureKind = StructureElementKind.STRUCT;
		proposalKind = CompletionProposalKind.STRUCT;
	}
	
	@Override
	protected void visitUnion(INamedElement element) {
		structureKind = StructureElementKind.UNION;
		proposalKind = CompletionProposalKind.UNION;
	}
	
	@Override
	protected void visitClass(INamedElement element) {
		structureKind = StructureElementKind.CLASS;
		proposalKind = CompletionProposalKind.CLASS;
	}
	
	@Override
	protected void visitInterface(INamedElement element) {
		structureKind = StructureElementKind.INTERFACE;
		proposalKind = CompletionProposalKind.INTERFACE;
	}
	
	
	@Override
	protected void visitTemplate(INamedElement element) {
		structureKind = StructureElementKind.TEMPLATE;
		proposalKind = CompletionProposalKind.TEMPLATE;
	}
	
	
	@Override
	public void doVisit(DefinitionEnum defEnum) {
		structureKind = StructureElementKind.ENUM_TYPE;
		proposalKind = CompletionProposalKind.ENUM;
		type = getTypeDesc(defEnum.type);
	}
	
	@Override
	protected void visitMixin(INamedElement node) {
		structureKind = StructureElementKind.MIXIN;
		proposalKind = CompletionProposalKind.MIXIN;
	}
	
	@Override
	protected void visitAlias(INamedElement node) {
		structureKind = StructureElementKind.ALIAS;
		proposalKind = CompletionProposalKind.ALIAS;
		
		if(node instanceof DefinitionAliasFragment) {
			DefinitionAliasFragment aliasFragment = (DefinitionAliasFragment) node;
			type = getTypeDesc(aliasFragment.target);
		} else {
			// TODO: type for other aliases
		}
	}
	
	
	@Override
	protected void visitTuple(INamedElement element) {
		structureKind = null; // Not a structural element
		proposalKind = CompletionProposalKind.TUPLE;
	}
	
	@Override
	protected void visitType(INamedElement element) {
		structureKind = null; // Not a structural element
		proposalKind = CompletionProposalKind.TYPE;
	}
	
	@Override
	protected void visitError(INamedElement element) {
		structureKind = null; // Not a structural element
		proposalKind = CompletionProposalKind.ERROR;
	}
	
	
	/* -----------------  ----------------- */
	
	
	protected String getTypeDesc(Reference type) {
		return type == null ? null : type.toStringAsCode();
	}
	
	public static ElementAttributes getAttributes(INamedElement namedElement) {
		EnumSet<EAttributeFlag> flagsSet = ElementAttributes.newFlagsSet();
		
		if(namedElement.getArcheType() == EArcheType.Alias) {
			flagsSet.add(EAttributeFlag.ALIASED);
		}
		
		if(namedElement instanceof CommonDefinition) {
			CommonDefinition commonDefinition = (CommonDefinition) namedElement;
			return getDeclarationModifierFlags(commonDefinition, flagsSet);
		}
		
		// TODO: fragment elements
		
		return new ElementAttributes(null, flagsSet);
	}
	
	public static ElementAttributes getDeclarationModifierFlags(CommonDefinition elem, 
			EnumSet<EAttributeFlag> flagsSet) {
		
		
		setFlagIfHasAttribute(elem, AttributeKinds.STATIC, flagsSet, EAttributeFlag.STATIC);
		setFlagIfHasAttribute(elem, AttributeKinds.FINAL, flagsSet, EAttributeFlag.FINAL);
		
		// Report these for variable only
		if(elem.getArcheType() == EArcheType.Variable) {
			setFlagIfHasAttribute(elem, AttributeKinds.CONST, flagsSet, EAttributeFlag.CONST); 
			setFlagIfHasAttribute(elem, AttributeKinds.IMMUTABLE, flagsSet, EAttributeFlag.IMMUTABLE);
		}
		
		setFlagIfHasAttribute(elem, AttributeKinds.ABSTRACT, flagsSet, EAttributeFlag.ABSTRACT);
		
		// set these for Function only
		if(elem.getArcheType() == EArcheType.Function) {
			setFlagIfHasAttribute(elem, AttributeKinds.OVERRIDE, flagsSet, EAttributeFlag.OVERRIDE);
		}
		
		if(elem instanceof ITemplatableElement) {
			ITemplatableElement templatableElement = (ITemplatableElement) elem;
			if(templatableElement.isTemplated()) {
				flagsSet.add(EAttributeFlag.TEMPLATED);
			}
		}
		
		EProtection prot = elem.getEffectiveProtection();
		
		return new ElementAttributes(prot, flagsSet);
	}
	
	public static void setFlagIfHasAttribute(CommonDefinition def, AttributeKinds attrib, 
			EnumSet<EAttributeFlag> flags, EAttributeFlag flag) {
		if(def.hasAttribute(attrib)) {
			flags.add(flag);
		}
	}
	
}