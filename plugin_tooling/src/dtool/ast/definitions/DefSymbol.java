/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(String id) {
		super(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SYMBOL;
	}
	
	@Override
	protected ASTNode getParent_Concrete() {
		return assertCast(parent, DefUnit.class);
	}
	
	/** @return the defunit associated with this defSymbol. Cannot be null. */
	public DefUnit getDefUnit() {
		return (DefUnit) getParent();
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefSymbol(name);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
	public DefSymbol createCopy() {
		DefSymbol defname = this;
		DefSymbol defSymbol = new DefSymbol(defname.name);
		SourceRange sourceRangeOrNull = defname.getSourceRangeOrNull();
		if(sourceRangeOrNull != null) {
			defSymbol.setSourceRange(sourceRangeOrNull);
		}
		return defSymbol;
	}
	
}