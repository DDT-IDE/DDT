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

import dtool.ast.declarations.DeclarationAllocatorFunction;
import dtool.ast.declarations.AbstractConditionalDeclaration;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAliasDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;

/**
 * An Abstract visitor with default implementation for just the endVisit methods.
 * This is an utility class that is safer than {@link ASTDefaultVisitor} when modifications to 
 * the AST hierarchy happen, because subclass will still have a compiler error when 
 * new visit methods are added (unlike {@link ASTDefaultVisitor} 
 */
public abstract class ASTAbstractVisitor implements IASTVisitor {
	
	@Override public void endVisit(ASTNeoNode node) {}
	
	@Override public void endVisit(DefUnit node) {}
	
	@Override public void endVisit(Module node) {}
	
	@Override public void endVisit(DeclarationModule node) { }
	
	@Override public void endVisit(DefinitionStruct node) {}
	@Override public void endVisit(DefinitionUnion node) {}
	@Override public void endVisit(DefinitionClass node) {}
	
	@Override public void endVisit(DefinitionInterface node) {}
	
	@Override public void endVisit(DefinitionTemplate node) {}
	
	@Override public void endVisit(DefinitionVariable node) {}
	
	@Override public void endVisit(DefinitionEnum node) {}
	
	@Override public void endVisit(DefinitionTypedef node) {}
	
	@Override public void endVisit(DefinitionAliasDecl node) { }
	@Override public void endVisit(DefinitionAliasFragment node) { }
	
	@Override public void endVisit(DefinitionFunction node) { }
	
	@Override public void endVisit(DefinitionConstructor node) {}
	
	@Override public void endVisit(Resolvable node) {}
	
	@Override public void endVisit(Reference node) {}
	
	@Override public void endVisit(DeclarationImport node) {}
	
	@Override public void endVisit(DeclarationInvariant node) {}
	
	@Override public void endVisit(DeclarationUnitTest node) {}
	
	@Override public void endVisit(AbstractConditionalDeclaration node) {}
	
	@Override public void endVisit(DeclarationSpecialFunction node) {}
	@Override public void endVisit(DeclarationAllocatorFunction node) {}
	
}