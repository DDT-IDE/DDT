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

import dtool.ast.declarations.AbstractConditionalDeclaration;
import dtool.ast.declarations.AttribAlign;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AttribLinkage;
import dtool.ast.declarations.AttribPragma;
import dtool.ast.declarations.AttribProtection;
import dtool.ast.declarations.DeclarationAllocatorFunction;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpFunctionLiteral;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralChar;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNewAnonClass;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypeFunction;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.RefTypeof;
import dtool.ast.references.Reference;

/**
 * Strict non-polymorphic visitor
 */
public class ASTHomogenousVisitor implements IASTVisitor {
	
	public static final boolean VISIT_CHILDREN = true;
	public static final boolean DONT_VISIT_CHILDREN = false;
	
	@Override
	public boolean preVisit(ASTNode node) {
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node) {
	}
	
	@Override
	public final boolean visit(ASTNode node) {
		return true;
	}
	
	@Override
	public final void endVisit(ASTNode node) {
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
	public final void endVisit(DefUnit node) {
	}
	
	@Override
	public final boolean visit(Module node) {
		return true;
	}
	
	@Override
	public final void endVisit(Module node) {
	}
	
	@Override
	public final boolean visit(DeclarationModule node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationModule node) {
	}
	
	@Override
	public final boolean visit(DeclarationImport node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationImport node) {
	}
	
	@Override
	public final boolean visit(ImportContent node) {
		return true;
	}
	
	@Override
	public final boolean visit(ImportAlias node) {
		return true;
	}
	
	@Override
	public final boolean visit(ImportSelective node) {
		return true;
	}
	
	@Override
	public final boolean visit(ImportSelectiveAlias node) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationEmpty node) {
		return true;
	}
	
	@Override
	public final boolean visit(AttribLinkage node) {
		return true;
	}
	
	@Override
	public final boolean visit(AttribAlign node) {
		return true;
	}
	
	@Override
	public final boolean visit(AttribPragma node) {
		return true;
	}
	
	@Override
	public final boolean visit(AttribProtection node) {
		return true;
	}
	
	@Override
	public final boolean visit(AttribBasic node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionStruct node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionStruct node) {
	}
	
	@Override
	public final boolean visit(DefinitionUnion node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionUnion node) {
	}
	
	@Override
	public final boolean visit(DefinitionClass node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionClass node) {
	}
	
	@Override
	public final boolean visit(DefinitionInterface node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionInterface node) {
	}
	
	@Override
	public final boolean visit(DefinitionTemplate node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionTemplate node) {
	}
	
	@Override
	public final boolean visit(DefinitionVariable node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionVariable node) {
	}
	
	@Override
	public final boolean visit(DefVarFragment node) {
		return true;
	}
	
	@Override
	public final boolean visit(DefinitionEnum node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionEnum node) {
	}
	
	@Override
	public final boolean visit(DefinitionAliasVarDecl node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionAliasVarDecl node) {
	}
	
	@Override
	public final boolean visit(DefinitionAliasFragment node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionAliasFragment node) {
	}
	
	@Override
	public final boolean visit(DefinitionFunction node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionFunction node) {
	}
	
	@Override
	public final boolean visit(DefinitionConstructor node) {
		return true;
	}
	
	@Override
	public final void endVisit(DefinitionConstructor node) {
	}
	
	@Override
	public final boolean visit(Resolvable node) {
		return true;
	}
	
	@Override
	public final void endVisit(Resolvable node) {
	}
	
	@Override
	public final boolean visit(Reference node) {
		return true;
	}
	
	@Override
	public final void endVisit(Reference node) {
	}
	
	@Override
	public final boolean visit(RefIdentifier node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefImportSelection node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefQualified node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefModuleQualified node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefPrimitive node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefModule node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTypeDynArray node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTypePointer node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTypeFunction node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefIndexing node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTypeof node) {
		return true;
	}
	
	@Override
	public final boolean visit(RefTemplateInstance node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpThis node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpSuper node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpNull node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpArrayLength node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralBool node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralInteger node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralString node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralChar node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpLiteralFloat node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpReference node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpPrefix node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpPostfixOperator node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpInfix node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpConditional node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpFunctionLiteral node) {
		return true;
	}
	
	@Override
	public final boolean visit(ExpNewAnonClass node) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationMixinString node) {
		return true;
	}
	
	@Override
	public final boolean visit(DeclarationInvariant node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationInvariant node) {
	}
	
	@Override
	public final boolean visit(DeclarationUnitTest node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationUnitTest node) {
	}
	
	@Override
	public final boolean visit(AbstractConditionalDeclaration node) {
		return true;
	}
	
	@Override
	public final void endVisit(AbstractConditionalDeclaration node) {
	}
	
	@Override
	public final boolean visit(DeclarationSpecialFunction node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationSpecialFunction node) {
	}
	
	@Override
	public final boolean visit(DeclarationAllocatorFunction node) {
		return true;
	}
	
	@Override
	public final void endVisit(DeclarationAllocatorFunction node) {
	}
	
}