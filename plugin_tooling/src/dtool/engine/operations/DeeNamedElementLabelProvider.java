/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.operations;

import static melnorme.utilbox.core.CoreUtil.tryCast;

import dtool.ast.definitions.AbstractFunctionDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl.AliasVarDeclFragment;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.references.Reference;
import dtool.ddoc.TextUI;
import dtool.engine.analysis.IVarDefinitionLike;
import melnorme.lang.tooling.symbols.INamedElement;

public class DeeNamedElementLabelProvider {
	
	public static String getLabelForContentAssistPopup(INamedElement namedElement) {
		return new DeeNamedElementLabelProvider().getLabel(namedElement);
	}
	
	public String getLabel(INamedElement namedElement) {
		
		switch (namedElement.getArcheType()) {
		case Module:
			return namedElement.getName();
		case Package:
			return namedElement.getName();
		default:
			break;
		}
		
		// We should NOT try to resolve namedElement to its true defUnit because that can be a costly operation,
		// and want to calculate a label quickly, without the need for parsing or other semantic operations
		DefUnit defUnit = tryCast(namedElement, DefUnit.class); 
		if(defUnit == null) {
			return namedElement.getName();
		}
		
		if(defUnit instanceof IVarDefinitionLike) {
//			IVarDefinitionLike varDefinitionLike = (IVarDefinitionLike) defUnit;
			// TODO: add more info to label, such as var type.
		}
		
		switch (defUnit.getNodeType()) {
		case DEFINITION_VARIABLE: {
			DefinitionVariable elem = (DefinitionVariable) defUnit;
			return elem.getName() + getTypeSegmentForVar(elem.type);
		}
		case DEFINITION_VAR_FRAGMENT: {
			DefVarFragment elem = (DefVarFragment) defUnit;
			Reference type = elem.getDeclaredType();
			return elem.getName() + getTypeSegmentForVar(type);
		}
		
		case FUNCTION_PARAMETER: {
			FunctionParameter elem = (FunctionParameter) defUnit;
			return elem.getName() + getTypeSegmentForVar(elem.type);
		}
		
		case DEFINITION_CONSTRUCTOR: {
			DefinitionConstructor elem = (DefinitionConstructor) defUnit;
			return getFnExtendedName(elem);
		}
		case DEFINITION_FUNCTION: {
			DefinitionFunction elem = (DefinitionFunction) defUnit;
			return getFnExtendedName(elem) + getTypeSegmentForVar(elem.getDeclaredReturnType());
		}
		
		case DEFINITION_ALIAS_FRAGMENT: {
			DefinitionAliasFragment elem = (DefinitionAliasFragment) defUnit;
			return elem.getName() + getAliasSegment(elem.target);
		}
		case DEFINITION_ALIAS_VAR_DECL: {
			DefinitionAliasVarDecl elem = (DefinitionAliasVarDecl) defUnit;
			return elem.getName() + getAliasSegment(elem.target);
		}
		case ALIAS_VAR_DECL_FRAGMENT: {
			AliasVarDeclFragment elem = (AliasVarDeclFragment) defUnit;
			return elem.getName() + getAliasSegment(elem.getAliasTarget());
		}
		case DEFINITION_ALIAS_FUNCTION_DECL: {
			DefinitionAliasFunctionDecl elem = (DefinitionAliasFunctionDecl) defUnit;
			// TODO: print the correct alias target (a function type)
			return elem.getName() + getAliasSegment(elem.target) + "(?)";
		}
		
		
		default: break;
		}
		
		if(defUnit instanceof DefinitionAggregate) {
			return defUnit.getName();
		}
		
		return defUnit.getName();
	}

	public String getTypeSegmentForVar(Reference typeRef) {
		return " : " + TextUI.typeRefToUIString(typeRef);
	}
	
	public String getFnExtendedName(AbstractFunctionDefinition elem) {
		return elem.getExtendedName(true, true);
	}
	
	public String getAliasSegment(Reference target) {
		String targetToString = target == null ? "?" : target.toStringAsCode();
		if(targetToString.isEmpty()) {
			targetToString = "?";
		}
		return " -> " + targetToString;
	}
	
	/* -----------------  ----------------- */
	
	public static class DeeNamedElementSimpleLabelProvider extends DeeNamedElementLabelProvider {
		@Override
		public String getTypeSegmentForVar(Reference typeRef) {
			return "";
		}
		
		@Override
		public String getFnExtendedName(AbstractFunctionDefinition elem) {
			return elem.getExtendedName(true, false);
		}
		
		@Override
		public String getAliasSegment(Reference target) {
			return "";
		}
	}
	
}