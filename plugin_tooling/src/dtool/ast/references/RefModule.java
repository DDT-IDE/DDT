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
package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Set;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import melnorme.utilbox.collections.ArrayView;
import dtool.engine.analysis.ModuleProxy;
import dtool.parser.common.BaseLexElement;
import dtool.parser.common.IToken;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends NamedReference {
	
	public final ArrayView<IToken> packageList;
	public final BaseLexElement moduleToken;
	public final ArrayView<String> packages; // TODO: Old API, refactor?
	public final String module;
	
	public RefModule(ArrayView<IToken> packageList, BaseLexElement moduleToken) {
		this.packageList = assertNotNull(packageList);
		this.moduleToken = assertNotNull(moduleToken);
		this.packages = ArrayView.create(tokenArrayToStringArray(packageList));
		this.module = moduleToken.getSourceValue();
		
		assertTrue(packages.size() == 0 || !packages.get(0).isEmpty());
	}
	
	public static String[] tokenArrayToStringArray(ArrayView<IToken> tokenArray) {
		String[] stringArray = new String[tokenArray.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = tokenArray.get(i).getSourceValue();
		}
		return stringArray;
	}
	
	public String getModuleSimpleName() {
		return module;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_MODULE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new RefModule(packageList, moduleToken);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendTokenList(packageList, ".", true);
		cp.append(module);
	}
	
	@Override
	public String getCoreReferenceName() {
		return getRefModuleFullyQualifiedName();
	}
	
	@Override
	public boolean isMissingCoreReference() {
		return module == null || module.isEmpty();
	}
	
	public String getRefModuleFullyQualifiedName() {
		return toStringAsCode();
	}
	
	private String[] getPackageNames() {
		return packages.getInternalArray();
	}
	
	/* -----------------  ----------------- */
	
	// TODO: we could cache this result in semantic resolution
	public INamedElement createNamespaceFragment(ISemanticContext context) {
		if(getPackageNames().length == 0) {
			return resolveTargetElement(context);
		}
		
		if(isMissingCoreReference()) {
			return null;
		} else {
			INamedElement moduleElem = new ModuleProxy(getRefModuleFullyQualifiedName(), context, false, this);
			return PackageNamespace.createNamespaceElement(getPackageNames(), moduleElem);
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void doPerformNameLookup(CommonScopeLookup lookup) {
		ScopeNameResolution scopeResolution = new ScopeNameResolution(lookup);
		Set<String> matchedModules = lookup.findMatchingModules();
		for (String moduleFQName : matchedModules) {
			scopeResolution.visitNamedElement(new ModuleProxy(moduleFQName, lookup.context, true, this));
		}
		lookup.addSymbolsToMatches(scopeResolution.getNames());
	}
	
}