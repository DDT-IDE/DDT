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
package dtool.ast.declarations;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.collections.ArrayView;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.ast.statements.IStatement;

/**
 * An import Declaration.
 * This is considered an INonScopedBlock because it might contain aliasing
 * imports and selective imports, which are primary-space {@link DefUnit}s.
 */
public class DeclarationImport extends ASTNode implements INonScopedContainer, IDeclaration, IStatement {
	
	public final NodeVector<IImportFragment> imports;
	public final boolean isStatic;
	public boolean isPublicImport; // aka public imports
	
	public DeclarationImport(boolean isStatic, NodeVector<IImportFragment> imports) {
		this.imports = parentize(imports);
		this.isStatic = isStatic;
		this.isPublicImport = false; // TODO, should be determined by surrounding analysis
	}
	
	public final ArrayView<ASTNode> imports_asNodes() {
		return CoreUtil.<ArrayView<ASTNode>>blindCast(imports);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_IMPORT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, imports);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DeclarationImport(isStatic, clone(imports));
	}
	
	public static interface IImportFragment extends IASTNode {
		
		public void evaluateImportsScopeContribution(ScopeNameResolution scopeRes, boolean isSecondaryScope);
		
		public RefModule getModuleRef();
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return imports_asNodes();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isStatic, "static ");
		
		cp.append("import ");
		cp.appendList(imports_asNodes(), ", ");
		cp.append(";");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void evaluateForScopeLookup(ScopeNameResolution scopeRes, boolean isSecondaryScope, 
			boolean publicImportsOnly) {
		
		if(publicImportsOnly && !isPublicImport)
			return; // Don't consider private contributions
		
		for (IImportFragment impFrag : imports) {
			impFrag.evaluateImportsScopeContribution(scopeRes, isSecondaryScope);
		}
		
	}
	
}