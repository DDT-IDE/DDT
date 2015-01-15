/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
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
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.AliasSemantics.RefAliasSemantics;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.Reference;

public class ImportSelectiveAlias extends DefUnit implements IImportSelectiveSelection {
	
	public final RefImportSelection target;
	
	public ImportSelectiveAlias(DefSymbol defName, RefImportSelection impSelection) {
		super(defName);
		this.target = parentize(impSelection);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_SELECTIVE_ALIAS;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, target);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new ImportSelectiveAlias(clone(defName), clone(target));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendStrings(getName(), " = ");
		cp.append(target);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	/* -----------------  ----------------- */
	
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new RefAliasSemantics(this, pickedElement) {
			@Override
			protected Reference getAliasTarget() {
				return target;
			}
		};
	}
	
}