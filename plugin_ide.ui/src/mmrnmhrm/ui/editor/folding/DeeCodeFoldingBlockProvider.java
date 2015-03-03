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

import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast_actual.ASTNode;
import mmrnmhrm.ui.editor.EditorUtil;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Region;

import _org.eclipse.dltk.ui.text.folding.DelegatingFoldingStructureProvider.FoldingContent;
import _org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider;
import dtool.ast.definitions.Module;

public class DeeCodeFoldingBlockProvider implements IFoldingBlockProvider {
	
	protected int blockLinesMin;
	protected boolean collapseFunctions;
	protected boolean collapseFunctionLiterals;
	protected boolean collapseAggregates;
	protected boolean collapseAnonClasses;
	protected boolean collapseUnittests;
	protected boolean collapseConditionals;
	
	@Override
	public void initializePreferences(IPreferenceStore preferenceStore) {
		blockLinesMin = preferenceStore.getInt(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT);
		
		collapseFunctions = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_METHODS);
		collapseAggregates = preferenceStore.getBoolean(PreferenceConstants.EDITOR_FOLDING_INIT_CLASSES);
		collapseUnittests = preferenceStore.getBoolean(
				DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_UNITTESTS);
		collapseConditionals = preferenceStore.getBoolean(
				DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_CONDITIONALS);
		collapseFunctionLiterals = preferenceStore.getBoolean(
				DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_FUNCTIONLITERALS);
		collapseAnonClasses = preferenceStore.getBoolean(
				DeeFoldingPreferenceConstants.EDITOR_FOLDING_INIT_ANONCLASSES);
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
	public void computeFoldableBlocks(FoldingContent content) {
		Module deeModule = EditorUtil.getParsedModule_NoWaitInUI(content, content.getFilePath());
		
		if (deeModule != null) {
			deeModule.accept(new ASTVisitor() {
				
				@Override
				public boolean preVisit(ASTNode node) {
					switch (node.getNodeType()) {
					case DEFINITION_STRUCT:
					case DEFINITION_UNION:
					case DEFINITION_CLASS:
					case DEFINITION_INTERFACE:
					case DEFINITION_TEMPLATE:
						reportBlock(node, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						break;
					case DEFINITION_FUNCTION:
						reportBlock(node, DeeFoldingBlockKind.FUNCTION, collapseFunctions);
						break;
					case EXP_FUNCTION_LITERAL:
						reportBlock(node, DeeFoldingBlockKind.FUNCTIONLITERALS, collapseFunctionLiterals);
						break;
					case EXP_NEW_ANON_CLASS:
						reportBlock(node, DeeFoldingBlockKind.ANONCLASSES, collapseAnonClasses);
						break;
					case DECLARATION_UNITEST:
						reportBlock(node, DeeFoldingBlockKind.UNITTEST, collapseUnittests);
						break;
					case DECLARATION_DEBUG_VERSION:
						reportBlock(node, DeeFoldingBlockKind.CONDITIONALS, collapseConditionals);
						break;
					default:
					}
					return VISIT_CHILDREN;
				}
			});
		}
	}
	
	protected void reportBlock(ASTNode node, DeeFoldingBlockKind foldingBlockKind, boolean collapse) {
		if(node.hasSourceRangeInfo()) {
			Region region = new Region(node.getStartPos(), node.getLength());
			///  XXX: BM: DLTK, do we understand the full consequences of giving region as a key? 
			requestor.acceptBlock(node.getStartPos(), node.getEndPos(), foldingBlockKind, region, collapse);
		}
	}
	
}
