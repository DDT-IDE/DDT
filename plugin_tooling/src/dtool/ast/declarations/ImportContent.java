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
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefModule;

public class ImportContent extends ASTNode implements IImportFragment {
	
	public final RefModule moduleRef;
	
	public ImportContent(RefModule refModule) {
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	protected ASTNode getParent_Concrete() {
		assertTrue(parent instanceof DeclarationImport || parent instanceof ImportSelective);
		return parent;
	}
	
	public DeclarationImport getDeclarationImport() {
		ASTNode parent = super.getParent();
		if(parent instanceof DeclarationImport) {
			return (DeclarationImport) parent;
		} else {
			return ((ImportSelective) parent).getDeclarationImport();
		}
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_CONTENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, moduleRef);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(moduleRef);
	}
	
	@Override
	public void doNodeSimpleAnalysis() {
		assertTrue(getDeclarationImport() != null);
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public void evaluateImportsScopeContribution(ScopeNameResolution scopeRes) {
		resolveStaticImport(this, scopeRes);
		if(!getDeclarationImport().isStatic) {
			resolveContentImport(this, scopeRes);
		}
	}
	
	public static void resolveStaticImport(ImportContent importStatic, ScopeNameResolution scopeRes) {
		scopeRes.addImportNameElement(importStatic);
	}
	
	public INamedElement getNamespaceFragment(ISemanticContext context) {
		return moduleRef.getNamespaceFragment(context);
	}
	
	public static void resolveContentImport(ImportContent impContent, ScopeNameResolution scopeRes) {
		IConcreteNamedElement targetModule = resolveTargetModule(scopeRes.getContext(), impContent);
		if(targetModule instanceof IScopeElement) {
			IScopeElement scopeElement = (IScopeElement) targetModule;
			
			/*FIXME: BUG here if scope already evaluated */
			
			scopeRes.evaluateScopeElements(scopeElement.getScopeNodeList(), false);
		}
	}
	
	public static IConcreteNamedElement resolveTargetModule(ISemanticContext context, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String moduleName = impSelective.getModuleRef().module;
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, moduleName));
		return CommonScopeLookup.resolveModule(context, impSelective, moduleFullName);
	}
	
}