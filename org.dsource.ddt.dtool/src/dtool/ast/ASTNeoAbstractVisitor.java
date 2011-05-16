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

public class ASTNeoAbstractVisitor implements IASTNeoVisitor {

	@Override
	public void preVisit(ASTNeoNode node) {
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
	}

	@Override
	public boolean visit(ASTNeoNode node) {
		return false;
	}

	@Override
	public void endVisit(ASTNeoNode node) {
	}

	@Override
	public boolean visit(Symbol elem) {
		return false;
	}

	@Override
	public boolean visit(DefUnit elem) {
		return false;
	}

	@Override
	public void endVisit(DefUnit node) {
	}

	@Override
	public boolean visit(Module elem) {
		return false;
	}

	@Override
	public void endVisit(Module node) {
	}

	@Override
	public boolean visit(Definition elem) {
		return false;
	}

	@Override
	public void endVisit(Definition node) {
	}

	@Override
	public boolean visit(DefinitionAggregate elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionAggregate node) {
	}

	@Override
	public boolean visit(DefinitionTemplate elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionTemplate node) {
	}

	@Override
	public boolean visit(DefinitionClass elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionClass node) {
	}

	@Override
	public boolean visit(DefinitionVariable elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionVariable elem) {
	}

	@Override
	public boolean visit(DefinitionEnum elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionEnum elem) {
	}

	@Override
	public boolean visit(DefinitionTypedef elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionTypedef elem) {
	}

	@Override
	public boolean visit(DefinitionAlias elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionAlias elem) {
	}

	@Override
	public boolean visit(DefinitionFunction elem) {
		return false;
	}

	@Override
	public void endVisit(DefinitionFunction elem) {
	}

	@Override
	public boolean visit(Resolvable elem) {
		return false;
	}

	@Override
	public void endVisit(Resolvable node) {
	}

	@Override
	public boolean visit(Reference elem) {
		return false;
	}

	@Override
	public void endVisit(Reference node) {
	}

	@Override
	public boolean visit(CommonRefNative elem) {
		return false;
	}

	@Override
	public boolean visit(NamedReference elem) {
		return false;
	}

	@Override
	public boolean visit(CommonRefQualified elem) {
		return false;
	}

	@Override
	public boolean visit(RefIdentifier elem) {
		return false;
	}

	@Override
	public boolean visit(RefTemplateInstance elem) {
		return false;
	}

	@Override
	public boolean visit(DeclarationImport elem) {
		return false;
	}
	
}
