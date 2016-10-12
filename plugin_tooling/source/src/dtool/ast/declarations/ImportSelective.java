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

import static dtool.util.NewUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.DevelopmentCodeMarkers;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;

public class ImportSelective extends ASTNode implements INonScopedContainer, IImportFragment {
	
	public static interface IImportSelectiveSelection extends IASTNode {
		//String getTargetName();
	}
	
	public final IImportFragment fragment;
	public final NodeVector<IImportSelectiveSelection> impSelFrags;
	
	public ImportSelective(IImportFragment fragment, NodeVector<IImportSelectiveSelection> frags) {
		this.fragment = parentize(fragment);
		this.impSelFrags = parentizeFrags(frags);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_SELECTIVE;
	}
	
	@Override
	protected DeclarationImport getParent_Concrete() {
		return assertCast(parent, DeclarationImport.class);
	}
	
	public DeclarationImport getDeclarationImport() {
		return getParent_Concrete();
	}
	
	public NodeVector<IImportSelectiveSelection> parentizeFrags(NodeVector<IImportSelectiveSelection> frags) {
		if (frags != null) {
			for (IImportSelectiveSelection selection : frags) {
				((ASTNode) selection).setParent(this);
				if (selection instanceof ImportSelectiveAlias) {
					((ImportSelectiveAlias) selection).target.impSel = this;
				} else if (selection instanceof RefImportSelection) {
					((RefImportSelection) selection).impSel = this;
				} else {
					assertFail();
				}
			}
		}
		return frags;
	}
	
	@Override
	public RefModule getModuleRef() {
		return fragment.getModuleRef();
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, fragment);
		acceptVisitor(visitor, impSelFrags);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ImportSelective(clone(fragment), clone(impSelFrags));
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return impSelFrags;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(fragment, " : ");
		cp.appendList(impSelFrags, ", ");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void evaluateImportsScopeContribution(ScopeNameResolution scopeRes, boolean isSecondaryScope) {
		if(isSecondaryScope) {
			return;
		}
		if(fragment instanceof ImportContent) {
			if(DevelopmentCodeMarkers.DISABLED_FUNCTIONALITY) {
				// DMD doesn't work like this.
				ImportContent importContent = (ImportContent) fragment;
				ImportContent.resolveStaticImport(scopeRes, importContent.moduleRef);
			}
		} else if(fragment instanceof ImportAlias){
			// TODO: blah, blah, stupid feature anyways
		}
		resolveScopeElements(this, scopeRes);
	}
	
	public static void resolveScopeElements(ImportSelective impSelective, ScopeNameResolution scopeRes) {
		
		ISemanticContext context = scopeRes.getContext();
		
		INamedElement targetModule = ImportContent.resolveTargetModule(context, impSelective);
		if (targetModule == null)
			return;
			
		for(IImportSelectiveSelection impSelFrag: impSelective.impSelFrags) {
			if(impSelFrag instanceof RefImportSelection) {
				RefImportSelection refImportSelection = (RefImportSelection) impSelFrag;
				String name = refImportSelection.getDenulledIdentifier();
				// Do pre-emptive matching
				if(!scopeRes.getLookup().matchesName(name)) {
					continue;
				}
				INamedElement selectedElement = refImportSelection.resolveTargetElement(context);
				scopeRes.visitNamedElement(selectedElement);
			}
		}
	}
	
}