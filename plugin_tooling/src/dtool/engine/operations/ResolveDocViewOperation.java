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
package dtool.engine.operations;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.CommonException;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefinitionEnumVar;
import dtool.ast.definitions.DefinitionEnumVar.DefinitionEnumVarFragment;
import dtool.ast.definitions.DefinitionVariable.DefinitionAutoVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.AutoReference;
import dtool.ast.references.NamedReference;
import dtool.ddoc.TextUI;
import dtool.engine.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.analysis.IVarDefinitionLike;

public class ResolveDocViewOperation extends AbstractDToolOperation {
	
	public ResolveDocViewOperation(SemanticManager semanticManager, Path filePath, int offset, Path compilerPath,
			String dubPath) throws CommonException {
		super(semanticManager, filePath, offset, compilerPath, dubPath);
	}
	
	public String perform() throws CommonException {
		ResolvedModule resolvedModule = getResolvedModule(fileLoc);
		
		Module module = resolvedModule.getModuleNode();
		ASTNode pickedNode = ASTNodeFinder.findElement(module, offset);
		ISemanticContext mr = resolvedModule.getSemanticContext();
		
		INamedElement relevantElementForDoc = null;
		if(pickedNode instanceof DefSymbol) {
			relevantElementForDoc = ((DefSymbol) pickedNode).getDefUnit();
		} else if(pickedNode instanceof NamedReference) {
			relevantElementForDoc = ((NamedReference) pickedNode).resolveTargetElement(mr);
		} else if(pickedNode instanceof AutoReference) {
			AutoReference autoReference = (AutoReference) pickedNode;
			return getDDocHTMLViewForAutoLike(mr, autoReference.getParent_());
		} else if(pickedNode instanceof AttribBasic) {
			AttribBasic attribBasic = (AttribBasic) pickedNode;
			if(attribBasic.attribKind == AttributeKinds.AUTO) {
				if(attribBasic.getParent() instanceof DeclarationAttrib) {
					DeclarationAttrib declAttrib = (DeclarationAttrib) attribBasic.getParent();
					return getDDocHTMLViewForAuto(mr, declAttrib);
				}
			}
		} else if(pickedNode instanceof DefinitionEnumVar) {
			DefinitionEnumVar definitionEnumVar = (DefinitionEnumVar) pickedNode;
			if(definitionEnumVar.isOffsetAtEnumKeyword(offset)) {
				if(definitionEnumVar.defFragments.size() == 1) {
					DefinitionEnumVarFragment firstEnumDef = definitionEnumVar.defFragments.get(0);
					return getDDocHTMLViewForAutoLike(mr, firstEnumDef);
				}
			}
		}
		
		return relevantElementForDoc == null ? null : TextUI.getDDocHTMLRender(relevantElementForDoc);
	}
	
	protected String getDDocHTMLViewForAuto(ISemanticContext mr, DeclarationAttrib declAttrib) {
		
		IDeclaration singleDecl = declAttrib.getSingleDeclaration();
		if(singleDecl instanceof DefinitionAutoVariable) {
			DefinitionAutoVariable defVar = (DefinitionAutoVariable) singleDecl;
			if(defVar.getFragments().isEmpty()) {
				return getDDocHTMLViewForAutoLike(mr, defVar);
			}
		}
		return null;
	}
	
	protected String getDDocHTMLViewForAutoLike(ISemanticContext context, IVarDefinitionLike defVar) {
		INamedElement resolvedType = defVar.getSemantics(context).resolveTypeForValueContext();
		
		if(resolvedType == null || resolvedType.getArcheType().isError()) {
			return TextUI.span("semantic_error", "color:red;",
				"<b> Error: Could not resolve auto initializer </b>");
		}
		return TextUI.getDDocHTMLRender(resolvedType);
	}
	
}