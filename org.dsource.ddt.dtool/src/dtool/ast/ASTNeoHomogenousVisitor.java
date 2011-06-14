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
package dtool.ast;

import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionCtor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefNative;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public class ASTNeoHomogenousVisitor extends ASTNeoAbstractVisitor implements IASTNeoVisitor {
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
	}
	
	@Override
	public final boolean visit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public final boolean visit(Symbol node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefUnit node) {
		return true;
	}
	
	@Override
	public final boolean visit(Module node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionTemplate node) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionStruct node) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionUnion node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionClass node) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionInterface node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionVariable node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionEnum node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionTypedef node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionAlias node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionFunction node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionCtor node) {
		return true;
	}
	
	@Override
	public final boolean visit(Resolvable node) {
		return true;
	}
	
	@Override
	public final boolean visit(Reference node) {
		return true;
	}
	
	@Override
	public final boolean visit(CommonRefNative node) {
		return true;
	}
	
	@Override
	public final boolean visit(NamedReference node) {
		return true;
	}
	
	@Override
	public final boolean visit(CommonRefQualified node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefIdentifier node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTemplateInstance node) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationImport node) {
		return true;
	}
	@Override
	public final boolean visit(DeclarationInvariant node) {
		return true;
	}
	@Override
	public final boolean visit(DeclarationUnitTest node) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationConditional node) {
		return true;
	}
	@Override
	public final void endVisit(DeclarationConditional node) {
	}
	
}
