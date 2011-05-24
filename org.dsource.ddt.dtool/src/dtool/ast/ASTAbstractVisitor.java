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

public abstract class ASTAbstractVisitor implements IASTNeoVisitor {
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
	}
	
	@Override
	public boolean visit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public void endVisit(ASTNeoNode node) {
	}
	
	@Override
	public boolean visit(Symbol elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefUnit elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefUnit node) {
	}
	
	@Override
	public boolean visit(Module elem) {
		return true;
	}
	
	@Override
	public void endVisit(Module node) {
	}
	
	@Override
	public boolean visit(Definition elem) {
		return true;
	}
	
	@Override
	public void endVisit(Definition node) {
	}
	
	@Override
	public boolean visit(DefinitionAggregate elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionAggregate node) {
	}
	
	@Override
	public boolean visit(DefinitionTemplate elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionTemplate node) {
	}
	
	@Override
	public boolean visit(DefinitionClass elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionClass node) {
	}
	
	@Override
	public boolean visit(DefinitionVariable elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionVariable elem) {
	}
	
	@Override
	public boolean visit(DefinitionEnum elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionEnum elem) {
	}
	
	@Override
	public boolean visit(DefinitionTypedef elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionTypedef elem) {
	}
	
	@Override
	public boolean visit(DefinitionAlias elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionAlias elem) {
	}
	
	@Override
	public boolean visit(DefinitionFunction elem) {
		return true;
	}
	
	@Override
	public void endVisit(DefinitionFunction elem) {
	}
	
	@Override
	public boolean visit(Resolvable elem) {
		return true;
	}
	
	@Override
	public void endVisit(Resolvable node) {
	}
	
	@Override
	public boolean visit(Reference elem) {
		return true;
	}
	
	@Override
	public void endVisit(Reference node) {
	}
	
	@Override
	public boolean visit(CommonRefNative elem) {
		return true;
	}
	
	@Override
	public boolean visit(NamedReference elem) {
		return true;
	}
	
	@Override
	public boolean visit(CommonRefQualified elem) {
		return true;
	}
	
	@Override
	public boolean visit(RefIdentifier elem) {
		return true;
	}
	
	@Override
	public boolean visit(RefTemplateInstance elem) {
		return true;
	}
	
	@Override
	public boolean visit(DeclarationImport elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationImport elem) {
	}
	
	@Override
	public boolean visit(DeclarationInvariant elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationInvariant elem) {
	}
	
	@Override
	public boolean visit(DeclarationUnitTest elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationUnitTest elem) {
	}
	
	@Override
	public boolean visit(DeclarationConditional elem) {
		return true;
	}
	@Override
	public void endVisit(DeclarationConditional elem) {
	}
	
}
