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
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNode;
import dtool.ast.ASTSwitchVisitor;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionEnumVar.DefinitionEnumVarFragment;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionMixinInstance;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;

public class ASTSwitchVisitorTester extends ASTSwitchVisitor {
	
	protected Class<?> visitKlass;

	@Override
	public boolean visitOther(ASTNode node) {
		return false;
	}
	
	@Override
	public void endVisitOther(ASTNode node) {
		assertTrue(visitKlass == null);
	}
	
	@Override
	public boolean visit(Module node) {
		visitKlass = node.getClass();
		return false;
	}
	
	@Override
	public void endVisit(Module node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionStruct node) {
		visitKlass = node.getClass();
		return false;
	}
	
	@Override
	public void endVisit(DefinitionStruct node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionUnion node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionUnion node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionClass node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionClass node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionInterface node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionInterface node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionTemplate node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionTemplate node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionMixinInstance node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionMixinInstance node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionEnumVarFragment node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionEnumVarFragment node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionEnum node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionEnum node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(EnumMember node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(EnumMember node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionAliasVarDecl node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionAliasVarDecl node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionAliasFragment node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionAliasFragment node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionAliasFunctionDecl node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionAliasFunctionDecl node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionFunction node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionFunction node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionConstructor node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionConstructor node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefinitionVariable node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefinitionVariable node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(DefVarFragment node) {
		visitKlass = node.getClass();
		return false;	
	}
	
	@Override
	public void endVisit(DefVarFragment node) {
		assertTrue(node.getClass() == visitKlass);
	}
	
	@Override
	public boolean visit(NamedReference elem) {
		return false;	
	}
	
}