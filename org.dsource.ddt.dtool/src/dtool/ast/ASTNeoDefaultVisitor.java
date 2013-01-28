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

import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
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
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.InitializerArray;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.Reference;
import dtool.ast.references.TypeDelegate;
import dtool.ast.references.TypeFunction;
import dtool.ast.references.TypeMapArray;
import dtool.ast.references.TypeStaticArray;
import dtool.ast.references.TypeTypeof;

public abstract class ASTNeoDefaultVisitor extends ASTNeoAbstractVisitor implements IASTNeoVisitor {
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
	}
	
	/* -------------------- */
	
	@Override
	public boolean visit(ASTNeoNode node) {
		return true;
	}
	
	@Override
	public boolean visit(Symbol elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefUnit elem) {
		return true;
	}
	
	@Override public boolean visit(Module elem) { return true; }
	@Override public boolean visit(DeclarationModule node) { return true; }
	
	@Override public final boolean visit(DeclarationImport node) { return true; }
	@Override public boolean visit(ImportContent node) { return true; }
	@Override public boolean visit(ImportSelective node) { return true; }
	@Override public boolean visit(ImportAlias node) { return true; }
	@Override public boolean visit(ImportSelectiveAlias node) { return true; }
	
	@Override public boolean visit(DeclarationEmpty node) { return true; }
	
	//-- Various Declarations
	@Override public boolean visit(DeclarationLinkage node) { return true; }
	@Override public boolean visit(DeclarationAlign node) { return true; }
	@Override public boolean visit(DeclarationPragma node) { return true; }
	@Override public boolean visit(DeclarationProtection node) { return true; }
	@Override public boolean visit(DeclarationBasicAttrib node) { return true; }
	
	//-- Aggregates
	@Override
	public boolean visit(DefinitionTemplate elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionStruct elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionUnion elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionClass elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionInterface elem) {
		return true;
	}
	
	@Override public boolean visit(DefinitionVariable elem) { return true; }
	@Override public boolean visit(DefinitionVarFragment elem) { return true; }
	@Override public boolean visit(InitializerExp elem) { return true; }
	@Override public boolean visit(InitializerArray elem) { return true; }
	@Override public boolean visit(InitializerStruct elem) { return true; }
	@Override public boolean visit(InitializerVoid elem) { return true; }
	
	@Override
	public boolean visit(DefinitionEnum elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionTypedef elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionAlias elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionFunction elem) {
		return true;
	}
	
	@Override
	public boolean visit(DefinitionCtor node) {
		return true;
	}
	
	@Override
	public boolean visit(Resolvable elem) {
		return true;
	}
	
	@Override
	public boolean visit(Reference elem) {
		return true;
	}
	
	@Override public boolean visit(RefIdentifier elem) { return true; }
	@Override public boolean visit(RefImportSelection elem) { return true; }
	@Override public boolean visit(RefModuleQualified elem) { return true; }
	@Override public boolean visit(RefQualified elem) { return true; }
	@Override public boolean visit(RefPrimitive elem) { return true; }
	@Override public boolean visit(RefModule elem) { return true; }
	
	@Override public boolean visit(RefTypeDynArray elem) { return true; }
	@Override public boolean visit(RefTypePointer elem) { return true; }
	@Override public boolean visit(TypeDelegate elem) { return true; }
	@Override public boolean visit(TypeFunction elem) { return true; }
	@Override public boolean visit(TypeMapArray elem) { return true; }
	@Override public boolean visit(TypeStaticArray elem) { return true; }
	
	@Override
	public boolean visit(TypeTypeof elem) {
		return true;
	}
	@Override
	public boolean visit(RefTemplateInstance elem) {
		return true;
	}
	
	@Override
	public boolean visit(DeclarationMixinString node) { return false; }
	
	@Override
	public boolean visit(DeclarationInvariant elem) {
		return true;
	}
	@Override
	public boolean visit(DeclarationUnitTest elem) {
		return true;
	}
	@Override
	public boolean visit(DeclarationConditional elem) {
		return true;
	}
	
	@Override
	public boolean visit(ExpLiteralFunc elem) {
		return true;
	}
	@Override
	public boolean visit(ExpLiteralNewAnonClass elem) {
		return true;
	}

	
}
