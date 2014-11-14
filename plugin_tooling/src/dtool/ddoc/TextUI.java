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
package dtool.ddoc;

import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.util.Collections;

import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DeeDocAccessor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.references.Reference;

public class TextUI extends HTMLWriterUtil {
	
	public static String getLabelForHoverSignature(INamedElement namedElement) {
		
		switch (namedElement.getArcheType()) {
		case Module:
			return namedElement.getFullyQualifiedName();
		case Package:
			return namedElement.getFullyQualifiedName();
		default:
			break;
		}
		
		DefUnit defUnit = tryCast(namedElement, DefUnit.class); 
		if(defUnit == null) {
			return namedElement.getFullyQualifiedName();
		}
		
		ASTCodePrinter cp = new ASTCodePrinter();
		
		switch (defUnit.getNodeType()) {
		case DEFINITION_VARIABLE: {
			DefinitionVariable var = (DefinitionVariable) defUnit;
			
			return typeRefToUIString(var.type) + " " + var.getName();
		}
		case DEFINITION_VAR_FRAGMENT: {
			DefVarFragment fragment = (DefVarFragment) defUnit;
			
			return typeRefToUIString(fragment.getDeclaredType()) + " " + fragment.getName();
		}
		
		case DEFINITION_FUNCTION: {
			DefinitionFunction function = (DefinitionFunction) defUnit; 
			cp.appendStrings(typeRefToUIString(function.retType), " ");
			cp.append(function.getName());
			cp.appendList("(", function.tplParams, ", ", ") ");
			cp.appendList("(", function.getParams_asNodes(), ", ", ") ");
			return cp.toString();
		}
		
		default: break;
		}
		
		if(defUnit instanceof DefinitionAggregate) {
			DefinitionAggregate defAggr = (DefinitionAggregate) defUnit;
			cp.append(defAggr.getName());
			cp.appendList("(", defAggr.tplParams, ",", ") ");
			return cp.toString();
		}
		
		// Default hover signature:
		return defUnit.getName();
	}
	
	public static String typeRefToUIString(Reference typeReference) {
		if(typeReference == null) {
			return "auto";
		}
		return typeReference.toStringAsCode();
	}
	
	/* -----------------  ----------------- */
	
	public static String getDDocHTMLRender(INamedElement defElement) {
		Ddoc ddoc = defElement.resolveDDoc();
		
		String sig = TextUI.getLabelForHoverSignature(defElement);
		sig = convertoToHTML(sig);
		String str;
		str = "<b>" +sig+ "</b>" 
		+"  "+ span("archetype", "color: #915F6D;", "("+defElement.getArcheType().toString()+")");
		
		if(ddoc != null) {
			StringBuffer stringBuffer = DeeDocAccessor.transform(ddoc, Collections.<String, String>emptyMap());
			str += "<br/><br/>" + stringBuffer.toString();
		}
		return str;
	}
	
}