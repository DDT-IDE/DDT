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
package dtool.ast.declarations;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;

public class ImportAlias extends DefUnit implements IImportFragment {
		
	public final RefModule moduleRef;
	
	public ImportAlias(ProtoDefSymbol defId, RefModule refModule) {
		super(defId);
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_ALIAS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, moduleRef);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendStrings(getName(), " = ");
		cp.append(moduleRef);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias; // Maybe should be ImportAlias
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		// Do nothing. Aliasing imports do not contribute secondary-space DefUnits
		// TODO: this is a bug in D, it's not according to spec.
	}
	
	@Override
	public INamedElementSemantics getNodeSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			resolveSearchInReferredContainer(search, moduleRef);
		}
		
		@Override
		public IConcreteNamedElement resolveConcreteElement() {
			return null; /*FIXME: BUG here*/
		}
	};
	
}