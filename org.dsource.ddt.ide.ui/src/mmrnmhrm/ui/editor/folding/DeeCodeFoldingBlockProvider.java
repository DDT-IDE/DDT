/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.folding;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Region;

import dtool.ast.ASTNeoHomoVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionFunction;

public class DeeCodeFoldingBlockProvider implements IFoldingBlockProvider {
	
	protected int blockLinesMin;
	protected boolean collapseFunctions;
	protected boolean collapseAggregates;
	
	@Override
	public void initializePreferences(IPreferenceStore preferenceStore) {
		blockLinesMin = preferenceStore.getInt(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT);
		
		collapseFunctions = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_METHODS);
		collapseAggregates = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_CLASSES);
	}
	
	protected IFoldingBlockRequestor requestor;
	
	@Override
	public void setRequestor(IFoldingBlockRequestor requestor) {
		this.requestor = requestor;
	}
	
	@Override
	public int getMinimalLineCount() {
		return blockLinesMin;
	}
	
	@Override
	public void computeFoldableBlocks(IFoldingContent content) {
		if (content.getModelElement() instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) content.getModelElement();
			IModuleDeclaration moduleDeclaration = SourceParserUtil.parse(sourceModule, null);
			if (moduleDeclaration instanceof DeeModuleDeclaration) {
				DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
				deeModuleDecl.neoModule.accept(new ASTNeoHomoVisitor() {
					
					@Override
					protected boolean enterNode(ASTNeoNode node) {
						if (node instanceof DefinitionAggregate) {
							reportBlock(node, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						} else if (node instanceof DefinitionFunction) {
							reportBlock(node, DeeFoldingBlockKind.FUNCTION, collapseFunctions);
						} if (node instanceof DeclarationConditional) {
							//reportBlock(node, DeeFoldingBlockKind.FUNCTION, collapseFunctions);
						}
						return true;
					}
					
					@Override
					protected void leaveNode(ASTNeoNode elem) {
					}
					
				});
			}
		}
	}
	
	protected void reportBlock(ASTNeoNode node, DeeFoldingBlockKind foldingBlockKind, boolean collapse) {
		Region region = new Region(node.getStartPos(), node.getLength());
		///  XXX: BM: DLTK, do we understand the full consequences of giving region as a key? 
		requestor.acceptBlock(node.getStartPos(), node.getEndPos(), foldingBlockKind, region, collapse);
	}
	
}
