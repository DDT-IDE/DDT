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

import dtool.ast.declarations.DeclarationImport;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Definition;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
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

public class ASTNeoHomogenousVisitor implements IASTNeoVisitor {
	
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
	public final void endVisit(ASTNeoNode node) {
	}
	
	@Override
	public final boolean visit(Symbol elem) {
		return true;
	}
	
	@Override
	public final boolean visit(DefUnit elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefUnit node) {
	}
	
	@Override
	public final boolean visit(Module elem) {
		return true;
	}
	
	@Override
	public final void endVisit(Module node) {
	}
	
	@Override
	public final boolean visit(Definition elem) {
		return true;
	}
	
	@Override
	public final void endVisit(Definition node) {
	}
	
	@Override
	public final boolean visit(DefinitionAggregate elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionAggregate node) {
	}
	
	@Override
	public final boolean visit(DefinitionTemplate elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionTemplate node) {
	}
	
	@Override
	public final boolean visit(DefinitionClass elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionClass node) {
	}
	
	@Override
	public final boolean visit(DefinitionVariable elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionVariable elem) {
	}
	
	@Override
	public final boolean visit(DefinitionEnum elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionEnum elem) {
	}
	
	@Override
	public final boolean visit(DefinitionTypedef elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionTypedef elem) {
	}
	
	@Override
	public final boolean visit(DefinitionAlias elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionAlias elem) {
	}
	
	@Override
	public final boolean visit(DefinitionFunction elem) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionFunction elem) {
	}
	
	@Override
	public final boolean visit(Resolvable elem) {
		return true;
	}
	
	@Override
	public final void endVisit(Resolvable node) {
	}
	
	@Override
	public final boolean visit(Reference elem) {
		return true;
	}
	
	@Override
	public final void endVisit(Reference node) {
	}
	
	@Override
	public final boolean visit(CommonRefNative elem) {
		return true;
	}
	
	@Override
	public final boolean visit(NamedReference elem) {
		return true;
	}
	
	@Override
	public final boolean visit(CommonRefQualified elem) {
		return true;
	}
	
	@Override
	public final boolean visit(RefIdentifier elem) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTemplateInstance elem) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationImport elem) {
		return true;
	}
	
}
