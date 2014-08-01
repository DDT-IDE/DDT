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

import static melnorme.utilbox.misc.CollectionUtil.getFirstElementOrNull;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefinitionVariable.DefinitionAutoVariable;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ddoc.TextUI;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.IResolvable;

public class ResolveDocViewOperation extends AbstractDToolOperation {
	
	protected final Path filePath;
	protected final int offset;
	
	public ResolveDocViewOperation(SemanticManager semanticManager, Path filePath, int offset) {
		super(semanticManager);
		this.filePath = filePath;
		this.offset = offset;
	}
	
	public String perform() {
		ResolvedModule resolvedModule;
		try {
			resolvedModule = filePath == null ? null : getResolvedModule(filePath);
		} catch (ExecutionException e) {
			resolvedModule = null;
		}
		if(resolvedModule == null) {
			return null;
		}
		Module module = resolvedModule.getModuleNode();
		ASTNode pickedNode = ASTNodeFinder.findElement(module, offset);
		IModuleResolver mr = resolvedModule.getModuleResolver();
		
		INamedElement relevantElementForDoc = null;
		if(pickedNode instanceof DefSymbol) {
			relevantElementForDoc = ((DefSymbol) pickedNode).getDefUnit();
		} else if(pickedNode instanceof NamedReference) {
			relevantElementForDoc = ((NamedReference) pickedNode).findTargetDefElement(mr);
		} else if(pickedNode instanceof AttribBasic) {
			AttribBasic attribBasic = (AttribBasic) pickedNode;
			if(attribBasic.attribKind == AttributeKinds.AUTO) {
				if(attribBasic.getParent() instanceof DeclarationAttrib) {
					DeclarationAttrib declAttrib = (DeclarationAttrib) attribBasic.getParent();
					return getDDocHTMLViewForAuto(mr, declAttrib);
				}
			}
		}
		
		return relevantElementForDoc == null ? null : TextUI.getDDocHTMLRender(relevantElementForDoc);
	}
	
	protected String getDDocHTMLViewForAuto(IModuleResolver mr, DeclarationAttrib declAttrib) {
		
		IDeclaration singleDecl = declAttrib.getSingleDeclaration();
		if(singleDecl instanceof DefinitionAutoVariable) {
			DefinitionAutoVariable defVar = (DefinitionAutoVariable) singleDecl;
			if(defVar.getFragments().isEmpty()) {
				IResolvable effectiveType = defVar.getEffectiveType();
				INamedElement resolvedType = getFirstElementOrNull(effectiveType.findTargetDefElements(mr, true));
				return TextUI.getDDocHTMLRender(resolvedType);
			}
		}
		return null;
	}
	
}