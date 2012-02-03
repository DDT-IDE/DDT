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

import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingBlockRequestor;
import org.eclipse.dltk.ui.text.folding.IFoldingContent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Region;

import dtool.ast.ASTNeoDefaultVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationConditionalDV;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;

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
	public void computeFoldableBlocks(IFoldingContent content) {
		if (content.getModelElement() instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) content.getModelElement();
			Module deeModule = DeeModuleParsingUtil.parseAndGetAST(sourceModule);
			if (deeModule != null) {
				deeModule.accept(new ASTNeoDefaultVisitor() {
					
					@Override
					public boolean visit(DefinitionStruct elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					@Override
					public boolean visit(DefinitionUnion elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					@Override
					public boolean visit(DefinitionClass elem) {
						reportBlock(elem, DeeFoldingBlockKind.AGGREGATE, collapseAggregates);
						return true;
					}
					@Override
					public boolean visit(DefinitionInterface elem) {
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
					public boolean visit(ExpLiteralFunc elem) {
						reportBlock(elem, DeeFoldingBlockKind.FUNCTIONLITERALS, collapseFunctionLiterals);
						return true;
					}
					
					@Override
					public boolean visit(ExpLiteralNewAnonClass elem) {
						reportBlock(elem, DeeFoldingBlockKind.ANONCLASSES, collapseAnonClasses);
						return true;
					}
					
					@Override
					public boolean visit(DeclarationUnitTest elem) {
						reportBlock(elem, DeeFoldingBlockKind.UNITTEST, collapseUnittests);
						return true;
					}
					
					@Override
					public boolean visit(DeclarationConditional elem) {
						if(elem instanceof DeclarationConditionalDV) {
							reportBlock(elem, DeeFoldingBlockKind.CONDITIONALS, collapseConditionals);
						}
						return true;
					}
					
				});
			}
		}
	}
	
	protected void reportBlock(ASTNeoNode node, DeeFoldingBlockKind foldingBlockKind, boolean collapse) {
		if(node.hasSourceRangeInfo()) {
			Region region = new Region(node.getStartPos(), node.getLength());
			///  XXX: BM: DLTK, do we understand the full consequences of giving region as a key? 
			requestor.acceptBlock(node.getStartPos(), node.getEndPos(), foldingBlockKind, region, collapse);
		}
	}
	
}
