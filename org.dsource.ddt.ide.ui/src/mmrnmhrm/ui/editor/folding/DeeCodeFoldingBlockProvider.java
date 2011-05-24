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
import org.dsource.ddt.ide.core.model.DeeParserUtil;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Region;

import dtool.ast.ASTAbstractVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionTemplate;

public class DeeCodeFoldingBlockProvider implements IFoldingBlockProvider {
	
	protected int blockLinesMin;
	protected boolean collapseFunctions;
	protected boolean collapseAggregates;
	protected boolean collapseUnittests;
	protected boolean collapseConditionals;
	
	@Override
	public void initializePreferences(IPreferenceStore preferenceStore) {
		blockLinesMin = preferenceStore.getInt(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT);
		
		collapseFunctions = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_METHODS);
		collapseAggregates = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_CLASSES);
		collapseUnittests = preferenceStore.getBoolean(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_UNITTESTS);
		collapseConditionals = preferenceStore.getBoolean(DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_CONDITIONALS);
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
			DeeModuleDeclaration deeModuleDecl = DeeParserUtil.getASTFromModule(sourceModule);
			if (deeModuleDecl != null) {
				deeModuleDecl.neoModule.accept(new ASTAbstractVisitor() {
					
					@Override
					public boolean visit(DefinitionAggregate elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					
					@Override
					public boolean visit(DefinitionClass elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					
					@Override
					public boolean visit(DefinitionTemplate elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					
					@Override
					public boolean visit(DefinitionFunction elem) {
						reportBlock(elem, DeeFoldingBlockKind.FUNCTION, collapseFunctions);
						return true;
					}
					
					@Override
					public boolean visit(DeclarationUnitTest elem) {
						reportBlock(elem, DeeFoldingBlockKind.UNITTEST, collapseUnittests);
						return true;
					}
					
					@Override
					public boolean visit(DeclarationConditional elem) {
						reportBlock(elem, DeeFoldingBlockKind.CONDITIONALS, collapseConditionals);
						return true;
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
